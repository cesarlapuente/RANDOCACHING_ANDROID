package com.alborgis.ting.mainapp.stories;

import com.alborgis.ting.base.model.Info;
import com.alborgis.ting.base.model.Info.InfoItemListener;
import com.alborgis.ting.base.utils.Util;
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
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class StoryDetailActivity extends Activity implements InfoItemListener {

	public static final String PARAM_KEY_STORY_NID	=	"param_key_story_nid";
	
	MainApp app;
	
	String nid;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	
	ImageView imageView;
	TextView tvBody;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_stories__layout_story_detail);

		this.app = (MainApp) getApplication();
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			nid = b.getString(PARAM_KEY_STORY_NID);
		}
		

		capturarControles();
		inicializarForm();
		escucharEventos();

		cargarDatos();
	}
	

	

	protected void onResume() {
		super.onResume();

	}
	
	public void onPause() {
		super.onPause();
	}

	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		imageView = (ImageView) findViewById(R.id.imageView);
		tvBody = (TextView) findViewById(R.id.tvBody);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyNormal = TINGTypeface.getGullyNormalTypeface(this);
		tvTitle.setTypeface(tfGullyLight);
		tvBody.setTypeface(tfGullyNormal);
		
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(StoryDetailActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
	}

	
	private void cargarDatos(){
		// Mostrar cargando
		showLoading(true);

		// Hago la llamada al servicio con los parametros
		Info.getInfo(nid, null, app.drupalClient, app.drupalSecurity, this);
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



	public void onInfoItemLoad(Info info) {
		showLoading(false);
		if(info != null){
			
			if(info.title != null && !info.title.equals("")){
				tvTitle.setText(info.title.toUpperCase());
			}else{
				tvTitle.setText(getString(R.string.historias_sin_titulo));
			}

			if(info.body != null && !info.body.equals("") && !info.body.equals("null")){
				tvBody.setText( Html.fromHtml(info.body), BufferType.SPANNABLE );
			}else{
				tvBody.setText(getString(R.string.historias_sin_descripcion_disponible));
			}
			
			Util.loadBitmapOnImageView(this, imageView, info.featuredImage, null, 0, R.anim.anim_fade_in_300, 0, 0);
		}
	}

	public void onInfoItemError(String error) {
		showLoading(false);
		MessageDialog.showMessage(this, getString(R.string.historias_error_al_cargar), getString(R.string.historias_no_se_puede_cargar_la_historia_en_estos_momentos));
	}




	

}
