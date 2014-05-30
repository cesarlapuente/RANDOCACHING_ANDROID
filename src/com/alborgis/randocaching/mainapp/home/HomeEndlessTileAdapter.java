package com.alborgis.randocaching.mainapp.home;

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
import com.alborgis.ting.base.model.CheckinsGame;
import com.alborgis.ting.base.model.Destination;
import com.alborgis.ting.base.model.EnigmasGame;
import com.alborgis.ting.base.model.GeocachesGame;
import com.alborgis.ting.base.model.PhotohidesGame;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.common.TINGColor;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;

public class HomeEndlessTileAdapter extends ArrayAdapter<Object> {
	

	LayoutInflater mInflater;
	List<Object> itemList;
	Context ctx;
	int layoutId;
	
	int colorPrincipalEnigmasGame;
	int colorPrincipalCheckinsGame;
	int colorPrincipalPhotohidesGame;
	int colorPrincipalDestination;
	int colorPrincipalGeocachesGameOne;
	int colorPrincipalGeocachesGameFull;
	int colorPrincipalGeocachesGameBattle;
	
	int colorOscuroEnigmasGame;
	int colorOscuroCheckinsGame;
	int colorOscuroPhotohidesGame;
	int colorOscuroGeocachesGameOne;
	int colorOscuroGeocachesGameFull;
	int colorOscuroGeocachesGameBattle;
	
	
	int colorOther;
	
	int resIdImgEnigmasGame;
	int resIdImgCheckinsGame;
	int resIdImgGeocachesGame;
	int resIdImgPhotohidesGame;

	public class ViewHolder {
		RelativeLayout tileContent;
		RelativeLayout bottomBand;
		ImageView imgViewBackgroundTile;
		TextView tvMainTextDestination;
		TextView tvMainTextGame;
		TextView tvDificultad;
		TextView tvCompletado;
		TextView tvValoracion;
		int index;
	}

	public HomeEndlessTileAdapter(Context ctx, int layoutId, List<Object> itemList) {
		super(ctx, layoutId, itemList);
		this.itemList = itemList;
		this.ctx = ctx;
		this.layoutId = layoutId;
		mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inicializar los colores posibles de las miniaturas
		this.colorPrincipalEnigmasGame = TINGColor.getColor(this.ctx, R.color.naranja_challenger_principal);
		this.colorPrincipalCheckinsGame = TINGColor.getColor(this.ctx, R.color.verde_messenger_principal);
		this.colorPrincipalPhotohidesGame = TINGColor.getColor(this.ctx, R.color.azul_discover_principal);
		this.colorPrincipalGeocachesGameOne = TINGColor.getColor(this.ctx, R.color.verde_explorer_one_principal);
		this.colorPrincipalGeocachesGameFull = TINGColor.getColor(this.ctx, R.color.verde_explorer_full_principal);
		this.colorPrincipalGeocachesGameBattle = TINGColor.getColor(this.ctx, R.color.verde_explorer_battle_principal);
		this.colorPrincipalDestination = TINGColor.getColor(ctx, R.color.azul_principal_oscuro_transparente_50);
		
		this.colorOscuroEnigmasGame = TINGColor.getColor(this.ctx, R.color.naranja_challenger_oscuro);
		this.colorOscuroCheckinsGame = TINGColor.getColor(this.ctx, R.color.verde_messenger_oscuro);
		this.colorOscuroPhotohidesGame = TINGColor.getColor(this.ctx, R.color.azul_discover_oscuro);
		this.colorOscuroGeocachesGameOne = TINGColor.getColor(this.ctx, R.color.verde_explorer_one_oscuro);
		this.colorOscuroGeocachesGameFull = TINGColor.getColor(this.ctx, R.color.verde_explorer_full_oscuro);
		this.colorOscuroGeocachesGameBattle = TINGColor.getColor(this.ctx, R.color.verde_explorer_battle_oscuro);
		this.colorOther = TINGColor.getColor(ctx, R.color.negro);
		// Inicializar las im‡genes posibles para los juegos
		this.resIdImgEnigmasGame = R.drawable.btn_state_ppal_peque_challenger;
		this.resIdImgCheckinsGame = R.drawable.btn_state_ppal_peque_messenger;
		this.resIdImgGeocachesGame = R.drawable.btn_state_ppal_peque_explorer;
		this.resIdImgPhotohidesGame  = R.drawable.btn_state_ppal_peque_discover;
	}


	public int getCount() {
		if (itemList != null) {
			return itemList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
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
			holder.bottomBand = (RelativeLayout) convertView
					.findViewById(R.id.bottomBand);
			holder.imgViewBackgroundTile = (ImageView) convertView.findViewById(R.id.imgViewBackgroundTile);
			holder.tvMainTextDestination = (TextView) convertView.findViewById(R.id.tvMainTextDestination);
			holder.tvMainTextGame = (TextView) convertView.findViewById(R.id.tvMainTextGame);
			holder.tvDificultad = (TextView) convertView.findViewById(R.id.tvDificultad);
			holder.tvCompletado = (TextView) convertView.findViewById(R.id.tvCompletado);
			holder.tvValoracion = (TextView) convertView.findViewById(R.id.tvValoracion);
			Typeface tf = TINGTypeface.getGullyCondensedTypeface(ctx);
			holder.tvMainTextDestination.setTypeface(tf);
			holder.tvMainTextGame.setTypeface(tf);
			holder.tvDificultad.setTypeface(tf);
			holder.tvCompletado.setTypeface(tf);
			holder.tvValoracion.setTypeface(tf);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Recoger el item
		Object item = getItem(position);
		
		holder.tvCompletado.setText("0%");
		
		
		// Comprobar la categor’a del objeto
		if(item.getClass().equals(CheckinsGame.class)){
			// Es un juego de checkin
			CheckinsGame game = (CheckinsGame)item;
			// Color
			holder.tileContent.setBackgroundColor(this.colorPrincipalCheckinsGame);
			// Imagen de fondo
			holder.imgViewBackgroundTile.setImageResource(this.resIdImgCheckinsGame);
			// Ocultar texto destino
			holder.tvMainTextDestination.setVisibility(View.GONE);
			// Mostrar texto juego
			holder.tvMainTextGame.setVisibility(View.VISIBLE);
			// Mostrar nombre del juego
			holder.tvMainTextGame.setText(game.title.toUpperCase());
			// Poner el texto del color del juego oscuro
			holder.tvMainTextGame.setTextColor(colorOscuroCheckinsGame);
			// Mostrar la banda de abajo
			holder.bottomBand.setVisibility(View.VISIBLE);
		}else if(item.getClass().equals(EnigmasGame.class)){
			// Es un juego de enigmas
			EnigmasGame game = (EnigmasGame)item;
			// Color
			holder.tileContent.setBackgroundColor(this.colorPrincipalEnigmasGame);
			// Imagen de fondo
			holder.imgViewBackgroundTile.setImageResource(this.resIdImgEnigmasGame);
			// Ocultar texto destino
			holder.tvMainTextDestination.setVisibility(View.GONE);
			// Mostrar texto juego
			holder.tvMainTextGame.setVisibility(View.VISIBLE);
			// Mostrar nombre del juego
			holder.tvMainTextGame.setText(game.title.toUpperCase());
			// Poner el texto del color del juego oscuro
			holder.tvMainTextGame.setTextColor(colorOscuroEnigmasGame);
			// Mostrar la banda de abajo
			holder.bottomBand.setVisibility(View.VISIBLE);
		}else if(item.getClass().equals(GeocachesGame.class)){
			// Es un juego de geocaches
			GeocachesGame game = (GeocachesGame)item;
			// Imagen de fondo
			holder.imgViewBackgroundTile.setImageResource(this.resIdImgGeocachesGame);
			// Ocultar texto destino
			holder.tvMainTextDestination.setVisibility(View.GONE);
			// Mostrar texto juego
			holder.tvMainTextGame.setVisibility(View.VISIBLE);
			// Mostrar nombre del juego
			holder.tvMainTextGame.setText(game.title.toUpperCase());
			// Poner la dificultad
			holder.tvDificultad.setText( String.valueOf(game.level) );
			// Poner el progreso
			holder.tvCompletado.setText(game.progress + "%");
			// Poner la valoracion
			if( game.vote != null){ 
				holder.tvValoracion.setText( String.valueOf(game.vote.getNumStars()) );
			}else{
				holder.tvValoracion.setText( String.valueOf(0) );
			}
			// Mostrar la banda de abajo
			holder.bottomBand.setVisibility(View.VISIBLE);

			// Mostrar la modalidad del geocache
			if(game.modality != null){
				if(game.modality.equals(GeocachesGame.MODALITY.ONE)){
					// Color
					holder.tileContent.setBackgroundColor(this.colorPrincipalGeocachesGameOne);
					// Poner el texto del color del juego oscuro
					holder.tvMainTextGame.setTextColor(colorOscuroGeocachesGameOne);
				}else if(game.modality.equals(GeocachesGame.MODALITY.FULL)){
					// Color
					holder.tileContent.setBackgroundColor(this.colorPrincipalGeocachesGameFull);
					// Poner el texto del color del juego oscuro
					holder.tvMainTextGame.setTextColor(colorOscuroGeocachesGameFull);
				}else if(game.modality.equals(GeocachesGame.MODALITY.BATTLE)){
					// Color
					holder.tileContent.setBackgroundColor(this.colorPrincipalGeocachesGameBattle);
					// Poner el texto del color del juego oscuro
					holder.tvMainTextGame.setTextColor(colorPrincipalGeocachesGameOne);
				}
			}
		}else if(item.getClass().equals(PhotohidesGame.class)){
			// Es un juego de enigmas
			PhotohidesGame game = (PhotohidesGame)item;
			// Color
			holder.tileContent.setBackgroundColor(this.colorPrincipalPhotohidesGame);
			// Imagen de fondo
			holder.imgViewBackgroundTile.setImageResource(this.resIdImgPhotohidesGame);
			// Ocultar texto destino
			holder.tvMainTextDestination.setVisibility(View.GONE);
			// Mostrar texto juego
			holder.tvMainTextGame.setVisibility(View.VISIBLE);
			// Mostrar nombre del juego
			holder.tvMainTextGame.setText(game.title.toUpperCase());
			// Poner el texto del color del juego oscuro
			holder.tvMainTextGame.setTextColor(colorOscuroPhotohidesGame);
			// Mostrar la banda de abajo
			holder.bottomBand.setVisibility(View.VISIBLE);
		}else if(item.getClass().equals(Destination.class)){
			// Es un destino (ciudad)
			Destination dest = (Destination)item;
			// Color
			holder.tileContent.setBackgroundColor(this.colorPrincipalDestination);
			// Imagen de fondo con la imagen destacada del destino
			Util.loadBitmapOnImageView(ctx, holder.imgViewBackgroundTile, dest.featuredImage, null, 0, R.anim.anim_fade_in_300, 200, 200);
			// Ocultar texto juego
			holder.tvMainTextGame.setVisibility(View.GONE);
			// Mostrar texto destino
			holder.tvMainTextDestination.setVisibility(View.VISIBLE);
			// Mostrar nombre del destino
			holder.tvMainTextDestination.setText(dest.title.toUpperCase());
			// Ocultar la banda de abajo
			holder.bottomBand.setVisibility(View.GONE);
		}else{
			// Color
			holder.tileContent.setBackgroundColor(this.colorOther);
		}


		return convertView;
	}

}
