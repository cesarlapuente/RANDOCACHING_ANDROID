package com.alborgis.randocaching.mainapp.awards;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alborgis.ting.base.model.Award;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;

public class AwardsEndlessTileAdapter extends ArrayAdapter<Award> {
	

	LayoutInflater mInflater;
	List<Award> itemList;
	Context ctx;
	int layoutId;


	public class ViewHolder {
		RelativeLayout tileContent;
		ImageView imgViewBackgroundTile;
		TextView tvMainText;
		int index;
	}

	public AwardsEndlessTileAdapter(Context ctx, int layoutId, List<Award> itemList) {
		super(ctx, layoutId, itemList);
		this.itemList = itemList;
		this.ctx = ctx;
		this.layoutId = layoutId;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


	public int getCount() {
		if (itemList != null) {
			return itemList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Award getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		//return itemList.get(position).hashCode();
		return position;
	}
	
	public void removeAllItems(){
		if(itemList != null){
			itemList.clear();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(layoutId, null);
			holder.tileContent = (RelativeLayout) convertView
					.findViewById(R.id.tileContent);
			holder.imgViewBackgroundTile = (ImageView) convertView.findViewById(R.id.imgViewBackgroundTile);
			holder.tvMainText = (TextView) convertView.findViewById(R.id.tvMainText);
			Typeface tf = TINGTypeface.getGullyCondensedTypeface(ctx);
			holder.tvMainText.setTypeface(tf);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Recoger el item
		Award item = getItem(position);
		
		holder.tvMainText.setText(item.title.toUpperCase());
		
		// Imagen de fondo con la imagen destacada del destino
		Util.loadBitmapOnImageView(ctx, holder.imgViewBackgroundTile, item.featuredImage, null, 0, R.anim.anim_fade_in_300, 200, 200);


		return convertView;
	}

}
