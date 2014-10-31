package com.serveme.savemyphone.receivers;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.util.MyTracker;

public class HiddenLockUtility {
	public static void checkAndHandleHiddenLock(final Context context,
			String packageName) {
		SharedPreferences preferences = context.getSharedPreferences("mypref",
				Context.MODE_PRIVATE);
		if (preferences.getBoolean("hidden_lock_active", false)) {
			ArrayList<Launcher> launchers = new ArrayList<Launcher>(0);

			Collections.addAll(launchers, (Launcher[]) new Gson().fromJson(
					preferences.getString("hidden_lock", null),
					new GenericArrayType() {

						@Override
						public Type getGenericComponentType() {

							return Launcher.class;
						}
					}));
			for (Launcher launcher : launchers) {
				if (launcher.getPackageName().equals(packageName)) {
					Editor editor = preferences.edit();
					editor.remove("hidden_lock_active");
					editor.remove("hidden_lock");
					editor.apply();

					// ≈–« ﬂ«‰ «·ÃÂ«“ €Ì— „€·ﬁ «› ÕÂ« ··„” Œœ„ ·ÌﬁÊ„ » ’ÕÌÕ
					// «·Ê÷⁄
					if (!new PrefEditor(context).isLocked()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setMessage(R.string.uninstall_hidden_lock);
						builder.setPositiveButton(context.getResources()
								.getString(android.R.string.yes),
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										MyTracker
												.fireButtonPressedEvent(
														context,
														"Ok_replace_hidden_lock_dialog");
									}
								});
						builder.setNegativeButton(context.getResources()
								.getString(android.R.string.no),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										MyTracker
												.fireButtonPressedEvent(
														context,
														"Cancel_replace_hidden_lock_dialog");
									}
								});
						builder.show();
					}
					break;
				}
			}

		}
	}
}
