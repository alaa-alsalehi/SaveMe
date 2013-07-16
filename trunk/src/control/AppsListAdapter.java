package control;

import java.util.List;

import view.AdminActivity;

import com.serveme.savemyphone.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AppsListAdapter extends BaseAdapter {

	private Context context;
	private List<ResolveInfo> aList = null;
	private List<String> whitelist = null;
	private static boolean[] status;
	private LayoutInflater inflater = null;

	// Constructor
	public AppsListAdapter(Context c) {
		this.context = c;
		Intent in = new Intent(Intent.ACTION_MAIN);
		in.addCategory(Intent.CATEGORY_LAUNCHER);
		aList = c.getPackageManager().queryIntentActivities(in, 0);
		whitelist = AdminActivity.getWhiteList();
		status = new boolean[aList.size()];
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return aList.size();
	}

	@Override
	public Object getItem(int position) {
		return aList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder; // more performance for ListView by use Holder
								// Pattern

		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			convertView = inflater.inflate(R.layout.item_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.tg = (ToggleButton) convertView
					.findViewById(R.id.enable_disable);
			// first you set Tag Not get it
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final ResolveInfo appinfo = aList.get(position);
		
		viewHolder.tg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					status[position] = true;
					
					if (!whitelist.contains(appinfo.activityInfo.packageName)) {
						AdminActivity.getDBOperator().insertˆApp(	appinfo.activityInfo.packageName);
						whitelist.add(appinfo.activityInfo.packageName);
					}
				} else {
					status[position] = false;
					if (whitelist.contains(appinfo.activityInfo.packageName)) {
						AdminActivity.getDBOperator().deleteApp(appinfo.activityInfo.packageName);
						whitelist.remove(appinfo.activityInfo.packageName);
					}
				}

			}
		});

		// ”ƒ«· ÕÊ· „Ê÷Ê⁄ «· — Ì» Ê»ﬁ«¡ Õ«·… «·“—
		// ”ƒ«· ⁄‰ «·base adapter  Ê arrayadapter Ê«·›—ﬁ »Ì‰Â„ ›Ì «·≈” Œœ«„ ø
		viewHolder.name.setText(appinfo.loadLabel((context.getPackageManager())));
		Drawable img = appinfo.loadIcon(context.getPackageManager());
		img.setBounds(0, 0, 75, 75);
		viewHolder.icon.setBackgroundDrawable(img);
		Log.v("hi", status[position] + "");
		if (whitelist.contains(appinfo.activityInfo.packageName)) {
			status[position] = true;
		}
		viewHolder.tg.setChecked(status[position]);

		return convertView;
	}

	public static class ViewHolder {
		public TextView name;
		public ImageView icon;
		public ToggleButton tg;
	}

}
