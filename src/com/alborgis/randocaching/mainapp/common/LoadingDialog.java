package com.alborgis.randocaching.mainapp.common;

import com.alborgis.randocaching.mainapp.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

public class LoadingDialog extends Dialog {

	Context ctx;

	public LoadingDialog(Context context) {
		super(context);
		ctx = context;
	}
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.mod_common__layout_loading_dialog);
	    
	    this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    
	    capturarControles();
		inicializarForm();
		escucharEventos();

	  }



	private void capturarControles(){
		
	}
	
	private void escucharEventos() {
		
	}
	
	private void inicializarForm(){
		// Poner tipograf’as

		setCancelable(false);
	}
	
	
	// Para modificar el control de carga desde cualquier lugar
	private static LoadingDialog loadingDialog = null;
	public static void showLoading(Context _ctx){
		if(loadingDialog == null){
			if(_ctx != null){
				Activity act = (Activity)_ctx;
				if(!act.isFinishing()){
					loadingDialog = new LoadingDialog(_ctx);
					loadingDialog.show();
				}
			}
			
		}else{
			if(_ctx != null){
				Activity act = (Activity)_ctx;
				if(!act.isFinishing()){
					loadingDialog.show();
				}
			}
		}
		
		
	}
	public static void hideLoading(Context _ctx){
		if(loadingDialog != null && _ctx != null){
			Activity act = (Activity)_ctx;
			if(!act.isFinishing()){
				loadingDialog.dismiss();
				loadingDialog = null;
			}
		}
	}
}
