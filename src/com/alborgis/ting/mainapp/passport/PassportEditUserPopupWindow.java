package com.alborgis.ting.mainapp.passport;

import java.io.File;

import com.alborgis.ting.base.libraries.async_image_setter.AsyncImageSetter;
import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.base.model.Passport;
import com.alborgis.ting.base.model.ResourceFile;
import com.alborgis.ting.base.model.ResourceFile.FileUploadListener;
import com.alborgis.ting.base.model.User.UserUpdateListener;
import com.alborgis.ting.base.model.User;
import com.alborgis.ting.base.utils.ExternalStorage;
import com.alborgis.ting.base.utils.Util;
import com.alborgis.ting.mainapp.MainApp;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.common.LoadingDialog;
import com.alborgis.ting.mainapp.common.MessageDialog;
import com.alborgis.ting.mainapp.common.TINGTypeface;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView.OnItemClickListener;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayAdapterView.OnItemSelectedListener;
import com.alborgis.ting.mainapp.common.custom_controls.two_way_gridview.TwoWayGridView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class PassportEditUserPopupWindow implements UserUpdateListener, FileUploadListener {
	
	Activity activity;
	MainApp app;
	LayoutInflater layoutInflater;
	View layout;
	PopupWindow popupWindow;
	
	EditText tbNombre;
	EditText tbApellidos;
	EditText tbCurrentPass;
	EditText tbNewPass;
	Button btnActualizar;
	Button btnCancelar;
	Button btnEditarPass;
	ImageView imageViewPictureSeleccionado;
	TwoWayGridView gridView;
	SelectPictureGridViewAdapter adapter;
	
	int[] availableImages = new int[] {
			R.raw.btn_avatar_femenino_01_normal, 
			R.raw.btn_avatar_masculino_01_normal, 
			R.raw.btn_avatar_femenino_02_normal, 
			R.raw.btn_avatar_masculino_02_normal,
			R.raw.btn_avatar_femenino_03_normal,
			R.raw.btn_avatar_masculino_03_normal,
			R.raw.btn_avatar_femenino_04_normal,
			R.raw.btn_avatar_masculino_04_normal,
	};
	
	int selectedPictureResId;

	Passport currentUser;
	PassportEditUserPopupListener listener;
	
	public static interface PassportEditUserPopupListener {
		public void onPassportEditUserPopupDismiss();
	}
	
	
	
	public PassportEditUserPopupWindow(Activity _activity, Passport _currentUser, PassportEditUserPopupListener _listener){
		this.activity = _activity;
		this.app = (MainApp)this.activity.getApplication();
		this.listener = _listener;
		this.currentUser = _currentUser;
		
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate the popup.xml
		this.layout = layoutInflater.inflate(R.layout.mod_passport__edit_user_popup, null);
		
		capturarControles();
		
		popupWindow = new PopupWindow(activity);
		popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setContentView(layout);
		popupWindow.setFocusable(true);
		
		popupWindow.setOutsideTouchable(true);
		
		popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		popupWindow.getContentView().setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_BACK){
					hide();
				}
				return false;
			}
		});
		
		
		inicializarForm();
		escucharEventos();
	}
	
	
	public void disableImageSection(){
		imageViewPictureSeleccionado.setVisibility(View.GONE);
		gridView.setVisibility(View.GONE);
	}
	public void disableUserDataSection(){
		tbNombre.setVisibility(View.GONE);
		tbApellidos.setVisibility(View.GONE);
		btnEditarPass.setVisibility(View.GONE);
		tbNewPass.setVisibility(View.GONE);
		tbCurrentPass.setVisibility(View.GONE);
	}
	
	
	private void capturarControles(){
		tbNombre = (EditText)layout.findViewById(R.id.tbNombre);
		tbApellidos = (EditText)layout.findViewById(R.id.tbApellidos);
		tbCurrentPass = (EditText)layout.findViewById(R.id.tbCurrentPass);
		tbNewPass = (EditText)layout.findViewById(R.id.tbNewPass);
		btnActualizar = (Button)layout.findViewById(R.id.btnActualizar);
		btnCancelar = (Button)layout.findViewById(R.id.btnCancel);
		gridView = (TwoWayGridView)layout.findViewById(R.id.gridView);
		btnEditarPass = (Button)layout.findViewById(R.id.btnCambiarPass);
		imageViewPictureSeleccionado = (ImageView)layout.findViewById(R.id.imageViewPictureSeleccionado);
	}
	
	private void inicializarForm(){
		// Poner tipograf’as
		Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(activity);
		Typeface tfBold = TINGTypeface.getGullyBoldTypeface(activity);
		tbNombre.setTypeface(tfNormal);
		tbApellidos.setTypeface(tfNormal);
		tbCurrentPass.setTypeface(tfNormal);
		tbNewPass.setTypeface(tfNormal);
		btnEditarPass.setTypeface(tfNormal);
		btnActualizar.setTypeface(tfBold);
		btnCancelar.setTypeface(tfBold);
		
		// Poner los datos actuales del usuario
		if(currentUser != null){
			if(currentUser.firstName != null && !currentUser.firstName.equals("")){
				tbNombre.setText(currentUser.firstName);
			}
			if(currentUser.lastName != null && !currentUser.lastName.equals("")){
				tbApellidos.setText(currentUser.lastName);
			}
			if(currentUser.avatarImage != null && !currentUser.avatarImage.equals("")){
				Milog.d("Tiene una imagen");
				Util.loadBitmapOnImageView(activity, imageViewPictureSeleccionado, currentUser.avatarImage, null, 0, R.anim.anim_fade_in_300, 0, 0);
			}else{
				Milog.d("No tiene una imagen");
				AsyncImageSetter asyncImageSetter = new AsyncImageSetter(activity, imageViewPictureSeleccionado, R.drawable.btn_add_foto_o_avatar_normal, false);
				asyncImageSetter.execute();
			}
		}
		// Cargar datos en el grid
		adapter = new SelectPictureGridViewAdapter(activity, R.layout.mod_passport__edit_user_picture_gridview_item, availableImages);
		gridView.setAdapter(adapter);
		
		// Ocultar la secci—n de editar las passwords
		mostrarSeccionPasswords(false);
		
	}
	
	private void escucharEventos(){
		btnEditarPass.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				btnEditarPass.setVisibility(View.GONE);
				mostrarSeccionPasswords(true);
			}
		});
		btnActualizar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(validarCampos()){
					
					if(selectedPictureResId != 0){
						// Hay que subir nueva imagen
						String nameFich = "avatar.png";
						String path = ExternalStorage.copiarFicheroDeRecursosACacheInterna(app, selectedPictureResId, nameFich);
						File file = new File(path);
						byte[] byteArray = ResourceFile.convertImageToByteArray(file);
						String fileBase64 = ResourceFile.convertByteArrayToB64(byteArray);
						showLoading(true);
						ResourceFile.fileUpload(app.drupalClient, fileBase64, nameFich, PassportEditUserPopupWindow.this);
					}else{
						// Hay que actualizar el usuario (sin imagen)
						showLoading(true);
						User.update(app.drupalClient, currentUser.uid, tbNombre.getText().toString(), tbApellidos.getText().toString(), tbCurrentPass.getText().toString(), tbNewPass.getText().toString(), null, null, PassportEditUserPopupWindow.this);
					}
					
					
				}
			}
		});
		btnCancelar.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(TwoWayAdapterView<?> parent, View view,	int position, long id) {
				Milog.d("onItemClick");
				selectedPictureResId = availableImages[position];
				AsyncImageSetter imgSetter = new AsyncImageSetter(activity, imageViewPictureSeleccionado, selectedPictureResId, true);
				imgSetter.execute();
			}
			
		});
		gridView.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(TwoWayAdapterView<?> parent, View view,	int position, long id) {
				Milog.d("onItemSelected");
			}
			public void onNothingSelected(TwoWayAdapterView<?> parent) {
				Milog.d("onNothingSelected");
			}
		});
	}
	
	
	private void showLoading(boolean show){
		if(show){
			// Mostrar el panel de carga
			LoadingDialog.showLoading(activity);
		}else{
			// Ocultar el panel de carga
			LoadingDialog.hideLoading(activity);
		}
	}
	
	
	private boolean validarCampos(){
		//Validar campos
		if(tbCurrentPass.getText().toString().length() == 0 && tbNewPass.getText().toString().length() == 0){
			// Si no ha metido ninguna de las contrase–as, validarlo
			return true;
		}else{
			// Si ha metido al menos una contrase–a, validar que introduza las 2
			if(tbCurrentPass.getText().toString().length() < 3){
				MessageDialog.showMessage(activity, "Contrase–a actual", "Por favor, introduce tu contrase–a actual");
				return false;
			}
			if(tbNewPass.getText().toString().length() < 3){
				MessageDialog.showMessage(activity, "Nueva contrase–a", "Por favor, introduce la nueva contrase–a para cambiarla");
				return false;
			}
		}
		
		return true;
	}
	
	
	private void mostrarSeccionPasswords(boolean show){
		if(show){
			tbCurrentPass.setVisibility(View.VISIBLE);
			tbNewPass.setVisibility(View.VISIBLE);
		}else{
			tbCurrentPass.setVisibility(View.GONE);
			tbNewPass.setVisibility(View.GONE);
		}
	}
	
	// The method that displays the popup.
	public void showPopup(View view) {
		
		// Comprobar si se est‡ mostrando. Si se est‡ mostrando cerrarlo
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
			return;
		}
		
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
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
	
	
	
	public class SelectPictureGridViewAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		Context ctx;
		int layoutId;
		int[] images;

		public class ViewHolder {
			RelativeLayout rootView;
			ImageView imgViewBackgroundTile;
			int index;
		}

		public SelectPictureGridViewAdapter(Context ctx, int layoutId, int[] resIdImages) {
			this.ctx = ctx;
			this.layoutId = layoutId;
			this.images = resIdImages;
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		public int getCount() {
			return images.length;
		}

		@Override
		public Object getItem(int position) {
			return images[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(layoutId, null);
				holder.imgViewBackgroundTile = (ImageView) convertView.findViewById(R.id.imgViewBackgroundTile);
				holder.rootView = (RelativeLayout)convertView.findViewById(R.id.rootView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Recoger el item
			int image = images[position];

			AsyncImageSetter imgSetter = new AsyncImageSetter(ctx, holder.imgViewBackgroundTile, image, true);
			imgSetter.execute();


			return convertView;
		}
	}

	
	
	public void onFileUploaded(String fid, String uri) {
		// Se ha subido una imagen nueva
		// Actualizar el usuario
		showLoading(true);
		User.update(app.drupalClient, currentUser.uid, tbNombre.getText().toString(), tbApellidos.getText().toString(), tbCurrentPass.getText().toString(), tbNewPass.getText().toString(), fid, uri, this);
	}

	public void onFileUploadError(String error) {
		// Ha dado error al subir imagen
		showLoading(false);
		MessageDialog.showMessage(activity, "Error", "Error durante la subida de la imagen");
	}


	public void onUserUpdateSuccess() {
		// Se ha actualizado el usuario
		showLoading(false);
		Milog.d("Exito al actualizar usuario");
		listener.onPassportEditUserPopupDismiss();
		popupWindow.dismiss();
	}
	public void onUserUpdateError(String error) {
		// Error al actualizar el usuario
		showLoading(false);
		Milog.d("Error al actualizar usuario: " + error);
		MessageDialog.showMessage(activity, "Error", "Error al actualizar los datos del usuario");
	}

	
	
	
}
