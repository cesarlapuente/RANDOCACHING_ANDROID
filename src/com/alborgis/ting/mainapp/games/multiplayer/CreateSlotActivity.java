package com.alborgis.ting.mainapp.games.multiplayer;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Game;
import com.alborgis.ting.base.model.Slot;
import com.alborgis.ting.base.model.Slot.SlotCreateListener;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.home.MainActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class CreateSlotActivity extends Activity {

	public static final String PARAM_KEY_NID_GAME	=	"param_nid_game";
	public static final String PARAM_KEY_TITLE_GAME		=	"param_title_game";
	
	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	
	EditText tbTitle;
	Button btnJuegoSeleccionado;
	TextView tvHeaderNumMinJugadores;
	SeekBar seekBarNumMinJugadores;
	TextView tvNumMinJugadores;
	TextView tvHeaderNumMaxJugadores;
	SeekBar seekBarNumMaxJugadores;
	TextView tvNumMaxJugadores;
	Button btnCrearPartida;
	
	String nidGame;
	String titleGame;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_games__layout_create_slot);

		this.app = (MainApp) getApplication();

		Bundle b = getIntent().getExtras();
		if(b != null){
			nidGame = b.getString(PARAM_KEY_NID_GAME);
			titleGame = b.getString(PARAM_KEY_TITLE_GAME);
		}

		capturarControles();
		inicializarForm();
		escucharEventos();
	}
	

	

	protected void onResume() {
		super.onResume();

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
		tbTitle = (EditText) findViewById(R.id.tbTitle);
		btnJuegoSeleccionado = (Button) findViewById(R.id.btnJuego);
		tvHeaderNumMinJugadores = (TextView) findViewById(R.id.tvHeaderNumMinJugadores);
		seekBarNumMinJugadores = (SeekBar) findViewById(R.id.seekBarNumMinJugadores);
		tvNumMinJugadores = (TextView) findViewById(R.id.tvNumMinJugadores);
		tvHeaderNumMaxJugadores = (TextView) findViewById(R.id.tvHeaderNumMaxJugadores);
		seekBarNumMaxJugadores = (SeekBar) findViewById(R.id.seekBarNumMaxJugadores);
		tvNumMaxJugadores = (TextView) findViewById(R.id.tvNumMaxJugadores);
		btnCrearPartida = (Button) findViewById(R.id.btnCrearSlot);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyNormal = TINGTypeface.getGullyNormalTypeface(this);
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(this);
		tvTitle.setTypeface(tfGullyLight);
		
		tbTitle.setTypeface(tfGullyNormal);
		btnJuegoSeleccionado.setTypeface(tfGullyNormal);
		tvHeaderNumMaxJugadores.setTypeface(tfGullyNormal);
		tvNumMaxJugadores.setTypeface(tfGullyNormal);
		tvHeaderNumMinJugadores.setTypeface(tfGullyNormal);
		tvNumMinJugadores.setTypeface(tfGullyNormal);
		btnCrearPartida.setTypeface(tfGullyBold);
		
		// Configurar el formulario de entrada
		btnJuegoSeleccionado.setText( "Juego: " + titleGame);
		btnJuegoSeleccionado.setEnabled(false);
		
		seekBarNumMinJugadores.setMax(10);
		seekBarNumMinJugadores.setProgress(2);
		tvNumMinJugadores.setText(seekBarNumMinJugadores.getProgress() + " jugadores");
		
		seekBarNumMaxJugadores.setMax(10);
		seekBarNumMaxJugadores.setProgress(5);
		tvNumMaxJugadores.setText(seekBarNumMaxJugadores.getProgress() + " jugadores");
		
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(CreateSlotActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		seekBarNumMinJugadores.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBar.getProgress() < 2){
					seekBar.setProgress(2);
					Toast.makeText(CreateSlotActivity.this, "El nœmero m’nimo de jugadores debe ser de al menos 2", Toast.LENGTH_SHORT).show();
				}
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvNumMinJugadores.setText(progress + " jugadores");
			}
		});
		
		seekBarNumMaxJugadores.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(seekBar.getProgress() < 2){
					seekBar.setProgress(2);
					Toast.makeText(CreateSlotActivity.this, "El nœmero m’nimo de jugadores debe ser de al menos 2", Toast.LENGTH_SHORT).show();
				}
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvNumMaxJugadores.setText(progress + " jugadores");
			}
		});
		
		btnCrearPartida.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				crearPartida();
			}
		});
		
	}
	
	
	private void crearPartida(){
		if( validarCampos() ){
			
			// Crear un slot
			Slot slot = new Slot();
			slot.title = tbTitle.getText().toString();
			slot.numMinPlayers = seekBarNumMinJugadores.getProgress();
			slot.numMaxPlayers = seekBarNumMaxJugadores.getProgress();
			Game game = new Game();
			game.nid = nidGame;
			slot.game = game;
			Milog.d("Game nid: " + nidGame);
			
			showLoading(true);
			Slot.createSlot(slot, app.drupalClient, app.drupalSecurity, new SlotCreateListener() {
				public void onSlotCreated(String nidSlot) {
					Milog.d("Creado slot: " + nidSlot);
					showLoading(false);
					
					// Abrir la pantalla de unirse al juego (join)
					Intent intent = new Intent(CreateSlotActivity.this, JoinSlotActivity.class);
					intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, nidSlot);
					startActivity(intent);
					finish(); // Cerrar la ventana actual
				}
				public void onSlotCreateError(String error) {
					Milog.d("Error al crear slot: " + error);
					showLoading(false);
					MessageDialog.showMessage(CreateSlotActivity.this, "Error", "Error al crear partida. " + error);
				}
			});
		}
	}
	
	

	private boolean validarCampos(){
		if(tbTitle.getText().toString() == null || tbTitle.getText().toString().isEmpty()){
			MessageDialog.showMessage(this, "T’tulo de la partida", "Por favor, introduce un t’tulo para la nueva partida");
			return false;
		}
		return true;
	}
	
	private void showLoading(boolean show){
		if(show){
			// Mostrar el panel de carga
			LoadingDialog.showLoading(this);
		}else{
			// Ocultar el panel de carga
			LoadingDialog.hideLoading(this);
		}
	}
	
	


}
