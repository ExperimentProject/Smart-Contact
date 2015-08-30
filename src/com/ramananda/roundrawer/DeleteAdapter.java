package com.ramananda.roundrawer;

import java.io.ByteArrayInputStream;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rama.contactmanager.R;
import com.rama.model.CircleCropBorder;
import com.rama.model.ContactItem;
import com.ramananda.utiles.Conts;
import com.ramandanda.helperdb.ContactDBHelper;

public class DeleteAdapter extends ArrayAdapter<ContactItem> {
	Context myContext;
	LayoutInflater inflater;

	List<ContactItem> DataList;
	ContactDBHelper dbHelper;
	private SparseBooleanArray mSelectedItemsIds;

	Bitmap circleBitmap;

	// Constructor for get Context and list

	public DeleteAdapter(Context context, List<ContactItem> lists) {

		super(context, R.layout.delete_items, lists);
		dbHelper = ContactDBHelper.getInstance(getContext());
		mSelectedItemsIds = new SparseBooleanArray();

		this.myContext = context;

		DataList = lists;
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		return DataList.size();
	}

	@Override
	public ContactItem getItem(int position) {
		return DataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return DataList.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) myContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.delete_items, null);
			holder.name = (TextView) convertView.findViewById(R.id.deleteText);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.deleteImage);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(DataList.get(position).getName());

		byte[] bs = DataList.get(position).getImages();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bs);

		Bitmap imagesBitmap = BitmapFactory.decodeStream(byteArrayInputStream);
		circleBitmap = CircleCropBorder.getCroppedBitmap(imagesBitmap, 120);
		holder.imageView.setImageBitmap(circleBitmap);
		convertView
				.setBackgroundColor(mSelectedItemsIds.get(position) ? Conts.HIGHLIGHT_COLOR
						: color.transparent);
		return convertView;
	}

	@Override
	public void remove(ContactItem key) {
		dbHelper.deleteContact(key.getName());
		notifyDataSetChanged();
	}

	public List<ContactItem> getMyList() {
		return DataList;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void selectView(int position, boolean value) {
		if (value) {
			mSelectedItemsIds.put(position, value);
		} else {
			mSelectedItemsIds.delete(position);
		}
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public int getSelectCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectIds() {
		return mSelectedItemsIds;
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public class ViewHolder {
		View v;
		TextView name;
		ImageView imageView;
	}
}
