package com.serveme.savemyphone.control;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.view.utils.BackgroundUtility;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StanderListAdapter extends BaseAdapter {
	private String choosedBacground;

	class ImageLoader extends AsyncTask<Void, Void, BitmapDrawable> {
		private int position;
		private WeakReference<ImageButton> wrimageView;

		public ImageLoader(ImageButton imageButton) {
			wrimageView = new WeakReference<ImageButton>(imageButton);
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		protected BitmapDrawable doInBackground(Void... params) {
			ImageButton imageButton = wrimageView.get();
			if (imageButton != null) {
				return BackgroundUtility.getSampledBitmapDrawableFromAsset(
						context, "background/" + assets[position],
						imageButton.getWidth(), imageButton.getHeight());
			} else{
				Log.d("problem", "problem");
				return null;
			}
		}

		@Override
		protected void onPostExecute(BitmapDrawable result) {
			ImageButton imageButton = wrimageView.get();
			if (imageButton != null)
				imageButton.setImageDrawable(result);
		}

	}

	class ViewHolder {
		public ImageButton imageButton;
		public ImageLoader imageLoader;
		public int position;
	}

	private final Context context;
	private String[] assets;

	public StanderListAdapter(Context context) {
		this.context = context;
		try {
			assets = context.getAssets().list("background");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return assets.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.background_item, parent, false);

			holder = new ViewHolder();
			holder.imageButton = (ImageButton) convertView
					.findViewById(R.id.stander_list_item_imageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			if (!holder.imageLoader.isCancelled())
				holder.imageLoader.cancel(true);
		}
		holder.imageLoader = new ImageLoader(holder.imageButton);
		holder.imageLoader.setPosition(position);
		holder.position = position;
		holder.imageLoader.execute();
		holder.imageButton.setTag(holder);
		holder.imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holder = (ViewHolder) v.getTag();
				ImageView background = (ImageView) ((Activity) v.getContext())
						.findViewById(R.id.background_image);
				BitmapDrawable bitmapDrawable = BackgroundUtility
						.getBitmapDrawableFromAsset(context, "background/"
								+ assets[holder.position]);
				setChoosedBacground("background/" + assets[holder.position]);
				background.setBackground(bitmapDrawable);
			}
		});
		return convertView;
	}

	public String getChoosedBacground() {
		return choosedBacground;
	}

	public void setChoosedBacground(String choosedBacground) {
		Log.d("choosed", choosedBacground);
		this.choosedBacground = choosedBacground;
	}

}
