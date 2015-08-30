package com.rama.contactmanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.rama.model.CircleCropBorder;
import com.rama.model.ContactItem;
import com.ramandanda.helperdb.ContactDBHelper;

@SuppressLint("DefaultLocale")
public class AddContact extends Activity {

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;

	private EditText etName, etEmail, etMobile, etDesignation, etCity, etPhone;
	Button btnAnother;
	Spinner sp;
	ArrayAdapter<String> groupAdapter;
	ContactDBHelper contactDBHelper;

	// initialize circle image
	private Bitmap bitmap, circleBitmap;
	private ImageView imageView;

	// set visibility state

	String[] items = new String[] { "Phone", "City", "Designation" };

	ArrayAdapter<String> viewAdapter;
	AlertDialog a;
	AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.add_contact_activity);

		btnAnother = (Button) findViewById(R.id.btnAnother);
		etName = (EditText) findViewById(R.id.contactName);
		etMobile = (EditText) findViewById(R.id.contactPhoneNo);
		etEmail = (EditText) findViewById(R.id.contactEmail);
		etCity = (EditText) findViewById(R.id.contactCity);
		etDesignation = (EditText) findViewById(R.id.contactDescription);
		etPhone = (EditText) findViewById(R.id.contactHome);

		etPhone.setVisibility(View.GONE);
		etDesignation.setVisibility(View.GONE);
		etCity.setVisibility(View.GONE);

		// set db instance

		contactDBHelper = ContactDBHelper.getInstance(getApplicationContext());

		imageView = (ImageView) findViewById(R.id.imageView);
		sp = (Spinner) findViewById(R.id.spSelectGroup);

		groupAdapter = new ArrayAdapter<String>(AddContact.this,
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.group_list));
		sp.setAdapter(groupAdapter);

		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.person2);
		circleBitmap = CircleCropBorder.getCroppedBitmap(bitmap, 120);

		imageView.setImageBitmap(circleBitmap);
		imageView.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});

		// set items
		viewAdapter = new ArrayAdapter<String>(AddContact.this,
				android.R.layout.simple_list_item_1, items);

		createAnotherOption();
	}

	public void setAnother(View v) {
		a.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_done:
			insertData();
			break;
		case R.id.action_discard:
			Intent intent = new Intent(AddContact.this, MainActivity.class);
			startActivity(intent);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void insertData() {
		String name = etName.getText().toString();
		String mobile = etMobile.getText().toString();
		String email = etEmail.getText().toString();
		String spGroup = sp.getSelectedItem().toString();
		if (circleBitmap == null) {
			circleBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.person2);
		}
		// convert bitmap to byte
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		circleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte imageInByte[] = stream.toByteArray();

		String city = null;
		String designation = null;
		String phone = null;
		if (etCity.getVisibility() == View.VISIBLE) {
			city = etCity.getText().toString();
		}
		if (etDesignation.getVisibility() == View.VISIBLE) {
			designation = etDesignation.getText().toString();
		}
		if (etPhone.getVisibility() == View.VISIBLE) {
			phone = etPhone.getText().toString();
		}

		if (!name.equals("") && !mobile.equals("")) {
			boolean exist = contactDBHelper.existName(name);
			if (exist) {
				Toast.makeText(getApplicationContext(), "name already exist",
						Toast.LENGTH_LONG).show();
			} else {
				ContactItem contacts = new ContactItem(name, mobile, phone,
						email, imageInByte, designation, city, spGroup);
				long insert = contactDBHelper.insertContacts(contacts);

				if (insert > 0) {
					Toast.makeText(getApplicationContext(), "data save sucess",
							Toast.LENGTH_LONG).show();
					Intent in = new Intent(AddContact.this, MainActivity.class);
					in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(in);
				} else {
					Toast.makeText(getApplicationContext(), "data save failed",
							Toast.LENGTH_LONG).show();
				}
			}

		}

	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Gellary",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(AddContact.this);
		builder.setTitle("Add Photo !");
		builder.setItems(items, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (items[which].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, PICK_FROM_CAMERA);

				} else if (items[which].equals("Choose from Gellary")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							PICK_FROM_GALLERY);
				} else if (items[which].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == PICK_FROM_GALLERY) {
				onSelectFromGalleryResult(data);
			} else if (requestCode == PICK_FROM_CAMERA) {
				onCaptureImageResult(data);
			}
		}
	}

	public void createAnotherOption() {

		builder = new AlertDialog.Builder(this);
		// builder.setIcon(R.drawable.ic_launcher);
		// builder.setTitle("Select Font Size");
		builder.setAdapter(viewAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selected = items[which];
				if (selected.equals("Designation")) {
					etDesignation.setVisibility(View.VISIBLE);
					// set items
					designationDialog();

				} else if (selected.equals("Phone")) {
					etPhone.setVisibility(View.VISIBLE);
					phoneDialog();
				} else if (selected.equals("City")) {
					etCity.setVisibility(View.VISIBLE);
					cityDialog();
				}
			}
		});
		a = builder.create();
	}

	public void designationDialog() {
		if (etPhone.getVisibility() == View.VISIBLE
				&& etCity.getVisibility() == View.VISIBLE
				&& etDesignation.getVisibility() == View.VISIBLE) {
			btnAnother.setVisibility(View.GONE);
		} else if (etPhone.getVisibility() == View.VISIBLE) {
			items = new String[] { "City" };
		} else if (etCity.getVisibility() == View.VISIBLE) {
			items = new String[] { "Phone" };
		} else {
			items = new String[] { "Phone", "City" };
		}
		viewAdapter = new ArrayAdapter<String>(AddContact.this,
				android.R.layout.simple_list_item_1, items);

		builder = new AlertDialog.Builder(this);
		// builder.setIcon(R.drawable.ic_launcher);
		// builder.setTitle("Select Font Size");
		builder.setAdapter(viewAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selected = items[which];
				if (selected.equals("Phone")) {
					etPhone.setVisibility(View.VISIBLE);
					phoneDialog();
				} else if (selected.equals("City")) {
					etCity.setVisibility(View.VISIBLE);
					cityDialog();
				}
			}
		});
		a = builder.create();
	}

	public void phoneDialog() {
		if (etDesignation.getVisibility() == View.VISIBLE
				&& etCity.getVisibility() == View.VISIBLE
				&& etPhone.getVisibility() == View.VISIBLE) {
			btnAnother.setVisibility(View.GONE);
		} else if (etDesignation.getVisibility() == View.VISIBLE) {
			items = new String[] { "City" };
		} else if (etCity.getVisibility() == View.VISIBLE) {
			items = new String[] { "Designation" };
		} else {
			items = new String[] { "City", "Designation" };
		}

		viewAdapter = new ArrayAdapter<String>(AddContact.this,
				android.R.layout.simple_list_item_1, items);

		builder = new AlertDialog.Builder(this);
		// builder.setIcon(R.drawable.ic_launcher);
		// builder.setTitle("Select Font Size");
		builder.setAdapter(viewAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selected = items[which];
				if (selected.equals("Designation")) {
					etDesignation.setVisibility(View.VISIBLE);

					// set items
					designationDialog();
				} else if (selected.equals("City")) {
					etCity.setVisibility(View.VISIBLE);
					cityDialog();
				}
			}
		});
		a = builder.create();
	}

	public void cityDialog() {
		if (etDesignation.getVisibility() == View.VISIBLE
				&& etPhone.getVisibility() == View.VISIBLE
				&& etCity.getVisibility() == View.VISIBLE) {
			btnAnother.setVisibility(View.GONE);
		} else if (etDesignation.getVisibility() == View.VISIBLE) {
			items = new String[] { "Phone" };
		} else if (etPhone.getVisibility() == View.VISIBLE) {
			items = new String[] { "Designation" };
		} else {
			items = new String[] { "Phone", "Designation" };
		}

		viewAdapter = new ArrayAdapter<String>(AddContact.this,
				android.R.layout.simple_list_item_1, items);

		builder = new AlertDialog.Builder(this);
		builder.setAdapter(viewAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selected = items[which];
				if (selected.equals("Designation")) {
					etDesignation.setVisibility(View.VISIBLE);

					// set items
					designationDialog();
				} else if (selected.equals("Phone")) {
					etPhone.setVisibility(View.VISIBLE);
					phoneDialog();
				}
			}
		});
		a = builder.create();
	}

	// get image from gallery

	public void onSelectFromGalleryResult(Intent data) {
		Uri selectUriImage = data.getData();

		String[] projection = { MediaColumns.DATA };

		Cursor cursor = managedQuery(selectUriImage, projection, null, null,
				null);

		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		String selectImagePath = cursor.getString(column_index);

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(selectImagePath, options);

		final int REQUIRED_SIZE = 250;

		int scale = 1;
		while (options.outWidth / scale / 2 > REQUIRED_SIZE
				&& options.outHeight / scale / 2 >= REQUIRED_SIZE) {
			scale *= 2;
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(selectImagePath, options);

		circleBitmap = CircleCropBorder.getCroppedBitmap(bitmap, 120);
		imageView.setImageBitmap(circleBitmap);
	}

	// get image for camera
	public void onCaptureImageResult(Intent data) {
		bitmap = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		File designation = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".jpg");

		FileOutputStream fo;

		try {
			designation.createNewFile();
			fo = new FileOutputStream(designation);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		circleBitmap = CircleCropBorder.getCroppedBitmap(bitmap, 120);
		imageView.setImageBitmap(circleBitmap);
	}

}
