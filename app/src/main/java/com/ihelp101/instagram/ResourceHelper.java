package com.ihelp101.instagram;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ResourceHelper {
	public static String getString(Context context, int id, Object...formatArgs) {
		return getOwnResources(context).getString(id, formatArgs);
	}
	
	public static String getString(Context context, int id) {
		return getOwnResources(context).getString(id);
	}

	public static Resources getOwnResources(Context context) {
		return getResourcesForPackage(context, "com.ihelp101.instagram");
	}

	public static Resources getResourcesForPackage(Context context, String packageName) {
		try {
			return context.getPackageManager().getResourcesForApplication(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getFileContents (String fileName) {
		String fileContents = "Nope";

		File file = new File(fileName);
try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				System.out.println("Info2: " +line);
				fileContents = line;
			}
			br.close();
		}
		catch (IOException e) {
			System.out.println("Info: " +e);
			fileContents = "Nope";

		}
		return fileContents;
	}
}
