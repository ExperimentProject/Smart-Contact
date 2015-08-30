package com.rama.contactmanager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.rama.model.ContactItem;
import com.rama.model.ContactListAdapter;
import com.ramananda.utiles.ListData;
import com.ramandanda.helperdb.ContactDBHelper;

public class MainActivity extends FragmentActivity {

	ArrayList<ContactItem> contacts;
	ContactDBHelper dbHelper;
	// DrawImage resizeImage;
	ArrayList<ListData> dataList;

	ContactListAdapter adapter;

	Bitmap bitmap;
	ListView lv;
	boolean status = true;
	SharedPreferences preferences;

	// private static final String myPreference = "Mypreference";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);

		getActionBar().setSplitBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00968C")));

		dbHelper = ContactDBHelper.getInstance(getApplicationContext());
		// resizeImage = new DrawImage();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		status = preferences.getBoolean("status", true);

		// sync contact

		if (status) {
			LoadContactsAyscn load = new LoadContactsAyscn(this);
			load.execute();
		}

		contacts = new ArrayList<ContactItem>();
		dataList = new ArrayList<ListData>();

		// set list value
		setDataList();

		lv.setTextFilterEnabled(true);

	}

	private void setDataList() {
		contacts = dbHelper.getAllContacts();
		for (ContactItem cons : contacts) {
			ListData ds = new ListData(cons);
			dataList.add(ds);
		}

		lv = (ListView) findViewById(R.id.contactList);
		adapter = new ContactListAdapter(this, dataList);
		lv.setAdapter(adapter);
	}

	// add contact in contact list

	class LoadContactsAyscn extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pd;

		Context c;

		public LoadContactsAyscn(Context c) {
			this.c = c;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pd = ProgressDialog.show(MainActivity.this, "Loading Contacts",
					"Please Wait.....");

		}

		@Override
		protected Boolean doInBackground(Void... params) {

			Cursor c = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);

			while (c.moveToNext()) {
				String contactName = c
						.getString(c
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phNumber = c
						.getString(c
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				// String image_uri = c
				// .getString(c
				// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
				//
				// if (image_uri != null) {
				// bitmap = resizeImage.retriveImage(image_uri);
				// }

				if (bitmap == null) {
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.person2);
				}
				// convert bitmap to byte
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte imageInByte[] = stream.toByteArray();

				ContactItem items = new ContactItem(contactName, phNumber,
						null, null, imageInByte, null, null, null);

				dbHelper.insertContacts(items);

			}
			c.close();
			status = false;
			return status;
		}

		@Override
		protected void onPostExecute(Boolean contacts) {
			super.onPostExecute(contacts);

			preferences = PreferenceManager.getDefaultSharedPreferences(c);
			SharedPreferences.Editor editor = preferences.edit();

			editor.putBoolean("status", false);
			editor.commit();

			setDataList();

			pd.cancel();

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_new:
			Intent intent = new Intent(MainActivity.this, AddContact.class);
			startActivity(intent);
			break;
		case R.id.action_delete_multiple:
			Intent del = new Intent(MainActivity.this, DeleteActivity.class);
			startActivity(del);
			break;
		case R.id.action_search:
			Intent search = new Intent(MainActivity.this, DeleteActivity.class);
			startActivity(search);
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// set search menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		// searchView.setBackground(getResources().getStringArray(id));
		searchView.setIconifiedByDefault(true);

		SearchView.OnQueryTextListener textListener = new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String newText) {
				adapter.getFilter().filter(newText);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				adapter.getFilter().filter(query);
				return true;
			}
		};
		searchView.setOnQueryTextListener(textListener);
		return super.onCreateOptionsMenu(menu);
	}

}
