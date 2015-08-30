package com.ramananda.utiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class Conts {


	public static final int HIGHLIGHT_COLOR = 0x999be6ff;

	public static final String EXTRA_NAME = "exname";
	public static final String EXTRA_NUMBER = "exnumber";
	public static final String EXTRA_EMAIL = "exemail";
	public static final String EXTRA_PHOTO = "exphoto";
	public static final String EXTRA_CITY = "excity";
	public static final String EXTRA_DES = "exdesignation";
	public static final String EXTRA_GROUP = "exgroup";

	// update
	public static final String UPEXTRA_NAME = "upexname";
	public static final String UPEXTRA_NUMBER = "upexnumber";
	public static final String UPEXTRA_EMAIL = "upexemail";
	public static final String UPEXTRA_PHOTO = "upexphoto";
	public static final String UPEXTRA_CITY = "upexcity";
	public static final String UPEXTRA_DES = "upexdesignation";
	public static final String UPEXTRA_GROUP = "upexgroup";

	// alert dialog box
	public static AlertDialog showDialog(Context context, String title,
			String msg, String postitive, String negative,
			OnClickListener clickListener1, OnClickListener clickListener2) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton(postitive, clickListener1);
		if (negative != null && clickListener2 != null)
			builder.setNegativeButton(negative, clickListener2);

		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}

	
}
