package com.alborgis.randocaching.mainapp.splash;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.randocaching.mainapp.R;

//Adaptador del carrusel
public class CarouselAdapter extends PagerAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<String> images = null;

	public CarouselAdapter(Context _ctx, ArrayList<String> _images) {
		context = _ctx;
		mInflater = LayoutInflater.from(context);
		images = _images;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	public Object instantiateItem(ViewGroup container, int position) {
		ViewGroup item_carrousel = (ViewGroup) mInflater.inflate(
				R.layout.mod_splash__item_carrousel, container, false);
		ImageView imgView = (ImageView) item_carrousel
				.findViewById(R.id.imageView);
		Util.loadBitmapOnImageView(context, imgView, images.get(position), null, 0, R.anim.anim_fade_in_300, 0, 0);
		container.addView(item_carrousel);
		return item_carrousel;
	}

	public int getCount() {
		return images.size();
	}

	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}
}
