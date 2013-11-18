package com.serveme.savemyphone.view;

import com.serveme.savemyphone.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;



public class PasswordEntryActivity extends Activity{
	
CustomKeyboard mCustomKeyboard;
    
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_entry);
      
        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.xml.hexkbd );
        mCustomKeyboard.registerEditText(R.id.edittext); 
        
        EditText e = (EditText) findViewById(R.id.edittext);
        e.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        
//        mCustomKeyboard.registerEditText(R.id.edittext1);
//        mCustomKeyboard.registerEditText(R.id.edittext2);
    }
    
    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }
            public char charAt(int index) {
                return '*'; // This is the important part
            }
            public int length() {
                return mSource.length(); // Return default
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };
    
//    @Override public void onBackPressed() { 
//    	// NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity 
//        if( mCustomKeyboard.isCustomKeyboardVisible() ) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
//    }
}
