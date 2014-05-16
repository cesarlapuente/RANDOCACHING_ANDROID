package com.alborgis.ting.mainapp.games.geocache_full;

import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.help.TINGTip;
import com.alborgis.ting.mainapp.common.help.TINGTipWizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;


public class GeocacheFullTipManager {
	
	
	public static void showTipsForMapStartup(Context ctx, SharedPreferences prefs, boolean forceShow, boolean modoDemo, boolean geolocatedGame){
		
		TINGTipWizard wizard = new TINGTipWizard(ctx, prefs);
		
		// Sacar unos mensajes si est‡ en modo demo y otros si est‡ en modo normal
		if(modoDemo){
			TINGTip tip1 = new TINGTip();
			tip1.title = ctx.getString(R.string.full_tip_title_modo_demo);
			tip1.message = ctx.getString(R.string.full_tip_message_estas_jugando_en_modo_demo);
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
			
			TINGTip tip2 = new TINGTip();
			tip2.title = ctx.getString(R.string.full_tip_title_objetivo);
			tip2.message = ctx.getString(R.string.full_tip_message_tu_objetivo_es_encontrar_todos_los_tesoros_escondidos_por_gully);
			tip2.gravity = Gravity.BOTTOM;
			wizard.addTip(tip2);
		}else{
			TINGTip tip1 = new TINGTip();
			tip1.title = ctx.getString(R.string.full_tip_title_bienvenido_al_juego);
			tip1.message = ctx.getString(R.string.full_tip_message_bienvenido_al_juego_del_geocaching_full);
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
		}
		
		// Sacar unos mensajes si el el juego es geolocated
		if(geolocatedGame){
			TINGTip tip1 = new TINGTip();
			tip1.title = ctx.getString(R.string.full_tip_title_juego_outdoor);
			tip1.message = ctx.getString(R.string.full_tip_message_este_juego_es_outdoor);
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
		}else{
			TINGTip tip1 = new TINGTip();
			tip1.title = ctx.getString(R.string.full_tip_title_juego_indoor);
			tip1.message = ctx.getString(R.string.full_tip_message_este_juego_es_indoor);
			tip1.gravity = Gravity.BOTTOM;
			wizard.addTip(tip1);
		}
		
		TINGTip tip3 = new TINGTip();
		tip3.title = ctx.getString(R.string.full_tip_title_brujula);
		tip3.message = ctx.getString(R.string.full_tip_message_si_te_sientes_perdido_puedes_consultar_la_brujula);
		tip3.resIdImage = R.drawable.btn_realidad_aumentada_en_mapa_normal;
		tip3.gravity = Gravity.BOTTOM;
		wizard.addTip(tip3);
		
		TINGTip tip4 = new TINGTip();
		tip4.title = ctx.getString(R.string.full_tip_title_seleccionar_un_cofre);
		tip4.message = ctx.getString(R.string.full_tip_message_para_abrir_un_cofre_toca_sobre_uno_de_los_iconos_del_mapa);
		tip4.resIdImage = R.drawable.btn_poi_cache_normal;
		tip4.gravity = Gravity.BOTTOM;
		wizard.addTip(tip4);
		
		wizard.showWizard(1000, forceShow);
		
	}
	
	
	public static void showTipsBeforeOpenCofre(Context ctx, SharedPreferences prefs, boolean forceShow){
		TINGTip tip1 = new TINGTip();
		tip1.title = ctx.getString(R.string.full_tip_title_abrir_un_cofre);
		tip1.message = ctx.getString(R.string.full_tip_message_cuando_seleccionas_un_cofre_podras_abrirlo_desde_su_bocadillo);
		tip1.resIdImage = R.drawable.btn_abrir_cofre_normal;
		tip1.gravity = Gravity.BOTTOM;
		
		
		TINGTip tip2 = new TINGTip();
		tip2.title = ctx.getString(R.string.full_tip_title_abrir_un_cofre);
		tip2.message = ctx.getString(R.string.full_tip_message_si_el_boton_de_abrir_cofre_esta_desactivado_no_estas_cerca_del_lugar);
		tip2.resIdImage = R.drawable.btn_abrir_cofre_disabled;
		tip2.gravity = Gravity.BOTTOM;
		
		TINGTip tip3 = new TINGTip();
		tip3.title = ctx.getString(R.string.full_tip_title_abrir_un_cofre);
		tip3.message = ctx.getString(R.string.full_tip_message_la_distancia_a_la_que_te_encuentras_de_un_cofre_aparece_sobre_el_boton_abrir_cofre);
		tip3.gravity = Gravity.BOTTOM;
		
		TINGTipWizard wizard = new TINGTipWizard(ctx, prefs);
		wizard.addTip(tip1);
		wizard.addTip(tip2);
		wizard.addTip(tip3);
		
		wizard.showWizard(2000, forceShow);
	}
	
}
