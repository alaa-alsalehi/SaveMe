package com.serveme.savemyphone.view;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.util.MyTracker;

public class UserView extends FrameLayout {

	private List<Launcher> appsinfolist;
	private DBOperations db;
	private GridAdapter ga;
	private GridView gridView;

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
				try {
					Launcher launcher = appsinfolist.get(position);
					Intent i = new Intent();
					i.setAction(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					i.setClassName(launcher.getPackageName(),
							launcher.getActivity());
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getContext().startActivity(i);
					MyTracker.fireButtonPressedEvent(getContext(), "run_app");
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getContext(), "Application not Installed",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		ImageButton unlock=(ImageButton) view.findViewById(R.id.unlock);
		unlock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent userActivityIntent=new Intent(getContext(),UserActivity.class);
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
		ga.notifyDataSetInvalidated();
		MyTracker.getUncaughtExceptionHandler();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
