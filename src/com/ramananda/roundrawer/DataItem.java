package com.ramananda.roundrawer;

import android.graphics.drawable.Drawable;

public class DataItem {
	private String label;
	private Drawable drawable;
	private int navigationInfo;

	public DataItem(String label, Drawable drawable, int navigationInfo) {
		this.label = label;
		this.drawable = drawable;
		this.navigationInfo = navigationInfo;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public int getNavigationInfo() {
		return navigationInfo;
	}

	public void setNavigationInfo(int navigationInfo) {
		this.navigationInfo = navigationInfo;
	}

}
