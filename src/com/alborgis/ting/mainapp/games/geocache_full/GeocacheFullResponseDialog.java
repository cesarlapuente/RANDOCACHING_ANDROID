package com.alborgis.ting.mainapp.games.geocache_full;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Badge;
import com.alborgis.ting.base.model.BundleEarned;
import com.alborgis.ting.base.model.Stamp;
import com.alborgis.ting.base.model.Visado;
import com.alborgis.ting.base.model.Vote;
import com.alborgis.ting.base.model.Vote.VoteSetListener;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.passport.PassportActivity;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class GeocacheFullResponseDialog extends Dialog {
	
	public static final int STATE_GULLY_SALTADO_ORDEN		=	1;
	public static final int STATE_GULLY_TESORO_ENCONTRADO	=	2;
	public static final int STATE_GULLY_COFRE_VACIO			=	3;
	
	MainApp app;
	Context ctx;
	Activity activity;
	GeocacheOneResponseDialogListener listener;

	String title;
	String body;
	String buttonText;
	BundleEarned bundleEarned;
	int gullyState;
	boolean showRatingBar;
	String nidGeocacheGame;
	
	ImageView imageViewRespuesta;
	ImageView imageViewGully;
	TextView tvTitle;
	TextView tvBody;
	Button btnCerrar;
	LinearLayout panelLogrosObtenidos;
	TextView tvLogrosObtenidos;;
	Button btnPasaporte;
	
	LinearLayout panelVotar;
	TextView tvVotar;
	RatingBar ratingBar;
	
	
	
	public static interface GeocacheOneResponseDialogListener {
		public void onGeocacheOneResponseDialogDismiss();
	}

	
	public GeocacheFullResponseDialog(Context context, Activity _act, Application _app, String _title, String _body, String _buttonText, BundleEarned _bundleEarned, int _gullyState, boolean _showRatingBar, String _nidGeocacheGame, GeocacheOneResponseDialogListener _listener) {
		super(context);
		app = (MainApp)_app;
		ctx = context;
		activity = _act;
		listener = _listener;
		this.title = _title;
		this.body = _body;
		this.buttonText = _buttonText;
		this.bundleEarned = _bundleEarned;
		this.gullyState = _gullyState;
		this.showRatingBar = _showRatingBar;
		this.nidGeocacheGame = _nidGeocacheGame;
	}
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.mod_geocache_full__geocache_response_popup);
	    
	    this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    
	    capturarControles();
		inicializarForm();
		escucharEventos();

	  }

	@Override
	public void dismiss() {
		listener.onGeocacheOneResponseDialogDismiss();
		super.dismiss();
	}
	


	private void capturarControles(){
		imageViewRespuesta = (ImageView)findViewById(R.id.imageViewRespuesta);
		imageViewGully = (ImageView)findViewById(R.id.gully);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvBody = (TextView)findViewById(R.id.tvBody);
		btnCerrar = (Button)findViewById(R.id.btnCerrar);
		panelLogrosObtenidos = (LinearLayout)findViewById(R.id.panelLogrosObtenidos);
		tvLogrosObtenidos = (TextView)findViewById(R.id.tvLogrosObtenidos);
		btnPasaporte = (Button)findViewById(R.id.btnPasaporte);
		panelVotar = (LinearLayout)findViewById(R.id.layoutVotar);
		tvVotar = (TextView)findViewById(R.id.tvVotar);
		ratingBar = (RatingBar)findViewById(R.id.ratingBar);
	}
	
	private void escucharEventos() {
		btnCerrar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		btnPasaporte.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(ctx, PassportActivity.class);
				ctx.startActivity(intent);
				activity.finish();
			}
		});
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
				if(fromUser){
					int numStars = (int)rating;
					// Si la votaci—n procede del usuario, realizar la votaci—n
					Vote.setVote(app.drupalClient, app.drupalSecurity, nidGeocacheGame, numStars, new VoteSetListener() {
						public void onVoteSent(Vote voteSent) {
							Milog.d("Voto hecho: " + voteSent.value);
						}
						public void onVoteSendError(String error) {
							Milog.d("Error al votar: " + error);
						}
					});
				}
			}
		});
	}
	
	private void inicializarForm(){
		// Poner tipograf’as
		Typeface tfGullyBold = TINGTypeface.getGullyBoldTypeface(ctx);
		Typeface tfGullyNormal = TINGTypeface.getGullyNormalTypeface(ctx);
		Typeface tfDroidSansNormal = TINGTypeface.getDroidSansNormalTypeface(ctx);
		
		tvTitle.setTypeface(tfGullyBold);
		tvBody.setTypeface(tfDroidSansNormal);
		btnCerrar.setTypeface(tfGullyNormal);
		tvVotar.setTypeface(tfGullyNormal);
		
		
		tvLogrosObtenidos.setTypeface(tfDroidSansNormal);
		btnPasaporte.setTypeface(tfGullyNormal);
		
		btnCerrar.setText(buttonText);
		
		tvTitle.setText(title);
		tvBody.setText(Html.fromHtml(body), BufferType.SPANNABLE);
		
		// Comprobar el estado de la respuesta
		if(this.gullyState == STATE_GULLY_TESORO_ENCONTRADO){
			this.imageViewRespuesta.setImageResource(R.drawable.icono_acierto_cofre_popup);
			this.imageViewGully.setImageResource(R.drawable.gully_acierto_popup);
		}else if(this.gullyState == STATE_GULLY_COFRE_VACIO){
			this.imageViewRespuesta.setImageResource(R.drawable.icono_error_cofre_popup);
			this.imageViewGully.setImageResource(R.drawable.gully_error_popup);
		}else if(this.gullyState == STATE_GULLY_SALTADO_ORDEN){
			this.imageViewRespuesta.setImageResource(R.drawable.icono_error_cofre_popup);
			this.imageViewGully.setImageResource(R.drawable.gully_error_popup);
		}
		
		// Comprobar si hay que mostrar o no la barra de votaci—n
		if(showRatingBar){
			panelVotar.setVisibility(View.VISIBLE);
		}else{
			panelVotar.setVisibility(View.GONE);
		}

		// Comprobar los logros obtenidos
		if(bundleEarned != null){
			// Mostrar el panel de logros
			panelLogrosObtenidos.setVisibility(View.VISIBLE);
			String texto = "";
			// Comprobar si hay medallas
			if(bundleEarned.getBadges() != null && bundleEarned.getBadges().size() > 0){
				texto += "HAS GANADO ESTAS MEDALLAS: \n";
				for(Badge badge : bundleEarned.getBadges()){
					texto += "\n - " + badge.title;
				}
				texto += "\n";
			}
			// Comprobar si hay sellos
			if(bundleEarned.getStamps() != null && bundleEarned.getStamps().size() > 0){
				texto += "TE HEMOS OTORGADO ESTOS SELLOS: \n";
				for(Stamp stamp : bundleEarned.getStamps()){
					texto += "\n - " + stamp.title;
				}
				texto += "\n";
			}
			// Comprobar si hay visados
			if(bundleEarned.getVisados() != null && bundleEarned.getVisados().size() > 0){
				texto += "HAS OBTENIDO ESTOS VISADOS: \n";
				for(Visado visado : bundleEarned.getVisados()){
					texto += "\n - " + visado.title;
				}
				texto += "\n";
			}
			// Comprobar si hay puntos
			if(bundleEarned.getPoints() > 0){
				texto += "SUMAS " + bundleEarned.getPoints() +" PUNTOS";
			}
			
			// Poner el texto en la etiqueta
			tvLogrosObtenidos.setText(texto);
			
		}else{
			// Ocultar el panel de logros
			panelLogrosObtenidos.setVisibility(View.GONE);
		}

	}
}
