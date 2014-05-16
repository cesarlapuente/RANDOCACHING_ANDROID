package com.alborgis.ting.mainapp.common;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Game;
import com.alborgis.ting.base.model.Game.GameTypesListListener;
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

public class GameTypesListPopupWindow implements GameTypesListListener {
	
	Activity activity;
	MainApp app;
	LayoutInflater layoutInflater;
	
	View layout;
	ListView listView;
	RelativeLayout panelCargando;
	
	
	PopupWindow popupWindow;
	
	
	ArrayList<BasicNameValuePair> gameTypes;
	ListGameTypesAdapter adapter;
	
	BasicNameValuePair gameTypeSeleccionado;
	
	GameTypesListPopupListener listener;
	
	public static interface GameTypesListPopupListener {
		public void onGameTypesListItemSelect(BasicNameValuePair gameType);
	}
	
	
	
	public GameTypesListPopupWindow(Activity _activity, GameTypesListPopupListener _listener){
		this.activity = _activity;
		this.app = (MainApp)this.activity.getApplication();
		this.listener = _listener;
		
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate the popup.xml
		this.layout = layoutInflater.inflate(R.layout.mod_common__game_type_menu, null);
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
				BasicNameValuePair gameTypeSel = gameTypes.get(position);
				listener.onGameTypesListItemSelect(gameTypeSel);
				
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
	public void showPopup(View view, BasicNameValuePair _gameTypeSeleccionado) {
		
		gameTypeSeleccionado = _gameTypeSeleccionado;
		
		// Comprobar si se est‡ mostrando. Si se est‡ mostrando cerrarlo
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
			return;
		}
		
		if(this.gameTypes == null || this.gameTypes.size() == 0){
			// Mostrar el panel de cargando
			showLoading(true);
			// Cargar los datos en la lista
			Game.listGameTypes(app.drupalClient, app.drupalSecurity, this);
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

	

	@Override
	public void onGameTypesListLoad(ArrayList<BasicNameValuePair> listItems) {
		this.gameTypes = new ArrayList<BasicNameValuePair>();
		
		if(gameTypes != null){
			// A–adir el elemento 'Mostrar' todos
			BasicNameValuePair gameTypeTodos = new BasicNameValuePair("-1", activity.getString(R.string.common_todos));
			this.gameTypes.add(gameTypeTodos);
			// A–adir los gameTypes al array general
			this.gameTypes.addAll(listItems);
		}
		


		adapter = new ListGameTypesAdapter(activity, this.gameTypes);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		showLoading(false);
	}

	@Override
	public void onGameTypesListError(String error) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private void showLoading(boolean show){
		if(show){
			panelCargando.setVisibility(View.VISIBLE);
		}else{
			panelCargando.setVisibility(View.GONE);
		}
	}
	
	
	
	private class ListGameTypesAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context ctx;
		private ArrayList<BasicNameValuePair> listaItems;

		public class ViewHolder {
			TextView lblTitle;
		}

		public ListGameTypesAdapter(Context _ctx, ArrayList<BasicNameValuePair> _items) {
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
				convertView = mInflater.inflate(R.layout.mod_common__game_type_menu_item, null);
				holder.lblTitle = (TextView) convertView.findViewById(R.id.lblTitle);
				Typeface tf = TINGTypeface.getGullyNormalTypeface(ctx);
				holder.lblTitle.setTypeface(tf);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			BasicNameValuePair item = listaItems.get(position);
			holder.lblTitle.setText(item.getValue().toUpperCase());
			
			if(gameTypeSeleccionado != null && item.getName() != null && item.getName().equalsIgnoreCase(gameTypeSeleccionado.getName())){
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
