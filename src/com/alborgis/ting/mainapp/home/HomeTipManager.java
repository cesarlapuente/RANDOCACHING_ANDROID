package com.alborgis.ting.mainapp.home;

import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.help.TINGTip;
import com.alborgis.ting.mainapp.common.help.TINGTipWizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;


public class HomeTipManager {
	
	// Muestra el asistente para el home
	public static void showTipsForHomeStartup(Context ctx, SharedPreferences prefs, boolean forceShow){
		TINGTip tip1 = new TINGTip();
		tip1.title = "JUEGOS Y DESTINOS";
		tip1.message = "Utiliza la cuadrícula para ver los distintos JUEGOS Y DESTINOS";
		tip1.gravity = Gravity.BOTTOM;
		
		TINGTip tip2 = new TINGTip();
		tip2.title = "AYUDA Y LOGROS";
		tip2.message = "Pulsa este icono para ver tu PASAPORTE y más secciones";
		tip2.resIdImage = R.drawable.btn_desliza_menu_inferior_normal;
		tip2.gravity = Gravity.TOP;
		
		
		TINGTip tip3 = new TINGTip();
		tip3.title = "TOCA Y DIVIÉRTETE";
		tip3.message = "Toca sobre un JUEGO para comenzar a jugar";
		tip3.gravity = Gravity.CENTER;
		
		
		TINGTipWizard wizard = new TINGTipWizard(ctx, prefs);
		wizard.addTip(tip1);
		wizard.addTip(tip2);
		wizard.addTip(tip3);
		
		wizard.showWizard(2000, forceShow);
		
	}
}
