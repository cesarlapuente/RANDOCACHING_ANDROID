package com.alborgis.ting.mainapp;
import java.util.ArrayList;

import com.alborgis.ting.base.drupalsvcsapi.Drupal7ServicesClient;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.push_not.PushNotificationsClient;
import com.alborgis.ting.base.utils.Drupal7Security;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.ting.mainapp.common.map_layer_change.CapaBase;
import com.esri.android.map.bing.BingMapsLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.SpatialReference;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MainApp extends Application {
	
	// Nombre del fichero de la BBDD
	//public String NOMBRE_FICH_BBDD = "bd_app.sqlite"; No es necesario utilizar la BBDD por el momento

	// Identificador del recurso del fichero de BBDD
	//public int RES_ID_FICH_BBBDD = R.raw.bd_app; No es necesario utilizar la BBDD por el momento
	
	// Nombres de dominios y urls de servicios
	//public String URL_SERVIDOR = "http://travelingames.alborgis.com"; // Desarrollo (Sin la barra del final)
	public String URL_SERVIDOR = "http://ting.thetravelingames.com"; // Produccion (Sin la barra del final)

	// Nombre del endpoint
	public String ENDPOINT = "rest";
	
	// Tiempo m‡ximo que dura una sesi—n abierta (en segundos)
	public int MAX_SESION_LIFETIME = 1296000000; // 15 d’as
	
	// Constante del radio maximo de distancia para buscar juegos y destinos en el home (en metros)
	public int MAX_DISTANCE_SEARCH_HOME = 1000000 		/*50000*/;
	
	// Constante del radio maximo de distancia para buscar historias
	public int MAX_DISTANCE_SEARCH_STORIES = 1000000 		/*50000*/;
	
	// Constante de la distancia m‡xima a la que te tienes que encontrar para capturar un geocache (metros)
	public int MAX_DISTANCE_CAPTURE_GEOCACHE = /*20*/ 		1000000;
	
	// Constante del radio maximo de distancia para buscar pois y rutas en la ra (en metros)
	public int MAX_DISTANCE_SEARCH_GEOMETRIES_RA = 1000000 		/*50000*/;

	public long TIMEOUT_MAP_MILLISECONDS		=		30 * 1000;
	
	// Clave para el sdk de Wikitude (realidad aumentada)
	public static final String	WIKITUDE_SDK_KEY = "iOZvH7+suXnh1MgIpSZUGFtfjC3CeNOIAFzQ2+fPlh2jWhEI1mEBfQwxuTkvrtAcW6gcXv5weF+Xugqac0A9AGwjkKs7LnxIAyUoyaqHWM/HH6tED3isrNz34hIhCyxphp27HEk0waRTFy9PwcZd4t3cHfToQxE/VECTVlJYfRRTYWx0ZWRfX6X9y+Xy1PM2YMKs3+OeLua/S+YbEp1ZSC5dT3IodzYFPVk5MiWFDr1bk3RJxXKkuTzLH4tPqoTSi61sz7ORyCJ9S0t3U8CDuYa68+h90MUnnbOeVOtoPp5A3/6htt+6PS/Tebt2htNK8dCfKN8rNe9Ci/hip65/GsbKjmM3HQCVwX9KX9dieDr1WJu0dC0ivdvKJoT/UAFC3ZMBwC0pHf0Z0i8pOSD6t/To/VembonIk4PQlXpefZxH6WmkcmzqAYHQqeVHRa9WdilZoA0qOvUlUTEht1BaPt1fqxljeesGbtwaO4NGTwER5b5VUqotptLjGpOdDYELubnZjKd3r32iJ1zZnfd4L4w9GVpRXaoIzCMlAyFAVqbUoIZd4TfAuSd+1CKBvkNKb2+YSKf91vLVkwpPv7rRr/yRG3W5xksCL7L0tHA+1Wi/lCDuDKlJoH4iOXRbDz1j";

	// Clave para las push notifications
	public static final String GCM_SENDER_ID	=	"632405262523";
	
	// Define la el api key para los mapas de bing
	public String BING_MAPS_KEY = "AknzEuD2VRWfmk_HRnAq7SIBMNE6n3qXskFXUDhEW5kWjTlnec8I5dHjWht9_j_O";
	
	// User id de Arcgis 
	public String ARCGIS_USERID	=	"LY5rSp7PnAqA34EH";

	// Preferencias de la aplicaci—n
	public SharedPreferences preferencias;

	// Referencia espacial para los mapas
	public SpatialReference spatialReference = SpatialReference.create(102100);
		
	// Capa base que se encuentra seleccionada actualmente
	public CapaBase capaBaseSeleccionada = null;

	// Instancia del cliente para los servicios de drupal
	public Drupal7ServicesClient drupalClient;
	
	// Instancia para gestionar la seguridad con los servicios de drupal
	public Drupal7Security drupalSecurity;
	
	// Instancia para gestionar las push notifications
	public PushNotificationsClient pushNotificationsClient;
	
	// Clave secreta drupal
	public String drupalSecretKey = "G4U8r48G3dj6a4wL";
	
	// IV Drupal
	public String drupalIV = "GHSkhnI4783f72D9";
	
	// Array de claves Drupal
	public ArrayList<String> drupalKeys = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
	        add("X339w64o");				
	        add("f51y5nEh");					
	        add("P048s9FE");				
	        add("k6bSL8w2");					
	        add("7a6U185c");					
	        add("2q3I3f8t");					
	        add("tWQhk8bh");					
	        add("V3V141aW");					
	        add("WN33kdvi");					
	        add("z3I9Wrl9");	
	    }
	};
	
	
	
	// Claves para acceder a las cookies
	public String COOKIE_KEY_ID_USUARIO_LOGUEADO = "idUsuarioLogueado";
	public String COOKIE_KEY_ID_SESION_USUARIO_LOGUEADO = "idSesionUsuarioLogueado";
	public String COOKIE_KEY_NICK_USUARIO_LOGUEADO = "nickUsuarioLogueado";
	
	public String COOKIE_KEY_LAST_EMAIL_LOGGED_IN = "last_email_logged_in";
	public String COOKIE_KEY_LAST_PASS_LOGGED_IN = "last_pass_logged_in";
	
	
	
	
	
	// Nombre del almacen de cookies
	public String getPreferencesKEY(Context ctx) {
		return Util.getPackageName(ctx) + "_cookies";
	}

	// Evento al lanzarse la aplicaci—n. Poner aqu’ las inicializaciones
	public void onCreate(){
		this.inicializarAplicacion();

	}

	// MŽtodo que hace las primeras operaciones al arrancar la aplicaci—n
	public void inicializarAplicacion() {

		Context _ctx = getApplicationContext();
		
		/*
		 * No es necesario utilizar la bbdd
		 * // Obtener una copia de la BBDD con permisos de escritura
		boolean haySD = ExternalStorage.tieneSD(_ctx);
		if (haySD) {
			// Si tiene almacenamiento externo disponible, generamos la
			// estructura de directorios y ficheros
			ExternalStorage.comprobarDirectorioAppSD(this, RES_ID_FICH_BBBDD, NOMBRE_FICH_BBDD);
		} else {
			// No tiene tarjeta SD. Salir.
			return;
		}*/
		
		
		// Inicializar las cookies
		this.preferencias = _ctx.getSharedPreferences(
						this.getPreferencesKEY(_ctx), Context.MODE_PRIVATE);
				
		// Inicializar el cliente de Drupal para Servicios 3
		if (drupalClient == null) {
			Milog.d("Drupal client init...");
			drupalClient = new Drupal7ServicesClient(
					URL_SERVIDOR, 
					ENDPOINT, 
					Drupal7ServicesClient.SERVICES_MODULE_VERSION.V_3_3, 
					MAX_SESION_LIFETIME, 
					this.preferencias);
			
		}
		// Inicializar la seguridad de Drupal para Servicios 3
		if (this.drupalSecurity == null) {
			Milog.d("Drupal security init...");
			this.drupalSecurity = new Drupal7Security(this.drupalSecretKey, this.drupalIV, this.drupalKeys);
		}
		
		// Inicializar el cliente de push notifications
		if(this.pushNotificationsClient == null){
			Milog.d("Push notifications client init...");
			pushNotificationsClient = new PushNotificationsClient(this, preferencias, drupalClient, GCM_SENDER_ID);
		}

		// Poner el id de Arcgis
		ArcGISRuntime.setClientId(ARCGIS_USERID);
		
		// Colocar la capa base por defecto para los mapas
		this.setCapaBaseSeleccionadaPorDefecto();

	}

	
	public void setCapaBaseSeleccionadaPorDefecto() {
		CapaBase capa = new CapaBase(this);
		capa.setIdentificador(CapaBase.CAPA_BASE_TIPO_BING_AERIAL);
		capa.setEtiqueta("Bing Aerial");
		capa.setClaseCapaBase(BingMapsLayer.class);
		this.capaBaseSeleccionada = capa;
	}

}
