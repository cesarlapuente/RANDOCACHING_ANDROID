package com.alborgis.ting.mainapp.common;

import android.content.Context;
import android.graphics.Color;

public class TINGColor {
	
	public static int getColorWithAlpha(int colorHex, int alphaToApply){
		int red =   (colorHex >> 16) & 0xFF;
		int green = (colorHex >> 8) & 0xFF;
		int blue =  (colorHex >> 0) & 0xFF;
		//int alpha = (colorHex >> 24) & 0xFF;
		return Color.argb(alphaToApply, red, green, blue);
	}
	
	
	public static int getColor(Context _ctx, int colorResId){
		return TINGColor.getColor(_ctx, colorResId, 255);
	}
	
	public static int getColor(Context _ctx, int colorResId, int alphaToApply){
		int color = _ctx.getResources().getColor(colorResId);
		int colorWithAlpha = TINGColor.getColorWithAlpha(color, 200);
		return colorWithAlpha;
	}
}
