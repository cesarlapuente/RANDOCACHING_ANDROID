package com.alborgis.randocaching.mainapp.home;

import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.common.help.TINGTip;
import com.alborgis.randocaching.mainapp.common.help.TINGTipWizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;


public class HomeTipManager {
	
	// Muestra el asistente para el home
	public static void showTipsForHomeStartup(Context ctx, SharedPreferences prefs, boolean forceShow){
		TINGTip tip1 = new TINGTip();
		tip1.title = ctx.getString(R.string.home_tip_title_juegos_y_destinos);
		tip1.message = ctx.getString(R.string.home_tip_message_utiliza_la_cuadricula_para_ver_los_juegos_y_destinos);
		tip1.gravity = Gravity.BOTTOM;
		
		TINGTip tip2 = new TINGTip();
		tip2.title = ctx.getString(R.string.home_tip_title_ayuda_y_logros);
		tip2.message = ctx.getString(R.string.home_tip_message_pulsa_este_icono_para_ver_mas_secciones);
		tip2.resIdImage = R.drawable.btn_desliza_menu_inferior_normal;
		tip2.gravity = Gravity.TOP;
		
		
		TINGTip tip3 = new TINGTip();
		tip3.title = ctx.getString(R.string.home_tip_title_toca_y_diviertete);
		tip3.message = ctx.getString(R.string.home_tip_message_toca_sobre_un_juego_para_comenzar_a_jugar);
		tip3.gravity = Gravity.CENTER;
		
		
		TINGTipWizard wizard = new TINGTipWizard(ctx, prefs);
		wizard.addTip(tip1);
		wizard.addTip(tip2);
		wizard.addTip(tip3);
		
		wizard.showWizard(2000, forceShow);
		
	}
}
