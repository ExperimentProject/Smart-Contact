package com.rama.contactmanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.ActionBar;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.rama.model.CircleCropBorder;
import com.rama.model.ContactItem;
import com.ramananda.utiles.Conts;
import com.ramandanda.helperdb.ContactDBHelper;

public class EditActivity extends Activity {

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;

	private EditText etName, etEmail, etDesignation, etCity, etNumber, etHome;
	private Spinner sp;
	ArrayAdapter<String> groupAdapter;
	ContactDBHelper contactDBHelper;

	// initialize circle image
	Bitmap bitmap, circleBitmap;
	ImageView imageView;

	ArrayAdapter<String> viewAdapter;
	AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.edit_activity);

		etName = (EditText) findViewById(R.id.upContactName);
		etNumber = (EditText) findViewById(R.id.upContactPhoneNo);
		etEmail = (EditText) findViewById(R.id.upContactEmail);
		etCity = (EditText) findViewById(R.id.upContactCity);
		etDesignation = (EditText) findViewById(R.id.upContactDescription);
		etHome = (EditText) findViewById(R.id.upContactHome);
		imageView = (ImageView) findViewById(R.id.upimageView);

		// set db instance
		contactDBHelper = ContactDBHelper.getInstance(getApplicationContext());

		sp = (Spinner) findViewById(R.id.upSpSelectGroup);

		ActionBar actionBar = getActionBar();
		// remove icon
		actionBar.setIcon(android.R.color.transparent);
		// actionBar.setIcon(R.drawable.ic_action_back);

		// set extra value

		setAllInfo();

		imageView.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.edit_done:
			updateContactValue();
			break;
		case R.id.edit_discard:
			Intent intent = new Intent(EditActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void setAllInfo() {

		byte[] img = getIntent().getByteArrayExtra(Conts.UPEXTRA_PHOTO);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				img);

		bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
		circleBitmap = CircleCropBorder.getCroppedBitmap(bitmap, 120);
		imageView.setImageBitmap(circleBitmap);
		etName.setText(getIntent().getStringExtra(Conts.UPEXTRA_NAME));
		etEmail.setText(getIntent().getStringExtra(Conts.UPEXTRA_EMAIL));
		etDesignation.setText(getIntent().getStringExtra(Conts.UPEXTRA_DES));
		etCity.setText(getIntent().getStringExtra(Conts.UPEXTRA_CITY));
		etNumber.setText(getIntent().getStringExtra(Conts.UPEXTRA_NUMBER));

	}

	private void updateContactValue() {
		String updateKey = getIntent().getStringExtra(Conts.UPEXTRA_NUMBER);

		String name = etName.getText().toString();
		String email = etEmail.getText().toString();
		String number = etNumber.getText().toString();
		String city = etCity.getText().toString();
		String designation = etDesignation.getText().toString();
		if (circleBitmap == null) {
			circleBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.person2);
		}
		// convert bitmap to byte
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		circleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte imageInByte[] = stream.toByteArray();

		ContactItem updateContact = new ContactItem(name, number, email,
				imageInByte, designation, city);

		int updated = contactDBHelper.updateContact(updateKey, updateContact);
		if (updated > 0) {
			Toast.makeText(getApplicationContext(),
					updated + "  row updated sucess", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(EditActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), updated + " no updated",
					Toast.LENGTH_LONG).show();
		}

	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Gellary",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
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
