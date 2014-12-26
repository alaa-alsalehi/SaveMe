package com.serveme.savemyphone.view.utils;

import java.io.IOException;
import java.io.InputStream;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

public class BackgroundUtility {

	public static BitmapDrawable getBitmapDrawableFromAsset(Context context,
			String filePath) {
		AssetManager assetManager = context.getAssets();

		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(filePath);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			Tracker tracker = EasyTracker.getInstance(context);
			tracker.send(MapBuilder.createException(
					new AnalyticsExceptionParser().getDescription(Thread
							.currentThread().toString(), e), false).build());
		}
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				context.getResources(), bitmap);
		bitmapDrawable.setTileModeY(TileMode.MIRROR);
		bitmapDrawable.setTileModeX(TileMode.MIRROR);

		return bitmapDrawable;
	}

	public static BitmapDrawable getSampledBitmapDrawableFromAsset(
			Context context, String filePath, int reqWidth, int reqHeight) {
		AssetManager assetManager = context.getAssets();

		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(filePath);
			bitmap = decodeSampledBitmapFrotmStream(context, istr, reqWidth,
					reqHeight);
		} catch (IOException e) {
			Tracker tracker = EasyTracker.getInstance(context);
			tracker.send(MapBuilder.createException(
					new AnalyticsExceptionParser().getDescription(Thread
							.currentThread().toString(), e), false).build());
		}
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				context.getResources(), bitmap);

		return bitmapDrawable;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFrotmStream(Context context,
			InputStream inputStream, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(inputStream, null, options);
		try {
			if (inputStream.markSupported())
				inputStream.reset();
		} catch (IOException e) {
			Tracker tracker = EasyTracker.getInstance(context);
			tracker.send(MapBuilder.createException(
					new AnalyticsExceptionParser().getDescription(Thread
							.currentThread().toString(), e), false).build());
		}
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(inputStream, null, options);
	}

}
