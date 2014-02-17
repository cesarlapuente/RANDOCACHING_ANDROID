package com.alborgis.ting.mainapp.common;

import com.alborgis.ting.mainapp.R;
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

	}
	
	
	// Para modificar el control de carga desde cualquier lugar
	private static LoadingDialog loadingDialog = null;
	public static void showLoading(Context _ctx){
		if(loadingDialog == null){
			if(_ctx != null){
				loadingDialog = new LoadingDialog(_ctx);
			}
			
		}
		loadingDialog.show();
	}
	public static void hideLoading(){
		if(loadingDialog != null){
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
}
