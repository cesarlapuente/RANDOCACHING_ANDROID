package com.alborgis.ting.mainapp.common;

import android.content.Context;
import android.graphics.Typeface;

import com.alborgis.ting.base.utils.Util;

public class TINGTypeface {
	
	
	public static Typeface getGullyBlackTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyBlack.ttf");
	}
	public static Typeface getGullyBoldTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyBold.ttf");
	}
	public static Typeface getGullyBoldCondensedTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyBoldCondensed.ttf");
	}
	public static Typeface getGullyCondensedTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyCondensed.ttf");
	}
	public static Typeface getGullyLightTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyLight.ttf");
	}
	public static Typeface getGullyLightCondensedTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyLightCondensed.ttf");
	}
	public static Typeface getGullyNormalTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/GullyNormal.ttf");
	}
	public static Typeface getDroidSansNormalTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/DroidSans.ttf");
	}
	public static Typeface getDroidSansBoldTypeface(Context _ctx){
		return Util.getTypeface(_ctx, "fonts/DroidSans-Bold.ttf");
	}
}
