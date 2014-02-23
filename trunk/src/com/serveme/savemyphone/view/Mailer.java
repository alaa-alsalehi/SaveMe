package com.serveme.savemyphone.view;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.ads.c;
import com.serveme.savemyphone.R;
//import com.google.android.gms.auth.GoogleAuthUtil;
import com.serveme.savemyphone.mail.Mail;
import com.serveme.savemyphone.util.MyTracker;

public class Mailer extends AsyncTask<String, Integer, Void> {

	private Context context;
	private String messageBody;

	public Mailer(Context context, String messageBody) {
		this.context = context;
		this.messageBody = messageBody;
	}

	protected void onProgressUpdate() {
		// called when the background task makes any progress
	}

	protected void onPreExecute() {
		// called before doInBackground() is started
	}

	 protected void onPostExecute(Long result) {
		// called after doInBackground() has finished
			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("Message has been sent to your email with temporary password \nDon't close this screen because if you get our from this screen, the password sent to you will be useless and you will need to open this screen again and wait for new one!");
			builder.show();
	}

	@Override
	protected Void doInBackground(String... arg0) {
		sendRecoveryMail();
		return null;
	}
	
	public boolean sendRecoveryMail() {
		Mail mail = new Mail();
		mail.setFrom("serveme.ps@gmail.com");
		mail.setUser("serveme.ps");
		mail.setPass("#$^JF56Za7@1");
		mail.setTo(new String[] { "omar.albelbaisy@gmail.com" });
		mail.setSubject("Recovery info for \"Save Me\" android app");
		mail.setBody(messageBody);

		try {
			return mail.send();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return false;
	}

	// private String[] getAccountNames() {
	// AccountManager mAccountManager = AccountManager.get(context);
	// Account[] accounts =
	// mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
	// String[] names = new String[accounts.length];
	// for (int i = 0; i < names.length; i++) {
	// names[i] = accounts[i].name;
	// }
	// return names;
	// }
}
