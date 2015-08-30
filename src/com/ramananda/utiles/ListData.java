package com.ramananda.utiles;

import com.rama.model.ContactItem;

public class ListData {
	private ContactItem data;

	private boolean isChecked;

	public ListData(ContactItem data) {
		this.data = data;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public ContactItem getData() {
		return data;
	}

	public void setData(ContactItem data) {
		this.data = data;
	}

	public boolean isChecked() {
		return isChecked;
	}
	
	
}
