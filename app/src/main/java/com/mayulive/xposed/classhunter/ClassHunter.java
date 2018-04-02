package com.mayulive.xposed.classhunter;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;


/**
 * Contains various constants used by the library,
 * and some userful methods.
 */
public class ClassHunter
{
	public static boolean DEBUG_ALWAYS_SEARCH = true;

	public static boolean DEBUG_CLASS_COMPARISON = false;
	public static boolean DEBUG_CLASS_SIMILARITY = false;

	public static boolean DEBUG_COMPARISON = false;
	public static boolean DEBUG_CLASS_SEARCH = false;
	public static boolean DEBUG_SIMILARITY_RANKING = false;
	public static boolean DEBUG_SERVER = false;

	public static int HOOK_SIGNATURE = 1;
	public static int MODULE_SIGNATURE = 1;

	private static final String TAG = ClassHunter.class.getSimpleName();

	/**
	 * Load a class. Name preferred, canonical accepted.
	 * @param path		The name of the class (Full path)
	 * @param loader	The ClassLoader
	 * @return			The loaded class, or null.
	 */
	public static Class loadClass(String path, ClassLoader loader)
	{
		Class returnClass = null;

		try
		{
			returnClass = loader.loadClass(path);
		}
		catch (Exception ex)
		{

		}

		//sigh

		if (returnClass == null)
		{
			int dividerCount = path.length() - path.replace(".", "").length();
			StringBuffer modifiedPath = new StringBuffer(path);
			for (int i = 0; i < dividerCount; i++)
			{
				int lastIndex = modifiedPath.lastIndexOf(".");
				modifiedPath.deleteCharAt(lastIndex);
				modifiedPath.insert(lastIndex, "$");

				try
				{
					returnClass = loader.loadClass(modifiedPath.toString());
					break;
				}
				catch (Exception ex)
				{
					//Expected
				}
			}
		}

		return returnClass;
	}

	/**
	 * Returns the ZIP CRC32 of the input apk file.
	 * May be used by ProfileCache to tell when the app was updated.
	 * @param apkPath	The path to the apk file
	 * @return			The 4-byte CRC32 in int form, or -1
	 */
	public static int getApkCRC32(String apkPath)
	{
		int crc32 = -1;
		FileInputStream bis = null;
		try
		{
			bis = new FileInputStream(new File(apkPath));

			int read = 0;
			byte[] buffer = new byte[18];
			ByteBuffer bb = ByteBuffer.wrap(buffer);

			read = bis.read(buffer);

			if (read != -1)
			{
				bb.position(14);
				crc32 = bb.getInt();
			}

			bis.close();
		}
		catch (FileNotFoundException e)
		{
			Log.e(TAG, "Failed to get APK CRC: File not found");
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Failed to get APK CRC");
			ex.printStackTrace();
		}
		finally
		{
			try {bis.close();}
			catch (Exception ex){ /* What do though */ }
		}

		Log.i(TAG, "Got APK CRC32: "+Integer.toHexString(crc32));
		return crc32;
	}

	public static String getLogTag(Class clazz)
	{
		return TAG + ":"+clazz.getSimpleName();
	}
}
