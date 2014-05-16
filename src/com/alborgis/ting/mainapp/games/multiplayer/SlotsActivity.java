package com.alborgis.ting.mainapp.games.multiplayer;

import java.util.ArrayList;
import java.util.List;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Slot;
import com.alborgis.ting.base.model.Slot.SlotsListListener;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.common.custom_controls.endless_vertical_listview.EndlessVerticalListView;
import com.alborgis.ting.mainapp.common.custom_controls.endless_vertical_listview.EndlessVerticalListView.EndlessListViewListener;
import com.alborgis.ting.mainapp.home.MainActivity;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SlotsActivity extends Activity implements SlotsListListener, EndlessListViewListener {

	
	public static final String PARAM_KEY_FILTER_GAME_NID	=	"param_key_filter_game_nid";
	public static final String PARAM_KEY_FILTER_GAME_TITLE	=	"param_key_filter_game_title";
	public static final String PARAM_KEY_FILTER_GAME_TYPE	=	"param_key_filter_game_type";
	public static final String PARAM_KEY_FILTER_GAME_MODALITY	=	"param_key_filter_game_modality";
	
	
	private static final int ITEMS_BY_PAGE		=		18;
	private static final int START_PAGE			=		1;
	
	String filterGameNid;
	String filterGameTitle;
	String filterGameType;
	String filterGameModality;
	
	
	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	
	Button btnCrearSlot;
	Button btnActualizar;

	EndlessVerticalListView listView;
	SlotsEndlessAdapter adapter;
	int listViewNextPageToAsk = START_PAGE;
	
	RelativeLayout panelCargandoListView;
	
	RelativeLayout panelListViewVacio;
	TextView tvNoHayElementos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_games__layout_slots);

		this.app = (MainApp) getApplication();
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			filterGameNid = b.getString(PARAM_KEY_FILTER_GAME_NID);
			filterGameTitle = b.getString(PARAM_KEY_FILTER_GAME_TITLE);
			filterGameType = b.getString(PARAM_KEY_FILTER_GAME_TYPE);
			filterGameModality = b.getString(PARAM_KEY_FILTER_GAME_MODALITY);
		}

		capturarControles();
		inicializarForm();
		escucharEventos();

		
		
		app.pushNotificationsClient.start(this); // Registrar el dispositivo para que reciba notificaciones push
	}
	

	

	protected void onResume() {
		super.onResume();

		// Poner p‡gina inicial
		listViewNextPageToAsk = START_PAGE;
		cargarDatos();
	}
	
	public void onPause() {
		super.onPause();
	}

	public void finish() {
		super.finish();
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnCrearSlot = (Button) findViewById(R.id.btnCrearSlot);
		btnActualizar = (Button) findViewById(R.id.btnActualizar);
		listView = (EndlessVerticalListView) findViewById(R.id.listView);
		panelCargandoListView = (RelativeLayout) findViewById(R.id.panelCargandoListView);
		panelListViewVacio = (RelativeLayout) findViewById(R.id.panelListViewVacio);
		tvNoHayElementos = (TextView) findViewById(R.id.tvNoHayElementos);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(this);
		tvTitle.setTypeface(tfGullyLight);
		tvNoHayElementos.setTypeface(tfGullyBold);
		btnCrearSlot.setTypeface(tfGullyBold);
		btnActualizar.setTypeface(tfGullyBold);
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SlotsActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
			}
		});
		
		btnCrearSlot.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SlotsActivity.this, CreateSlotActivity.class);
				intent.putExtra(CreateSlotActivity.PARAM_KEY_NID_GAME, filterGameNid);
				intent.putExtra(CreateSlotActivity.PARAM_KEY_TITLE_GAME, filterGameTitle);
				startActivity(intent);
			}
		});
		
		btnActualizar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Poner p‡gina inicial
				listViewNextPageToAsk = START_PAGE;
				cargarDatos();
			}
		});
		
	}

	
	private void cargarDatos(){
		// Mostrar cargando
		showLoading(true);
		
		// Ocultar el panel de grid Vac’o
		showEmptyListView(false);

		
		// Hago la llamada al servicio con los parametros
		Milog.d("P‡gina que voy a pedir: " + listViewNextPageToAsk);
		Slot.listSlots(null, ITEMS_BY_PAGE, listViewNextPageToAsk, null, app.drupalClient, app.drupalSecurity, this);		
	}

	
	private void showLoading(boolean show){
		if(show){
			// Mostrar el panel de carga
			this.panelCargandoListView.setVisibility(View.VISIBLE);
		}else{
			// Ocultar el panel de carga
			this.panelCargandoListView.setVisibility(View.GONE);
		}
	}
	
	private void showEmptyListView(boolean show){
		if(show){
			// Mostrar el panel de carga
			this.panelListViewVacio.setVisibility(View.VISIBLE);
		}else{
			// Ocultar el panel de carga
			this.panelListViewVacio.setVisibility(View.GONE);
		}
	}
	


	public void onSlotsListLoad(ArrayList<Slot> slots) {
		if(slots != null){
			
			Milog.d("Nœmero de elementos devueltos: " + slots.size());
			
			if(adapter == null || listViewNextPageToAsk == START_PAGE){
				// Si el adapter es nulo o la p‡gina es la inicial, inicializarlo y asignar la colecci—n inicial de objetos
				adapter = new SlotsEndlessAdapter(this,
						R.layout.mod_games__slot_listview_item, slots);
				listView.setAdapter(adapter);
				listView.setListener(this);
				listView.setLoadingView(panelCargandoListView);
				
				// Mostrar el panel de gridVacio, cuando no haya elementos
				if(slots.size() == 0){
					showEmptyListView(true);
				}
				
			}else{
				if(slots.size() > 0){
					listView.hideFooterView();
					if(Build.VERSION.SDK_INT >= 11){
						adapter.addAll(slots);
					}else{
						for(Slot obj : slots){
							adapter.add(obj);
						}
					}
					adapter.notifyDataSetChanged();
					listView.setLoading(false);
				}else{
					// Ocultar cargando
					showLoading(false);
				}
			}
			
			
			listViewNextPageToAsk++;
		}
	}

	public void onSlotsListError(String error) {
		Milog.d("Error al cargar datos en el listview");
		// Ocultar cargando
		showLoading(false);
	}

	
	



	public void onListViewRequestLoadData() {
		Milog.d("onGridViewRequestNewData");
		cargarDatos();
	}




	
	public class SlotsEndlessAdapter extends ArrayAdapter<Slot> {
		

		LayoutInflater mInflater;
		List<Slot> itemList;
		Context ctx;
		int layoutId;


		public class ViewHolder {
			RelativeLayout content;
			TextView tvTitle;
			TextView tvOwner;
			TextView tvGame;
			TextView tvPlayers;
			TextView tvState;
			Button btnJoin;
			int index;
		}

		public SlotsEndlessAdapter(Context ctx, int layoutId, List<Slot> itemList) {
			super(ctx, layoutId, itemList);
			this.itemList = itemList;
			this.ctx = ctx;
			this.layoutId = layoutId;
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		public int getCount() {
			if (itemList != null) {
				return itemList.size();
			} else {
				return 0;
			}
		}

		@Override
		public Slot getItem(int position) {
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			//return itemList.get(position).hashCode();
			return position;
		}
		
		public void removeAllItems(){
			if(itemList != null){
				itemList.clear();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(layoutId, null);
				holder.content = (RelativeLayout) convertView.findViewById(R.id.content);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
				holder.tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
				holder.tvGame = (TextView) convertView.findViewById(R.id.tvGame);
				holder.tvPlayers = (TextView) convertView.findViewById(R.id.tvPlayers);
				holder.tvState = (TextView) convertView.findViewById(R.id.tvState);
				holder.btnJoin = (Button) convertView.findViewById(R.id.btnJoinSlot);
				Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(ctx);
				Typeface tfBold = TINGTypeface.getGullyBoldTypeface(ctx);
				holder.tvTitle.setTypeface(tfBold);
				holder.tvOwner.setTypeface(tfNormal);
				holder.tvGame.setTypeface(tfNormal);
				holder.tvPlayers.setTypeface(tfNormal);
				holder.tvState.setTypeface(tfNormal);
				holder.btnJoin.setTypeface(tfNormal);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			final Slot slot = getItem(position);
			
			// Poner t’tulo
			if(slot.title != null && !slot.title.isEmpty()){
				holder.tvTitle.setText(slot.title.toUpperCase());
			}else{
				holder.tvTitle.setText(getString(R.string.multiplayer_partida_sin_nombre));
			}
			
			// Poner juego
			if(slot.game != null && slot.game.title != null){
				holder.tvGame.setText(getString(R.string.multiplayer_juego) + ": " + slot.game.title);
			}else{
				holder.tvGame.setText(getString(R.string.multiplayer_sin_juego));
			}
			
			// Poner jugadores
			holder.tvPlayers.setText( String.format("%d / %d %s", slot.numCurrentPlayers, getString(R.string.multiplayer_jugadores), slot.numMaxPlayers) );
			
			// Poner creador
			if(slot.owner != null){
				holder.tvOwner.setText(getString(R.string.multiplayer_creada_por) + " " + slot.owner.mail);
			}else{
				holder.tvOwner.setText(getString(R.string.multiplayer_sin_creador));
			}
			
			// Poner estado de la partida
			if(slot.active){
				if(slot.numCurrentPlayers < slot.numMaxPlayers){
					holder.tvState.setText(getString(R.string.multiplayer_estado_en_curso_aun_queda_hueco));
				}else{
					holder.tvState.setText(getString(R.string.multiplayer_estado_en_curso));
				}
			}else{
				if(slot.finished){
					holder.tvState.setText(getString(R.string.multiplayer_estado_finalizada));
				}else{
					if(slot.numCurrentPlayers >= slot.numMinPlayers){
						holder.tvState.setText(getString(R.string.multiplayer_estado_lista_para_jugar));
					}else{
						holder.tvState.setText(getString(R.string.multiplayer_estado_esperando_jugadores));
					}
				}
				
			}
			
			
			// El bot—n de unirse
			if(slot.numCurrentPlayers < slot.numMaxPlayers){
				holder.btnJoin.setVisibility(View.VISIBLE);
				holder.btnJoin.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						// Abrir la pantalla de unirse al juego (join)
						Intent intent = new Intent(ctx, JoinSlotActivity.class);
						intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, slot.nid);
						ctx.startActivity(intent);
					}
				});
			}else{
				holder.btnJoin.setVisibility(View.GONE);
			}


			return convertView;
		}

	}



	


	

}
