package com.serveme.savemyphone.view;

import java.math.BigInteger;
import java.security.SecureRandom;

import android.app.Activity;
import android.os.Bundle;

//import com.google.android.gms.auth.GoogleAuthUtil;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;

public class RecoveryActivity extends Activity {

	private PrefEditor pe;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recovery_activity);
		String generatedPass = new SessionIdentifierGenerator().nextSessionId();
		String recoverymessage = "Hi,\n\n"
				+ "This is your temporary password: " + generatedPass + "\n\n"
				+ "Note: You can use it to unlock Save Me while you didn't closed the recovery screen\n"
				+ "In case you have closed the recovery screen you need to open it againt and wait until new temporary password sent to you!\n"
				+ "After that you can make a new pattern and continue to use your apps\n\n"
				+ "Regards\n"
				+ "Serve Me Team";
		Mailer m = new Mailer(RecoveryActivity.this,recoverymessage);
		pe = new PrefEditor(RecoveryActivity.this);
		pe.setTempPassword(generatedPass);
		m.execute("");
	}

	public final class SessionIdentifierGenerator {
		private SecureRandom random = new SecureRandom();

		public String nextSessionId() {
			return new BigInteger(130, random).toString(32);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pe.removeTempPassword();
	}
	
}
