package com.hdzx.tenement.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class CloseKeyBoard {
	
	public static void closeInputKeyBoard(Activity activity) {
		InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
