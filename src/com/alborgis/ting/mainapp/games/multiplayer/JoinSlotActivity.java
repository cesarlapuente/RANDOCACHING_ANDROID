package com.alborgis.ting.mainapp.games.multiplayer;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.GeocachesGame;
import com.alborgis.ting.base.model.Slot;
import com.alborgis.ting.base.model.Slot.SlotItemListener;
import com.alborgis.ting.base.model.Slot.SlotLeaveListener;
import com.alborgis.ting.base.model.Slot.SlotStartListener;
import com.alborgis.ting.base.model.User;
import com.alborgis.ting.base.model.Slot.SlotJoinListener;
import com.alborgis.ting.base.model.Slot.SlotUserIsJoinedListener;
import com.alborgis.ting.base.model.User.UserSessionListener;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.MessageDialog.MessageDialogListener;
import com.alborgis.ting.mainapp.common.push.TINGPushHandler;
import com.alborgis.ting.mainapp.common.push.TINGPushHandler.EVENTS;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.games.geocache_battle.GeocacheBattleMapActivity;
import com.alborgis.ting.mainapp.home.MainActivity;
import com.alborgis.ting.mainapp.login.LoginPopupWindow;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JoinSlotActivity extends Activity {

	
	public static final String PARAM_KEY_SLOT_NID	=	"param_key_slot_nid";

	public static boolean isVisible = false; // Esta variable va cambiando si el activity est‡ visible o no
	
	
	String nidSlot;
	
	
	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	TextView tvGameTitle;
	TextView tvJugadores;
	ListView listViewPlayers;
	TextView tvSlotState;
	Button btnJugar;
	Button btnAbandonar;
	
	PlayersSlotAdapter adapter;
	
	Slot currentSlot;
	
	User currentUser;
	
	
	BroadcastReceiver pushReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			String message = extras.getString("message");
			String data = extras.getString("data");
	       	if(data != null){
	      		try {
	      			JSONObject dataDic = new JSONObject(data);
	               	String event = dataDic.getString("event");
	               	String slot_nid = dataDic.getString("slot_nid");
	               	if(slot_nid != null && slot_nid.equals(nidSlot)){
	               		// Comprobar por el evento que nos env’an y mostrar notificacion
		               	if(event.equals(EVENTS.USER_JOINED_TO_SLOT)){
		               		// Si el evento es que un usuario se ha unido a una partida...
		               		if(isVisible){
		               			Toast toast = Toast.makeText(JoinSlotActivity.this, message, Toast.LENGTH_SHORT);
			               		toast.setGravity(Gravity.BOTTOM, 0, 0);
			               		toast.show();
		               		}
		               		
		               		
		               	}else if(event.equals(EVENTS.USER_START_SLOT_PLAY)){
		               		// Si el evento es que un usuario ha comenzado jugando la partida...
		               		if(isVisible){
		               			Toast toast = Toast.makeText(JoinSlotActivity.this, message, Toast.LENGTH_SHORT);
			               		toast.setGravity(Gravity.BOTTOM, 0, 0);
			               		toast.show();
		               		}
		               		
		               	}else if(event.equals(EVENTS.USER_LEAVED_SLOT)){
		               		// Si el evento es que un usuario ha abandonado la partida...
		               		if(isVisible){
		               			Toast toast = Toast.makeText(JoinSlotActivity.this, message, Toast.LENGTH_SHORT);
			               		toast.setGravity(Gravity.BOTTOM, 0, 0);
			               		toast.show();
		               		}
		               		
		               	}

		               	// Recargar la lista de usuarios
	       				retrieveSlotData();
	               	}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	              	
	              	
	        }
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_games__layout_join_slot);

		this.app = (MainApp) getApplication();
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			nidSlot = b.getString(PARAM_KEY_SLOT_NID);
			Milog.d("Slot nid recibido en el Bundle: " + nidSlot);
		}

		capturarControles();
		inicializarForm();
		escucharEventos();
	}
	



	protected void onResume() {
		super.onResume();
		isVisible = true; // Poner que el activity est‡ visible
		checkSession();
		updateView();
	}
	
	public void onPause() {
		super.onPause();
		isVisible = false; // Poner que el activity est‡ no visible (en background)
	}

	public void finish() {
		super.finish();
		unregisterReceiver(pushReceiver);
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvGameTitle = (TextView) findViewById(R.id.tvGameTitle);
		tvJugadores = (TextView) findViewById(R.id.tvJugadores);
		listViewPlayers = (ListView)findViewById(R.id.listViewPlayers);
		tvSlotState = (TextView) findViewById(R.id.tvSlotState);
		btnJugar = (Button) findViewById(R.id.btnJugar);
		btnAbandonar = (Button)findViewById(R.id.btnAbandonar);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyNormal = TINGTypeface.getGullyLightTypeface(this);
		tvTitle.setTypeface(tfGullyLight);
		tvGameTitle.setTypeface(tfGullyNormal);
		tvJugadores.setTypeface(tfGullyLight);
		tvSlotState.setTypeface(tfGullyNormal);
		
		btnAbandonar.setTypeface(tfGullyNormal);
		
		btnJugar.setVisibility(View.INVISIBLE);
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(JoinSlotActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		btnJugar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkSessionForStartPlayingSlot();
			}
		});
		
		btnAbandonar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				MessageDialog.showMessageWith2Buttons(JoinSlotActivity.this, getString(R.string.multiplayer_abandonar_la_partida), getString(R.string.multiplayer_seguro_que_deseas_abandonar_la_partida), getString(R.string.multiplayer_si),  getString(R.string.multiplayer_no), new MessageDialogListener() {
					public void onPositiveButtonClick(final MessageDialog dialog) {
						LoadingDialog.showLoading(JoinSlotActivity.this);
						Slot.leaveSlot(nidSlot, app.deviceLang, app.drupalClient, app.drupalSecurity, new SlotLeaveListener() {
							public void onSlotUserLeaved(String uid, String nidSlot) {
								LoadingDialog.hideLoading(JoinSlotActivity.this);
								dialog.dismiss();
								Toast.makeText(JoinSlotActivity.this, getString(R.string.multiplayer_abandonaste_la_partida), Toast.LENGTH_SHORT).show();
								finish();
							}
							public void onSlotUserLeaveError(String error) {
								LoadingDialog.hideLoading(JoinSlotActivity.this);
								dialog.dismiss();
								MessageDialog.showMessage(JoinSlotActivity.this, getString(R.string.multiplayer_error), error);
							}
						});
					}
					public void onNegativeButtonClick(MessageDialog dialog) {
						dialog.dismiss();
					}
				});
			}
		});
		
		// Registrar eventos al recibir notificaciones
		this.registerReceiver(pushReceiver, new IntentFilter("MyGCMMessageReceived"));

	}
	
	
	private void updateView(){
		
		btnJugar.setVisibility(View.INVISIBLE);
		
		if(currentSlot != null){
			// Actualizar el t’tulo del slot
			if(currentSlot.title != null && !currentSlot.title.isEmpty()){
				tvTitle.setText(currentSlot.title.toUpperCase());
			}else{
				tvTitle.setText(getString(R.string.multiplayer_partida_sin_nombre));
			}
			
			// Actualizar el titulo del juego
			if(currentSlot.game != null && currentSlot.game.title != null && !currentSlot.game.title.isEmpty()){
				tvGameTitle.setText(currentSlot.game.title.toUpperCase());
			}else{
				tvGameTitle.setText(getString(R.string.multiplayer_jugador_sin_nombre));
			}
			
			// Actualizar los jugadores
			if(currentSlot.players != null){
				if(adapter == null){
					// Si el adapter es nulo o la p‡gina es la inicial, inicializarlo y asignar la colecci—n inicial de objetos
					adapter = new PlayersSlotAdapter(this,
							R.layout.mod_games__player_listview_item, currentSlot.players);
					listViewPlayers.setAdapter(adapter);
				}else{
					adapter.setItems(currentSlot.players);
					adapter.notifyDataSetChanged();
				}
			}
			
			// Actualizar el estado de la partida
			if(currentSlot.active){
				if(currentSlot.numCurrentPlayers < currentSlot.numMaxPlayers){
					tvSlotState.setText(getString(R.string.multiplayer_estado_en_curso_aun_queda_hueco));
				}else{
					tvSlotState.setText(getString(R.string.multiplayer_estado_en_curso));
				}
				btnJugar.setVisibility(View.VISIBLE);
			}else{
				if(currentSlot.numCurrentPlayers >= currentSlot.numMinPlayers){
					tvSlotState.setText(getString(R.string.multiplayer_estado_lista_para_jugar));
					btnJugar.setVisibility(View.VISIBLE);
				}else{
					tvSlotState.setText(getString(R.string.multiplayer_estado_esperando_jugadores));
					btnJugar.setVisibility(View.INVISIBLE);
				}
			}
			
			if(currentSlot.finished){
				tvSlotState.setText(getString(R.string.multiplayer_estado_finalizada));
				btnJugar.setVisibility(View.INVISIBLE);
				btnAbandonar.setVisibility(View.INVISIBLE);
			}
			
			
			// Actualizar el usuario logueado
			currentUser = User.getCurrentUser(app.preferencias);
			
			// Quitar las notificaciones de este slot
			try{
				TINGPushHandler.cancelNotification(this, Integer.parseInt(nidSlot));
			}catch(Exception ex){}
			
		}
		
		
		
	}
	
	
	
	private void checkSession(){
		LoadingDialog.showLoading(this);
		User.checkIfUserIsLoggedIn(app.drupalClient,app.preferencias,
				new UserSessionListener() {
					public void onSessionChecked(
							boolean userIsLoggedIn, boolean isTempUser) {
						if (!userIsLoggedIn || isTempUser) {
							LoadingDialog.hideLoading(JoinSlotActivity.this);
							boolean enableDemoLogin = false;
							// Si no est‡ logueado pedir login
							LoginPopupWindow lp = new LoginPopupWindow(
									JoinSlotActivity.this,
									getApplication(),
									JoinSlotActivity.this, enableDemoLogin, true,
									new LoginPopupWindow.LoginPopupWindowListener() {
										public void onLoginPopupWindowDismiss(boolean isLoggedIn, boolean isTempUser) {
											if (!isLoggedIn || isTempUser) {
												finish();
											}else{
												// Comprobar si el usuario actual est‡ unido al slot
												checkIfUserIsAlreadyJoinedOnThisSlot();
											}
										}
									});
							lp.show();
						} else {
							// Comprobar si el usuario actual est‡ unido al slot
							checkIfUserIsAlreadyJoinedOnThisSlot();
						}
					}

					public void onSessionError(String error) {
						LoadingDialog.hideLoading(JoinSlotActivity.this);
						MessageDialog.showMessage(
								JoinSlotActivity.this, getString(R.string.multiplayer_error),
								getString(R.string.multiplayer_error_al_comprobar_sesion));
					}
				});
	}
	
	
	
	private void retrieveSlotData(){
		showLoading(true);
		Slot.getSlot(nidSlot, app.deviceLang, app.drupalClient, app.drupalSecurity, new SlotItemListener() {
			public void onSlotItemLoad(Slot slot) {
				showLoading(false);
				currentSlot = slot;
				updateView();
			}
			public void onSlotItemError(String error) {
				showLoading(false);
				MessageDialog.showMessage(
						JoinSlotActivity.this, getString(R.string.multiplayer_error),
						getString(R.string.multiplayer_error_al_obtener_datos_de_la_partida));
			}

		});
	}
	
	
	private void checkIfUserIsAlreadyJoinedOnThisSlot(){
		showLoading(true);
		Slot.isJoined(nidSlot, app.deviceLang, app.drupalClient, app.drupalSecurity, new SlotUserIsJoinedListener() {
			public void onSlotUserIsJoined(boolean alreadyJoined) {
				Milog.d("Check user is joined successfull");
				if(!alreadyJoined){
					// Si no est‡ unido, unirlo
					joinSlot();
				}else{
					showLoading(false);
					retrieveSlotData();
				}

			}
			
			public void onSlotUserIsJoinedError(String error) {
				Milog.d("Error al comprobar si el usuario est‡ unido");
				showLoading(false);
				
				MessageDialog.showMessage(JoinSlotActivity.this, getString(R.string.multiplayer_error), error);
			}
		});
	}
	
	
	private void joinSlot(){
		// Preguntar si quiere unirse a la partida
		MessageDialog.showMessageWith2Buttons(this, getString(R.string.multiplayer_unirse_a_partida), getString(R.string.multiplayer_quieres_unirte_a_esta_partida), getString(R.string.multiplayer_unirme), getString(R.string.multiplayer_cancelar), new MessageDialogListener() {
			public void onPositiveButtonClick(MessageDialog dialog) {
				dialog.dismiss();
				showLoading(true);
				Slot.joinSlot(nidSlot, app.deviceLang, app.drupalClient, app.drupalSecurity, new SlotJoinListener() {
					public void onSlotUserJoined(String uid, String nidSlot) {
						Milog.d("Unido a slot: " + nidSlot);
						
						retrieveSlotData();
						showLoading(false);
					}
					public void onSlotUserJoinError(String error) {
						Milog.d("Error al unirse a slot: " + error);
						showLoading(false);
						MessageDialog.showMessage(JoinSlotActivity.this, getString(R.string.multiplayer_error), error);
					}
				});
			}
			public void onNegativeButtonClick(MessageDialog dialog) {
				dialog.dismiss();
				finish();
			}
		});
		
		
		
	}

	
	
	private void checkSessionForStartPlayingSlot(){
		showLoading(true);
		User.checkIfUserIsLoggedIn(app.drupalClient,app.preferencias,
				new UserSessionListener() {
					public void onSessionChecked(
							boolean userIsLoggedIn, boolean isTempUser) {
						if (!userIsLoggedIn || isTempUser) {
							LoadingDialog.hideLoading(JoinSlotActivity.this);
							boolean enableDemoLogin = false;
							// Si no est‡ logueado pedir login
							LoginPopupWindow lp = new LoginPopupWindow(
									JoinSlotActivity.this,
									getApplication(),
									JoinSlotActivity.this, enableDemoLogin, true,
									new LoginPopupWindow.LoginPopupWindowListener() {
										public void onLoginPopupWindowDismiss(boolean isLoggedIn, boolean isTempUser) {
											if (!isLoggedIn || isTempUser) {
												
											}else{
												startPlayingSlot();
											}
										}
									});
							lp.show();
						} else {
							startPlayingSlot();
						}
					}

					public void onSessionError(String error) {
						LoadingDialog.hideLoading(JoinSlotActivity.this);
						MessageDialog.showMessage(
								JoinSlotActivity.this, getString(R.string.multiplayer_error),
								getString(R.string.multiplayer_error_al_comprobar_sesion));
					}
				});
	}
	
	private void startPlayingSlot(){
		Slot.startSlot(nidSlot, app.deviceLang, app.drupalClient, app.drupalSecurity, new SlotStartListener() {
			public void onSlotUserStart(String uid, String nidSlot) {
				showLoading(false);
				
				// Abrir la pantalla de juego
				openGameActivity();
			}
			public void onSlotUserStartError(String error) {
				Milog.d("Error al unirse a slot: " + error);
				showLoading(false);
				MessageDialog.showMessage(JoinSlotActivity.this, getString(R.string.multiplayer_error), error);
				
			}
		});
	}

	
	
	private void openGameActivity(){
		if(currentSlot != null && currentSlot.game != null && currentSlot.game.type != null && currentSlot.game.modality != null){
			// Si est‡n todos los datos del juego...
			if(currentSlot.game.type.equals(GeocachesGame.DRUPAL_TYPE)){
				// Si el tipo de juego es Geocache...
				if(currentSlot.game.modality.equals(GeocachesGame.MODALITY.BATTLE)){
					// Si la modalidad de Geocache es Battle...
					Intent intent = new Intent(this,
							GeocacheBattleMapActivity.class);
					intent.putExtra(GeocacheBattleMapActivity.PARAM_KEY_GAME_NID,
							currentSlot.game.nid);
					intent.putExtra(GeocacheBattleMapActivity.PARAM_KEY_SLOT_NID,
							currentSlot.nid);
					startActivity(intent);
				}
			}
		}else{
			MessageDialog.showMessage(JoinSlotActivity.this, getString(R.string.multiplayer_error), getString(R.string.multiplayer_datos_erroneos_en_la_partida));
		}
	}
	
	private void showLoading(boolean show){
		if(show){
			// Mostrar el panel de carga
			LoadingDialog.showLoading(this);
		}else{
			// Ocultar el panel de carga
			LoadingDialog.hideLoading(JoinSlotActivity.this);
		}
	}


	
	
	public class PlayersSlotAdapter extends ArrayAdapter<User> {
		LayoutInflater mInflater;
		List<User> itemList;
		Context ctx;
		int layoutId;


		public class ViewHolder {
			RelativeLayout content;
			TextView tvPlayerName;
			int index;
		}

		public PlayersSlotAdapter(Context ctx, int layoutId, List<User> itemList) {
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
		public User getItem(int position) {
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
		
		public void setItems(List<User> itemList){
			this.itemList = itemList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(layoutId, null);
				holder.content = (RelativeLayout) convertView.findViewById(R.id.content);
				holder.tvPlayerName = (TextView) convertView.findViewById(R.id.tvPlayerName);
				Typeface tfNormal = TINGTypeface.getGullyBoldTypeface(ctx);
				holder.tvPlayerName.setTypeface(tfNormal);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			User user = getItem(position);
			
			// Poner t’tulo
			if(user.mail != null && !user.mail.isEmpty()){
				String userTxt = user.mail;
				if(currentUser != null && currentUser.uid.equals(user.uid)){
					userTxt += " " + getString(R.string.multiplayer_yo);
				}
				holder.tvPlayerName.setText(userTxt);
			}else{
				holder.tvPlayerName.setText(getString(R.string.multiplayer_jugador_sin_nombre));
			}
			
		
			return convertView;
		}

	}




	


	



	


	

}
