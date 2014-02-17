package com.alborgis.ting.mainapp.games.geocache_one;

import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.help.TINGTip;
import com.alborgis.ting.mainapp.common.help.TINGTipWizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;


public class GeocacheOneTipManager {
	
	
	public static void showTipsForMapStartup(Context ctx, SharedPreferences prefs, boolean forceShow, boolean modoDemo, boolean geolocatedGame){
		
		TINGTipWizard wizard = new TINGTipWizard(ctx, prefs);
		
		// Sacar unos mensajes si está en modo demo y otros si está en modo normal
		if(modoDemo){
			TINGTip tip1 = new TINGTip();
			tip1.title = "MODO DEMO";
			tip1.message = "¡Bienvenido al juego del Geocaching One! Estás jugando en modo DEMO. Recuerda que puedes jugar con tu cuenta de Travel in Games o de Facebook para poder obtener logros, ganar premios y guardar la partida";
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
			
			TINGTip tip2 = new TINGTip();
			tip2.title = "OBJETIVO";
			tip2.message = "Tu objetivo es encontrar el tesoro que Gully ha escondido en alguno de los cofres repartidos por la zona que ves sobre el mapa";
			tip2.gravity = Gravity.BOTTOM;
			wizard.addTip(tip2);
		}else{
			TINGTip tip1 = new TINGTip();
			tip1.title = "¡BIENVENIDO AL JUEGO!";
			tip1.message = "¡Bienvenido al juego del Geocaching One! Tu objetivo es encontrar el tesoro que Gully ha escondido en alguno de los cofres repartidos por la zona que ves sobre el mapa";
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
		}
		
		// Sacar unos mensajes si el el juego es geolocated
		if(geolocatedGame){
			TINGTip tip1 = new TINGTip();
			tip1.title = "JUEGO OUTDOOR";
			tip1.message = "Este juego es 'Outdoor'. Quiere decir que para jugar esta partida necesitarás estar cerca de los cofres para poderlos abrir";
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
		}else{
			TINGTip tip1 = new TINGTip();
			tip1.title = "JUEGO INDOOR";
			tip1.message = "Este juego es 'Indoor'. Quiere decir que puedes estar en cualquier lugar para abrir los cofres y encontrar el tesoro";
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
		}
		
		

		TINGTip tip2 = new TINGTip();
		tip2.title = "INTENTOS";
		tip2.message = "En la parte inferior de la pantalla verás los intentos que tienes. Cada vez que abres un cofre pierdes un intento. Los cofres ya abiertos no te quitan intentos";
		tip2.resIdImage = R.drawable.icono_cofre_por_abrir;
		tip2.gravity = Gravity.TOP;
		wizard.addTip(tip2);
		
		TINGTip tip3 = new TINGTip();
		tip3.title = "BRÚJULA";
		tip3.message = "Si te sientes perdido, siempre puedes consultar la brújula. Te permitirá mostrar los cofres sobre la cámara, y de esta manera orientarte";
		tip3.resIdImage = R.drawable.btn_realidad_aumentada_en_mapa_normal;
		tip3.gravity = Gravity.BOTTOM;
		wizard.addTip(tip3);
		
		TINGTip tip4 = new TINGTip();
		tip4.title = "SELECCIONAR UN COFRE";
		tip4.message = "Para abrir un cofre toca sobre uno de los iconos del mapa";
		tip4.resIdImage = R.drawable.btn_poi_cache_normal;
		tip4.gravity = Gravity.BOTTOM;
		wizard.addTip(tip4);
		
		wizard.showWizard(1000, forceShow);
		
	}
	
	
	public static void showTipsBeforeOpenCofre(Context ctx, SharedPreferences prefs, boolean forceShow){
		TINGTip tip1 = new TINGTip();
		tip1.title = "ABRIR UN COFRE";
		tip1.message = "Cuando seleccionas un cofre aparecerá un bocadillo donde verás un botón que te permite abrir el cofre.";
		tip1.resIdImage = R.drawable.btn_abrir_cofre_normal;
		tip1.gravity = Gravity.BOTTOM;
		
		
		TINGTip tip2 = new TINGTip();
		tip2.title = "ABRIR UN COFRE";
		tip2.message = "Si el botón de 'Abrir Cofre' está desactivado quiere decir que no estás lo suficientemente cerca del lugar. ¡Tendrás que acercarte más!";
		tip2.resIdImage = R.drawable.btn_abrir_cofre_disabled;
		tip2.gravity = Gravity.BOTTOM;
		
		TINGTip tip3 = new TINGTip();
		tip3.title = "ABRIR UN COFRE";
		tip3.message = "La distancia a la que te encuentras de un cofre aparece sobre el botón de 'Abrir cofre'. Tu posición en el mapa está indicada por el círculo azul. Cuando estés cerca del cofre, podrás abrirlo.";
		tip3.gravity = Gravity.BOTTOM;
		
		TINGTipWizard wizard = new TINGTipWizard(ctx, prefs);
		wizard.addTip(tip1);
		wizard.addTip(tip2);
		wizard.addTip(tip3);
		
		wizard.showWizard(2000, forceShow);
	}
	
}
