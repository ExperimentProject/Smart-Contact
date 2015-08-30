package com.rama.contactmanager;

import java.io.ByteArrayInputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rama.model.CircleCropBorder;
import com.ramananda.utiles.Conts;
import com.ramandanda.helperdb.ContactDBHelper;

public class DetailsActivity extends Activity {

	TextView txtName, txtEmail, txtCity, txtDesignation, txtNumber;
	ImageView imgPhoto, imgCall;

	String name, email, number, designation, city;
	byte[] img;

	ContactDBHelper dbHelper;

	private Bitmap bitmap, circleBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.details_activity);
		initializeAll();

		dbHelper = ContactDBHelper.getInstance(getApplicationContext());

		ActionBar actionBar = getActionBar();
		// remove icon
		actionBar.setIcon(android.R.color.transparent);
		// actionBar.setIcon(R.drawable.ic_action_back);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);

		setAllInfo();
	}

	private void setAllInfo() {
		name = getIntent().getStringExtra(Conts.EXTRA_NAME);
		email = getIntent().getStringExtra(Conts.EXTRA_EMAIL);
		number = getIntent().getStringExtra(Conts.EXTRA_NUMBER);
		city = getIntent().getStringExtra(Conts.EXTRA_CITY);
		designation = getIntent().getStringExtra(Conts.EXTRA_DES);

		img = getIntent().getByteArrayExtra(Conts.EXTRA_PHOTO);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				img);

		bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
		bitmap = CircleCropBorder.getCroppedBitmap(bitmap, 120);
		imgPhoto.setImageBitmap(bitmap);
		txtName.setText(name);

		txtEmail.setText(email);
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.person2);
			circleBitmap = CircleCropBorder.getCroppedBitmap(bitmap, 120);

			imgPhoto.setImageBitmap(circleBitmap);
		}

		txtNumber.setText(number);
		txtCity.setText(city);
		txtDesignation.setText(designation);

	}

	private void initializeAll() {
		txtName = (TextView) findViewById(R.id.txtName);
		imgPhoto = (ImageView) findViewById(R.id.txtPersonImage);
		txtNumber = (TextView) findViewById(R.id.txtNumber);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtDesignation = (TextView) findViewById(R.id.txtDesignation);
		txtCity = (TextView) findViewById(R.id.txtCity);

		imgCall = (ImageView) findViewById(R.id.img_call);

		imgCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + number));
				startActivity(callIntent);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.action_edit:
			setEditData();
			break;
		case R.id.action_delete:
			deleteContact();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setEditData() {
		Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
		intent.putExtra(Conts.UPEXTRA_NAME, name);
		intent.putExtra(Conts.UPEXTRA_PHOTO, img);
		intent.putExtra(Conts.UPEXTRA_NUMBER, number);
		intent.putExtra(Conts.UPEXTRA_CITY, city);
		intent.putExtra(Conts.UPEXTRA_DES, designation);
		intent.putExtra(Conts.UPEXTRA_EMAIL, email);

		startActivity(intent);
	}

	private void deleteContact() {
		Conts.showDialog(this, "Delete Contact", "Are u sure !", "Yes", "No",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String deletName = getIntent().getStringExtra(
								Conts.EXTRA_NAME);
						dbHelper.deleteContact(deletName);
						Intent deleteIntent = new Intent(DetailsActivity.this,
								MainActivity.class);
						deleteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(deleteIntent);
					}
				}, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				});
	}

}
