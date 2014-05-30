package com.alborgis.randocaching.mainapp.passport;

import com.alborgis.ting.base.model.Passport;
import com.alborgis.ting.base.model.Passport.PassportListener;
import com.alborgis.ting.base.model.User;
import com.alborgis.ting.base.model.User.UserLogoutListener;
import com.alborgis.ting.base.model.User.UserSessionListener;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.MainApp;
import com.alborgis.randocaching.mainapp.common.LoadingDialog;
import com.alborgis.randocaching.mainapp.common.MessageDialog;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;
import com.alborgis.randocaching.mainapp.common.MessageDialog.MessageDialogListener;
import com.alborgis.randocaching.mainapp.home.MainActivity;
import com.alborgis.randocaching.mainapp.login.LoginPopupWindow;
import com.alborgis.randocaching.mainapp.login.LoginPopupWindow.LoginPopupWindowListener;
import com.alborgis.randocaching.mainapp.passport.PassportEditUserPopupWindow.PassportEditUserPopupListener;
import com.facebook.Session;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PassportActivity extends Activity implements LoginPopupWindowListener {

	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	
	RelativeLayout layoutEditarFoto;
	ImageView imageViewAvatarUsuario;
	TextView tvAddFoto1;
	TextView tvAddFoto2;
	TextView tvNombreApellidos;
	TextView tvEmail;
	Button btnEditarDatosUsuario;
	Button btnLogout;
	
	TextView tvHeaderPuntos;
	TextView tvNumPuntos;
	TextView tvNumDiscoverGames;
	TextView tvNumMessengerGames;
	TextView tvNumExplorerGames;
	TextView tvNumChallengerGames;
	TextView tvHeaderNumMedallas;
	TextView tvNumMedallas;
	TextView tvHeaderNumVisados;
	TextView tvNumVisados;
	TextView tvHeaderNumSellos;
	TextView tvNumSellos;
	
	Button btnVerMedallas;
	Button btnVerVisados;
	Button btnVerSellos;
	
	Passport passport;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_passport__layout_passport);
		
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

		this.app = (MainApp) getApplication();

		capturarControles();
		inicializarForm();
		escucharEventos();

		checkLogin();
	}
	

	

	protected void onResume() {
		super.onResume();

	}


	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		layoutEditarFoto = (RelativeLayout)findViewById(R.id.layoutEditarFoto);
		imageViewAvatarUsuario = (ImageView) findViewById(R.id.imageViewAvatarUsuario);
		tvAddFoto1 = (TextView) findViewById(R.id.tvAddFoto1);
		tvAddFoto2 = (TextView) findViewById(R.id.tvAddFoto2);
		tvNombreApellidos = (TextView) findViewById(R.id.tvNombreApellidos);
		tvEmail = (TextView) findViewById(R.id.tvEmail);
		btnEditarDatosUsuario = (Button) findViewById(R.id.btnEditarDatosUsuario);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		tvHeaderPuntos = (TextView) findViewById(R.id.tvHeaderPuntos);
		tvNumPuntos = (TextView) findViewById(R.id.tvNumPuntos);
		tvNumDiscoverGames = (TextView) findViewById(R.id.tvNumDiscover);
		tvNumMessengerGames = (TextView) findViewById(R.id.tvNumMessenger);
		tvNumExplorerGames = (TextView) findViewById(R.id.tvNumExplorer);
		tvNumChallengerGames = (TextView) findViewById(R.id.tvNumChallenger);
		tvHeaderNumMedallas = (TextView) findViewById(R.id.tvHeaderMedallas);
		tvNumMedallas = (TextView) findViewById(R.id.tvNumMedallas);
		tvHeaderNumVisados = (TextView) findViewById(R.id.tvHeaderVisados);
		tvNumVisados = (TextView) findViewById(R.id.tvNumVisados);
		tvHeaderNumSellos = (TextView) findViewById(R.id.tvHeaderSellos);
		tvNumSellos = (TextView) findViewById(R.id.tvNumSellos);
		btnVerMedallas = (Button) findViewById(R.id.btnVerMedallas);
		btnVerVisados = (Button) findViewById(R.id.btnVerVisados);
		btnVerSellos = (Button) findViewById(R.id.btnVerSellos);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyNormal = TINGTypeface.getGullyNormalTypeface(this);
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(this);

		tvTitle.setTypeface(tfGullyLight);
		tvAddFoto1.setTypeface(tfGullyLight);
		tvAddFoto2.setTypeface(tfGullyLight);
		
		tvNombreApellidos.setTypeface(tfGullyLight);
		tvEmail.setTypeface(tfGullyBold);
		btnEditarDatosUsuario.setTypeface(tfGullyNormal);
		btnVerMedallas.setTypeface(tfGullyNormal);
		btnVerVisados.setTypeface(tfGullyNormal);
		btnVerSellos.setTypeface(tfGullyNormal);
		btnLogout.setTypeface(tfGullyNormal);
		
		tvHeaderPuntos.setTypeface(tfGullyLight);
		tvNumPuntos.setTypeface(tfGullyBold);
		tvNumDiscoverGames.setTypeface(tfGullyBold);
		tvNumMessengerGames.setTypeface(tfGullyBold);
		tvNumExplorerGames.setTypeface(tfGullyBold);
		tvNumChallengerGames.setTypeface(tfGullyBold);
		tvHeaderNumMedallas.setTypeface(tfGullyLight);
		tvNumMedallas.setTypeface(tfGullyBold);
		tvHeaderNumSellos.setTypeface(tfGullyLight);
		tvNumSellos.setTypeface(tfGullyBold);
		tvHeaderNumVisados.setTypeface(tfGullyLight);
		tvNumVisados.setTypeface(tfGullyBold);
		
		// De primeras ocultar la imagen del usuario
		imageViewAvatarUsuario.setVisibility(View.GONE);
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PassportActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		layoutEditarFoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PassportEditUserPopupWindow edit = new PassportEditUserPopupWindow(PassportActivity.this, passport, new PassportEditUserPopupListener() {
					public void onPassportEditUserPopupDismiss() {
						checkLogin();
					}
				});
				edit.disableUserDataSection();
				edit.showPopup(layoutEditarFoto);
			}
		});
		
		btnEditarDatosUsuario.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				PassportEditUserPopupWindow edit = new PassportEditUserPopupWindow(PassportActivity.this, passport, new PassportEditUserPopupListener() {
					public void onPassportEditUserPopupDismiss() {
						checkLogin();
					}
				});
				edit.disableImageSection();
				edit.showPopup(btnEditarDatosUsuario);
			}
		});
		
		btnLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				MessageDialog.showMessageWith2Buttons(PassportActivity.this, getString(R.string.pasaporte_desconexion), getString(R.string.pasaporte_deseas_desconectarte_de_travel_in_game), getString(R.string.pasaporte_desconectar),  getString(R.string.pasaporte_cancelar), 
						new MessageDialogListener() {
							public void onPositiveButtonClick(MessageDialog dialog) {
								dialog.dismiss();
								showLoading(true);
								User.logout(app.drupalClient, new UserLogoutListener() {
									public void onLogoutSuccess() {
										// Borrar la sesi—n de facebook
										Session session = Session.getActiveSession();
										if(session != null){
											session.closeAndClearTokenInformation();
										}
										// Comprobar sesi—n
										checkLogin();
									}
									public void onLogoutError(String error) {
										showLoading(false);
										MessageDialog.showMessage(PassportActivity.this, getString(R.string.pasaporte_error),	getString(R.string.pasaporte_no_se_puede_desconectar_la_sesion_actual));
									}
								});
							}
							public void onNegativeButtonClick(MessageDialog dialog) {
								dialog.dismiss();
							}
						});
				
				
			}
		});
		
		btnVerMedallas.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(PassportActivity.this, PassportGridActivity.class);
				intent.putParcelableArrayListExtra(PassportGridActivity.PARAM_KEY_BADGES, passport.badges);
				intent.putExtra(PassportGridActivity.PARAM_KEY_TITLE, getString(R.string.pasaporte_medallas));
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		
		btnVerVisados.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(PassportActivity.this, PassportGridActivity.class);
				intent.putParcelableArrayListExtra(PassportGridActivity.PARAM_KEY_VISADOS, passport.visados);
				intent.putExtra(PassportGridActivity.PARAM_KEY_TITLE, getString(R.string.pasaporte_visados));
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		
		btnVerSellos.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(PassportActivity.this, PassportGridActivity.class);
				intent.putParcelableArrayListExtra(PassportGridActivity.PARAM_KEY_STAMPS, passport.stamps);
				intent.putExtra(PassportGridActivity.PARAM_KEY_TITLE, getString(R.string.pasaporte_sellos));
				startActivity(intent);
				overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
			}
		});
		
		
		
		
		
		
		
		
		
		
		
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
	
	private void checkLogin(){
		// Comprobar la sesi—n
		showLoading(true);
		// Quitar el bot—n de logout
		btnLogout.setVisibility(View.GONE);
		User.checkIfUserIsLoggedIn(app.drupalClient,app.preferencias,
				new UserSessionListener() {
					public void onSessionChecked(boolean userIsLoggedIn, boolean isTempUser) {
						
						if (!userIsLoggedIn || isTempUser) {
							showLoading(false);
							// Si no est‡ logueado pedir login
							LoginPopupWindow lp = new LoginPopupWindow(PassportActivity.this, getApplication(),PassportActivity.this, false, false, PassportActivity.this);
							lp.show();
						} else {
							// Si est‡ logueado seguir
							if (!isTempUser) {
								// Cargar datos del pasaporte
								cargarDatosPasaporte();
								// Mostrar el bot—n de logout
								btnLogout.setVisibility(View.VISIBLE);
							} else {
								showLoading(false);
								checkLogin(); // Volver a empezar
							}

						}
					}

					public void onSessionError(String error) {
						showLoading(false);
						MessageDialog.showMessage(PassportActivity.this, getString(R.string.pasaporte_error),	getString(R.string.pasaporte_error_al_comprobar_sesion));
					}
				});
	}

	private void cargarDatosPasaporte(){
		showLoading(true);
		Passport.getPassport(app.deviceLang, app.drupalClient, new PassportListener() {
			public void onPassportLoad(Passport _passport) {
				showLoading(false);
				passport = _passport;
				// Rellenar la imgen del usuario
				if(passport.avatarImage != null && !passport.avatarImage.equals("")){
					imageViewAvatarUsuario.setVisibility(View.VISIBLE);
					tvAddFoto1.setVisibility(View.GONE);
					tvAddFoto2.setVisibility(View.GONE);
					Util.loadBitmapOnImageView(PassportActivity.this, imageViewAvatarUsuario, passport.avatarImage, null, 0, R.anim.anim_fade_in_300, 0, 0);
				}else{
					imageViewAvatarUsuario.setVisibility(View.GONE);
					tvAddFoto1.setVisibility(View.VISIBLE);
					tvAddFoto2.setVisibility(View.VISIBLE);
				}
				
				// Rellenar el campo nombre y apellidos
				String strNombreApellidos = "";
				if(passport.firstName != null && !passport.firstName.equals("")){
					strNombreApellidos += passport.firstName.toUpperCase();
					if(passport.lastName != null && !passport.lastName.equals("")){
						strNombreApellidos += " " + passport.lastName.toUpperCase();
					}
				}else if(passport.name != null && !passport.name.equals("")){
					strNombreApellidos += passport.name;
				}
				tvNombreApellidos.setText(strNombreApellidos);
				
				// Rellenar el campo email
				if(passport.mail != null && !passport.mail.equals("")){
					tvEmail.setText(passport.mail);
				}
				
				// Rellenar el campo puntos
				tvNumPuntos.setText( String.valueOf(passport.points) );
				
				// Rellenar el campo num discover
				int numDiscover = 0;
				if(passport.photohidesGames != null){ numDiscover = passport.photohidesGames.size(); }
				tvNumDiscoverGames.setText( String.valueOf(numDiscover) );
				
				// Rellenar el campo num messenger
				int numMessenger = 0;
				if(passport.checkinsGames != null){ numMessenger = passport.checkinsGames.size(); }
				tvNumMessengerGames.setText( String.valueOf(numMessenger) );
				
				// Rellenar el campo num explorer
				int numExplorer = 0;
				if(passport.geocachesGames != null){ numExplorer = passport.geocachesGames.size(); }
				tvNumExplorerGames.setText( String.valueOf(numExplorer) );
				
				// Rellenar el campo num challenger
				int numChallenger = 0;
				if(passport.enigmasGames != null){ numChallenger = passport.enigmasGames.size(); }
				tvNumChallengerGames.setText( String.valueOf(numChallenger) );
				
				// Rellenar el campo num medallas
				int numBadges = 0;
				if(passport.badges != null){ numBadges = passport.badges.size(); }
				tvNumMedallas.setText( String.valueOf(numBadges) );
				
				// Rellenar el campo num visados
				int numVisados = 0;
				if(passport.visados != null){ numVisados = passport.visados.size(); }
				tvNumVisados.setText( String.valueOf(numVisados) );
				
				// Rellenar el campo num sellos
				int numStamps = 0;
				if(passport.stamps != null){ numStamps = passport.stamps.size(); }
				tvNumSellos.setText( String.valueOf(numStamps) );
				
				
			}
			public void onPassportError(String error) {
				showLoading(false);
				MessageDialog.showMessage(PassportActivity.this, getString(R.string.pasaporte_error),	getString(R.string.pasaporte_error_al_cargar_pasaporte));
			}
		});
	}
	
	


	public void onLoginPopupWindowDismiss(boolean isLoggedIn, boolean isTempUser) {
		if(isLoggedIn && !isTempUser){
			// Mostrar el bot—n de logout
			btnLogout.setVisibility(View.VISIBLE);
			// Si est‡ logueado y no es un usuario temporal, cargar los datos del pasaporte
			cargarDatosPasaporte();
		}else{
			// No est‡ logueado o es un usuario de prueba, salir
			finish();
		}
	}
	

	

}
