package com.alborgis.ting.mainapp.games.geocache_full;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.BundleEarned;
import com.alborgis.ting.base.model.GeoPoint;
import com.alborgis.ting.base.model.Geocache;
import com.alborgis.ting.base.model.Geocache.GeocacheFullCaptureListener;
import com.alborgis.ting.base.model.Geocache.GeocacheFullListListener;
import com.alborgis.ting.base.model.Poi;
import com.alborgis.ting.base.utils.DataConection;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.TINGColor;
import com.alborgis.ting.mainapp.common.TINGSound;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.common.map_layer_change.CapaBase;
import com.alborgis.ting.mainapp.home.MainActivity;
import com.alborgis.ting.mainapp.ra.JSONPOIParser;
import com.alborgis.ting.mainapp.ra.RAActivity;
import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

public class GeocacheFullMapActivity extends Activity implements LocationListener, GeocacheFullListListener,
		GeocacheFullCaptureListener {

	public static final String PARAM_KEY_DEMO_PLAYING = "key_demo_playing";
	public static final String PARAM_KEY_GAME_NID = "key_game_nid";
	public static final String PARAM_KEY_GAME_TITLE = "key_game_title";
	public static final String PARAM_KEY_GAME_GEOLOCATED = "key_game_geolocated";
	public static final String PARAM_KEY_GAME_NEXT_GEOCACHE_NID = "key_game_next_geocache_nid";
	public static final String PARAM_KEY_GAME_FIRST_GEOCACHE_NID = "key_game_first_geocache_nid";
	public static final String PARAM_KEY_GAME_LAST_GEOCACHE_NID = "key_game_last_geocache_nid";

	boolean demoPlaying;
	String nidGame;
	String titleGame;
	boolean geolocatedGame;
	String nidNextGeocache;
	String nidFirstGeocache;
	String nidLastGeocache;

	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;

	MainApp app;
	MapView mapView;
	ImageButton btnRA;
	Callout callout;
	View calloutContentView;
	TextView lblMessageCallout;
	Button btnCaptureCallout;

	GraphicsLayer capaGeometrias;
	GraphicsLayer capaHighlights;

	ArrayList<Geocache> geocaches;
	Geocache currentSelectedGeocache;

	GeoPoint lastLocation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_geocache_full__mapa_geocaches);

		this.app = (MainApp) getApplication();
		
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

		Bundle b = getIntent().getExtras();
		if (b != null) {
			demoPlaying = b.getBoolean(PARAM_KEY_DEMO_PLAYING);
			nidGame = b.getString(PARAM_KEY_GAME_NID);
			titleGame = b.getString(PARAM_KEY_GAME_TITLE);
			geolocatedGame = b.getBoolean(PARAM_KEY_GAME_GEOLOCATED);
			nidNextGeocache = b.getString(PARAM_KEY_GAME_NEXT_GEOCACHE_NID);
			nidFirstGeocache = b.getString(PARAM_KEY_GAME_FIRST_GEOCACHE_NID);
			nidLastGeocache = b.getString(PARAM_KEY_GAME_LAST_GEOCACHE_NID);
		}

		capturarControles();
		escucharEventos();
		inicializarForm();
	}

	protected void onPause() {
		super.onPause();
		if (mapView != null) {
			mapView.pause();
		}
	}

	public void onResume() {
		super.onResume();
		if (mapView != null) {
			mapView.unpause();
		}
	}

	public void finish() {
		super.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (callout != null && callout.isShowing()) {
				callout.hide();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		mapView = (MapView) findViewById(R.id.mapa);
		btnRA = (ImageButton)findViewById(R.id.btnRA);
	}

	public void escucharEventos() {

		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(GeocacheFullMapActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		btnRA.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(geocaches != null && geocaches.size() > 0){
					// Crear los pois
					ArrayList<Poi> pois = new ArrayList<Poi>();
					for(Geocache geocache : geocaches){
						Poi poi = new Poi();
						poi.nid = geocache.nid;
						poi.title = geocache.title;
						poi.body = geocache.body;
						poi.coordinates = geocache.coordinates;
						pois.add(poi);
					}
					String poisJSON = JSONPOIParser.parseToJSONArray(app, pois).toString();
					Intent intent = new Intent(GeocacheFullMapActivity.this, RAActivity.class);
					intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING, "Realidad aumentada");
					intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "wikitudeWorld" + File.separator + "index.html");
					intent.putExtra(RAActivity.PARAM_KEY_JSON_POI_DATA, poisJSON );
					startActivity(intent);
					overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
				}else{
					Toast.makeText(GeocacheFullMapActivity.this, "No hay geocachés que mostrar", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Al tocar un punto en el mapa
		this.mapView.setOnSingleTapListener(new OnSingleTapListener() {

			private static final long serialVersionUID = 1L;

			public void onSingleTap(float x, float y) {
				// Si el mapa no está cargado, salir
				if (!mapView.isLoaded()) {
					return;
				}

				// Recuperamos los gráficos
				int[] graphicsIDS = capaGeometrias.getGraphicIDs(x, y, 8);
				if (graphicsIDS != null && graphicsIDS.length > 0) {
					Milog.d("Hay graficos en la zona pulsada");
					int targetId = graphicsIDS[0];

					Graphic gr = capaGeometrias.getGraphic(targetId);

					if (gr != null) {
						Map<String, Object> attrs = gr.getAttributes();
						String nid = (String) attrs.get("nid");

						// Mostrar ayuda contextual
						GeocacheFullTipManager.showTipsBeforeOpenCofre(GeocacheFullMapActivity.this, app.preferencias, false);
						
						// Establecer el contenido del callout y mostrarlo
						View contenidoCallout = getViewForCalloutWithThisData(nid);
						callout.animatedShow(
								mapView.toMapPoint(new Point(x, y)),
								contenidoCallout);

						// Centrar el mapa en ese lugar
						Point geoPoint = (Point)gr.getGeometry();
						mapView.centerAt(geoPoint, true);
						
					}

				} else {
					if (callout != null && callout.isShowing()) {
						callout.animatedHide();
					}
				}
			}
		});

		this.mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {

				if (OnStatusChangedListener.STATUS.INITIALIZED == status
						&& source == mapView) {

					callout = mapView.getCallout();
					// Establecer el estilo del callout
					callout.setStyle(R.xml.style_callout_mapa_explorer);
					callout.setMaxWidth(1000);
					callout.setMaxHeight(1000);

					LocationService ls = mapView.getLocationService();
					ls.setLocationListener(GeocacheFullMapActivity.this);
					ls.setAutoPan(false);
					ls.start();

					recargarDatos();

				}

			}

		});

		this.mapView.setOnZoomListener(new OnZoomListener() {

			/**
					 * 
					 */
			private static final long serialVersionUID = 1L;

			public void preAction(float pivotX, float pivotY, double factor) {
				Milog.d("Antes de cambiar el zoom a " + factor);
			}

			public void postAction(float pivotX, float pivotY, double factor) {
				Milog.d("Despues de cambiar el zoom a " + factor);
			}
		});

	}

	private void inicializarForm() {
		// Poner tipografías
		Typeface tfLight = TINGTypeface.getGullyLightTypeface(this);

		tvTitle.setTypeface(tfLight);

		// Comprobar si tiene el gps activado
		LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	    	// Si no lo tiene activado, avisarle
	        mostrarMensajeNoTieneGPSActivado();
	    }
		
	    // Inicializar la capa donde irán los highlights
	 	capaHighlights = new GraphicsLayer();
	 	mapView.addLayer(capaHighlights);
	    
		// Inicializar la capa donde irán los geocaches
		capaGeometrias = new GraphicsLayer();
		mapView.addLayer(capaGeometrias);
		
		

		// Inicializar la capa base
		ponerCapaBase();

		// Poner el título de la ventana
		this.tvTitle.setText(titleGame.toUpperCase());
		
		// Mostrar el asistente de ayuda
		GeocacheFullTipManager.showTipsForMapStartup(this, app.preferencias, false, demoPlaying, geolocatedGame);
	}

	private void recargarDatos() {

		// Mostrar el panel cargando
		showLoading(true);
		while (!DataConection.hayConexion(this)) {
			// Mientras no haya conexión espero...
		}
		// Cargar los geocaches una vez que tengo conexión
		Geocache.fullList(nidGame, 0, 0, 300000, 0, 0, app.drupalClient,
				app.drupalSecurity, this);
	}

	public void onGeocacheFullListLoad(ArrayList<Geocache> items) {
		if (items != null) {
			this.geocaches = items;

			// Limpiar la capa de geocaches
			capaGeometrias.removeAll();
			
			Drawable drawableIconGeocacheOriginal = getResources().getDrawable(R.drawable.btn_poi_cache_normal);
			Bitmap bitmapIconGeocache = ((BitmapDrawable) drawableIconGeocacheOriginal).getBitmap();
			Drawable drawableIconGeocacheRedim = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmapIconGeocache, 48, 64, true));

			for (Geocache geocache : this.geocaches) {
				if (geocache != null && geocache.coordinates != null) {
					Point puntoProyectado = GeometryEngine
							.project(geocache.coordinates.longitude,
									geocache.coordinates.latitude,
									app.spatialReference);
					if (puntoProyectado != null) {
						HashMap<String, Object> attrs = new HashMap<String, Object>();
						attrs.put("nid", geocache.nid);
					    PictureMarkerSymbol sym = new PictureMarkerSymbol(drawableIconGeocacheRedim);
					    sym.setOffsetY(32);
						Graphic gr = new Graphic(puntoProyectado, sym, attrs);
						capaGeometrias.addGraphic(gr);
					}
				}
			}
			
			
			
			
			if(geolocatedGame){
				// Señalar el siguiente Geocache que debe abrir
				highlightNextGeocacheToOpen();
			}else{
				// Señalar el primer geocache y el último
				highlightFirstAndLastGeocaches();
			}
			

			centrarEnExtentCapa(capaGeometrias);
		}

		showLoading(false);
	}

	public void onGeocacheFullListError(String error) {
		Toast.makeText(this, "Error en la carga de geocaches",
				Toast.LENGTH_SHORT).show();
		showLoading(false);
	}

	public void onGeocacheFullCaptureSuccess(BundleEarned bundleEarned, String nidGeocache, String nextGeocacheToCapture, int statusCode) {

		
		if (statusCode == 1) {
			Geocache geo = getGeocache(nidGeocache);
			
			Milog.d("Estado 1. Has capturado TODOS los geocaches");
			
			String title = geo.title;
			String body = "¡Enhorabuena!, Has encontrado todos los tesoros \n" + geo.body;
			String buttonText = "Volver a jugar";
			GeocacheFullResponseDialog popup = new GeocacheFullResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheFullResponseDialog.STATE_GULLY_TESORO_ENCONTRADO, true, nidGame, new GeocacheFullResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					finish();
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.win);

			/*MessageDialog.showMessage(
					this,
					"Capturado",
					"Geocache capturado correctamente, descubierto un tesoro. Seguir jugando);*/
		} else if (statusCode == 2) {
			Geocache geo = getGeocache(nidGeocache);
			
			Milog.d("Estado 2. Acabas de capturar un geocache. A por el siguiente");
			
			String title = geo.title;
			String body = "¡Has encontrado un tesoro! \n" + geo.body;
			String buttonText = "Continuar";
			GeocacheFullResponseDialog popup = new GeocacheFullResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheFullResponseDialog.STATE_GULLY_TESORO_ENCONTRADO, false, nidGame, new GeocacheFullResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.success);
			
			
			// Señalar el siguiente geocache a capturar
			if(nextGeocacheToCapture != null){
				Milog.d("Geocache abierto: " + nidGeocache + "  Geocache siguiente: " + nextGeocacheToCapture);
				nidNextGeocache = nextGeocacheToCapture;
				highlightNextGeocacheToOpen();
			}
			
			
		} else if (statusCode == 3) {
			Geocache geo = getGeocache(nidGeocache);
			
			Milog.d("Estado 3. Este geocache está vacío");
			
			String title = geo.title;
			String body = geo.body;
			String buttonText = "Ir a otro lugar";
			GeocacheFullResponseDialog popup = new GeocacheFullResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheFullResponseDialog.STATE_GULLY_COFRE_VACIO, false, nidGame, new GeocacheFullResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.empty);
			/*MessageDialog.showMessage(this, "Geocaché vacío",	"El geocaché esta vacio);*/
		} else if (statusCode == 4) {
			Geocache geo = getGeocache(nidGeocache);
			
			Milog.d("Estado 4. Te has saltado el orden para capturar los cachés");
			
			String title = geo.title;
			String body = "Debes seguir el orden para abrir los geocachés.\n Vuelves a empezar.\n" + geo.body;
			String buttonText = "Volver a empezar";
			GeocacheFullResponseDialog popup = new GeocacheFullResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheFullResponseDialog.STATE_GULLY_SALTADO_ORDEN, true, nidGame, new GeocacheFullResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					finish();
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.game_over);
			
			// Poner que el siguiente geocache a abrir es el primero del array
			nidNextGeocache = nidFirstGeocache;
			highlightNextGeocacheToOpen();

			/*MessageDialog.showMessage(this, "Te has saltado el orden", "Te has saltado el orden para abrir los geocaches. Vuelves a empezar);*/
		} else if (statusCode == 5) {
			Geocache geo = getGeocache(nidGeocache);
			
			Milog.d("Estado 5. Ya has abierto este geocache antes");
			
			String title = geo.title;
			String body = "Ya has abierto este geocache antes. " + geo.body;
			String buttonText = "Prueba otro lugar";
			GeocacheFullResponseDialog popup = new GeocacheFullResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheFullResponseDialog.STATE_GULLY_COFRE_VACIO, false, nidGame, new GeocacheFullResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.empty);

			/*MessageDialog.showMessage(this, "Geocache consultado",
					"Ya has consultado este geocache. ");*/
		} else {
			MessageDialog.showMessage(this, "Error", "Error general");
		}

		showLoading(false);
	}

	public void onGeocacheFullCaptureError(String error) {
		Milog.d("Error al capturar geocache: " + error);
		MessageDialog.showMessage(this, "Error", "No se puede capturar el geocache");
		showLoading(false);
	}

	public void onLocationChanged(Location newLoc) {
		if (newLoc != null) {
			GeoPoint gp = new GeoPoint();
			gp.latitude = newLoc.getLatitude();
			gp.longitude = newLoc.getLongitude();
			gp.altitude = newLoc.getAltitude();
			this.lastLocation = gp;

			recalcularDistancia();
		}
	}

	public void onProviderDisabled(String arg0) {

	}

	public void onProviderEnabled(String arg0) {

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	
	private void highlightNextGeocacheToOpen(){
		capaHighlights.removeAll();
		Milog.d("Next geocache to highlight: " + nidNextGeocache);
		Geocache nextGeocache = getGeocache(nidNextGeocache);
		if(nextGeocache != null && nextGeocache.coordinates != null){
			Milog.d("Next geocache != null && Next geocache.coordinates != null");
			Point puntoProyectado = GeometryEngine
					.project(nextGeocache.coordinates.longitude,
							nextGeocache.coordinates.latitude,
							app.spatialReference);
			if (puntoProyectado != null) {
				HashMap<String, Object> attrs = new HashMap<String, Object>();
				// Agregar simbolo para señalarlo
			    SimpleMarkerSymbol sym = new SimpleMarkerSymbol(Color.TRANSPARENT, 64, SimpleMarkerSymbol.STYLE.CIRCLE);
			    sym.setOutline(new SimpleLineSymbol(TINGColor.getColor(this, R.color.verde_explorer_principal), 8, SimpleLineSymbol.STYLE.DOT));
			    sym.setOffsetY(32);
				Graphic gr = new Graphic(puntoProyectado, sym, attrs);
				capaHighlights.addGraphic(gr);
				// Agregar texto para señalarlo
				TextSymbol txtSym = new TextSymbol(18, "Dirígete aquí", Color.WHITE, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.TOP);
				Graphic grTxt = new Graphic(puntoProyectado, txtSym, attrs);
				capaHighlights.addGraphic(grTxt);
			}
		}
	}
	
	private void highlightFirstAndLastGeocaches(){
		Geocache firstGeocache = getGeocache(nidFirstGeocache);
		Geocache lastGeocache = getGeocache(nidLastGeocache);

		if(firstGeocache != null && firstGeocache.coordinates != null){
			Milog.d("First geocache != null && First geocache.coordinates != null");
			Point puntoProyectado = GeometryEngine
					.project(firstGeocache.coordinates.longitude,
							firstGeocache.coordinates.latitude,
							app.spatialReference);
			if (puntoProyectado != null) {
				HashMap<String, Object> attrs = new HashMap<String, Object>();
			    TextSymbol sym = new TextSymbol(18, "Inicio", Color.WHITE, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.TOP);
				Graphic gr = new Graphic(puntoProyectado, sym, attrs);
				capaHighlights.addGraphic(gr);
			}
		}
		if(lastGeocache != null && lastGeocache.coordinates != null){
			Milog.d("Last geocache != null && Last geocache.coordinates != null");
			Point puntoProyectado = GeometryEngine
					.project(lastGeocache.coordinates.longitude,
							lastGeocache.coordinates.latitude,
							app.spatialReference);
			if (puntoProyectado != null) {
				HashMap<String, Object> attrs = new HashMap<String, Object>();
			    TextSymbol sym = new TextSymbol(18, "Final", Color.WHITE, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.TOP);
				Graphic gr = new Graphic(puntoProyectado, sym, attrs);
				capaHighlights.addGraphic(gr);
			}
		}
	}
	
	private void centrarEnExtentCapa(GraphicsLayer capa) {
		// Hacer zoom a la capa de geometrias
		Envelope env = new Envelope();
		Envelope NewEnv = new Envelope();
		for (int i : capa.getGraphicIDs()) {
			Geometry geom = capa.getGraphic(i).getGeometry();
			geom.queryEnvelope(env);
			NewEnv.merge(env);
		}
		this.mapView.setExtent(NewEnv, 100);
	}

	public void ponerCapaBase() {
		CapaBase capaSeleccionada = app.capaBaseSeleccionada;
		Object capaBase = capaSeleccionada.getMapLayer();

		// Corrección, para que no cambie la capa base cuando la seleccionada es
		// la misma que ya estaba (ahorra datos)
		Layer[] capas = mapView.getLayers();
		if (capas != null) {
			if (capas.length > 0) {
				Object capa0 = capas[0];
				// si la capa base seleccionada es del mismo tipo que la capa 0
				if (capaBase.getClass().getName()
						.equals(capa0.getClass().getName())) {
					if (capaBase.getClass() == BingMapsLayer.class) {
						;
						BingMapsLayer capaBaseCasted = (BingMapsLayer) capaBase;
						BingMapsLayer capa0Casted = (BingMapsLayer) capa0;

						if (capaBaseCasted.getMapStyle().equals(
								capa0Casted.getMapStyle())) {
							return;
						} else {
							mapView.removeLayer(0);
						}
					} else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
						ArcGISTiledMapServiceLayer capaBaseCasted = (ArcGISTiledMapServiceLayer) capaBase;
						ArcGISTiledMapServiceLayer capa0Casted = (ArcGISTiledMapServiceLayer) capa0;
						String strUrlCapaBaseCasted = capaBaseCasted.getUrl()
								.toString();
						String strUrlCapa0Casted = capa0Casted.getUrl()
								.toString();
						if (strUrlCapaBaseCasted.equals(strUrlCapa0Casted)) {
							return;
						} else {
							mapView.removeLayer(0);
						}
					}
				} else {// si la capa base seleccionada no es del mismo tipo que
						// la capa 0

					if (capaBase.getClass() == BingMapsLayer.class) {
						mapView.removeLayer(0);
					} else if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {
						mapView.removeLayer(0);
					}
				}
			}
			// btnAbrirCapas.setEnabled(true);
			if (capaBase.getClass() == ArcGISTiledMapServiceLayer.class) {

				if (capas.length > 0) {
					mapView.addLayer((ArcGISTiledMapServiceLayer) capaBase, 0);
				} else {
					mapView.addLayer((ArcGISTiledMapServiceLayer) capaBase);
				}

			} else if (capaBase.getClass() == BingMapsLayer.class) {

				if (capas.length > 0) {
					mapView.addLayer((BingMapsLayer) capaBase, 0);
				} else {
					mapView.addLayer((BingMapsLayer) capaBase);
				}

			} else {
				// otro tipo de capa
			}

			app.capaBaseSeleccionada = capaSeleccionada;
		}
	}

	public View getViewForCalloutWithThisData(final String nid) {
		if (this.calloutContentView == null) {
			this.calloutContentView = LayoutInflater.from(
					getApplicationContext()).inflate(
					R.layout.mod_geocache__layout_callout_geocache, null);
			this.lblMessageCallout = (TextView) calloutContentView
					.findViewById(R.id.lblMessage);
			this.btnCaptureCallout = (Button) calloutContentView
					.findViewById(R.id.btnCapture);
			Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(this);
			Typeface tfBold = TINGTypeface.getGullyBoldTypeface(this);
			this.lblMessageCallout.setTypeface(tfNormal);
			this.btnCaptureCallout.setTypeface(tfBold);
		}

		// Obtener el geocache
		Geocache geocache = getGeocache(nid);
		this.currentSelectedGeocache = geocache;

		if (geocache != null) {
			// Desactivar el botón de capturar por defecto
			btnCaptureCallout.setEnabled(false);

			// Poner un mensaje de espera
			lblMessageCallout.setText("Esperando GPS...");
			
			// Recalcular la distancia
			recalcularDistancia();
		}

		// Escuchar el evento del botón capturar
		this.btnCaptureCallout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (DataConection.hayConexion(GeocacheFullMapActivity.this)) {
					showLoading(true);
					Geocache.fullCapture(nid, nidGame, app.drupalClient,
							app.drupalSecurity, GeocacheFullMapActivity.this);
					callout.animatedHide();
				} else {
					MessageDialog.showMessage(GeocacheFullMapActivity.this,
							"No hay conexión",
							"No dispones de conexión a Internet. Inténtalo de nuevo");
				}

			}
		});

		return this.calloutContentView;
	}

	private void showLoading(boolean show) {
		if (show) {
			LoadingDialog.showLoading(this);
		} else {
			LoadingDialog.hideLoading(this);
		}
	}

	// Obtiene un geocache de la colección en base al nid que se le envía. Si no
	// nulo
	private Geocache getGeocache(String nid) {
		if (this.geocaches != null) {
			for (Geocache gc : this.geocaches) {
				if (gc != null) {
					if (gc.nid.equals(nid)) {
						return gc;
					}
				}
			}
		}
		return null;
	}

	private String getDistanceString(float distanceMetters) {
		if (distanceMetters > 1000) {
			int distKms = (int) (distanceMetters / 1000);
			return distKms + " Kms.";
		} else {
			int distMetters = (int) distanceMetters;
			return distMetters + " m.";
		}
	}

	private void recalcularDistancia() {
		// Recalular distancia
		if (currentSelectedGeocache != null) {
			// De primeras deshabilitar el botón de capturar
			btnCaptureCallout.setEnabled(false);

			// Comprobar si el juego es geolocalizado para habilitar o no el
			// botón
			// de capturar
			if (geolocatedGame) {
				// Comprobar coordenadas del usuario
				if (this.lastLocation != null) {
					// Ya hay coordenadas
					// Comprobar coordenadas del geocache
					GeoPoint gpGeocache = currentSelectedGeocache.coordinates;
					if (gpGeocache != null) {
						// Hay coordenadas de geocache
						// Comprobar si se cumple la distancia mínima que hay
						// que estar para capturar el cache
						float distMetros = GeoPoint.calculateDistance(
								this.lastLocation, gpGeocache);
						if (distMetros <= app.MAX_DISTANCE_CAPTURE_GEOCACHE) {
							// Se cumple la distancia
							btnCaptureCallout.setEnabled(true); // Habilitar el botón de capturar
							this.lblMessageCallout.setText("Bravo, lo has encontrado, Abre el cofre"); // Poner mensaje

						} else {
							// No se cumple la distancia
							btnCaptureCallout.setEnabled(false); // Deshabilitar el botón de capturar
							if(distMetros > 3000){
								// Si la distancia es mayor a 3 kms, decirle que se acerque más
								this.lblMessageCallout.setText("Aún estás lejos, este cofre está a " + getDistanceString(distMetros) + " de donde te encuentras. Aproxímate a este lugar y lo podrás abrir"); // Poner mensaje
							}else{
								// Si la distancia es inferior a 3 km, decirle que no le queda mucho
								this.lblMessageCallout.setText("No te queda mucho, solo estás a " + getDistanceString(distMetros) + ", sigue buscando"); // Poner mensaje
							}
							
							
						}
					} else {
						// No hay coordenadas de geocache
						btnCaptureCallout.setEnabled(false); // Deshabilito
																// botón de
																// capturar
						this.lblMessageCallout
								.setText("El cache no tiene coordenadas"); // Poner
																			// que
																			// el
																			// geocache
																			// no
																			// tiene
																			// coordenadas
					}
				} else {
					// No hay coordenadas aún
					btnCaptureCallout.setEnabled(false); // Deshabilito botón de
															// capturar
					this.lblMessageCallout.setText("Esperando al gps..."); // Poner que no hay coordenadas aún
				}

			} else {
				// Dejarle jugar sin comprobar las coordenadas
				btnCaptureCallout.setEnabled(true); // Habilitar el botón de
													// capturar
				this.lblMessageCallout
						.setText("Abre el cofre y descubre si esconde el tesoro de Gully"); // Poner
																					// que
																					// el
																					// geocache
																					// no
																					// tiene
																					// coordenadas
			}
		}
	}

	
	
	private void mostrarMensajeNoTieneGPSActivado() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Parece que no tienes encendido el GPS. ¿Quieres habilitarlo?")
	           .setCancelable(false)
	           .setPositiveButton("Si", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}

}