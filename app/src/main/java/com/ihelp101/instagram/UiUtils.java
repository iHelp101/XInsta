package com.ihelp101.instagram;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class UiUtils {
	public static boolean getActivityVisibleInDrawer(Context context) {
		PackageManager pm = context.getPackageManager();
		int enabled = pm.getComponentEnabledSetting(new ComponentName(
				context, context.getPackageName() + ".IntroActivity-Alias"));

		if (enabled == 2) {
			return false;
		} else {
			return true;
		}
	}

	public static void setActivityVisibleInDrawer(Context context, boolean visible) {
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName(context,
						context.getPackageName() + ".IntroActivity-Alias"),
				visible ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
		);
	}
}
