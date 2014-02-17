package com.alborgis.ting.mainapp.ra;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.wikitude.BasicArchitectActivity;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class RAActivity extends BasicArchitectActivity {
	
	public static final String PARAM_KEY_JSON_POI_DATA		=	"json_poi_data_string";

	protected String poiDataJSONString;

	
	MainApp app;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		app = (MainApp)getApplication();
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			poiDataJSONString = b.getString(PARAM_KEY_JSON_POI_DATA);
		}

		capturarControles();
		inicializarForm();
		escucharEventos();

		
		/*this.locationListener = new LocationListener() {

			@Override
			public void onStatusChanged( String provider, int status, Bundle extras ) {
			}

			@Override
			public void onProviderEnabled( String provider ) {
			}

			@Override
			public void onProviderDisabled( String provider ) {
			}

			@Override
			public void onLocationChanged( final Location location ) {
				Milog.d("Location changed");
				if (location!=null) {
					RAActivity.this.lastKnownLocaton = location;
				if ( RAActivity.this.architectView != null ) {
					if ( location.hasAltitude() ) {
						RAActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
					} else {
						RAActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAccuracy() );
					}
				}
				}
			}
		};

		this.architectView.registerSensorAccuracyChangeListener( this.sensorAccuracyListener );
		this.locationProvider = new LocationProvider( this, this.locationListener );*/

	}

	public void finish() {
	    super.finish();
	    overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);   
	}
	

	@Override
	protected void onPostCreate( final Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		
		
		// Cargar datos
		loadData();
		
		// Establecer el radio de distancia
		String radiusStr = String.valueOf(app.MAX_DISTANCE_SEARCH_GEOMETRIES_RA);
		RAActivity.this.callJavaScript("World.setRadiousMetters", new String[] { radiusStr });
		
		
		
		// Escuchar el evento de cuando se pulsa un elemento
		this.architectView.registerUrlListener(new ArchitectUrlListener() {
			// fetch e.g. document.location = "architectsdk://markerselected?id=1";
			public boolean urlWasInvoked(String uriString) {
				Log.d("Milog", "URL was invoqued: " + uriString);
				Uri invokedUri = Uri.parse(uriString);
				String idClicked = invokedUri.getQueryParameter("id");
				if ("markerselected".equalsIgnoreCase(invokedUri.getHost()) && idClicked != null) {
					// TODO si se pulsa un poi
					Log.d("Milog", "Pulsado un poi con id: " + idClicked);

					
				}
				return false;
			}
		});
	}
	
	
	
	private void capturarControles() {
		
	}
	
	private void escucharEventos(){
		
	}
	
	
	private void inicializarForm() {
		
	}
	
	
	
	
	
	boolean isLoading = false;
	
	final Runnable loadData = new Runnable() {

		@Override
		public void run() {
			
			isLoading = true;
			
			final int WAIT_FOR_LOCATION_STEP_MS = 2000;
			
			while (RAActivity.this.lastKnownLocaton == null && !RAActivity.this.isFinishing()) {
			
				RAActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Milog.d("Location fetching");
						//Toast.makeText(RAActivity.this, "Location fetching", Toast.LENGTH_SHORT).show();
					}
				});

				try {
					Thread.sleep(WAIT_FOR_LOCATION_STEP_MS);
				} catch (InterruptedException e) {
					break;
				}
			}
			
			if (RAActivity.this.lastKnownLocaton != null && !RAActivity.this.isFinishing()) {
				
				// Poner los datos
				RAActivity.this.callJavaScript("World.loadPoisFromJsonData", new String[] { poiDataJSONString });
			}
			
			isLoading = false;
		}
	};
	
	
	protected void loadData() {
		if (!isLoading) {
			final Thread t = new Thread(loadData);
			t.start();
		}
	}
	
	/**
	 * call JacaScript in architectView
	 * @param methodName
	 * @param arguments
	 */
	private void callJavaScript(final String methodName, final String[] arguments) {
		final StringBuilder argumentsString = new StringBuilder("");
		for (int i= 0; i<arguments.length; i++) {
			argumentsString.append(arguments[i]);
			if (i<arguments.length-1) {
				argumentsString.append(", ");
			}
		}
		
		if (this.architectView!=null) {
			final String js = ( methodName + "( " + argumentsString.toString() + " );" );
			this.architectView.callJavascript(js);
		}
	}

	
	
	
	
	
	
	
	
	
	
	

	
	

}