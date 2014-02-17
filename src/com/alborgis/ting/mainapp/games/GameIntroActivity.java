package com.alborgis.ting.mainapp.games;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.GeocachesGame;
import com.alborgis.ting.base.model.GeocachesGame.GeocacheGameItemListener;
import com.alborgis.ting.base.model.User;
import com.alborgis.ting.base.model.User.UserSessionListener;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.games.geocache_full.GeocacheFullMapActivity;
import com.alborgis.ting.mainapp.games.geocache_one.GeocacheOneMapActivity;
import com.alborgis.ting.mainapp.games.multiplayer.SlotsActivity;
import com.alborgis.ting.mainapp.login.LoginPopupWindow;
import com.alborgis.ting.mainapp.login.LoginPopupWindow.LoginPopupWindowListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class GameIntroActivity extends Activity implements
		LoginPopupWindowListener, GeocacheGameItemListener {

	public static final String PARAM_KEY_GAME_NID = "key_game_nid";
	public static final String PARAM_KEY_GAME_TYPE = "key_game_type";
	public static final String PARAM_KEY_GAME_MODALITY = "key_game_modality";

	MainApp app;

	RelativeLayout layoutHeader;
	RelativeLayout layoutHeaderFilete;
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;

	ImageView imageViewFondo;
	ImageView imageViewJuego;
	Button btnVolverAJugar;
	Button btnJugar;
	TextView tvDescripcion;

	String nidGame;
	String typeGame;
	String modalityGame;

	GeocachesGame geocachesGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_games__activity_game_intro);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			nidGame = b.getString(PARAM_KEY_GAME_NID);
			typeGame = b.getString(PARAM_KEY_GAME_TYPE);
			modalityGame = b.getString(PARAM_KEY_GAME_MODALITY);
		}

		this.app = (MainApp) getApplication();

		capturarControles();
		inicializarForm();
		escucharEventos();
	}

	protected void onResume() {
		super.onResume();

		// Recargar los datos
		recargarDatos();
	}

	public void onPause() {
		super.onPause();

	}

	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
	}

	private void capturarControles() {
		layoutHeader = (RelativeLayout)findViewById(R.id.headerNav);
		layoutHeaderFilete = (RelativeLayout)findViewById(R.id.headerFileteBottom);
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		btnJugar = (Button) findViewById(R.id.btnJugar);
		btnVolverAJugar = (Button) findViewById(R.id.btnReplay);
		imageViewJuego = (ImageView) findViewById(R.id.imageViewJuego);
		imageViewFondo = (ImageView) findViewById(R.id.imageViewFondo);
		tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
	}

	private void inicializarForm() {
		// Poner tipograf’as
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyNormal = TINGTypeface.getGullyNormalTypeface(this);
		Typeface tfDroidSansNormal = TINGTypeface
				.getDroidSansNormalTypeface(this);

		tvTitle.setTypeface(tfGullyLight);
		tvDescripcion.setTypeface(tfDroidSansNormal);
		btnJugar.setTypeface(tfGullyNormal);
		

		// Inicializar el juego y personalizar el formulario en funci—n del juego
		if (typeGame.equalsIgnoreCase(GeocachesGame.DRUPAL_TYPE)) {
			// Si es un juego de geocache
			this.geocachesGame = new GeocachesGame();
			// Discriminar entre la modalidad
			customizeForGeocacheGame();
		}

	}
	
	private void customizeForGeocacheGame(){
		layoutHeader.setBackgroundResource(R.color.verde_explorer_principal);
		layoutHeaderFilete.setBackgroundResource(R.color.verde_explorer_semioscuro);
		btnJugar.setBackgroundResource(R.drawable.btn_state_play_explorer);
		btnVolverAJugar.setBackgroundResource(R.drawable.btn_state_replay_explorer);
	}

	private void recargarDatos() {
		// De primeras ocultar el bot—n de jugar y el de replay
		btnJugar.setVisibility(View.GONE);
		btnVolverAJugar.setVisibility(View.GONE);

		// Cargar los datos del juego en funci—n del tipo
		if (typeGame.equalsIgnoreCase(GeocachesGame.DRUPAL_TYPE)) {
			LoadingDialog.showLoading(this);
			GeocachesGame.getGame(nidGame, app.drupalClient,
					app.drupalSecurity, this);
		}
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnJugar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Comprobar la sesi—n
				LoadingDialog.showLoading(GameIntroActivity.this);
				User.checkIfUserIsLoggedIn(app.drupalClient,
						new UserSessionListener() {
							public void onSessionChecked(
									boolean userIsLoggedIn, boolean isTempUser) {
								LoadingDialog.hideLoading();
								if (!userIsLoggedIn || isTempUser) {
									
									boolean enableDemoLogin = true;
									if(typeGame.equalsIgnoreCase(GeocachesGame.DRUPAL_TYPE) && modalityGame.equalsIgnoreCase(GeocachesGame.MODALITY.BATTLE)){
										// Si es battle, desactivarlo
										enableDemoLogin = false;
									}
									
									// Si no est‡ logueado pedir login
									LoginPopupWindow lp = new LoginPopupWindow(
											GameIntroActivity.this,
											getApplication(),
											GameIntroActivity.this, enableDemoLogin, true,
											GameIntroActivity.this);
									lp.show();
								} else {
									// Si est‡ logueado seguir
									if (!isTempUser) {
										play(false); // Jugar normal
									} else {
										play(true); // Jugar demo
									}

								}
							}

							public void onSessionError(String error) {
								LoadingDialog.hideLoading();
								MessageDialog.showMessage(
										GameIntroActivity.this, "Error",
										"Error al comprobar sesi—n");
							}
						});
			}
		});

		btnVolverAJugar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Comprobar la sesi—n
				LoadingDialog.showLoading(GameIntroActivity.this);
				User.checkIfUserIsLoggedIn(app.drupalClient,
						new UserSessionListener() {
							public void onSessionChecked(
									boolean userIsLoggedIn, boolean isTempUser) {
								if (!userIsLoggedIn) {
									LoadingDialog.hideLoading();
									
									boolean enableDemoLogin = true;
									if(typeGame.equalsIgnoreCase(GeocachesGame.DRUPAL_TYPE) && modalityGame.equalsIgnoreCase(GeocachesGame.MODALITY.BATTLE)){
										// Si es battle, desactivarlo
										enableDemoLogin = false;
									}
									
									// Si no est‡ logueado pedir login
									LoginPopupWindow lp = new LoginPopupWindow(
											GameIntroActivity.this,
											getApplication(),
											GameIntroActivity.this, enableDemoLogin, true,
											GameIntroActivity.this);
									lp.show();
								} else {
									// Si est‡ logueado seguir y volver a jugar
									replay();
								}
							}

							public void onSessionError(String error) {
								LoadingDialog.hideLoading();
								MessageDialog.showMessage(
										GameIntroActivity.this, "Error",
										"Error al comprobar sesi—n");
							}
						});

			}
		});
	}

	private void play(boolean demoMode) {
		if (typeGame.equalsIgnoreCase(GeocachesGame.DRUPAL_TYPE)) {
			
			if(modalityGame != null && modalityGame.equals(GeocachesGame.MODALITY.ONE)){
				
				// Es un juego de geocache One
				Intent intent = new Intent(GameIntroActivity.this,
						GeocacheOneMapActivity.class);
				if (demoMode) {
					intent.putExtra(GeocacheOneMapActivity.PARAM_KEY_DEMO_PLAYING,
							true);
				}
				intent.putExtra(GeocacheOneMapActivity.PARAM_KEY_GAME_NID,
						geocachesGame.nid);
				intent.putExtra(GeocacheOneMapActivity.PARAM_KEY_GAME_TITLE,
						geocachesGame.title);
				intent.putExtra(GeocacheOneMapActivity.PARAM_KEY_GAME_GEOLOCATED,
						geocachesGame.geolocated);
				intent.putExtra(
						GeocacheOneMapActivity.PARAM_KEY_GAME_TOTAL_ATTEMPTS,
						geocachesGame.totalAttempts);
				intent.putExtra(
						GeocacheOneMapActivity.PARAM_KEY_GAME_REMAIN_ATTEMPTS,
						geocachesGame.remainAttempts);
				startActivity(intent);
			}else if(modalityGame != null && modalityGame.equals(GeocachesGame.MODALITY.FULL)){
				
				// Es un juego de geocache Full
				Intent intent = new Intent(GameIntroActivity.this,
						GeocacheFullMapActivity.class);
				if (demoMode) {
					intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_DEMO_PLAYING,
							true);
				}
				intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_GAME_NID,
						geocachesGame.nid);
				intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_GAME_TITLE,
						geocachesGame.title);
				intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_GAME_GEOLOCATED,
						geocachesGame.geolocated);
				intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_GAME_NEXT_GEOCACHE_NID,
						geocachesGame.nidNextGeocache);
				intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_GAME_FIRST_GEOCACHE_NID,
						geocachesGame.nidFirstGeocache);
				intent.putExtra(GeocacheFullMapActivity.PARAM_KEY_GAME_LAST_GEOCACHE_NID,
						geocachesGame.nidLastGeocache);
				startActivity(intent);
			}else if(modalityGame != null && modalityGame.equals(GeocachesGame.MODALITY.BATTLE)){
				
				// Es un juego de geocache Battle
				Intent intent = new Intent(GameIntroActivity.this,
						SlotsActivity.class);
				intent.putExtra(SlotsActivity.PARAM_KEY_FILTER_GAME_NID, geocachesGame.nid);
				intent.putExtra(SlotsActivity.PARAM_KEY_FILTER_GAME_TITLE, geocachesGame.title);
				intent.putExtra(SlotsActivity.PARAM_KEY_FILTER_GAME_TYPE, geocachesGame.type);
				intent.putExtra(SlotsActivity.PARAM_KEY_FILTER_GAME_MODALITY, geocachesGame.modality);
				startActivity(intent);
			}
			
			
		}
	}

	private void replay() {
		// Lo que hay que hacer es resetear la partida, y volver a jugar

		if (typeGame.equals(GeocachesGame.DRUPAL_TYPE)) {
			if (modalityGame != null && modalityGame.equals(GeocachesGame.MODALITY.ONE)) {

				GeocachesGame.oneReset(nidGame, app.drupalClient,
						app.drupalSecurity,
						new GeocachesGame.GeocacheGameOneResetListener() {
							public void onGeocacheOneResetSuccess(
									String nidGeocacheGame) {
								GeocachesGame.getGame(nidGame,
										app.drupalClient, app.drupalSecurity,
										new GeocacheGameItemListener() {
											public void onGeocacheGameItemLoad(
													GeocachesGame _geocachesGame) {
												if (geocachesGame != null) {
													geocachesGame = _geocachesGame;
												}
												btnJugar.performClick();
											}

											public void onGeocacheGameItemError(
													String error) {
												LoadingDialog.hideLoading();
												MessageDialog
														.showMessage(
																GameIntroActivity.this,
																"Error",
																"Error al cargar el juego");
											}
										});
							}

							public void onGeocacheOneResetError(String error) {
								LoadingDialog.hideLoading();
								Toast.makeText(GameIntroActivity.this,
										"No se puede reiniciar la partida",
										Toast.LENGTH_SHORT).show();
							}
						});
				
			}else if (modalityGame != null && modalityGame.equals(GeocachesGame.MODALITY.FULL)) {

				GeocachesGame.fullReset(nidGame, app.drupalClient,
						app.drupalSecurity,
						new GeocachesGame.GeocacheGameFullResetListener() {
							public void onGeocacheFullResetSuccess(
									String nidGeocacheGame) {
								GeocachesGame.getGame(nidGame,
										app.drupalClient, app.drupalSecurity,
										new GeocacheGameItemListener() {
											public void onGeocacheGameItemLoad(
													GeocachesGame _geocachesGame) {
												if (geocachesGame != null) {
													geocachesGame = _geocachesGame;
												}
												btnJugar.performClick();
											}

											public void onGeocacheGameItemError(
													String error) {
												LoadingDialog.hideLoading();
												MessageDialog
														.showMessage(
																GameIntroActivity.this,
																"Error",
																"Error al cargar el juego");
											}
										});
							}

							public void onGeocacheFullResetError(String error) {
								LoadingDialog.hideLoading();
								Toast.makeText(GameIntroActivity.this,
										"No se puede reiniciar la partida",
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		}

	}

	@Override
	public void onLoginPopupWindowDismiss(boolean isLoggedIn,
			final boolean isTempUser) {
		if (isLoggedIn) {
			// Recargar de nuevo el juego, por si el juego ya ha sido completado
			// De primeras ocultar el bot—n de jugar y el de replay
			btnJugar.setVisibility(View.GONE);
			btnVolverAJugar.setVisibility(View.GONE);
			// Cargar los datos del juego en funci—n del tipo
			if (typeGame.equalsIgnoreCase(GeocachesGame.DRUPAL_TYPE)) {
				LoadingDialog.showLoading(this);
				GeocachesGame.getGame(nidGame, app.drupalClient,
						app.drupalSecurity, new GeocacheGameItemListener() {
							public void onGeocacheGameItemLoad(
									GeocachesGame _geocachesGame) {
								LoadingDialog.hideLoading();
								if (geocachesGame != null) {
									geocachesGame = _geocachesGame;
									Milog.d("Geolocated = "
											+ geocachesGame.geolocated);
									// Poner el t’tulo
									tvTitle.setText(geocachesGame.title
											.toUpperCase());
									// Poner el texto de descripci—n
									tvDescripcion.setText(
											Html.fromHtml(geocachesGame.body),
											BufferType.SPANNABLE);
									// Poner la imagen
									Util.loadBitmapOnImageView(
											GameIntroActivity.this,
											imageViewJuego,
											geocachesGame.featuredImage, null,
											0, R.anim.anim_fade_in_300, 0, 0);
									Util.loadBitmapOnImageView(
											GameIntroActivity.this,
											imageViewFondo,
											geocachesGame.featuredImage, null,
											0, R.anim.anim_fade_in_300, 0, 0);
									if (geocachesGame.done) {
										// Si ya est‡ hecho no dejarle jugar
										btnJugar.setVisibility(View.GONE);
										// Habilitar el bot—n de replay
										btnVolverAJugar
												.setVisibility(View.VISIBLE);
									} else {
										// Dejarle jugar si no est‡ completado
										btnJugar.setVisibility(View.VISIBLE);
										// Deshabilitar el bot—n de replay
										btnVolverAJugar
												.setVisibility(View.GONE);
										// Pasar a jugar
										if (!isTempUser) {
											play(false); // Jugar normal
										} else {
											play(true); // Jugar demo
										}

									}
								}
							}

							@Override
							public void onGeocacheGameItemError(String error) {
								LoadingDialog.hideLoading();
								MessageDialog.showMessage(
										GameIntroActivity.this, "Error",
										"Error al cargar el juego");
							}
						});
			}
		}
	}

	// Al cargar un juego de geocache
	public void onGeocacheGameItemLoad(GeocachesGame geocachesGame) {
		LoadingDialog.hideLoading();
		if (geocachesGame != null) {
			this.geocachesGame = geocachesGame;
			Milog.d("Geolocated = " + this.geocachesGame.geolocated);
			// Poner el t’tulo
			tvTitle.setText(this.geocachesGame.title.toUpperCase());
			// Poner el texto de descripci—n
			tvDescripcion.setText(Html.fromHtml(this.geocachesGame.body),
					BufferType.SPANNABLE);
			// Poner la imagen
			Util.loadBitmapOnImageView(this, imageViewJuego,
					this.geocachesGame.featuredImage, null, 0,
					R.anim.anim_fade_in_300, 0, 0);
			Util.loadBitmapOnImageView(GameIntroActivity.this, imageViewFondo,
					geocachesGame.featuredImage, null, 0,
					R.anim.anim_fade_in_300, 0, 0);
			if (this.geocachesGame.done) {
				// Si ya est‡ hecho no dejarle jugar
				btnJugar.setVisibility(View.GONE);
				// Habilitar el bot—n de replay
				btnVolverAJugar.setVisibility(View.VISIBLE);
			} else {
				// Dejarle jugar si no est‡ completado
				btnJugar.setVisibility(View.VISIBLE);
				// Deshabilitar el bot—n de replay
				btnVolverAJugar.setVisibility(View.GONE);
			}
		}
	}

	// Error al cargar juego de geocache
	public void onGeocacheGameItemError(String error) {
		LoadingDialog.hideLoading();
		MessageDialog.showMessage(GameIntroActivity.this, "Error",
				"Error al cargar el juego");
	}

}
