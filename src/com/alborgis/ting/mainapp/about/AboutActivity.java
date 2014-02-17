package com.alborgis.ting.mainapp.about;

import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.home.MainActivity;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends Activity {

	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	
	TextView tvTexto;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_about__layout_about);
		
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

		this.app = (MainApp) getApplication();

		capturarControles();
		inicializarForm();
		escucharEventos();

	}
	

	

	protected void onResume() {
		super.onResume();

	}


	public void finish() {
		super.finish();
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTexto = (TextView) findViewById(R.id.tvTexto);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		Typeface tfGullyNormal = TINGTypeface.getGullyNormalTypeface(this);

		tvTitle.setTypeface(tfGullyLight);
		tvTexto.setTypeface(tfGullyNormal);
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AboutActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

	
	

	

}
