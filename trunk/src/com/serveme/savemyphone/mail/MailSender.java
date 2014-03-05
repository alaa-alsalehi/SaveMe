package com.serveme.savemyphone.mail;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;


public class MailSender extends AsyncTask<String, Integer, Boolean> {

	private Context context;
	private String generated_code;

	public MailSender(Context context, String generated_code) {
		this.context = context;
		this.generated_code = generated_code;
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		return sendRecoveryMail();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		// called when the background task makes any progress
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// called after doInBackground() has finished
		String message = null;
		if(result){
			message = context.getResources().getString(R.string.recovery_activity_success_dialoge);
		} else {
			message = context.getResources().getString(R.string.recovery_activity_failure_dialoge);
		}
//		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setMessage(message);
//		builder.show();
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}



	public boolean sendRecoveryMail() {
		Mail mail = new Mail();
		mail.setFrom("serveme.ps@gmail.com");
		mail.setUser("serveme.ps");
		mail.setPass("#$^JF56Za7@1");
		mail.setTo(new String[]{new PrefEditor(context).getRecoveryEmail()});
		mail.setSubject(context.getResources().getString(R.string.mail_subject));
		mail.setBody(context.getResources().getString(R.string.email_message_part1) + generated_code + context.getResources().getString(R.string.email_message_part2));

		try {
			return mail.send();
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

}
