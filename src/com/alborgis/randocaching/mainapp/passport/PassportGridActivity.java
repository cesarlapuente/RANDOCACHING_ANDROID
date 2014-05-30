package com.alborgis.randocaching.mainapp.passport;

import java.util.ArrayList;

import com.alborgis.ting.base.libraries.async_image_setter.AsyncImageSetter;
import com.alborgis.ting.base.libraries.bitmap_manager.MyBitmapManager;
import com.alborgis.ting.base.model.Badge;
import com.alborgis.ting.base.model.Stamp;
import com.alborgis.ting.base.model.Visado;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.MainApp;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;
import com.alborgis.randocaching.mainapp.home.MainActivity;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PassportGridActivity extends Activity {

	public static final String PARAM_KEY_BADGES	=	"param_key_badges";
	public static final String PARAM_KEY_STAMPS	=	"param_key_stamps";
	public static final String PARAM_KEY_VISADOS	=	"param_key_visados";
	public static final String PARAM_KEY_TITLE		=	"param_key_title";
	MainApp app;
	
	ImageButton btnBack;
	ImageButton btnHome;
	TextView tvTitle;
	TextView tvHeader;
	
	GridView gridView;
	PassportGridAdapter adapter;
	
	String title;
	ArrayList<Badge> badges = null;
	ArrayList<Stamp> stamps = null;
	ArrayList<Visado> visados = null;
	
	ArrayList<Object> items = null;
	
	int placeHolderGridItem;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_passport__layout_passport_grid);
		
		this.setVolumeControlStream( AudioManager.STREAM_MUSIC );
		
		Bundle b = getIntent().getExtras();
		if(b != null){
			badges = b.getParcelableArrayList(PARAM_KEY_BADGES);
			stamps = b.getParcelableArrayList(PARAM_KEY_STAMPS);
			visados = b.getParcelableArrayList(PARAM_KEY_VISADOS);
			title = b.getString(PARAM_KEY_TITLE);
			
			items = new ArrayList<Object>();
			if(badges != null){
				for(Badge item : badges){
					items.add(item);
				}
				placeHolderGridItem = R.drawable.badge_falta;
			}else if(stamps != null){
				for(Stamp item : stamps){
					items.add(item);
				}
				placeHolderGridItem = R.drawable.sello_falta;
			}else if(visados != null){
				for(Visado item : visados){
					items.add(item);
				}
				placeHolderGridItem = R.drawable.visado_falta;
			}
		}

		this.app = (MainApp) getApplication();

		capturarControles();
		inicializarForm();
		escucharEventos();
		cargarDatos();

	}
	

	

	protected void onResume() {
		super.onResume();

	}


	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_pop_enter, R.anim.anim_pop_exit);
	}




	private void capturarControles() {
		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnHome = (ImageButton) findViewById(R.id.btnHome);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		gridView = (GridView) findViewById(R.id.gridView);
	}

	private void inicializarForm() {
		// Tipografias
		Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(this);
		tvTitle.setTypeface(tfGullyLight);
		tvHeader.setTypeface(tfGullyLight);
		tvHeader.setText(title);
	}

	private void escucharEventos() {
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		btnHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PassportGridActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}
	
	private void cargarDatos(){
		
		// A–adir elementos al final para que haya un total de 50 (los vac’o se rellenar‡n con un hueco)
		int total = 100;
		int vacios = total - items.size();
		for(int i=0; i<vacios; i++){
			Object objVacio = new Object();
			items.add(objVacio);
		}
		
		// Borrar las im‡genes ya cargadas de la cachŽ
		for(Object item : items){
			if(item.getClass().equals(Badge.class)){
				Badge badge = (Badge)item;
				MyBitmapManager.INSTANCE.removeFromCache(badge.featuredImage);
			}else if(item.getClass().equals(Stamp.class)){
				Stamp stamp = (Stamp)item;
				MyBitmapManager.INSTANCE.removeFromCache(stamp.featuredImage);
			}else{
				
			}
		}
		
		// Agregar datos al grid
		adapter = new PassportGridAdapter(this, R.layout.mod_passport__grid_view_item);
		gridView.setAdapter(adapter);
	}

	
	
	
	
	public class PassportGridAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		Context ctx;
		int layoutId;


		public class ViewHolder {
			ImageView imgViewItem;
			TextView tvMainText;
			int index;
		}

		public PassportGridAdapter(Context ctx, int layoutId) {
			this.ctx = ctx;
			this.layoutId = layoutId;
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		public int getCount() {
			if (items != null) {
				return items.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			//return itemList.get(position).hashCode();
			return position;
		}
		
		public void removeAllItems(){
			if(items != null){
				items.clear();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(layoutId, null);
				holder.imgViewItem = (ImageView) convertView.findViewById(R.id.imgViewItem);
				holder.tvMainText = (TextView) convertView.findViewById(R.id.tvMainText);
				Typeface tfGullyLight = TINGTypeface.getGullyLightTypeface(PassportGridActivity.this);
				holder.tvMainText.setTypeface(tfGullyLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			Object item = getItem(position);
			
			if(item.getClass().equals(Badge.class)){
				Badge badge = (Badge)item;
				// Mostrar el texto
				holder.tvMainText.setVisibility(View.VISIBLE);
				holder.tvMainText.setText(badge.title);
				Util.loadBitmapOnImageView(ctx, holder.imgViewItem, badge.featuredImage, null, placeHolderGridItem, R.anim.anim_fade_in_300, 0, 0);
			}else if(item.getClass().equals(Stamp.class)){
				Stamp stamp = (Stamp)item;
				// Mostrar el texto
				holder.tvMainText.setVisibility(View.VISIBLE);
				holder.tvMainText.setText(stamp.title);
				Util.loadBitmapOnImageView(ctx, holder.imgViewItem, stamp.featuredImage, null, placeHolderGridItem, R.anim.anim_fade_in_300, 0, 0);
			}else if(item.getClass().equals(Visado.class)){
				Visado visado = (Visado)item;
				// Mostrar el texto
				holder.tvMainText.setVisibility(View.VISIBLE);
				holder.tvMainText.setText(visado.title);
				Util.loadBitmapOnImageView(ctx, holder.imgViewItem, visado.featuredImage, null, placeHolderGridItem, R.anim.anim_fade_in_300, 0, 0);	
			}else{
				// Ocultar el texto
				holder.tvMainText.setVisibility(View.GONE);
				AsyncImageSetter asyncSetter = new AsyncImageSetter(PassportGridActivity.this, holder.imgViewItem, placeHolderGridItem, false);
				asyncSetter.execute();
			}

			return convertView;
		}

	}
	

}
