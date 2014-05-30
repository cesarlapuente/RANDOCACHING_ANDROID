package com.alborgis.randocaching.mainapp.stories;

import java.util.ArrayList;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Destination;
import com.alborgis.ting.base.model.Info;
import com.alborgis.ting.base.model.Info.InfoListListener;
import com.alborgis.ting.base.utils.GPS;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.MainApp;
import com.alborgis.randocaching.mainapp.awards.AwardsActivity;
import com.alborgis.randocaching.mainapp.common.DestinationListPopupWindow;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;
import com.alborgis.randocaching.mainapp.common.DestinationListPopupWindow.CitiesListPopupListener;
import com.alborgis.randocaching.mainapp.common.custom_controls.endless_horizontal_tile_gridview.EndlessHorizontalTileGridView;
import com.alborgis.randocaching.mainapp.common.custom_controls.endless_horizontal_tile_gridview.EndlessHorizontalTileGridView.EndlessGridViewListener;
import com.alborgis.randocaching.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView;
import com.alborgis.randocaching.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView.OnItemClickListener;
import com.alborgis.randocaching.mainapp.home.MainActivity;
import com.alborgis.randocaching.mainapp.passport.PassportActivity;

import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class StoriesActivity extends Activity implements InfoListListener, EndlessGridViewListener, LocationListener {

	public static final int ITEMS_BY_PAGE		=		18;
	public static final int START_PAGE			=		1;
	
	MainApp app;

	TextView tvMensajeBocadilloGully;
	TextView tvTitle;
	Button btnSelCiudad;
	ToggleButton btnGPS;
	
	ImageButton btnJugar;
	ImageButton btnPasaporte;
	ImageButton btnPremios;

	EndlessHorizontalTileGridView gridView;
	StoriesEndlessTileAdapter adapter;
	int gridViewNextPageToAsk = START_PAGE;
	
	RelativeLayout panelCargandoGrid;
	
	RelativeLayout panelGridVacio;
	TextView tvNoHayElementos;
	
	
	DestinationListPopupWindow comboCiudades;
	Destination filtroDestinoSeleccionado;
	
	GPS gps;
	Location lastLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_stories__layout_stories);
		
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

		this.app = (MainApp) getApplication();

		capturarControles();
		inicializarForm();
		escucharEventos();

		cargarDatos();
	}
	

	

	protected void onResume() {
		super.onResume();

	}
	
	public void onPause() {
		super.onPause();
		// Paramos el gps si est‡ activo
		if(gps != null){
			gps.stopLocating();
		}
	}

	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
	}




	private void capturarControles() {
		tvMensajeBocadilloGully = (TextView) findViewById(R.id.tvMensaje);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnSelCiudad = (Button)findViewById(R.id.btnSelCiudad);
		btnGPS = (ToggleButton)findViewById(R.id.btnGPS);
		btnJugar = (ImageButton)findViewById(R.id.btnJugar);
		btnPasaporte = (ImageButton)findViewById(R.id.btnPasaporte);
		btnPremios = (ImageButton)findViewById(R.id.btnPremios);
		gridView = (EndlessHorizontalTileGridView) findViewById(R.id.gridView);
		panelCargandoGrid = (RelativeLayout) findViewById(R.id.panelCargandoGrid);
		panelGridVacio = (RelativeLayout) findViewById(R.id.panelGridVacio);
		tvNoHayElementos = (TextView) findViewById(R.id.tvNoHayElementos);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(this);
		Typeface tfDroidSansNormal = TINGTypeface.getDroidSansNormalTypeface(this);
		tvTitle.setTypeface(tfGullyBold);
		tvNoHayElementos.setTypeface(tfGullyBold);
		tvMensajeBocadilloGully.setTypeface(tfDroidSansNormal);
		btnSelCiudad.setTypeface(tfDroidSansNormal);
		
		// Inicializar el gps
		gps = new GPS(this, this);
		
		// Inicializar el menœ de ciudades
		comboCiudades = new DestinationListPopupWindow(StoriesActivity.this, new CitiesListPopupListener() {
			public void onCitiesListItemSelect(Destination dest) {
				// Asignar el nuevo filtro de destino
				if(!dest.nid.equals("-1")){
					filtroDestinoSeleccionado = dest;
					// Establecer el nombre del filtro en el bot—n
					btnSelCiudad.setText(dest.title.toUpperCase());
				}else{
					filtroDestinoSeleccionado = null;
					// Establecer el nombre del filtro en el bot—n
					btnSelCiudad.setText(getString(R.string.historias_ciudad));
				}
				
				// Poner p‡gina inicial
				gridViewNextPageToAsk = START_PAGE;
				// Cargar los datos desde el principio
				cargarDatos();
			}
		});
		
		// Por defecto desactivar el bot—n del gps (no se tiene en cuenta el filtro de gps)
		btnGPS.setChecked(false);
		
	}

	private void escucharEventos() {
		
		btnSelCiudad.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				comboCiudades.showPopup(btnSelCiudad, filtroDestinoSeleccionado);
			}
		});
		btnGPS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					// Si est‡ marcado, ubicar por gps
					showLoading(true);
					gps.startLocating();
				}else{
					// Si no est‡ marcado borrar filtro de ubicaci—n y cargar elementos sin tener en cuenta la ubicaci—n
					// Olvidar el filtro de ubicaci—n
					lastLocation = null;
					// Poner p‡gina inicial
					gridViewNextPageToAsk = START_PAGE;
					// Cargar los datos desde el principio
					cargarDatos();
				}
			}
		});
		btnJugar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(StoriesActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		btnPasaporte.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(StoriesActivity.this, PassportActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		btnPremios.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(StoriesActivity.this, AwardsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(TwoWayAdapterView<?> parent, View view,	int position, long id) {
				Info info = adapter.getItem(position);
				Intent intent = new Intent(StoriesActivity.this, StoryDetailActivity.class);
				intent.putExtra(StoryDetailActivity.PARAM_KEY_STORY_NID, info.nid);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		
	}

	
	private void cargarDatos(){
		// Mostrar cargando
		showLoading(true);
		
		// Ocultar el panel de grid Vac’o
		showEmptyGrid(false);

		// Recojo los filtros que ha seleccionado
		String destNid = null;
		if(filtroDestinoSeleccionado != null){
			destNid = filtroDestinoSeleccionado.nid;
		}
		
		String lat = null;
		String lon = null;
		int radio = 0;
		if(lastLocation != null){
			lat = String.valueOf(lastLocation.getLatitude());
			lon = String.valueOf(lastLocation.getLongitude());
			radio = app.MAX_DISTANCE_SEARCH_STORIES;
		}
		
		// Hago la llamada al servicio con los parametros
		Milog.d("P‡gina que voy a pedir: " + gridViewNextPageToAsk);
		Info.listInfos(destNid, lat, lon, radio, ITEMS_BY_PAGE, gridViewNextPageToAsk, app.deviceLang, app.drupalClient, app.drupalSecurity, this);	
	}

	
	private void showLoading(boolean show){
		if(show){
			// Mostrar el panel de carga
			this.panelCargandoGrid.setVisibility(View.VISIBLE);
		}else{
			// Ocultar el panel de carga
			this.panelCargandoGrid.setVisibility(View.GONE);
		}
	}
	
	private void showEmptyGrid(boolean show){
		if(show){
			// Mostrar el panel de carga
			this.panelGridVacio.setVisibility(View.VISIBLE);
		}else{
			// Ocultar el panel de carga
			this.panelGridVacio.setVisibility(View.GONE);
		}
	}
	

	
	@Override
	public void onInfoListLoad(ArrayList<Info> infos) {
		if(infos != null){
			
			Milog.d("Nœmero de elementos devueltos: " + infos.size());
			
			if(adapter == null || gridViewNextPageToAsk == START_PAGE){
				// Si el adapter es nulo o la p‡gina es la inicial, inicializarlo y asignar la colecci—n inicial de objetos
				adapter = new StoriesEndlessTileAdapter(this,
						R.layout.mod_stories__horizontal_grid_view_item, infos);
				gridView.setAdapter(adapter);
				gridView.setListener(this);
				gridView.setLoadingView(panelCargandoGrid);
				
				// Mostrar el panel de gridVacio, cuando no haya elementos
				if(infos.size() == 0){
					showEmptyGrid(true);
				}
				
			}else{
				if(infos.size() > 0){
					gridView.hideFooterView();
					if(Build.VERSION.SDK_INT >= 11){
						adapter.addAll(infos);
					}else{
						for(Info obj : infos){
							adapter.add(obj);
						}
					}
					adapter.notifyDataSetChanged();
					gridView.setLoading(false);
				}else{
					// Ocultar cargando
					showLoading(false);
				}
			}
			
			
			gridViewNextPageToAsk++;
		}
	}



	public void onInfoListError(String error) {
		Milog.d("Error al cargar datos en el grid");
		// Ocultar cargando
		showLoading(false);
	}
	
	


	@Override
	public void onGridViewRequestLoadData() {
		Milog.d("onGridViewRequestNewData");
		cargarDatos();
	}




	@Override
	public void onLocationChanged(Location location) {
		if(location != null){
			Milog.d("onLocationChanged: " + location.getLatitude() + "  " + location.getLongitude() + " Generada por:" + location.getProvider());
			lastLocation = location;
			gps.stopLocating();
			
			// Poner p‡gina inicial
			gridViewNextPageToAsk = START_PAGE;
			// Cargar los datos desde el principio
			cargarDatos();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}




	


	

}
