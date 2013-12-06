package com.serveme.savemyphone.control;

import java.lang.ref.WeakReference;

import com.serveme.savemyphone.R;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader {

	public void load(ImageView icon, ResolveInfo appinfo, Context context) {
		LoadingThread task = new LoadingThread(icon,appinfo,context);
        task.execute();
		
	}
	
	class LoadingThread extends AsyncTask<String, Void, Drawable> {
		ResolveInfo info;
		Context context;
		ImageView img;
	    private final WeakReference<ImageView> imageViewReference;

	    public LoadingThread(ImageView icon, ResolveInfo appinfo,
				Context context) {
	    	imageViewReference = new WeakReference<ImageView>(icon);
	    	info = appinfo;
	    	this.context = context;
		}


	    @Override
	    // Actual download method, run in the task thread
	    protected Drawable doInBackground(String... params) {
	    	Drawable img = info.loadIcon(context.getPackageManager());
	    	int imagesize = (int) context.getResources().getDimension(R.dimen.image_size);
			img.setBounds(0, 0, imagesize, imagesize);
//			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//			int iconSize = am.getLauncherLargeIconSize();
//			img.setBounds(0, 0, iconSize,iconSize);
			return img;
	    }

		@Override
	    // Once the image is loaded, associates it to the imageView
	    protected void onPostExecute(Drawable img) {
	        if (isCancelled()) {
	            img = null;
	        }

	        if (imageViewReference != null) {
	            ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageDrawable(img);
	            }
	        }
	    }
	}

}
