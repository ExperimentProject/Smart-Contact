package com.rama.model;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rama.contactmanager.DetailsActivity;
import com.rama.contactmanager.R;
import com.ramananda.roundrawer.ColorGenerator;
import com.ramananda.roundrawer.TextDrawable;
import com.ramananda.utiles.Conts;
import com.ramananda.utiles.ListData;

public class ContactListAdapter extends BaseAdapter implements Filterable {

	NameFilter nameFilter;

	Bitmap circleBitmap;
	ListData positionItem;
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
	private TextDrawable.IBuilder mDrawableBuilder;

	Context context;
	ArrayList<ListData> contactData = new ArrayList<ListData>();
	ArrayList<ListData> contactSearch = new ArrayList<ListData>();

	public ContactListAdapter(Context context, ArrayList<ListData> data) {
		this.context = context;
		this.contactData = data;
		this.contactSearch = data;
		mDrawableBuilder = TextDrawable.builder().round();
	}

	@Override
	public int getCount() {
		return contactData.size();
	}

	@Override
	public ListData getItem(int position) {
		return contactData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_patern_layout, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		positionItem = getItem(position);

		// provide support for selected state
		updateCheckedState(holder, positionItem);
		holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// when the image is clicked, update the selected state
				ListData data = getItem(position);
				data.setChecked(!data.isChecked());
				updateCheckedState(holder, data);
			}
		});

		holder.callIcon.setOnClickListener(new CallListener(position));

		holder.textView.setText(positionItem.getData().getName());

		holder.textView.setOnClickListener(new DetailsListener(position));
		// holder.callIcon.setImageResource(R.drawable.ic_action_call);
		return convertView;
	}

	// add search filter

	@Override
	public Filter getFilter() {
		if (nameFilter == null) {
			nameFilter = new NameFilter();
		}
		return nameFilter;
	}

	private class NameFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (constraint != null && constraint.length() > 0) {
				ArrayList<ListData> filterList = new ArrayList<ListData>();
				for (int i = 0; i < contactSearch.size(); i++) {
					if ((contactSearch.get(i).getData().getName().toLowerCase())
							.contains(constraint.toString().toLowerCase())) {
						ListData words = new ListData(contactSearch.get(i)
								.getData());
						filterList.add(words);
					}
				}
				results.count = filterList.size();
				results.values = filterList;
			} else {
				results.count = contactSearch.size();
				results.values = contactSearch;
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			contactData = (ArrayList<ListData>) results.values;
			notifyDataSetChanged();
		}

	}

	private static class ViewHolder {

		private View view;
		private ImageView imageView;
		private TextView textView;
		private ImageView checkIcon;
		private ImageView callIcon;

		private ViewHolder(View view) {
			this.view = view;
			imageView = (ImageView) view.findViewById(R.id.imageView);
			textView = (TextView) view.findViewById(R.id.textView);
			checkIcon = (ImageView) view.findViewById(R.id.check_icon);
			callIcon = (ImageView) view.findViewById(R.id.callImage);
		}
	}

	// update check method

	private void updateCheckedState(ViewHolder holder, ListData item) {

		if (item.isChecked()) {
			holder.imageView.setImageDrawable(mDrawableBuilder.build(" ",
					0xff616161));
			// holder.view.setBackgroundColor(HIGHLIGHT_COLOR);
			// holder.checkIcon.setVisibility(View.VISIBLE);

			byte[] bs = item.getData().getImages();
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					bs);

			Bitmap imagesBitmap = BitmapFactory
					.decodeStream(byteArrayInputStream);
			circleBitmap = CircleCropBorder.getCroppedBitmap(imagesBitmap, 120);
			holder.view.setBackgroundColor(Conts.HIGHLIGHT_COLOR);
			holder.checkIcon.setVisibility(View.VISIBLE);
			holder.checkIcon.setImageBitmap(circleBitmap);
		} else {

			String fullName = String.valueOf(item.getData().getName());
			String firstName = String.valueOf(item.getData().getName()
					.charAt(0));
			String lastName = null;
			String finalText = null;
			int size = fullName.length();
			for (int i = 0; i < size; i++) {
				if (fullName.charAt(i) == ' ') {
					lastName = String.valueOf(fullName.charAt(i + 1));
					finalText = firstName.concat(lastName);
					break;
				} else {
					finalText = String.valueOf(item.getData().getName()
							.charAt(0));
				}
			}

			TextDrawable drawable = mDrawableBuilder.build(finalText,
					mColorGenerator.getColor(item.getData()));
			holder.imageView.setImageDrawable(drawable);
			holder.view.setBackgroundColor(Color.TRANSPARENT);
			holder.checkIcon.setVisibility(View.GONE);
		}
	}

	// set call listener

	private class CallListener implements OnClickListener {

		int position;

		public CallListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			try {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"
						+ contactData.get(position).getData().getPhone()));
				context.startActivity(callIntent);
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}

		}

	}

	// details contact class

	private class DetailsListener implements OnClickListener {

		int pos;

		public DetailsListener(int position) {
			this.pos = position;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, DetailsActivity.class);
			intent.putExtra(Conts.EXTRA_NAME, contactData.get(pos).getData()
					.getName());

			byte[] bs = contactData.get(pos).getData().getImages();

			intent.putExtra(Conts.EXTRA_PHOTO, bs);
			intent.putExtra(Conts.EXTRA_NUMBER, contactData.get(pos).getData()
					.getPhone());
			intent.putExtra(Conts.EXTRA_CITY, contactData.get(pos).getData()
					.getCity());
			intent.putExtra(Conts.EXTRA_DES, contactData.get(pos).getData()
					.getDesignation());
			intent.putExtra(Conts.EXTRA_EMAIL, contactData.get(pos).getData()
					.getEmail());
			context.startActivity(intent);
		}

	}
}
