package com.serveme.savemyphone.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.R.id;
import com.serveme.savemyphone.R.layout;
import com.serveme.savemyphone.R.string;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;

public class UnlockSettingsActivity extends ActionBarActivity {
	private List<Launcher> appsinfolist;
	private DBOperations db;
	private GridAdapter ga;
	private GridView gridView;
	private List<Launcher> launchers = new ArrayList<Launcher>();
	private List<Launcher> confirmLaunchers = new ArrayList<Launcher>();
	private boolean confirm;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toast = Toast.makeText(UnlockSettingsActivity.this,
				R.string.wrong_hidden_lock, Toast.LENGTH_LONG);
		setContentView(R.layout.activity_unlock_settings);
		db = DBOperations.getInstance(this);
		appsinfolist = new ArrayList<Launcher>();
		gridView = (GridView) findViewById(R.id.grid_view);
		appsinfolist.addAll(db.getWhiteListApps());
		ga = new GridAdapter(this, appsinfolist);
		gridView.setAdapter(ga);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);

		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setEnabled(false);
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Launcher launcher = appsinfolist.get(position);
				String step = getResources().getString(R.string.step);

				if (confirm) {
					confirmLaunchers.add(launcher);
					toast.cancel();
					toast = Toast.makeText(UnlockSettingsActivity.this, step
							+ " " + confirmLaunchers.size(), Toast.LENGTH_LONG);
					toast.show();
				} else {
					launchers.add(launcher);
					if (launchers.size() >= 2) {
						confirmButton.setEnabled(true);
					} else {
						confirmButton.setEnabled(false);
					}
					toast.cancel();
					toast = Toast.makeText(UnlockSettingsActivity.this, step
							+ " " + launchers.size(), Toast.LENGTH_LONG);
					toast.show();
				}
				return true;
			}
		});

		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((Button) v).getText().equals(
						getResources().getString(R.string.continu))) {
					((Button) v).setText(getResources().getString(
							R.string.confirm));
					((TextView) findViewById(R.id.hidden_lock_title))
							.setText(R.string.repeat_hidden_lock_message);
					confirm = true;
				} else {
					// set hidden pattern
					if (launchers.size() != confirmLaunchers.size()) {
						confirm = false;
						((Button) v).setText(getResources().getString(
								R.string.continu));
						toast.cancel();
						toast = Toast.makeText(UnlockSettingsActivity.this,
								R.string.wrong_hidden_lock, Toast.LENGTH_LONG);
						toast.show();
						((TextView) findViewById(R.id.hidden_lock_title))
								.setText(R.string.hidden_lock_message);
						confirmButton.setEnabled(false);
						confirmLaunchers.clear();
						launchers.clear();
						return;
					}
					for (int i = 0; i < launchers.size(); i++) {
						if (!launchers.get(i).equals(confirmLaunchers.get(i))) {
							confirm = false;
							((Button) v).setText(getResources().getString(
									R.string.continu));
							((TextView) findViewById(R.id.hidden_lock_title))
									.setText(R.string.hidden_lock_message);
							toast.cancel();
							toast = Toast.makeText(UnlockSettingsActivity.this,
									R.string.wrong_hidden_lock,
									Toast.LENGTH_LONG);
							toast.show();
							confirmButton.setEnabled(false);
							confirmLaunchers.clear();
							launchers.clear();
							return;
						}
					}
					SharedPreferences preferences = getSharedPreferences(
							"mypref", Context.MODE_PRIVATE);
					Editor edit = preferences.edit();
					edit.putBoolean("hidden_lock_active", true);
					edit.putString("hidden_lock", new Gson().toJson(launchers));

					edit.apply();
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		});
	}

}
