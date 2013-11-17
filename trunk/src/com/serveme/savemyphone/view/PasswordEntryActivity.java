//package com.serveme.savemyphone.view;
//
//import com.serveme.savemyphone.R;
//
//import android.app.Activity;
//import android.inputmethodservice.Keyboard;
//import android.inputmethodservice.KeyboardView;
//import android.os.Bundle;
//
//public class PasswordEntryActivity extends Activity{
//	
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//	super.onCreate(savedInstanceState);
//    setContentView(R.layout.password_entry);
//   
//    
//    // Create the Keyboard
//    Keyboard mKeyboard = new Keyboard(this,R.xml.hexkbd);
//
//    // Lookup the KeyboardView
//    KeyboardView mKeyboardView= (KeyboardView)findViewById(R.id.keyboardview);
//    // Attach the keyboard to the view
//    mKeyboardView.setKeyboard( mKeyboard );
//    // Do not show the preview balloons
//    mKeyboardView.setPreviewEnabled(false);
//	}
//}
