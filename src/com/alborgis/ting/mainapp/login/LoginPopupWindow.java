package com.alborgis.ting.mainapp.login;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.User;
import com.alborgis.ting.base.model.User.UserLoginListener;
import com.alborgis.ting.base.model.User.UserTempLoginListener;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.login.FacebookLoginActivity.FacebookLoginActivityListener;
import com.alborgis.ting.mainapp.register.RegisterPopupWindow;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

public class LoginPopupWindow extends Dialog {
	MainApp app;
	Context ctx;
	Activity activity;

	TextView tvTitleDialog;

	Button btnModoPrueba;

	RelativeLayout cajaLoginFacebook;
	TextView tvTitleSeccionLoginFB;
	TextView tvTitleSeccionLoginTing;

	EditText tbCorreo;
	EditText tbPass;

	Button btnEntrar;
	Button btnCancel;
	Button btnRegister;

	LoginPopupWindowListener listener;
	boolean testLoginSectionEnabled;
	boolean cancelable;

	public static interface LoginPopupWindowListener {
		public void onLoginPopupWindowDismiss(boolean isLoggedIn,
				boolean isTempUser);
	}

	public LoginPopupWindow(Context context, Application _app, Activity _activity,
			boolean _testLoginSectionEnabled, boolean _cancelable,
			LoginPopupWindowListener _listener) {
		super(context);
		app = (MainApp) _app;
		ctx = context;
		activity = _activity;
		testLoginSectionEnabled = _testLoginSectionEnabled;
		cancelable = _cancelable;
		listener = _listener;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_login__login_popup);

		this.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		capturarControles();
		inicializarForm();
		escucharEventos();

		setTestLoginSectionEnabled(testLoginSectionEnabled);
		setCancelable(cancelable);

	}

	// Activa desde fuera que los usuarios se puedan loguear como prueba
	private void setTestLoginSectionEnabled(boolean enabled) {
		if (enabled) {
			btnModoPrueba.setVisibility(View.VISIBLE);
		} else {
			btnModoPrueba.setVisibility(View.GONE);
		}
	}

	private void capturarControles() {
		tvTitleDialog = (TextView) findViewById(R.id.tvTitulo);
		btnModoPrueba = (Button) findViewById(R.id.btnModoPrueba);
		cajaLoginFacebook = (RelativeLayout) findViewById(R.id.cajaLoginFacebook);
		tvTitleSeccionLoginFB = (TextView) findViewById(R.id.tvLoginFB);
		tvTitleSeccionLoginTing = (TextView) findViewById(R.id.tvLoginTing);
		tbCorreo = (EditText) findViewById(R.id.tbMail);
		tbPass = (EditText) findViewById(R.id.tbPass);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnEntrar = (Button) findViewById(R.id.btnLogin);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		

	}

	private void escucharEventos() {

		btnModoPrueba.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				LoadingDialog.showLoading(ctx);
				User.checkIfUserIsLoggedIn(app.drupalClient,
						new User.UserSessionListener() {
							public void onSessionChecked(
									boolean userIsLoggedIn, boolean isTempUser) {
								if (userIsLoggedIn) {
									LoadingDialog.hideLoading();
									dismiss();
									if (isTempUser) {
										// Si est‡ logueado y es temporal
										listener.onLoginPopupWindowDismiss(
												true, true);
									} else {
										// Si est‡ logueado y no es temporal
										listener.onLoginPopupWindowDismiss(
												true, false);
									}
								} else {
									User.tempLogin(app.drupalClient,
											app.drupalSecurity,
											new UserTempLoginListener() {
												public void onTempLoginSuccess(
														User userRegistered) {
													LoadingDialog.hideLoading();
													listener.onLoginPopupWindowDismiss(
															true, true);
													dismiss();
												}

												public void onTempLoginError(
														String error) {
													LoadingDialog.hideLoading();
													MessageDialog.showMessage(
															ctx, "Error",
															"Error al entrar");
												}
											});
								}

							}

							public void onSessionError(String error) {
								LoadingDialog.hideLoading();
								MessageDialog.showMessage(ctx, "Error",
										"Error al entrar");
							}
						});

			}
		});

		cajaLoginFacebook.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Escuchador de la ventana de facebook
				FacebookLoginActivity.facebookLoginListener = new FacebookLoginActivityListener() {
					public void onFacebookLoginActivityFinish(boolean isLoggedInOnFacebook) {
						// Registrar el dispositivo para que reciba push notifications
						if(isLoggedInOnFacebook){
							app.pushNotificationsClient.start(activity);
						}
						// Notificar que se ha cerrado el cuadro de di‡loogo de login
						listener.onLoginPopupWindowDismiss(isLoggedInOnFacebook, false);
						dismiss();
					}
				};
				Intent intent = new Intent(ctx, FacebookLoginActivity.class);
				ctx.startActivity(intent);
			}
		});

		btnEntrar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Validar campos
				if (TextUtils.isEmpty(tbCorreo.getText().toString())) {
					MessageDialog.showMessage(ctx, "Correo electr—nico",
							"Por favor, introduce tu correo electr—nico");
					return;
				}
				if (TextUtils.isEmpty(tbPass.getText().toString())) {
					MessageDialog.showMessage(ctx, "Contrase–a",
							"Por favor, introduce tu contrase–a");
					return;
				}
				if (!tbCorreo.getText().toString().contains("@")) {
					MessageDialog.showMessage(ctx, "Correo electr—nico",
							"El correo electr—nico especificado no es v‡lido");
					return;
				}
				if (tbPass.getText().toString().length() < 3) {
					MessageDialog.showMessage(ctx, "Contrase–a",
							"La contrase–a es demasiado corta");
					return;
				}

				LoadingDialog.showLoading(ctx);
				User.checkIfUserIsLoggedIn(app.drupalClient,
						new User.UserSessionListener() {
							public void onSessionChecked(
									boolean userIsLoggedIn, boolean isTempUser) {
								if (userIsLoggedIn) {
									// Si est‡ logueado lo deslogueo y luego
									// logueo
									User.logout(app.drupalClient,
											new User.UserLogoutListener() {
												public void onLogoutSuccess() {
													Milog.d("Se ha deslogueado el usuario actual");
													// Ahora logueo
													User.login(
															app.drupalClient,
															app.drupalSecurity,
															tbCorreo.getText()
																	.toString(),
															tbPass.getText()
																	.toString(),
															new UserLoginListener() {
																public void onLoginSuccess(
																		User userLoggedIn) {
																	LoadingDialog
																			.hideLoading();
																	saveUserCredentials();
																	// Registrar el dispositivo para que reciba push notifications
																	app.pushNotificationsClient.start(activity);
																	// Notificar que se ha cerrado el cuadro de di‡loogo de login
																	listener.onLoginPopupWindowDismiss(
																			true,
																			false);
																	dismiss();
																}

																public void onLoginError(
																		String error) {
																	LoadingDialog
																			.hideLoading();
																	if (error
																			.contains("rong username")) {
																		MessageDialog
																				.showMessage(
																						ctx,
																						"Datos incorrectos",
																						"El usuario o la contrase–a no existe");
																	} else {
																		MessageDialog
																				.showMessage(
																						ctx,
																						"Error",
																						"Error al hacer login");
																	}

																}
															});
												}

												public void onLogoutError(
														String error) {
													Milog.d("Error al desloguear el usuario actual");
													LoadingDialog.hideLoading();
													MessageDialog
															.showMessage(ctx,
																	"Error",
																	"Error al hacer login");
												}
											});
								} else {
									// Si no est‡ logueado lo logueo
									User.login(app.drupalClient,
											app.drupalSecurity, tbCorreo
													.getText().toString(),
											tbPass.getText().toString(),
											new UserLoginListener() {
												public void onLoginSuccess(
														User userLoggedIn) {
													LoadingDialog.hideLoading();
													saveUserCredentials();
													// Registrar el dispositivo para que reciba push notifications
													app.pushNotificationsClient.start(activity);
													// Notificar que se ha cerrado el cuadro de di‡loogo de login
													listener.onLoginPopupWindowDismiss(
															true, false);
													dismiss();
												}

												public void onLoginError(
														String error) {
													LoadingDialog.hideLoading();
													if (error
															.contains("rong username")) {
														MessageDialog
																.showMessage(
																		ctx,
																		"Datos incorrectos",
																		"El usuario o la contrase–a no existe");
													} else {
														MessageDialog
																.showMessage(
																		ctx,
																		"Error",
																		"Error al hacer login");
													}
												}
											});
								}

							}

							public void onSessionError(String error) {
								LoadingDialog.hideLoading();
								MessageDialog.showMessage(ctx, "Error",
										"Error al entrar");
							}
						});

			}
		});

		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				listener.onLoginPopupWindowDismiss(false, false);
			}
		});

		btnRegister.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// Se ha pulsado el bot—n de registrar del panel de login. Lo
				// que hay
				// que hacer es la pantalla de registro
				RegisterPopupWindow rp = new RegisterPopupWindow(ctx, app,
						listener);
				rp.show();
				dismiss(); // Cerrar el di‡logo actual
			}
		});
	}

	private void inicializarForm() {
		// Poner tipograf’as
		Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(ctx);
		Typeface tfBold = TINGTypeface.getGullyBoldTypeface(ctx);
		tvTitleDialog.setTypeface(tfBold);
		btnModoPrueba.setTypeface(tfNormal);
		tvTitleSeccionLoginFB.setTypeface(tfBold);
		tvTitleSeccionLoginTing.setTypeface(tfBold);
		tbCorreo.setTypeface(tfNormal);
		tbPass.setTypeface(tfNormal);
		btnRegister.setTypeface(tfNormal);
		btnEntrar.setTypeface(tfNormal);
		btnCancel.setTypeface(tfNormal);

		loadUserCredentials();

	}

	private void saveUserCredentials() {
		// MŽtodo cl‡sico, en las SharedPreferences
		SharedPreferences.Editor edit = app.preferencias.edit();
		edit.putString(app.COOKIE_KEY_LAST_EMAIL_LOGGED_IN, tbCorreo.getText().toString());
		edit.putString(app.COOKIE_KEY_LAST_PASS_LOGGED_IN, tbPass.getText().toString());
		edit.commit();
	}

	private void loadUserCredentials() {
		// MŽtodo cl‡sico, en las SharedPreferences
		String savedEmail = app.preferencias.getString(app.COOKIE_KEY_LAST_EMAIL_LOGGED_IN, "");
		String savedPassword = app.preferencias.getString(app.COOKIE_KEY_LAST_PASS_LOGGED_IN, "");
		tbCorreo.setText(savedEmail);
		tbPass.setText(savedPassword);
	}

}
