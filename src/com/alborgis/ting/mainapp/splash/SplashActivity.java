package com.alborgis.ting.mainapp.splash;

import java.lang.reflect.Field;
import java.util.ArrayList;
import com.alborgis.ting.base.model.Misc;
import com.alborgis.ting.base.model.Misc.SplashImageListListener;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.about.AboutActivity;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.home.MainActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

public class SplashActivity extends Activity {

	// Define el tiempo que pasa entre animaci—n y animaci—n
	public static final int TIME_SHOWING_CARROUSEL_IMAGE = 3000;
	public static final int SPEED_CARROUSEL_TRANSITION = 350;
	public static final int DELAY_REACTIVATE_AUTOANIMATION = 5000;

	
	MainApp app;
	
	// Bot—n de entrar
	Button btnEntrar;
	
	// Bot—n de m‡s info
	Button btnAbout;
	
	// Carrusel
	ViewPager viewPager;

	// Adaptador del carrusel
	CarouselAdapter adapter;
	
	// Barra de progreso de conexi—n
	ProgressBar progressWheel;

	// Im‡genes que se cargar‡n en el carrusel
	ArrayList<String> images;

	// Manejador del hilo que gestiona la animaci—n del carrusel
	Handler handler = new Handler();

	// Definici—n de lo que se hace cada vez que se quiere autoanimar el
	// carrusel
	Runnable runnable = new Runnable() {
		public void run() {
			if(images != null && viewPager != null){
				int position = viewPager.getCurrentItem();
				if ( (position+1) >= images.size()) {
					position = 0;
				} else {
					position = position + 1;
				}
				viewPager.setCurrentItem(position, true);
				startAutoAnimatingCarrousel(TIME_SHOWING_CARROUSEL_IMAGE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_splash__activity_splash);
		
		app = (MainApp)getApplication();
		
		capturarControles();
		escucharEventos();
		inicializarForm();

	}

	protected void onResume() {
		super.onResume();
		
	}

	public void onPause() {
		super.onPause();
		
	}

	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void finish() {
		super.finish();
		// Paramos el hilo que auto-anima el carrusel
		stopAutoanimatingCarrousel();
	}

	private void capturarControles() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		btnEntrar = (Button) findViewById(R.id.btnEntrar);
		btnAbout = (Button) findViewById(R.id.btnAbout);
		progressWheel = (ProgressBar)findViewById(R.id.progressWheel);
	}

	private void escucharEventos() {
		btnEntrar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				openHomeActivity();
			}
		});
		
		btnAbout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				openAboutActivity();
			}
		});
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			int previousState = 0;
			boolean userScrollChange = false;
			public void onPageSelected(int arg0) {}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int state) {
				// Esto sirve para saber si el usuario ha cambiado la p‡gina del viewPager
				if (previousState == ViewPager.SCROLL_STATE_DRAGGING  && state == ViewPager.SCROLL_STATE_SETTLING)                            
	                userScrollChange = true;

	            else if (previousState == ViewPager.SCROLL_STATE_SETTLING  && state == ViewPager.SCROLL_STATE_IDLE)                                                
	                userScrollChange = false;

	            previousState = state;
	            
	            if(userScrollChange){
	            	// Parar el hilo de animar autom‡ticamente
	            	stopAutoanimatingCarrousel();
	            	// Arrancar el hilo a los pocos segundos
	            	startAutoAnimatingCarrousel(DELAY_REACTIVATE_AUTOANIMATION);
	            }
	            
			}
		});
		
	}

	private void inicializarForm() {
		// Poner tipograf’a
		Typeface tf = TINGTypeface.getGullyBoldTypeface(this);
		btnEntrar.setTypeface(tf);
		
		// Establecer un sistema para controlar la velocidad a la que se anima
		// el carrusel
		setCarouselSpeed(SPEED_CARROUSEL_TRANSITION);
		
		// Inicializar el adaptador del carrusel
		Misc.listImageCarrousel(app.drupalClient, app.drupalSecurity, new SplashImageListListener() {
			public void onSplashImageListLoad(ArrayList<String> listItems) {
				images = listItems;
				// Crear el adapter con los elementos
				adapter = new CarouselAdapter(SplashActivity.this, images);
				viewPager.setAdapter(adapter);
				// Lanzamos el hilo que auto-anima el carrusel
				startAutoAnimatingCarrousel(TIME_SHOWING_CARROUSEL_IMAGE);
				// Ocultar la rueda de carga
				progressWheel.setVisibility(View.GONE);
			}
			public void onSplashImageListError(String error) {
				// Ocultar la rueda de carga
				progressWheel.setVisibility(View.GONE);
			}
		});
		
	}
	
	
	
	private void startAutoAnimatingCarrousel(int afterTimeMillis){
		handler.postDelayed(runnable, afterTimeMillis);
	}
	
	private void stopAutoanimatingCarrousel(){
		if (handler != null) {
			handler.removeCallbacks(runnable);
		}
	}
	
	private void setCarouselSpeed(int speedMillis){
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
			CarouselFixedSpeedScroller scroller = new CarouselFixedSpeedScroller(viewPager.getContext(), sInterpolator, SPEED_CARROUSEL_TRANSITION);
			// scroller.setFixedDuration(5000);
			mScroller.set(viewPager, scroller);
		} catch (NoSuchFieldException e) {

		} catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
		
	}
	
	
	private void openHomeActivity(){
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
		finish();
	}
	
	private void openAboutActivity(){
		Intent intent = new Intent(SplashActivity.this, AboutActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
	}

}
