package com.alborgis.ting.mainapp.home;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Destination;
import com.alborgis.ting.base.model.Destination.DestinationListPaginatedListener;
import com.alborgis.ting.base.model.Game;
import com.alborgis.ting.base.model.Game.GamesListListener;
import com.alborgis.ting.base.model.GeocachesGame;
import com.alborgis.ting.base.utils.GPS;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.awards.AwardsActivity;
import com.alborgis.ting.mainapp.common.DestinationListPopupWindow;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.common.DestinationListPopupWindow.CitiesListPopupListener;
import com.alborgis.ting.mainapp.common.GameTypesListPopupWindow;
import com.alborgis.ting.mainapp.common.GameTypesListPopupWindow.GameTypesListPopupListener;
import com.alborgis.ting.mainapp.common.custom_controls.endless_horizontal_tile_gridview.EndlessHorizontalTileGridView;
import com.alborgis.ting.mainapp.common.custom_controls.endless_horizontal_tile_gridview.EndlessHorizontalTileGridView.EndlessGridViewListener;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView.OnItemClickListener;
import com.alborgis.ting.mainapp.destinations.DestinationDetailActivity;
import com.alborgis.ting.mainapp.games.GameIntroActivity;
import com.alborgis.ting.mainapp.passport.PassportActivity;
import com.alborgis.ting.mainapp.stories.StoriesActivity;

import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements EndlessGridViewListener, LocationListener, GamesListListener, DestinationListPaginatedListener {

	public static final int ITEMS_BY_PAGE		=		15;//18;
	public static final int START_PAGE			=		1;
	
	MainApp app;

	EndlessHorizontalTileGridView gridView;
	HomeEndlessTileAdapter adapter;
	int gridViewNextPageToAsk = START_PAGE;
	
	RelativeLayout panelCargandoGrid;
	
	RelativeLayout panelGridVacio;
	TextView tvNoHayElementos;
	
	RelativeLayout footer;
	
	TextView lblBocadilloTexto1;
	TextView lblBocadilloTexto2;
	
	Button btnMostrarJuegos;
	Button btnMostrarSoloDestinos;
	Button btnSelCiudad;
	Button btnSelJuego;
	ToggleButton btnGPS;
	ImageButton btnDeslizaMenuInf;
	
	ImageButton btnHistorias;
	ImageButton btnPasaporte;
	ImageButton btnPremios;
	
	DestinationListPopupWindow comboCiudades;
	Destination filtroDestinoSeleccionado;
	
	GameTypesListPopupWindow comboTiposJuegos;
	BasicNameValuePair filtroGameTypeSeleccionado;
	
	GPS gps;
	Location lastLocation;
	
	boolean mostrarSoloDestinos = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_home__activity_main);
		
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

	}




	private void capturarControles() {
		lblBocadilloTexto1 = (TextView) findViewById(R.id.bocadilloTexto1);
		lblBocadilloTexto2 = (TextView) findViewById(R.id.bocadilloTexto2);
		gridView = (EndlessHorizontalTileGridView) findViewById(R.id.gridView);
		panelCargandoGrid = (RelativeLayout) findViewById(R.id.panelCargandoGrid);
		panelGridVacio = (RelativeLayout) findViewById(R.id.panelGridVacio);
		tvNoHayElementos = (TextView) findViewById(R.id.tvNoHayElementos);
		btnMostrarJuegos = (Button) findViewById(R.id.btnMostrarJuegos);
		btnMostrarSoloDestinos = (Button) findViewById(R.id.btnMostrarSoloDestinos);
		btnSelCiudad = (Button) findViewById(R.id.btnSelCiudad);
		btnSelJuego = (Button) findViewById(R.id.btnSelJuego);
		btnGPS = (ToggleButton) findViewById(R.id.btnGPS);
		footer = (RelativeLayout)findViewById(R.id.footer);
		btnDeslizaMenuInf = (ImageButton) findViewById(R.id.btnDeslizaMenuInf);
		btnHistorias = (ImageButton) findViewById(R.id.btnHistorias);
		btnPasaporte = (ImageButton) findViewById(R.id.btnPasaporte);
		btnPremios = (ImageButton) findViewById(R.id.btnPremios);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(this);
		Typeface tfDroidSansNormal = TINGTypeface.getDroidSansNormalTypeface(this);
		lblBocadilloTexto1.setTypeface(tfDroidSansNormal);
		lblBocadilloTexto2.setTypeface(tfDroidSansNormal);
		btnSelCiudad.setTypeface(tfDroidSansNormal);
		btnSelJuego.setTypeface(tfDroidSansNormal);
		tvNoHayElementos.setTypeface(tfGullyBold);
		btnMostrarJuegos.setTypeface(tfDroidSansNormal);
		btnMostrarSoloDestinos.setTypeface(tfDroidSansNormal);
		
		// Inicializar el gps
		gps = new GPS(this, this);
		
		// Inicializar el menœ de ciudades
		comboCiudades = new DestinationListPopupWindow(MainActivity.this, new CitiesListPopupListener() {
			public void onCitiesListItemSelect(Destination dest) {
				// Asignar el nuevo filtro de destino
				if(!dest.nid.equals("-1")){
					filtroDestinoSeleccionado = dest;
					// Establecer el nombre del filtro en el bot—n
					btnSelCiudad.setText(dest.title.toUpperCase());
				}else{
					filtroDestinoSeleccionado = null;
					// Establecer el nombre del filtro en el bot—n
					btnSelCiudad.setText(getString(R.string.home_ciudad));
				}
				
				// Poner p‡gina inicial
				gridViewNextPageToAsk = START_PAGE;
				// Cargar los datos desde el principio
				cargarDatos();
			}
		});
		
		// Inicializar el menœ de ciudades
		comboTiposJuegos = new GameTypesListPopupWindow(MainActivity.this, new GameTypesListPopupListener() {
			public void onGameTypesListItemSelect(BasicNameValuePair gameType) {

				// Asignar el nuevo filtro de tipo de juego
				if(!gameType.getName().equals("-1")){
					filtroGameTypeSeleccionado = gameType;
					// Establecer el nombre del filtro en el bot—n
					btnSelJuego.setText(gameType.getValue().toUpperCase());
				}else{
					filtroGameTypeSeleccionado = null;
					// Establecer el nombre del filtro en el bot—n
					btnSelJuego.setText(getString(R.string.home_juego));
				}
				
				// Poner p‡gina inicial
				gridViewNextPageToAsk = START_PAGE;
				// Cargar los datos desde el principio
				cargarDatos();
			}
		});
		
		// Ocultar el footer al entrar
		footer.setVisibility(View.GONE);
		
		// Ocultar el bot—n de mostrar juegos al entrar
		btnMostrarJuegos.setVisibility(View.GONE);
		
		// Por defecto desactivar el bot—n del gps (no se tiene en cuenta el filtro de gps)
		btnGPS.setChecked(false);

		
		HomeTipManager.showTipsForHomeStartup(this, app.preferencias, false);
		
	}

	private void escucharEventos() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(TwoWayAdapterView<?> parent, View view,	int position, long id) {
				Object obj = adapter.getItem(position);
				if(obj.getClass().equals(GeocachesGame.class)){
					GeocachesGame game = (GeocachesGame)obj;
					Intent intent = new Intent(MainActivity.this, GameIntroActivity.class);
					intent.putExtra(GameIntroActivity.PARAM_KEY_GAME_NID, game.nid);
					intent.putExtra(GameIntroActivity.PARAM_KEY_GAME_TYPE, game.type);
					intent.putExtra(GameIntroActivity.PARAM_KEY_GAME_MODALITY, game.modality);
					startActivity(intent);
					overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
				}else if(obj.getClass().equals(Destination.class)){
					Destination destination = (Destination)obj;
					Intent intent = new Intent(MainActivity.this, DestinationDetailActivity.class);
					intent.putExtra(DestinationDetailActivity.PARAM_KEY_DESTINATION_NID, destination.nid);
					startActivity(intent);
					overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
				}else{
					//MessageDialog.showMessage(MainActivity.this, "Pr—ximamente", "Pr—ximamente disponible");
				}
			}
		});
		
		btnSelCiudad.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				comboCiudades.showPopup(btnSelCiudad, filtroDestinoSeleccionado);
			}
		});
		btnSelJuego.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				comboTiposJuegos.showPopup(btnSelJuego, filtroGameTypeSeleccionado);
			}
		});
		btnMostrarJuegos.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mostrarSoloDestinos = false;
				// Mostrar el bot—n de mostrar s—lo destinos
				btnMostrarSoloDestinos.setVisibility(View.VISIBLE);
				// Mostrar los filtros para juegos
				btnSelCiudad.setVisibility(View.VISIBLE);
				btnSelJuego.setVisibility(View.VISIBLE);
				// Ocultar el bot—n de mostrar juegos
				btnMostrarJuegos.setVisibility(View.GONE);
				// Ocultar el footer
				footer.setVisibility(View.GONE);
				btnDeslizaMenuInf.setImageResource(R.drawable.btn_state_desliza_menu_inf);
				
				// Poner p‡gina inicial
				gridViewNextPageToAsk = START_PAGE;
				// Cargar los datos desde el principio
				cargarDatos();
			}
		});
		btnMostrarSoloDestinos.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				mostrarSoloDestinos = true;
				// Ocultar el bot—n de mostrar s—lo destinos
				btnMostrarSoloDestinos.setVisibility(View.GONE);
				// Ocultar los filtros para juegos
				btnSelCiudad.setVisibility(View.GONE);
				btnSelJuego.setVisibility(View.GONE);
				// Mostrar el bot—n de mostrar juegos
				btnMostrarJuegos.setVisibility(View.VISIBLE);
				// Ocultar el footer
				footer.setVisibility(View.GONE);
				btnDeslizaMenuInf.setImageResource(R.drawable.btn_state_desliza_menu_inf);
				
				// Poner p‡gina inicial
				gridViewNextPageToAsk = START_PAGE;
				// Cargar los datos desde el principio
				cargarDatos();
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
		btnDeslizaMenuInf.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(footer.getVisibility() == View.GONE){
					footer.setVisibility(View.VISIBLE);
					btnDeslizaMenuInf.setImageResource(R.drawable.btn_state_pliega_menu_inf);
				}else if(footer.getVisibility() == View.VISIBLE){
					footer.setVisibility(View.GONE);
					btnDeslizaMenuInf.setImageResource(R.drawable.btn_state_desliza_menu_inf);
				}
			}
		});
		btnPasaporte.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, PassportActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		btnPremios.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, AwardsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		btnHistorias.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, StoriesActivity.class);
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
		
		
		if(mostrarSoloDestinos){
			String lat = null;
			String lon = null;
			int radio = 0;
			if(lastLocation != null){
				lat = String.valueOf(lastLocation.getLatitude());
				lon = String.valueOf(lastLocation.getLongitude());
				radio = app.MAX_DISTANCE_SEARCH_HOME;
			}
						
			// Hago la llamada al servicio con los parametros
			Milog.d("P‡gina que voy a pedir: " + gridViewNextPageToAsk);
			Destination.listDestinationsPaginated(lat, lon, radio, ITEMS_BY_PAGE, gridViewNextPageToAsk, null, app.drupalClient, app.drupalSecurity, this);
		}else{
			// Recojo los filtros que ha seleccionado
			String destNid = null;
			if(filtroDestinoSeleccionado != null){
				destNid = filtroDestinoSeleccionado.nid;
			}
			String gameType = null;
			if(filtroGameTypeSeleccionado != null){
				gameType = filtroGameTypeSeleccionado.getName();
			}
			String lat = null;
			String lon = null;
			int radio = 0;
			if(lastLocation != null){
				lat = String.valueOf(lastLocation.getLatitude());
				lon = String.valueOf(lastLocation.getLongitude());
				radio = app.MAX_DISTANCE_SEARCH_HOME;
			}
						
			// Hago la llamada al servicio con los parametros
			Milog.d("P‡gina que voy a pedir: " + gridViewNextPageToAsk);
			Game.listGames(destNid, gameType, lat, lon, radio, ITEMS_BY_PAGE, gridViewNextPageToAsk, null, app.drupalClient, app.drupalSecurity, this);
		}
		
		
				
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


	public void onGamesListLoad(ArrayList<Object> listItems) {
		if(listItems != null){
			
			Milog.d("Nœmero de elementos devueltos: " + listItems.size());
			
			if(adapter == null || gridViewNextPageToAsk == START_PAGE){
				// Si el adapter es nulo o la p‡gina es la inicial, inicializarlo y asignar la colecci—n inicial de objetos
				adapter = new HomeEndlessTileAdapter(this,
						R.layout.mod_home__horizontal_grid_view_item, listItems);
				gridView.setAdapter(adapter);
				gridView.setListener(this);
				gridView.setLoadingView(panelCargandoGrid);
				
				// Mostrar el panel de gridVacio, cuando no haya elementos
				if(listItems.size() == 0){
					showEmptyGrid(true);
				}
				
			}else{
				if(listItems.size() > 0){
					//gridView.addNewData(listItems);
					gridView.hideFooterView();
					if(Build.VERSION.SDK_INT >= 11){
						adapter.addAll(listItems);
					}else{
						for(Object obj : listItems){
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

	public void onGamesListError(String error) {
		Milog.d("Error al cargar datos en el grid");
		// Ocultar cargando
		showLoading(false);
	}
	
	
	
	

	public void onDestinationListLoad(ArrayList<Object> listItems) {
		if(listItems != null){
			
			Milog.d("Nœmero de elementos devueltos: " + listItems.size());
			
			if(adapter == null || gridViewNextPageToAsk == START_PAGE){
				// Si el adapter es nulo o la p‡gina es la inicial, inicializarlo y asignar la colecci—n inicial de objetos
				adapter = new HomeEndlessTileAdapter(this,
						R.layout.mod_home__horizontal_grid_view_item, listItems);
				gridView.setAdapter(adapter);
				gridView.setListener(this);
				gridView.setLoadingView(panelCargandoGrid);
				
				// Mostrar el panel de gridVacio, cuando no haya elementos
				if(listItems.size() == 0){
					showEmptyGrid(true);
				}
				
			}else{
				if(listItems.size() > 0){
					//gridView.addNewData(listItems);
					gridView.hideFooterView();
					if(Build.VERSION.SDK_INT >= 11){
						adapter.addAll(listItems);
					}else{
						for(Object obj : listItems){
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

	public void onDestinationListLoadError(String error) {
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
