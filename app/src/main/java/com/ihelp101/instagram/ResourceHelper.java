package com.ihelp101.instagram;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class ResourceHelper {
	public static String getString(Context context, int id, Object...formatArgs) {
		return getOwnResources(context).getString(id, formatArgs);
	}
	
	public static String getString(Context context, int id) {
		return getOwnResources(context).getString(id);
	}

	public static Resources getOwnResources(Context context) {
		return getResourcesForPackage(context, "com.ihelp101.xinsta");
	}

	public static Resources getResourcesForPackage(Context context, String packageName) {
		try {
			return context.getPackageManager().getResourcesForApplication(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
