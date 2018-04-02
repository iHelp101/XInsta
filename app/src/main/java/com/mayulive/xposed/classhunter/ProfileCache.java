package com.mayulive.xposed.classhunter;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import com.mayulive.xposed.classhunter.profiles.ClassProfile;
import com.mayulive.xposed.classhunter.profiles.ProfileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Static class for storing and loading the results of profile class searches.
 * Depending on the number of classes you have to search through it can be very slow.
 * It must be fed a save location to function. Recommended location is files dir of hooked app.
 */
public class ProfileCache
{
	private static final String TAG = ClassHunter.getLogTag( ProfileCache.class );

	/**
	 * When using a cached entry we normally ensure that the similarity is unchanged.
	 * If you intend to utilize the signature values to trigger updates instead, you may disable this.
	 */
	public static boolean ENSURE_SIMILARITY_MATCH = true;

	private static String mClassCachePath = null;
	private static Map<String, CachedProfile> mClassMap = new HashMap<>();

	private static final String TARGET_KEY = "target";
	private static final String CLASS_KEY = "class";
	private static final String HOOK_KEY = "hookSignature";
	private static final String MODULE_KEY = "moduleSignature";
	private static final String SIMILARITY_KEY = "similarity";

	/**
	 * Check the cache for the target class name.
	 * If found, its similarity must also match what it was when it was saved, and <code>MODULE_SIGNATURE</code>
	 * and <code>HOOK_SIGNATURE</code> must match as well.
	 * @param profile		The profile to check the cache for
	 * @param loader		ClassLoader
	 * @return	Return the cached class if similarity, module and hook versions match
	 */
	public static Class loadFromCacheIfMatch(ClassProfile profile, ClassLoader loader)
	{

		if (ClassHunter.DEBUG_CLASS_SEARCH)
		{
			Log.i(TAG, "Checking cache for entry: "+profile.getFullPath());
		}

		CachedProfile item = mClassMap.get(profile.getFullPath());
		if (item != null)
		{

			if (ClassHunter.DEBUG_CLASS_SEARCH)
			{
				Log.i(TAG, "Found entry: "+item.getClassPath());
			}


			if (item.getModuleVersion() == ClassHunter.MODULE_SIGNATURE && item.getHookVersion() == ClassHunter.HOOK_SIGNATURE)
			{
				if (ClassHunter.DEBUG_CLASS_SEARCH)
				{
					Log.i(TAG, "Cache item similary: "+item.getSimilarity());
				}

				Class clazz = item.load(loader);
				if (clazz != null)
				{
					if (ENSURE_SIMILARITY_MATCH)
					{
						float similarity = profile.getSimilarity(clazz, null);

						if (ClassHunter.DEBUG_CLASS_SEARCH)
						{
							Log.i(TAG, "Class similary: "+similarity);
						}


						if (similarity == item.getSimilarity())
						{
							return clazz;
						}
					}
					else
					{
						return clazz;
					}

				}
			}
		}

		return null;
	}

	/**
	 * Check the cache for the target class name.
	 * If found, its similarity must also match what it was when it was saved, and <code>MODULE_SIGNATURE</code>
	 * and <code>HOOK_SIGNATURE</code> must match as well.
	 * A profile may be associated with multiple results, of which we return <code>count</code>
	 * @param profile		The profile to check the cache for
	 * @param loader		ClassLoader
	 * @param count			The number of results to look for
	 * @return	Return the cached classes if similarity, module and hook versions match
	 */
	public static ArrayList<Class> loadFromCacheIfMatch( ClassProfile profile, int count, ClassLoader loader)
	{
		ArrayList<Class> searchResults = new ArrayList<Class>();

		for (int i = 0; i < count; i++)
		{
			CachedProfile item = mClassMap.get(profile.getFullPath() + "#" + i);
			if (item != null)
			{
				item.setUsed(true);
				if (item.getModuleVersion() == ClassHunter.MODULE_SIGNATURE && item.getHookVersion() == ClassHunter.HOOK_SIGNATURE)
				{
					Class clazz = item.load(loader);
					if (clazz != null)
					{
						float similarity = profile.getSimilarity(clazz,null);
						if (similarity == item.getSimilarity())
						{
							searchResults.add(clazz);
						}

					}
				}
			}
		}

		return searchResults;
	}

	/**
	 * Set where to save the cache. Recommended is hooked app's files dir. .json will be appended to the path
	 * @param path	The path to save the cache at
	 */
	public static void setSaveLocation(String path)
	{
		mClassCachePath = path;
	}


	/**
	 *	Update the cache entry for a single path/class pair
	 */
	protected static void updateEntry(String targetPath, String classPath, int hookVersion, int moduleVersion, float similarity)
	{
		CachedProfile item = new CachedProfile(targetPath, classPath, hookVersion, moduleVersion, similarity);
		item.setUsed(true);
		updateEntry(item);
	}

	/**
	 *	Update the cache entry for a path/class pair
	 *  A path may have multiple results associated with it. For these we use the same key and append #1, #2 etc to the end.
	 */
	protected static void updateEntry(String targetPath, ArrayList<Class> classes, int hookVersion, int moduleVersion, ClassProfile profile)
	{
		for (int i = 0; i < classes.size(); i++)
		{
			CachedProfile item = new CachedProfile(targetPath + "#" + i, ProfileUtil.getName(classes.get(i)), hookVersion, moduleVersion, profile.getSimilarity(classes.get(i),null));
			item.setUsed(true);
			updateEntry(item);
		}
	}

	protected static void updateEntry(CachedProfile item)
	{
		item.setUsed(true);
		mClassMap.put(item.getTargetPath(), item);
	}

	/**
	 * Set save location and save cache in one go
	 * @param path The path to save the cache at
	 */
	public static void saveCache(String path) throws IOException
	{
		setSaveLocation(path);
		saveCache();
	}

	/**
	 * Save the cache to the specified location
	 */
	public static void saveCache()
	{
		File file = new File(mClassCachePath + ".json");

		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));

			JsonWriter writer = new JsonWriter(out);
			writer.setIndent("  ");
			writer.beginArray();

			for (CachedProfile item : mClassMap.values())
			{
				if (item.getUsed())
				{
					writer.beginObject();
					writer.name(TARGET_KEY).value(item.getTargetPath());
					writer.name(CLASS_KEY).value(item.getClassPath());
					writer.name(HOOK_KEY).value(item.getHookVersion());
					writer.name(MODULE_KEY).value(item.getModuleVersion());
					writer.name(SIMILARITY_KEY).value(item.getSimilarity());
					writer.endObject();
				}


			}

			writer.endArray();
			writer.close();
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Failed to save profile cache");
			ex.printStackTrace();
		}
	}

	/**
	 * Set save location and load cache in one go
	 * @param path The path the cache is saved at
	 */
	public static void loadCache(String path)
	{
		setSaveLocation(path);
		loadCache();
	}

	/**
	 * Load the cache from the specified location
	 */
	public static void loadCache()
	{
		mClassMap.clear();

		try
		{
			File file = new File(mClassCachePath + ".json");
			BufferedReader in = new BufferedReader(new FileReader(file));

			JsonReader reader = new JsonReader(in);

			reader.beginArray();

			while (reader.hasNext())
			{
				CachedProfile item = new CachedProfile();

				reader.beginObject();
				while (reader.hasNext())
				{
					String currentName = reader.nextName();

					if (currentName.equals(TARGET_KEY))
						item.setTargetPath(reader.nextString());
					else if (currentName.equals(CLASS_KEY))
						item.setClassPath(reader.nextString());
					else if (currentName.equals(HOOK_KEY))
						item.setHookVersion(reader.nextInt());
					else if (currentName.equals(MODULE_KEY))
						item.setModuleVersion(reader.nextInt());
					else if (currentName.equals(SIMILARITY_KEY))
						item.setSimilarity((float) reader.nextDouble());

					mClassMap.put(item.getTargetPath(), item);
				}
				reader.endObject();

			}
			reader.endArray();
			reader.close();
		}
		catch (FileNotFoundException ex)
		{
			//Expected, but log anyway.
			Log.i(TAG, "Failed to load profile cache, file not found.");
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Failed to load profile cache");
			ex.printStackTrace();
		}
	}

}


