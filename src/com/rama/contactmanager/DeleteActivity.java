package com.rama.contactmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

import com.rama.model.ContactItem;
import com.ramananda.roundrawer.DeleteAdapter;
import com.ramandanda.helperdb.ContactDBHelper;

public class DeleteActivity extends Activity {

	ContactDBHelper dbHelper;
	DeleteAdapter adapter;
	ArrayList<ContactItem> dataList;

	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete);
		lv = (ListView) findViewById(R.id.delete_list);

		dbHelper = ContactDBHelper.getInstance(getApplicationContext());

		dataList = new ArrayList<ContactItem>();
		dataList = dbHelper.getAllContacts();

		adapter = new DeleteAdapter(getApplicationContext(), dataList);

		lv.setAdapter(adapter);

		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lv.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				adapter.removeSelection();
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.delete, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem menu) {
				switch (menu.getItemId()) {
				case R.id.action_delete_items:
					SparseBooleanArray selected = adapter.getSelectIds();

					for (int i = (selected.size() - 1); i >= 0; i--) {
						if (selected.valueAt(i)) {
							ContactItem selectedItem = adapter.getItem(selected
									.keyAt(i));
							adapter.remove(selectedItem);
						}
					}
					mode.finish();
					Intent i = new Intent(DeleteActivity.this,
							MainActivity.class);

					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					return true;

				default:
					return false;
				}

			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				final int checkedCount = lv.getCheckedItemCount();
				mode.setTitle(checkedCount + " Selected");
				adapter.toggleSelection(position);
				if (checked) {

				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
