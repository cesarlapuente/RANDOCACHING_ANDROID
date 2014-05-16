package com.alborgis.ting.mainapp.common.help;

import java.util.ArrayList;

import com.alborgis.ting.base.utils.Util;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.help.TINGTipDialog.TipDialogListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

public class TINGTipWizard {
	
	private SharedPreferences prefs;
	private ArrayList<TINGTip> tips;
	private Context context;
	private int nextTipIndex = 0;
	
	// Crea un nuevo asistente
	public TINGTipWizard(Context _ctx, SharedPreferences _prefs){
		tips = new ArrayList<TINGTip>();
		context = _ctx;
		prefs = _prefs;
	}
	
	// A–ade un nuevo tip al asistente
	public void addTip(TINGTip tip){
		tips.add(tip);
	}
	
	private String generateWizardId(){
		if(tips != null){
			StringBuilder sb = new StringBuilder();
			for(TINGTip tip : tips){
				if(tip != null && tip.title != null){
					sb.append(tip.title);
				}
				
			}
			String allTitles = sb.toString();
			return Util.md5(allTitles);
		}
		return "";
	}
	
	// Muestra el asistente
	public void showWizard(long afterDelayMillis, boolean forceShow){
		// Generar un id œnico para el asistente
		String idWizard = generateWizardId();

		// Comprobar si el asistente ya ha sido mostrado
		boolean alreadyShown = prefs.getBoolean(idWizard, false);
		
		// Si se fuerza a mostrar y adem‡s ya ha sido mostrado, lo que hay que hacer es eliminar la cookie que nos indica si ya ha sido mostrado
		if(forceShow && alreadyShown){
			// Almacenar que no lo hemos mostrado
			SharedPreferences.Editor prefsEdit = prefs.edit();
			prefsEdit.putBoolean(idWizard, false);
			prefsEdit.commit();
		}
		
		if(!alreadyShown){
			// Mostrar el asistente de ayuda
			if(tips.size() > 0){
				final int firstTipIndex = 0;
				final TINGTip firstTip = tips.get(firstTipIndex);
				nextTipIndex = firstTipIndex;
				
				// Mostrar el siguiente consejo tras los instantes que nos digan
				new Handler().postDelayed(new Runnable() {
					public void run() {
						showTip(firstTip, firstTipIndex);
						
					}
				}, afterDelayMillis);
			}
			
			// Almacenar que ya lo hemos mostrado
			SharedPreferences.Editor prefsEdit = prefs.edit();
			prefsEdit.putBoolean(idWizard, true);
			prefsEdit.commit();
		}
	}
	
	public void showWizard(){
		showWizard(0, false);
	}
	
	// Muestra un tip
	private void showTip(TINGTip tip, int index){
		
		// Configurar el mensaje en funci—n del ’ndice
		String txtSiguiente = context.getString(R.string.tips_siguiente);
		if(index == (tips.size() - 1) ){
			// Si es el œltimo consejo
			txtSiguiente = context.getString(R.string.tips_a_jugar);
		}
		
		TINGTipDialog.showMessageWith2Buttons(context, tip.title, tip.message, txtSiguiente, context.getString(R.string.tips_ocultar_ayuda), tip.resIdIcon, tip.resIdImage, tip.gravity, new TipDialogListener() {
			@Override
			public void onPositiveButtonClick(TINGTipDialog dialog) {
				dialog.dismiss();
				nextTipIndex++;
				if(nextTipIndex < tips.size()){
					final TINGTip nextTip = tips.get(nextTipIndex);
					
					// Mostrar el siguiente consejo tras unos instantes
					new Handler().postDelayed(new Runnable() {
						public void run() {
							showTip(nextTip, nextTipIndex);
						}
					}, 500);
					
				}
			}
			@Override
			public void onNegativeButtonClick(TINGTipDialog dialog) {
				dialog.dismiss();
			}
		});
	}
}
