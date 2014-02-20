package com.alborgis.ting.mainapp.login;

import java.util.Arrays;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.User;
import com.alborgis.ting.base.model.User.UserFBConnectListener;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.UserSettingsFragment;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class FacebookLoginActivity extends FragmentActivity {
	
    private UserSettingsFragment fbFragment;

    MainApp app;
    
    
    public static FacebookLoginActivityListener facebookLoginListener;
    public static interface FacebookLoginActivityListener {
		public void onFacebookLoginActivityFinish(boolean isLoggedInOnFacebook);
	}
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_login__layout_facebook_login);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fbFragment = (UserSettingsFragment) fragmentManager.findFragmentById(R.id.login_fragment);
        
        app = (MainApp)getApplication();
        
        fbFragment.setReadPermissions(Arrays.asList("email"));
        
        // Comprobar la sesión de Facebook
        if( !isFacebookSessionOpened() ){
        	fbFragment.setSessionStatusCallback(new Session.StatusCallback() {
                public void call(Session session, SessionState state, Exception exception) {
                	
                	if(session.isOpened()){
                		// La sesión está abierta
                		Milog.d("La sesión está abierta");
                		
                		
                		
                		
                		loginWithFacebookDrupal(session);
                		
                		
                	}else {
                		// La sesión está cerrada
                		Milog.d("La sesión está cerrada");
                	}
                }
            });
        }else{
        	loginWithFacebookDrupal(Session.getActiveSession());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    private boolean isFacebookSessionOpened(){
    	// Comprobar la sesión de Facebook
    	try{
    		if( Session.getActiveSession().isOpened() ){
            	// La sesión está activa
            	Milog.d("Sesion activa. Token: " + Session.getActiveSession().getAccessToken());
            	return true;
            }
    	}catch(Exception ex){
    		Milog.d("Excepcion al comprobar sesión activa de Facebook. Puede que la sesión no esté iniciada: " + ex.toString());
    	}
        
        return false;
    }
    
    
    private void loginWithFacebookDrupal(final Session session){

    	// Conectar con facebook a Drupal
		LoadingDialog.showLoading(FacebookLoginActivity.this);
		User.checkIfUserIsLoggedIn(app.drupalClient,app.preferencias,
				new User.UserSessionListener() {
					public void onSessionChecked(
							boolean userIsLoggedIn, boolean isTempUser) {
						if (userIsLoggedIn) {
							// Si está logueado lo deslogueo y luego
							// logueo
							User.logout(app.drupalClient,
									new User.UserLogoutListener() {
										public void onLogoutSuccess() {
											Milog.d("Se ha deslogueado el usuario actual");
											// Ahora logueo
											User.loginWithFacebook(
													app.drupalClient,
													app.drupalSecurity,
													session.getAccessToken(),
													app.preferencias,
													new UserFBConnectListener() {
														public void onFBConnectSuccess(
																User userLoggedIn) {
															LoadingDialog
																	.hideLoading(FacebookLoginActivity.this);
															if(facebookLoginListener != null){
																facebookLoginListener.onFacebookLoginActivityFinish(true);
															}
															finish();
														}

														public void onFBConnectError(
																String error) {
															LoadingDialog
																	.hideLoading(FacebookLoginActivity.this);
															if (error
																	.contains("rong username")) {
																MessageDialog
																		.showMessage(
																				FacebookLoginActivity.this,
																				"Datos incorrectos",
																				"El usuario o la contraseña no existe");
															} else {
																MessageDialog
																		.showMessage(
																				FacebookLoginActivity.this,
																				"Error",
																				"Error al hacer login");
															}

														}
													});
										}

										public void onLogoutError(
												String error) {
											Milog.d("Error al desloguear el usuario actual");
											LoadingDialog.hideLoading(FacebookLoginActivity.this);
											MessageDialog
													.showMessage(FacebookLoginActivity.this,
															"Error",
															"Error al hacer login");
										}
									});
						} else {
							// Si no está logueado lo logueo
							User.loginWithFacebook(
									app.drupalClient,
									app.drupalSecurity,
									session.getAccessToken(),
									app.preferencias,
									new UserFBConnectListener() {
										public void onFBConnectSuccess(
												User userLoggedIn) {
											LoadingDialog
													.hideLoading(FacebookLoginActivity.this);
											if(facebookLoginListener != null){
												facebookLoginListener.onFacebookLoginActivityFinish(true);
											}
											finish();
										}

										public void onFBConnectError(
												String error) {
											LoadingDialog
													.hideLoading(FacebookLoginActivity.this);
											if (error
													.contains("rong username")) {
												MessageDialog
														.showMessage(
																FacebookLoginActivity.this,
																"Datos incorrectos",
																"El usuario o la contraseña no existe");
											} else {
												MessageDialog
														.showMessage(
																FacebookLoginActivity.this,
																"Error",
																"Error al hacer login");
											}

										}
									});
						}

					}

					public void onSessionError(String error) {
						LoadingDialog.hideLoading(FacebookLoginActivity.this);
						MessageDialog.showMessage(FacebookLoginActivity.this, "Error",
								"Error al entrar");
					}
				});
    }
    
}