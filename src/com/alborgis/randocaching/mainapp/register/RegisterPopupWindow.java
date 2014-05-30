package com.alborgis.randocaching.mainapp.register;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.User;
import com.alborgis.randocaching.mainapp.MainApp;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.common.LoadingDialog;
import com.alborgis.randocaching.mainapp.common.MessageDialog;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;
import com.alborgis.randocaching.mainapp.common.MessageDialog.MessageDialogListener;
import com.alborgis.randocaching.mainapp.login.LoginPopupWindow.LoginPopupWindowListener;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterPopupWindow extends Dialog {
	MainApp app;
	Context ctx;
	
	TextView tvTitleDialog;
	
	EditText tbCorreo;
	EditText tbPass;
	EditText tbRepeatPass;
	EditText tbName;
	EditText tbLastName;

	Button btnCancel;
	Button btnRegister;
	
	LoginPopupWindowListener listener;

	public RegisterPopupWindow(Context context, Application _app, LoginPopupWindowListener _listener) {
		super(context);
		app = (MainApp)_app;
		ctx = context;
		listener = _listener;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.mod_register__register_popup);
	    
	    this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    
	    capturarControles();
		inicializarForm();
		escucharEventos();

	  }

	
	


	private void capturarControles(){
		tvTitleDialog = (TextView)findViewById(R.id.tvTitulo);
		tbCorreo = (EditText) findViewById(R.id.tbMail);
		tbPass = (EditText) findViewById(R.id.tbPass);
		tbRepeatPass = (EditText) findViewById(R.id.tbRepeatPass);
		tbName = (EditText) findViewById(R.id.tbName);
		tbLastName = (EditText) findViewById(R.id.tbLastName);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnCancel = (Button) findViewById(R.id.btnCancel);
	}
	
	private void escucharEventos() {
		
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				listener.onLoginPopupWindowDismiss(false, false);
			}
		});
		
		btnRegister.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(validarCampos()){
					
					MessageDialog.showMessageWith2Buttons(ctx, ctx.getString(R.string.register_crear_cuenta), ctx.getString(R.string.register_deseas_crear_tu_cuenta_en_travel_in_game), ctx.getString(R.string.register_crear_cuenta),  ctx.getString(R.string.register_cancelar), 
						new MessageDialogListener() {
							public void onPositiveButtonClick(MessageDialog dialog) {
								dialog.dismiss();
								registro();
							}
							public void onNegativeButtonClick(MessageDialog dialog) {
								dialog.dismiss();
							}
						});
				}
			}
		});
	}
	
	private void inicializarForm(){
		// Poner tipograf’as
		Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(ctx);
		Typeface tfBold = TINGTypeface.getGullyBoldTypeface(ctx);
		tvTitleDialog.setTypeface(tfBold);
		tbCorreo.setTypeface(tfNormal);
		tbPass.setTypeface(tfNormal);
		tbRepeatPass.setTypeface(tfNormal);
		tbName.setTypeface(tfNormal);
		tbLastName.setTypeface(tfNormal);
		btnRegister.setTypeface(tfNormal);
		btnCancel.setTypeface(tfNormal);
	}
	
	
	private boolean validarCampos(){
		if(TextUtils.isEmpty(tbCorreo.getText().toString())){
			MessageDialog.showMessage(ctx, ctx.getString(R.string.register_correo_electronico), ctx.getString(R.string.register_por_favor_introduce_tu_correo_electronico));
			return false;
		}
		if(!tbCorreo.getText().toString().contains("@")){
			MessageDialog.showMessage(ctx, ctx.getString(R.string.register_correo_electronico), ctx.getString(R.string.register_el_correo_electronico_especificado_no_es_valido));
			return false;
		}
		if(TextUtils.isEmpty(tbPass.getText().toString())){
			MessageDialog.showMessage(ctx, ctx.getString(R.string.register_contrasena), ctx.getString(R.string.register_por_favor_introduce_tu_contrasena));
			return false;
		}
		if(TextUtils.isEmpty(tbRepeatPass.getText().toString())){
			MessageDialog.showMessage(ctx, ctx.getString(R.string.register_contrasena), ctx.getString(R.string.register_por_favor_repite_tu_contrasena));
			return false;
		}
		if(tbPass.getText().toString().length() < 3){
			MessageDialog.showMessage(ctx, ctx.getString(R.string.register_contrasena), ctx.getString(R.string.register_la_contrasena_es_demasiado_corta));
			return false;
		}
		if(!tbPass.getText().toString().equals(tbRepeatPass.getText().toString())){
			MessageDialog.showMessage(ctx, ctx.getString(R.string.register_contrasenas), ctx.getString(R.string.register_las_contrasenas_no_coinciden));
			return false;
		}
		return true;
	}
	
	private void registro(){
		
			
			final User user = new User();
			user.mail = tbCorreo.getText().toString();
			user.pass = tbPass.getText().toString();
			user.firstName = tbName.getText().toString();
			user.lastName = tbLastName.getText().toString();
			
			
			LoadingDialog.showLoading(ctx);
			User.checkIfUserIsLoggedIn(app.drupalClient, app.preferencias, new User.UserSessionListener() {
				public void onSessionChecked(boolean userIsLoggedIn, boolean isTempUser) {
					if(userIsLoggedIn){
						// Si est‡ logueado lo deslogueo y luego registro
						User.logout(app.drupalClient, new User.UserLogoutListener() {
							public void onLogoutSuccess() {
								Milog.d("Se ha deslogueado el usuario actual");
								// Ahora logueo
								User.register(app.drupalClient, app.drupalSecurity, user, app.preferencias, new User.UserRegisterListener() {
									public void onRegisterSuccess(User userRegistered) {
										LoadingDialog.hideLoading(ctx);
										dismiss();
										listener.onLoginPopupWindowDismiss(true, false);
									}
									public void onRegisterError(String error) {
										LoadingDialog.hideLoading(ctx);
										MessageDialog.showMessage(ctx, ctx.getString(R.string.register_error_al_crear_cuenta), error);
									}
								});
							}
							public void onLogoutError(String error) {
								Milog.d("Error al desloguear el usuario actual");
								LoadingDialog.hideLoading(ctx);
								MessageDialog.showMessage(ctx, ctx.getString(R.string.register_error_al_crear_cuenta), error);
							}
						});
					}else{
						// Si no est‡ logueado lo registro y logueo
						User.register(app.drupalClient, app.drupalSecurity, user, app.preferencias, new User.UserRegisterListener() {
							public void onRegisterSuccess(User userRegistered) {
								LoadingDialog.hideLoading(ctx);
								dismiss();
								listener.onLoginPopupWindowDismiss(true, false);
							}
							public void onRegisterError(String error) {
								LoadingDialog.hideLoading(ctx);
								MessageDialog.showMessage(ctx, ctx.getString(R.string.register_error_al_crear_cuenta), error);
							}
						});
					}
					
				}
				public void onSessionError(String error) {
					LoadingDialog.hideLoading(ctx);
					MessageDialog.showMessage(ctx, ctx.getString(R.string.register_error_al_crear_cuenta), error);
				}
			});
			
			
			
			LoadingDialog.showLoading(ctx);

	}
	
}
