package com.ramandanda.helperdb;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rama.model.ContactItem;

public class ContactDBHelper extends SQLiteOpenHelper {

	private static ContactDBHelper dbInstance;

	private static final String DATABASE_NAME = "contactdatabase.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_CONTACTS = "tbl_contacts";

	private static final String KEY_ID = "id";
	private static final String NAME_FIELD = "contactName";
	private static final String PHONE_FIELD = "contactNo";
	private static final String HOME_PH_FIELD = "contactNoHome";
	private static final String IMAGE_FIELD = "image";
	private static final String EMAIL_FIELD = "contactEmail";
	private static final String DESIGNATION_FIELD = "contactDesignation";
	private static final String CITY_FIELD = "contactCity";
	private static final String GROUP_FIELD = "contactGroup";

	private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE "
			+ TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ NAME_FIELD + " TEXT," + PHONE_FIELD + " TEXT," + HOME_PH_FIELD
			+ " TEXT," + IMAGE_FIELD + " BLOB," + EMAIL_FIELD + " TEXT,"
			+ DESIGNATION_FIELD + " TEXT," + CITY_FIELD + " TEXT,"
			+ GROUP_FIELD + " TEXT" + ")";

	private ContactDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static ContactDBHelper getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new ContactDBHelper(context);
		}
		return dbInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_CONTACTS_TABLE);
		onCreate(db);
	}

	public long insertContacts(ContactItem items) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NAME_FIELD, items.getName());
		values.put(PHONE_FIELD, items.getPhone());
		values.put(HOME_PH_FIELD, items.getHomePhone());
		values.put(IMAGE_FIELD, items.getImages());
		values.put(EMAIL_FIELD, items.getEmail());
		values.put(DESIGNATION_FIELD, items.getDesignation());
		values.put(CITY_FIELD, items.getCity());
		values.put(GROUP_FIELD, items.getGroup());

		long inserted = db.insert(TABLE_CONTACTS, null, values);
		db.close();
		return inserted;

	}

	public ArrayList<ContactItem> getAllContacts() {
		ArrayList<ContactItem> allContats = new ArrayList<ContactItem>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS, null, null, null, null, null,
				NAME_FIELD + " ASC");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String name = cursor.getString(cursor
						.getColumnIndex(NAME_FIELD));
				String email = cursor.getString(cursor
						.getColumnIndex(EMAIL_FIELD));
				String phone = cursor.getString(cursor
						.getColumnIndex(PHONE_FIELD));
				String homePhone = cursor.getString(cursor
						.getColumnIndex(HOME_PH_FIELD));
				byte[] image = cursor.getBlob(cursor
						.getColumnIndex(IMAGE_FIELD));
				String designation = cursor.getString(cursor
						.getColumnIndex(DESIGNATION_FIELD));
				String city = cursor.getString(cursor
						.getColumnIndex(CITY_FIELD));
				String group = cursor.getString(cursor
						.getColumnIndex(GROUP_FIELD));

				ContactItem e = new ContactItem(name, phone, homePhone, email,
						image, designation, city, group);

				allContats.add(e);
				cursor.moveToNext();

			}
		}
		cursor.close();
		db.close();
		return allContats;

	}

	// delete contact
	public void deleteContact(String key) {

		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_CONTACTS, NAME_FIELD + " = ?",
				new String[] { "" + key });
		db.close();
	}

	// Search
	public boolean existName(String nameKey) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS, null, NAME_FIELD + "=?",
				new String[] { "" + nameKey }, null, null, null);
		// UserName Not Exist
		if (cursor.getCount() < 1) {
			cursor.close();
			return false;
		}
		// cursor.moveToFirst();
		// String getName = cursor.getString(cursor.getColumnIndex(NAME_FIELD));

		cursor.close();
		db.close();
		return true;
	}

	// ..............update data base method...................
	public int updateContact(String key, ContactItem contact) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NAME_FIELD, contact.getName());
		values.put(EMAIL_FIELD, contact.getEmail());
		values.put(PHONE_FIELD, contact.getPhone());
		values.put(IMAGE_FIELD, contact.getImages());
		values.put(CITY_FIELD, contact.getCity());
		values.put(DESIGNATION_FIELD, contact.getDesignation());

		int updated = db.update(TABLE_CONTACTS, values, PHONE_FIELD + "=?",
				new String[] { key });

		db.close();
		return updated;
	}

}
