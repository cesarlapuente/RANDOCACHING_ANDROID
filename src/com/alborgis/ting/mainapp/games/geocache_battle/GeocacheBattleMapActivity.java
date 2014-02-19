package com.alborgis.ting.mainapp.games.geocache_battle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.BundleEarned;
import com.alborgis.ting.base.model.GeoPoint;
import com.alborgis.ting.base.model.Geocache;
import com.alborgis.ting.base.model.GeocachesGame;
import com.alborgis.ting.base.model.Slot;
import com.alborgis.ting.base.model.Geocache.GeocacheBattleCaptureListener;
import com.alborgis.ting.base.model.Geocache.GeocacheBattleListListener;
import com.alborgis.ting.base.model.GeocachesGame.GeocacheGameItemListener;
import com.alborgis.ting.base.model.Slot.SlotLeaveListener;
import com.alborgis.ting.base.model.Poi;
import com.alborgis.ting.base.utils.DataConection;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.MessageDialog.MessageDialogListener;
import com.alborgis.ting.mainapp.common.TINGSound;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.common.map_layer_change.CapaBase;
import com.alborgis.ting.mainapp.common.push.TINGPushHandler.EVENTS;
import com.alborgis.ting.mainapp.common.push.TINGPushHandler.GEOCACHE_GAME_BATTLE_EVENTS;
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

public class GeocacheBattleMapActivity extends Activity implements
		LocationListener, GeocacheBattleListListener,
		GeocacheBattleCaptureListener {
	
	public static final String PARAM_KEY_GAME_NID = "key_game_nid";
	public static final String PARAM_KEY_SLOT_NID = "key_slot_nid";

	public static boolean isVisible = false; // Esta variable va cambiando si el activity est� visible o no
	
	String nidGame;
	String nidSlot;
	
	GeocachesGame game;

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
	Button btnAbandonar;

	GraphicsLayer capaGeometrias;
	
	LinearLayout layoutIntentos;

	ArrayList<Geocache> geocaches;
	Geocache currentSelectedGeocache;

	GeoPoint lastLocation;
	
	BroadcastReceiver pushReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			String data = extras.getString("data");
	       	if(data != null){
	      		try {
	      			JSONObject dataDic = new JSONObject(data);
	               	String event = dataDic.getString("event");
	               	String slot_nid = dataDic.getString("slot_nid");
	               	String title = dataDic.getString("title");
	               	String message = extras.getString("message");
	               	if(slot_nid != null && slot_nid.equals(nidSlot)){
	               		// Comprobar por el evento que nos env�an y mostrar notificacion
		               	if(event.equals(GEOCACHE_GAME_BATTLE_EVENTS.USER_FOUND_TREASURE)){
		               		// Si el evento es que un usuario ha encontrado el tesoro...
		               		// Mostrar mensaje de que ha perdido la partida, s�lo si el activity est� visible
		               		if(isVisible){
		               			MessageDialog.showMessageWith1Buttons(GeocacheBattleMapActivity.this, title, message, "Finalizar", new MessageDialogListener() {
									public void onPositiveButtonClick(MessageDialog dialog) {
										dialog.dismiss();
									}
									public void onNegativeButtonClick(MessageDialog dialog) {
										dialog.dismiss();
										finish();
									}
								});
		               		}
		               		
		               		
		               	}else if(event.equals(EVENTS.USER_JOINED_TO_SLOT)){
		               		// Si el evento es que un usuario se ha unido a una partida...
		               		if(isVisible){
		               			Toast toast = Toast.makeText(GeocacheBattleMapActivity.this, message, Toast.LENGTH_SHORT);
			               		toast.setGravity(Gravity.TOP, 0, 0);
			               		toast.show();
		               		}
		               		
		               		
		               	}else if(event.equals(EVENTS.USER_START_SLOT_PLAY)){
		               		// Si el evento es que un usuario ha comenzado jugando la partida...
		               		if(isVisible){
		               			Toast toast = Toast.makeText(GeocacheBattleMapActivity.this, message, Toast.LENGTH_SHORT);
			               		toast.setGravity(Gravity.TOP, 0, 0);
			               		toast.show();
		               		}
		               		
		               	}else if(event.equals(EVENTS.USER_LEAVED_SLOT)){
		               		// Si el evento es que un usuario ha abandonado la partida...
		               		if(isVisible){
		               			Toast toast = Toast.makeText(GeocacheBattleMapActivity.this, message, Toast.LENGTH_SHORT);
			               		toast.setGravity(Gravity.TOP, 0, 0);
			               		toast.show();
		               		}
		               		
		               	}
	               	}      	
	      		} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	              	
	              	
	        }
	    }
	};
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mod_geocache_battle__mapa_geocaches);

		this.app = (MainApp) getApplication();
		
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );

		Bundle b = getIntent().getExtras();
		if (b != null) {
			nidGame = b.getString(PARAM_KEY_GAME_NID);
			nidSlot = b.getString(PARAM_KEY_SLOT_NID);
		}

		capturarControles();
		escucharEventos();
		inicializarForm();
	}

	protected void onPause() {
		super.onPause();
		isVisible = false; // Poner que el activity est� no visible (en background)
		if (mapView != null) {
			mapView.pause();
		}
	}

	public void onResume() {
		super.onResume();
		isVisible = true; // Poner que el activity est� visible
		if (mapView != null) {
			mapView.unpause();
		}
	}

	public void finish() {
		super.finish();
		unregisterReceiver(pushReceiver);
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
		btnAbandonar = (Button)findViewById(R.id.btnAbandonar);
		layoutIntentos = (LinearLayout)findViewById(R.id.layoutIntentos);
	}

	public void escucharEventos() {

		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(GeocacheBattleMapActivity.this, MainActivity.class);
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
					Intent intent = new Intent(GeocacheBattleMapActivity.this, RAActivity.class);
					intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING, "Realidad aumentada");
					intent.putExtra(RAActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "wikitudeWorld" + File.separator + "index.html");
					intent.putExtra(RAActivity.PARAM_KEY_JSON_POI_DATA, poisJSON );
					startActivity(intent);
					overridePendingTransition(R.anim.anim_push_enter, R.anim.anim_push_exit);
				}else{
					Toast.makeText(GeocacheBattleMapActivity.this, "No hay geocach�s que mostrar", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnAbandonar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				MessageDialog.showMessageWith2Buttons(GeocacheBattleMapActivity.this, "�Abandonar la partida?", "�Seguro que deseas abandonar esta partida?", "Si",  "No", new MessageDialogListener() {
					public void onPositiveButtonClick(final MessageDialog dialog) {
						LoadingDialog.showLoading(GeocacheBattleMapActivity.this);
						Slot.leaveSlot(nidSlot, app.drupalClient, app.drupalSecurity, new SlotLeaveListener() {
							public void onSlotUserLeaved(String uid, String nidSlot) {
								LoadingDialog.hideLoading();
								dialog.dismiss();
								Toast.makeText(GeocacheBattleMapActivity.this, "Abandonaste la partida", Toast.LENGTH_SHORT).show();
								finish();
							}
							public void onSlotUserLeaveError(String error) {
								LoadingDialog.hideLoading();
								dialog.dismiss();
								MessageDialog.showMessage(GeocacheBattleMapActivity.this, "Error", error);
							}
						});
					}
					public void onNegativeButtonClick(MessageDialog dialog) {
						dialog.dismiss();
					}
				});
			}
		});
		
		
		// Registrar eventos al recibir notificaciones
		this.registerReceiver(pushReceiver, new IntentFilter("MyGCMMessageReceived"));

		// Al tocar un punto en el mapa
		this.mapView.setOnSingleTapListener(new OnSingleTapListener() {

			private static final long serialVersionUID = 1L;

			public void onSingleTap(float x, float y) {
				// Si el mapa no est� cargado, salir
				if (!mapView.isLoaded()) {
					return;
				}

				// Recuperamos los gr�ficos
				int[] graphicsIDS = capaGeometrias.getGraphicIDs(x, y, 8);
				if (graphicsIDS != null && graphicsIDS.length > 0) {
					Milog.d("Hay graficos en la zona pulsada");
					int targetId = graphicsIDS[0];

					Graphic gr = capaGeometrias.getGraphic(targetId);

					if (gr != null) {
						Map<String, Object> attrs = gr.getAttributes();
						String nid = (String) attrs.get("nid");

						// Mostrar ayuda contextual
						//GeocacheOneTipManager.showTipsBeforeOpenCofre(GeocacheBattleMapActivity.this, app.preferencias, false);
						
						// Establecer el contenido del callout y mostrarlo
						View contenidoCallout = getViewForCalloutWithThisData(nid);
						callout.animatedShow(
								mapView.toMapPoint(new Point(x, y)),
								contenidoCallout);

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
					ls.setLocationListener(GeocacheBattleMapActivity.this);
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
		// Poner tipograf�as
		Typeface tfLight = TINGTypeface.getGullyLightTypeface(this);

		tvTitle.setTypeface(tfLight);

		// Comprobar si tiene el gps activado
		LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
	    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	    	// Si no lo tiene activado, avisarle
	        mostrarMensajeNoTieneGPSActivado();
	    }
		
		// Inicializar la capa donde ir�n los geocaches
		capaGeometrias = new GraphicsLayer();
		mapView.addLayer(capaGeometrias);

		// Inicializar la capa base
		ponerCapaBase();
	}
	
	private void updateView(){
		// Poner el t�tulo de la ventana
		if(game.title != null){
			this.tvTitle.setText(game.title.toUpperCase());
		}
		
			
		// Actualizar intentos
		updateAttempts();
			
		// Mostrar el asistente de ayuda
		//GeocacheOneTipManager.showTipsForMapStartup(this, app.preferencias, false, demoPlaying, geolocatedGame);
	}

	private void recargarDatos() {

		// Mostrar el panel cargando
		showLoading(true);
		while (!DataConection.hayConexion(this)) {
			// Mientras no haya conexi�n espero...
		}
		
		// Cargar el  geocache Game
		GeocachesGame.getGame(nidGame, app.drupalClient, app.drupalSecurity, new GeocacheGameItemListener() {
			public void onGeocacheGameItemLoad(GeocachesGame geocachesGame) {
				// Asigno el geocacheGame
				game = geocachesGame;
				// Actualizar intentos
				updateAttempts();
				// Actualizar el view
				updateView();
				// Cargar los geocaches una vez que tengo conexi�n
				Geocache.battleList(nidGame, nidSlot, 0, 0, 300000, 0, 0, app.drupalClient,
						app.drupalSecurity, GeocacheBattleMapActivity.this);
			}
			
			@Override
			public void onGeocacheGameItemError(String error) {
				showLoading(false);
				MessageDialog.showMessage(GeocacheBattleMapActivity.this, "Error", "Error al cargar el juego");
			}
		});
		
		
	}

	public void onGeocacheBattleListLoad(ArrayList<Geocache> items) {
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

			centrarEnExtentCapa(capaGeometrias);
		}

		showLoading(false);
	}

	public void onGeocacheBattleListError(String error) {
		Toast.makeText(this, "Error en la carga de geocaches",
				Toast.LENGTH_SHORT).show();
		showLoading(false);
	}

	public void onGeocacheBattleCaptureSuccess(BundleEarned bundleEarned, String nidGeocache, int statusCode, int remainAttempts) {
		
		// Actualizar el n�mero de intentos restantes
		this.game.remainAttempts = remainAttempts;
		updateAttempts();
		
		if (statusCode == 1) {
			Geocache geo = getGeocache(nidGeocache);
			String title = geo.title;
			String body = geo.body;
			String buttonText = "Finalizar";
			GeocacheBattleResponseDialog popup = new GeocacheBattleResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheBattleResponseDialog.STATE_GULLY_TESORO_ENCONTRADO, true, nidGame, new GeocacheBattleResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					finish();
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.win);
			/*MessageDialog.showMessage(
					this,
					"Capturado",
					"Geocache capturado correctamente, descubierto el tesoro. Juego acabado. Te quedan "
							+ remainAttempts);*/
		} else if (statusCode == 2) {
			Geocache geo = getGeocache(nidGeocache);
			String title = geo.title;
			String body = geo.body;
			String buttonText = "Prueba otro lugar";
			GeocacheBattleResponseDialog popup = new GeocacheBattleResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheBattleResponseDialog.STATE_GULLY_COFRE_VACIO, false, nidGame, new GeocacheBattleResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.empty);
			/*MessageDialog.showMessage(this, "Geocach� vac�o",
					"El geocach� esta vacio. Pierdes un intento. Te quedan "
							+ remainAttempts);*/
		} else if (statusCode == 3) {
			Geocache geo = getGeocache(nidGeocache);
			String title = geo.title;
			String body = "Has agotado los intentos para encontrar el tesoro.\n" + geo.body;
			String buttonText = "Finalizar";
			GeocacheBattleResponseDialog popup = new GeocacheBattleResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheBattleResponseDialog.STATE_GULLY_AGOTADOS_INTENTOS, true, nidGame, new GeocacheBattleResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					finish();
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.game_over);
			/*MessageDialog.showMessage(this, "Agotados intentos",
					"Has agotado los intentos para encontrar el tesoro. Game over. Te quedan "
							+ remainAttempts);*/
		} else if (statusCode == 4) {
			Geocache geo = getGeocache(nidGeocache);
			String title = geo.title;
			String body = geo.body;
			String buttonText = "Prueba otro lugar";
			GeocacheBattleResponseDialog popup = new GeocacheBattleResponseDialog(this, this, app, title, body, buttonText, bundleEarned, GeocacheBattleResponseDialog.STATE_GULLY_COFRE_VACIO, false, nidGame, new GeocacheBattleResponseDialog.GeocacheOneResponseDialogListener() {
				public void onGeocacheOneResponseDialogDismiss() {
					
				}
			});
			popup.show();
			TINGSound.playSound(this, R.raw.empty);
			/*MessageDialog.showMessage(this, "Geocache consultado",
					"Ya has consultado este geocache y no ten�a el tesoro. Te quedan "
							+ remainAttempts);*/
		} else {
			MessageDialog.showMessage(this, "Error", "Error desconocido");
		}

		showLoading(false);
	}

	public void onGeocacheBattleCaptureError(String error) {
		Milog.d("Error al capturar geocache: " + error);
		MessageDialog.showMessage(this, "Error", "No se puede capturar el geocache. Es posible que la partida haya finalizado");
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

		// Correcci�n, para que no cambie la capa base cuando la seleccionada es
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
			// Desactivar el bot�n de capturar por defecto
			btnCaptureCallout.setEnabled(false);

			// Poner un mensaje de espera
			lblMessageCallout.setText("Esperando GPS...");
			
			// Recalcular la distancia
			recalcularDistancia();
		}

		// Escuchar el evento del bot�n capturar
		this.btnCaptureCallout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (DataConection.hayConexion(GeocacheBattleMapActivity.this)) {
					showLoading(true);
					Geocache.battleCapture(nid, nidGame, nidSlot, app.drupalClient,
							app.drupalSecurity, GeocacheBattleMapActivity.this);
					callout.animatedHide();
				} else {
					MessageDialog.showMessage(GeocacheBattleMapActivity.this,
							"No hay conexi�n",
							"No dispones de conexi�n a Internet. Int�ntalo de nuevo");
				}

			}
		});

		return this.calloutContentView;
	}

	private void showLoading(boolean show) {
		if (show) {
			LoadingDialog.showLoading(this);
		} else {
			LoadingDialog.hideLoading();
		}
	}

	// Obtiene un geocache de la colecci�n en base al nid que se le env�a. Si no
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
			// De primeras deshabilitar el bot�n de capturar
			btnCaptureCallout.setEnabled(false);

			// Comprobar si el juego es geolocalizado para habilitar o no el
			// bot�n
			// de capturar
			if (game.geolocated) {
				// Comprobar coordenadas del usuario
				if (this.lastLocation != null) {
					// Ya hay coordenadas
					// Comprobar coordenadas del geocache
					GeoPoint gpGeocache = currentSelectedGeocache.coordinates;
					if (gpGeocache != null) {
						// Hay coordenadas de geocache
						// Comprobar si se cumple la distancia m�nima que hay
						// que estar para capturar el cache
						float distMetros = GeoPoint.calculateDistance(
								this.lastLocation, gpGeocache);
						if (distMetros <= app.MAX_DISTANCE_CAPTURE_GEOCACHE) {
							// Se cumple la distancia
							btnCaptureCallout.setEnabled(true); // Habilitar el bot�n de capturar
							this.lblMessageCallout.setText("Bravo, lo has encontrado, Abre el cofre"); // Poner mensaje

						} else {
							// No se cumple la distancia
							btnCaptureCallout.setEnabled(false); // Deshabilitar el bot�n de capturar
							if(distMetros > 3000){
								// Si la distancia es mayor a 3 kms, decirle que se acerque m�s
								this.lblMessageCallout.setText("A�n est�s lejos, este cofre est� a " + getDistanceString(distMetros) + " de donde te encuentras. Aprox�mate a este lugar y lo podr�s abrir"); // Poner mensaje
							}else{
								// Si la distancia es inferior a 3 km, decirle que no le queda mucho
								this.lblMessageCallout.setText("No te queda mucho, solo est�s a " + getDistanceString(distMetros) + ", sigue buscando"); // Poner mensaje
							}
							
							
						}
					} else {
						// No hay coordenadas de geocache
						btnCaptureCallout.setEnabled(false); // Deshabilito
																// bot�n de
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
					// No hay coordenadas a�n
					btnCaptureCallout.setEnabled(false); // Deshabilito bot�n de
															// capturar
					this.lblMessageCallout.setText("Esperando al gps..."); // Poner que no hay coordenadas a�n
				}

			} else {
				// Dejarle jugar sin comprobar las coordenadas
				btnCaptureCallout.setEnabled(true); // Habilitar el bot�n de
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
	
	private void updateAttempts(){
		int intentosRestantes = game.remainAttempts;
		int intentosGastados = game.totalAttempts - game.remainAttempts;
		
		// Limpiar el layout
		this.layoutIntentos.removeAllViews();
		
		// Poner primero los intentos agotados
		for(int i=0; i<intentosGastados; i++){
			ImageView imgView = new ImageView(this);
			imgView.setImageResource(R.drawable.icono_cofre_abierto);
			this.layoutIntentos.addView(imgView);
		}
		
		// Poner luego los intentos restantes
		for(int i=0; i<intentosRestantes; i++){
			ImageView imgView = new ImageView(this);
			imgView.setImageResource(R.drawable.icono_cofre_por_abrir);
			this.layoutIntentos.addView(imgView);
		}
	}
	
	
	private void mostrarMensajeNoTieneGPSActivado() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Parece que no tienes encendido el GPS. �Quieres habilitarlo?")
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