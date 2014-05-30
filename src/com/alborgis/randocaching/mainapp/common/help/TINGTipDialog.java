package com.alborgis.randocaching.mainapp.common.help;

import com.alborgis.ting.base.libraries.async_image_setter.AsyncImageSetter;
import com.alborgis.randocaching.mainapp.R;
import com.alborgis.randocaching.mainapp.common.TINGTypeface;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TINGTipDialog extends Dialog {

	Context ctx;

	String title;
	String message;

	ImageView imgViewIcon;
	TextView tvTitle;
	TextView tvMessage;
	ImageView imgViewImage;
	Button btnNegative;
	Button btnPositive;

	String positiveButtonText;
	String negativeButtonText;

	int resIdIcon;
	int resIdImage;
	int gravity;

	TipDialogListener tipDialogListener;

	public interface TipDialogListener {
		public void onPositiveButtonClick(TINGTipDialog dialog);
		public void onNegativeButtonClick(TINGTipDialog dialog);
	}

	public TINGTipDialog(Context context, String _title, String _message) {
		super(context);
		ctx = context;
		title = _title;
		message = _message;
	}

	public TINGTipDialog(Context context, String _title, String _message,
			String _positiveButtonText, String _negativeButtonText,
			int _resIdIcon, int _resIdImage, int _gravity,
			TipDialogListener _tipDialogListener) {
		super(context);
		ctx = context;
		title = _title;
		message = _message;
		positiveButtonText = _positiveButtonText;
		negativeButtonText = _negativeButtonText;
		tipDialogListener = _tipDialogListener;
		resIdIcon = _resIdIcon;
		resIdImage = _resIdImage;
		gravity = _gravity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mod_common__layout_tip_dialog);

		this.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		capturarControles();
		inicializarForm();
		escucharEventos();
	}

	private void capturarControles() {
		imgViewIcon = (ImageView) findViewById(R.id.imgViewLogoTing);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvMessage = (TextView) findViewById(R.id.tvMessage);
		imgViewImage = (ImageView) findViewById(R.id.imgViewImage);
		btnNegative = (Button) findViewById(R.id.btnNegative);
		btnPositive = (Button) findViewById(R.id.btnPositive);
	}

	private void escucharEventos() {

		// Poner los listener a los botones
		btnPositive.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				if (tipDialogListener != null) {
					tipDialogListener.onPositiveButtonClick(TINGTipDialog.this);
				} else {
					TINGTipDialog.this.dismiss();
				}
			}
		});
		btnNegative.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				if (tipDialogListener != null) {
					tipDialogListener.onNegativeButtonClick(TINGTipDialog.this);
				} else {
					TINGTipDialog.this.dismiss();
				}

			}
		});

	}

	private void inicializarForm() {
		// Poner tipograf’as
		Typeface tfBold = TINGTypeface.getGullyBoldTypeface(ctx);
		Typeface tfNormal = TINGTypeface.getGullyNormalTypeface(ctx);

		tvTitle.setTypeface(tfBold);
		tvMessage.setTypeface(tfNormal);
		btnNegative.setTypeface(tfNormal);
		btnPositive.setTypeface(tfNormal);

		tvTitle.setText(title);
		tvMessage.setText(message);

		// Poner los textos a los botones
		if (this.positiveButtonText != null) {
			btnPositive.setText(this.positiveButtonText);
		}
		if (this.negativeButtonText != null) {
			btnNegative.setText(this.negativeButtonText);
		} else {
			btnNegative.setText("OK");
		}

		// Si no se pasa un listener, ocultar el bot—n positivo
		if (tipDialogListener == null) {
			btnPositive.setVisibility(View.GONE);
		}
		if (gravity != 0) {
			this.getWindow().setGravity(gravity);
		}

		if (resIdIcon != 0) {
			AsyncImageSetter imgSetter = new AsyncImageSetter(ctx, imgViewIcon,
					resIdIcon, false);
			imgSetter.execute();
		}

		if (resIdImage != 0) {
			AsyncImageSetter imgSetter = new AsyncImageSetter(ctx,
					imgViewImage, resIdImage, false);
			imgSetter.execute();
		} else {
			imgViewImage.setVisibility(View.GONE);
		}

		getWindow().getAttributes().windowAnimations = R.style.TipDialogAnimation;
		
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
	}

	// Para crear un mensaje informativo desde cualquier lugar
	public static void showMessage(Context _ctx, String _title, String _message) {
		TINGTipDialog messageDialog = new TINGTipDialog(_ctx, _title, _message);
		messageDialog.show();
	}

	// Para crear un mensaje con 1 bot—n con el texto deseado y que responda al
	// evento que le digamos
	public static void showMessageWith1Buttons(Context _ctx, String _title,
			String _message, String _negativeButtonText, int _resIdIcon,
			int _resIdImage, int _gravity, TipDialogListener _tipDialogListener) {
		TINGTipDialog messageDialog = new TINGTipDialog(_ctx, _title, _message,
				null, _negativeButtonText, _resIdIcon, _resIdImage, _gravity,
				_tipDialogListener);
		messageDialog.show();
	}

	// Para crear un mensaje con 2 botones desde cualquier lugar
	public static void showMessageWith2Buttons(Context _ctx, String _title,
			String _message, String _positiveButtonText,
			String _negativeButtonText, int _resIdIcon, int _resIdImage,
			int _gravity, TipDialogListener _tipDialogListener) {
		TINGTipDialog messageDialog = new TINGTipDialog(_ctx, _title, _message,
				_positiveButtonText, _negativeButtonText, _resIdIcon,
				_resIdImage, _gravity, _tipDialogListener);
		messageDialog.show();
	}

}
