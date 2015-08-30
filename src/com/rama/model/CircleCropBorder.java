package com.rama.model;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

public class CircleCropBorder {
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		Log.d("rnd", "GETTING CROP:" + "mStrokeColor");
		if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
			float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
			float factor = smallest / radius;
			sbmp = Bitmap.createScaledBitmap(bmp,
					(int) (bmp.getWidth() / factor),
					(int) (bmp.getHeight() / factor), false);
		} else {
			sbmp = bmp;
		}

		Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Paint stroke = new Paint();

		final Rect rect = new Rect(0, 0, radius, radius);

		paint.setAntiAlias(true);
		stroke.setAntiAlias(true);

		paint.setFilterBitmap(true);
		stroke.setFilterBitmap(true);

		paint.setDither(true);
		stroke.setDither(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		stroke.setColor(Color.parseColor("#008000"));
		stroke.setStyle(Style.STROKE);
		stroke.setStrokeWidth(2f);
		canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
				radius / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f, radius / 2
				- stroke.getStrokeWidth() / 2 + 0.1f, stroke);

		return output;
	}
}