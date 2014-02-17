package com.alborgis.ting.mainapp.common;

import com.alborgis.ting.mainapp.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageDialog extends Dialog {

	Context ctx;
	
	String title;
	String message;
	
	ImageView imgViewIcon;
	TextView tvTitle;
	TextView tvMessage;
	Button btnNegative;
	Button btnPositive;
	
	String positiveButtonText;
	String negativeButtonText; 
	
	MessageDialogListener messageDialogListener;
	public interface MessageDialogListener {
		public void onPositiveButtonClick(MessageDialog dialog);
		public void onNegativeButtonClick(MessageDialog dialog);
	}

	public MessageDialog(Context context, String _title, String _message) {
		super(context);
		ctx = context;
		title = _title;
		message = _message;
	}
	
	public MessageDialog(Context context, String _title, String _message, String _positiveButtonText, String _negativeButtonText, MessageDialogListener _messageDialogListener) {
		super(context);
		ctx = context;
		title = _title;
		message = _message;
		positiveButtonText = _positiveButtonText;
		negativeButtonText = _negativeButtonText;
		messageDialogListener = _messageDialogListener;
	}
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.mod_common__layout_message_dialog);
	    
	    this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    
	    capturarControles();
		inicializarForm();
		escucharEventos();
	  }



	private void capturarControles(){
		imgViewIcon = (ImageView)findViewById(R.id.imgViewLogoTing);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvMessage = (TextView)findViewById(R.id.tvMessage);
		btnNegative = (Button)findViewById(R.id.btnNegative);
		btnPositive = (Button)findViewById(R.id.btnPositive);
	}
	
	private void escucharEventos() {
		
		// Poner los textos a los botones
		if(this.positiveButtonText != null){
			btnPositive.setText(this.positiveButtonText);
		}
		if(this.negativeButtonText != null){
			btnNegative.setText(this.negativeButtonText);
		}else{
			btnNegative.setText("OK");
		}
		
		// Si no se pasa un listener, ocultar el bot—n positivo
		if(messageDialogListener == null){
			btnPositive.setVisibility(View.GONE);
		}
		
		
		// Poner los listener a los botones
		btnPositive.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				
				if(messageDialogListener != null){
					messageDialogListener.onPositiveButtonClick(MessageDialog.this);
				}else{
					MessageDialog.this.dismiss();
				}
			}
		});
		btnNegative.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				
				if(messageDialogListener != null){
					messageDialogListener.onNegativeButtonClick(MessageDialog.this);
				}else{
					MessageDialog.this.dismiss();
				}
				
			}
		});

	}
	
	private void inicializarForm(){
		// Poner tipograf’as
		Typeface tfBold = TINGTypeface.getGullyBoldTypeface(ctx);
		Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(ctx);
		
		tvTitle.setTypeface(tfBold);
		tvMessage.setTypeface(tfNormal);
		btnNegative.setTypeface(tfNormal);
		btnPositive.setTypeface(tfNormal);
		
		tvTitle.setText(title);
		tvMessage.setText(message);
	}
	
	
	// Para crear un mensaje informativo desde cualquier lugar
	public static void showMessage(Context _ctx, String _title, String _message){
		MessageDialog messageDialog = new MessageDialog(_ctx, _title, _message);
		messageDialog.show();
	}
	// Para crear un mensaje con 1 bot—n con el texto deseado y que responda al evento que le digamos
	public static void showMessageWith1Buttons(Context _ctx, String _title, String _message, String _negativeButtonText, MessageDialogListener _messageDialogListener){
		MessageDialog messageDialog = new MessageDialog(_ctx, _title, _message, null, _negativeButtonText, _messageDialogListener);
		messageDialog.show();
	}
	// Para crear un mensaje con 2 botones desde cualquier lugar
	public static void showMessageWith2Buttons(Context _ctx, String _title, String _message, String _positiveButtonText, String _negativeButtonText, MessageDialogListener _messageDialogListenerr){
		MessageDialog messageDialog = new MessageDialog(_ctx, _title, _message, _positiveButtonText, _negativeButtonText, _messageDialogListenerr);
		messageDialog.show();
	}
	
}
