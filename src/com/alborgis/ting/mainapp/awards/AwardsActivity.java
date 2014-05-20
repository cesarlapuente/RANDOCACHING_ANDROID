package com.alborgis.ting.mainapp.awards;

import java.util.ArrayList;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Award;
import com.alborgis.ting.base.model.Award.AwardListListener;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.common.custom_controls.endless_horizontal_tile_gridview.EndlessHorizontalTileGridView;
import com.alborgis.ting.mainapp.common.custom_controls.endless_horizontal_tile_gridview.EndlessHorizontalTileGridView.EndlessGridViewListener;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView.OnItemClickListener;
import com.alborgis.ting.mainapp.home.MainActivity;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AwardsActivity extends Activity implements AwardListListener, EndlessGridViewListener {

	public static final int ITEMS_BY_PAGE		=		18;
	public static final int START_PAGE			=		1;
	
	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;

	EndlessHorizontalTileGridView gridView;
	AwardsEndlessTileAdapter adapter;
	int gridViewNextPageToAsk = START_PAGE;
	
	RelativeLayout panelCargandoGrid;
	
	RelativeLayout panelGridVacio;
	TextView tvNoHayElementos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_awards__layout_awards);
		
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
	}

	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		gridView = (EndlessHorizontalTileGridView) findViewById(R.id.gridView);
		panelCargandoGrid = (RelativeLayout) findViewById(R.id.panelCargandoGrid);
		panelGridVacio = (RelativeLayout) findViewById(R.id.panelGridVacio);
		tvNoHayElementos = (TextView) findViewById(R.id.tvNoHayElementos);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(this);
		tvTitle.setTypeface(tfGullyLight);
		tvNoHayElementos.setTypeface(tfGullyBold);
		
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AwardsActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(TwoWayAdapterView<?> parent, View view,	int position, long id) {
				Award award = adapter.getItem(position);
				Intent intent = new Intent(AwardsActivity.this, AwardDetailActivity.class);
				intent.putExtra(AwardDetailActivity.PARAM_KEY_AWARD_NID, award.nid);
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

		
		// Hago la llamada al servicio con los parametros
		Milog.d("P‡gina que voy a pedir: " + gridViewNextPageToAsk);
		Award.listAwards(ITEMS_BY_PAGE, gridViewNextPageToAsk, app.deviceLang, app.drupalClient, app.drupalSecurity, this);		
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
	public void onAwardListLoad(ArrayList<Award> awards) {
		if(awards != null){
			
			Milog.d("Nœmero de elementos devueltos: " + awards.size());
			
			if(adapter == null || gridViewNextPageToAsk == START_PAGE){
				// Si el adapter es nulo o la p‡gina es la inicial, inicializarlo y asignar la colecci—n inicial de objetos
				adapter = new AwardsEndlessTileAdapter(this,
						R.layout.mod_awards__horizontal_grid_view_item, awards);
				gridView.setAdapter(adapter);
				gridView.setListener(this);
				gridView.setLoadingView(panelCargandoGrid);
				
				// Mostrar el panel de gridVacio, cuando no haya elementos
				if(awards.size() == 0){
					showEmptyGrid(true);
				}
				
			}else{
				if(awards.size() > 0){
					gridView.hideFooterView();
					if(Build.VERSION.SDK_INT >= 11){
						adapter.addAll(awards);
					}else{
						for(Award obj : awards){
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




	@Override
	public void onAwardListError(String error) {
		Milog.d("Error al cargar datos en el grid");
		// Ocultar cargando
		showLoading(false);
	}
	
	
	


	@Override
	public void onGridViewRequestLoadData() {
		Milog.d("onGridViewRequestNewData");
		cargarDatos();
	}




	


	

}
