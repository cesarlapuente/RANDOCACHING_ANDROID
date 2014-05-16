package com.alborgis.ting.mainapp.common;

import java.util.ArrayList;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Destination;
import com.alborgis.ting.base.model.Destination.DestinationListListener;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DestinationListPopupWindow implements DestinationListListener {
	
	Activity activity;
	MainApp app;
	LayoutInflater layoutInflater;
	
	View layout;
	ListView listView;
	RelativeLayout panelCargando;
	
	
	PopupWindow popupWindow;
	
	
	ArrayList<Destination> destinations;
	ListDestinationsAdapter adapter;
	
	Destination destinoSeleccionado;
	
	CitiesListPopupListener listener;
	
	public static interface CitiesListPopupListener {
		public void onCitiesListItemSelect(Destination dest);
	}
	
	
	
	public DestinationListPopupWindow(Activity _activity, CitiesListPopupListener _listener){
		this.activity = _activity;
		this.app = (MainApp)this.activity.getApplication();
		this.listener = _listener;
		
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate the popup.xml
		this.layout = layoutInflater.inflate(R.layout.mod_common__destination_menu, null);
		this.listView = (ListView)layout.findViewById(R.id.listView);
		this.panelCargando = (RelativeLayout)layout.findViewById(R.id.panelCargando);
		
		popupWindow = new PopupWindow(activity);
		popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setContentView(layout);
		popupWindow.setFocusable(true);
		
		//popupWindow = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
		
		popupWindow.setOutsideTouchable(true);
		
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Milog.d("OnItemClick");
				Destination destSel = destinations.get(position);
				listener.onCitiesListItemSelect(destSel);
				
				hide(); // Cerrar el popup
			}
		});
		
		popupWindow.getContentView().setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK){
					hide();
				}
				return false;
			}
		});
		
	}
	
	// The method that displays the popup.
	public void showPopup(View view, Destination _destinoSeleccionado) {
		
		destinoSeleccionado = _destinoSeleccionado;
		
		// Comprobar si se est‡ mostrando. Si se est‡ mostrando cerrarlo
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
			return;
		}
		
		if(this.destinations == null || this.destinations.size() == 0){
			// Mostrar el panel de cargando
			showLoading(true);
			// Cargar los datos en la lista
			Destination.loadList(app.drupalClient, this);
		}

		// Mostrar el popup
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		
		/*
		 * Since the anchor position for a popup is the left top of the anchor view,
		 * calculate the x position and y position and override the location manually
		 */
		int xPos = location[0] + view.getWidth() / 2 - layout.getMeasuredWidth() / 2;
		int yPos = location[1] + view.getMeasuredHeight();
		
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, xPos, yPos);
	}

	
	
	

	public void onDestinationListLoad(ArrayList<Destination> destinations) {
		this.destinations = new ArrayList<Destination>();
		
		if(destinations != null){
			// A–adir el elemento 'Mostrar' todos
			Destination destTodos = new Destination();
			destTodos.nid = "-1";
			destTodos.title = activity.getString(R.string.common_todas);
			this.destinations.add(destTodos);
			// A–adir los destinos al array general
			this.destinations.addAll(destinations);
		}
		


		adapter = new ListDestinationsAdapter(activity, this.destinations);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		showLoading(false);
	}

	public void onDestinationListLoadError(String error) {
		
	}
	
	
	
	private void showLoading(boolean show){
		if(show){
			panelCargando.setVisibility(View.VISIBLE);
		}else{
			panelCargando.setVisibility(View.GONE);
		}
	}
	
	
	
	private class ListDestinationsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context ctx;
		private ArrayList<Destination> listaItems;

		public class ViewHolder {
			TextView lblTitle;
		}

		public ListDestinationsAdapter(Context _ctx, ArrayList<Destination> _items) {
			this.listaItems = _items;
			this.ctx = _ctx;
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			if (listaItems != null) {
				return listaItems.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.mod_common__destination_menu_item, null);
				holder.lblTitle = (TextView) convertView.findViewById(R.id.lblTitle);
				Typeface tf = TINGTypeface.getGullyNormalTypeface(ctx);
				holder.lblTitle.setTypeface(tf);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			Destination item = listaItems.get(position);
			holder.lblTitle.setText(item.title.toUpperCase());
			
			if(destinoSeleccionado != null && item.nid != null && item.nid.equalsIgnoreCase(destinoSeleccionado.nid)){
				holder.lblTitle.setTextColor(Color.GRAY);
			}else{
				holder.lblTitle.setTextColor(Color.BLACK);
			}
	
			
			return convertView;
		}
	}
	
	
	
	public boolean isVisible(){
		if(popupWindow != null){
			return popupWindow.isShowing();
		}
		return false;
	}
	
	public void hide(){
		if(popupWindow != null){
			popupWindow.dismiss();
		}
	}
	
	
}
