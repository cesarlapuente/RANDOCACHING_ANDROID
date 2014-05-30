package com.alborgis.randocaching.mainapp.splash;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

//Gestiona la velocidad del carrusel
public class CarouselFixedSpeedScroller extends Scroller {

	private int mDuration = 0;

	public CarouselFixedSpeedScroller(Context context,
			Interpolator interpolator, int speedMillis) {
		super(context, interpolator);
		mDuration = speedMillis;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		// Ignore received duration, use fixed one instead
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		// Ignore received duration, use fixed one instead
		super.startScroll(startX, startY, dx, dy, mDuration);
	}
}
