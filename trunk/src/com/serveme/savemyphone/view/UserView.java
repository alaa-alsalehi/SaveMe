package com.serveme.savemyphone.view;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.view.BaseActivity;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.util.MyTracker;

public class UserView extends FrameLayout {

	private List<Launcher> appsinfolist;
	private DBOperations db;
	private GridAdapter ga;
	private GridView gridView;
	private Launcher[] launchers;
	private List<Launcher> currentLaunchers = new ArrayList<Launcher>();
	// „”ƒÊ· ⁄‰  ÕœÌÀ «·»—«„Ã
	// Õ Ï Ê≈‰ ﬂ«‰ «·»—‰«„Ã Ì⁄„· Õ«·Ì«
	DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			if (ga != null) {
				appsinfolist.clear();
				appsinfolist.addAll(db.getWhiteListApps());
				ga.notifyDataSetChanged();
			}
		}

		public void onInvalidated() {
			super.onInvalidated();
			if (ga != null) {
				appsinfolist.clear();
				appsinfolist.addAll(db.getWhiteListApps());
				ga.notifyDataSetInvalidated();
			}

		}
	};

	public UserView(Context context) {
		super(context);
		intialize();
	}

	public UserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		intialize();
	}

	public UserView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		intialize();
	}

	public void intialize() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.user_activity, this, true);
		db = DBOperations.getInstance(getContext());
		appsinfolist = new ArrayList<Launcher>();
		gridView = (GridView) view.findViewById(R.id.grid_view);
		appsinfolist.addAll(db.getWhiteListApps());
		ga = new GridAdapter(getContext(), appsinfolist);
		gridView.setAdapter(ga);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Launcher launcher = appsinfolist.get(position);
				try {
					Intent i = new Intent(getContext(), BaseActivity.class);
					i.putExtra("package", launcher.getPackageName());
					i.putExtra("activity", launcher.getActivity());
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
					getContext().startActivity(i);
					MyTracker.fireButtonPressedEvent(getContext(), "run_app");
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getContext(), "Application not Installed",
							Toast.LENGTH_LONG).show();
					db.deleteLauncher(launcher);
				}
			}
		});

		final ImageButton unlock = (ImageButton) view.findViewById(R.id.unlock);

		unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent userActivityIntent = new Intent(getContext(),
						UserActivity.class);
				userActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(userActivityIntent);
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		appsinfolist.clear();
		appsinfolist.addAll(db.getWhiteListApps());
		db.registerObserver(dataSetObserver);
		currentLaunchers.clear();
		final ImageButton unlock = (ImageButton) findViewById(R.id.unlock);
		SharedPreferences preferences = getContext().getSharedPreferences(
				"mypref", Context.MODE_PRIVATE);
		boolean hiddenLock = preferences
				.getBoolean("hidden_lock_active", false);
		if (hiddenLock) {
			unlock.setVisibility(View.GONE);
			launchers = new Gson().fromJson(
					preferences.getString("hidden_lock", null),
					new GenericArrayType() {

						@Override
						public Type getGenericComponentType() {

							return Launcher.class;
						}
					});
			gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					currentLaunchers.add(appsinfolist.get(position));
					for (int i = 0; i < currentLaunchers.size(); i++) {
						if (!launchers[i].equals(currentLaunchers.get(i))) {
							currentLaunchers.clear();
							return true;
						}
					}
					if (currentLaunchers.size() == launchers.length) {
						unlock.setVisibility(View.VISIBLE);
					}
					return true;
				}
			});
		} else {
			unlock.setVisibility(View.VISIBLE);
			gridView.setOnItemLongClickListener(null);
		}
		ga.notifyDataSetInvalidated();
		MyTracker.setUncaughtExceptionHandler(getContext());
		//  ÕœÌÀ «·ﬁ«∆„… ›Ì Õ«·  €ÌÌ— Ê÷⁄ SDCard
		getContext().registerReceiver(refreshList,
				new IntentFilter("refresh_white_list"));
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getContext().unregisterReceiver(refreshList);
		db.unregisterObserver(dataSetObserver);
	}

	private final BroadcastReceiver refreshList = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			appsinfolist.clear();
			appsinfolist.addAll(db.getWhiteListApps());
			ga.notifyDataSetInvalidated();
		}
	};
}
