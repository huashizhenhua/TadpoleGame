package org.tadpole.common;
import android.content.Context;
import android.widget.Toast;

class TadpoleApplication {
	public void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
}
