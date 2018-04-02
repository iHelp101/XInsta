package com.mayulive.xposed.classhunter;

import android.util.Log;

import com.mayulive.xposed.classhunter.packagetree.PackageTree;
import com.mayulive.xposed.classhunter.profiles.ClassProfile;
import com.mayulive.xposed.classhunter.profiles.ProfileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Backend for loading profiled classes. Accessed by user via methods in ProfileHelpers
 */
public class ProfileSearch
{

	private static final String TAG = ClassHunter.getLogTag( ProfileSearch.class );

	/**
	 * Search through a PackageTree for the specified profile.
	 * @param targetCount	The number of results to return
	 * @param targetProfile	The profile to look for
	 * @param param			The PackageTree to look in
	 * @return				The top <code>targetCount</code> most similar classes found
	 */
	public static ArrayList<Class> searchPathSimilarity(int targetCount, ClassProfile targetProfile, PackageTree param )
	{
		List<Class> searchResults = new ArrayList<Class>();

		searchResults = param.getAllAtDepth(targetProfile.getKnownPath(),targetProfile.getMinDepth(),targetProfile.getMaxDepth());

		if (ClassHunter.DEBUG_CLASS_SEARCH)
		{
			Log.i(TAG, "Candidate count: "+searchResults.size());
		}

		List< ProfileHelpers.ProfileSimilarity < Class > > similarities = ProfileHelpers.getSimilarityRanking(targetProfile, searchResults.toArray(new Class[searchResults.size()]), null);

		ArrayList<Class> returnResults = new ArrayList<>();
		for (int i = 0; i < targetCount; i++)
		{
			if (i >= similarities.size())
				break;

			returnResults.add(similarities.get(i).clazz);
		}

		return returnResults;
	}

	/**
	 * Search through a PackageTree for the specified profile.
	 * @param targetProfile	The profile to look for
	 * @param param			The PackageTree to look in
	 * @return				The top most similar classes found
	 */
	public static Class searchPathBestFirstMatch(ClassProfile targetProfile, PackageTree param)
	{
		ArrayList<Class> searchResults = searchPathSimilarity(1, targetProfile, param);

		if (searchResults.size() > 0)
		{
			return searchResults.get(0);
		}

		return null;
	}


	//Most methods the user cares about are in ProfileHelpers,
	//Including shortcuts to the two load methods below.

	/**
	 * Search and load a
	 * @param profile	The profile to search with
	 * @param count		The number of classes to return
	 * @param param		A populated PackageTree containing all classes
	 * @return			The top matches for the profile
	 */
	protected static ArrayList<Class> loadProfiledClasses(ClassProfile profile, int count, PackageTree param)
	{
		if (ClassHunter.DEBUG_CLASS_SEARCH)
			Log.i(TAG, "Loading multi profiled: "+profile.getFullPath()+", "+ profile.getKnownPath()+", count: "+count);

		//Try loading from cache.
		ArrayList<Class> searchResults = ProfileCache.loadFromCacheIfMatch(profile, count,param.getClassLoader());

		//If insufficient results, do a full search. Note that this is slow.
		if ( searchResults.size() < count )
		{
			searchResults = searchPathSimilarity(count, profile, param);

			if (!searchResults.isEmpty())
			{
				if (ClassHunter.DEBUG_CLASS_SEARCH)
				{
					Log.i(TAG, "Multi search result: ");
					for (Class result : searchResults)
					{
						Log.i(TAG, "Got search class. 	Target: "+profile.getFullPath()+", 	result: "+ProfileUtil.getName(result)+", similarity: "+profile.getSimilarity(result,null));
					}
				}

			}
			else
			{
				Log.i(TAG, "Failed to find result for "+profile.getFullPath());
			}
		}
		else
		{
			if (ClassHunter.DEBUG_CLASS_SEARCH)
			{
				Log.i(TAG, "Multi cache result: ");
				for (Class result : searchResults)
				{
					Log.i(TAG, "Got cache class. 	Target: "+profile.getFullPath()+", 	result: "+ProfileUtil.getName(result)+", similarity: "+profile.getSimilarity(result,null));
				}
			}

		}

		//Add the result to the cache for later
		if (!searchResults.isEmpty())
		{
			ProfileCache.updateEntry(profile.getFullPath(), searchResults, ClassHunter.HOOK_SIGNATURE, ClassHunter.MODULE_SIGNATURE, profile);
		}

		return searchResults;
	}

	/**
	 * Search for and load a class using a ClassProfile
	 * @param profile	The profile to search with
	 * @param param		A populated PackageTree containing all classes
	 * @return			The top match for the profile
	 */
	protected static Class loadProfiledClass(ClassProfile profile, PackageTree param)
	{

		if (ClassHunter.DEBUG_CLASS_SEARCH)
			Log.i(TAG, "Loading profiled: "+profile.getFullPath()+", "+ profile.getKnownPath());


		Class returnClass = null;



		//First check cache
		{
			returnClass = ProfileCache.loadFromCacheIfMatch(profile, param.getClassLoader());

			if (ClassHunter.DEBUG_CLASS_SEARCH)
			{
				//Just a debug message
				if (returnClass != null)
				{
					Log.i(TAG, "Got cache class. 	Target: "+profile.getFullPath()+", 	result: "+ProfileUtil.getName(returnClass)+", similarity: "+profile.getSimilarity(returnClass,null));
				}
			}
		}


		//If no result, try loading class by name
		if (!ClassHunter.DEBUG_ALWAYS_SEARCH && returnClass == null)
		{
			//First check the fullpath input and compare it to the profile. Use if complete match
			returnClass = ClassHunter.loadClass(profile.getFullPath(), param.getClassLoader());
			if (!profile.compareTo(returnClass,returnClass))
				returnClass = null;
			else
			{
				if (ClassHunter.DEBUG_CLASS_SEARCH)
					Log.i(TAG, "Got fullpath class. 	Target: "+profile.getFullPath()+", 	result: "+ ProfileUtil.getName(returnClass));
			}

		}

		//If we still don't have anything, do a full search. Note that this is very slow
		if ( returnClass == null )
		{
			returnClass = ProfileSearch.searchPathBestFirstMatch(profile, param);

			if (returnClass != null && ClassHunter.DEBUG_CLASS_SEARCH)
				Log.i(TAG, "Got search class. 	Target: "+profile.getFullPath()+", 	result: "+ProfileUtil.getName(returnClass)+", similarity: "+profile.getSimilarity(returnClass,null));
		}

		//Add the result to the cache for later
		if (returnClass != null)
		{
			ProfileCache.updateEntry(profile.getFullPath(), ProfileUtil.getName(returnClass), ClassHunter.HOOK_SIGNATURE, ClassHunter.MODULE_SIGNATURE, profile.getSimilarity(returnClass,null));
		}

		return returnClass;
	}

}

