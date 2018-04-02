package com.mayulive.xposed.classhunter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mayulive.xposed.classhunter.packagetree.PackageTree;
import com.mayulive.xposed.classhunter.profiles.ClassItem;
import com.mayulive.xposed.classhunter.profiles.ClassProfile;
import com.mayulive.xposed.classhunter.profiles.MethodProfile;
import com.mayulive.xposed.classhunter.profiles.Profile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper methods for loading and searching for classes and methods etc
 */
public class ProfileHelpers
{

	private static final String TAG = ClassHunter.getLogTag( ProfileHelpers.class );

	/**
	 * Compare a list of profiles to a list of items
	 * @param profileClasses	The profiles
	 * @param candidates		Objects the profile will be compared to
	 * @param rightParentClass	The parent class of the candidates, if they belong to a class.
	 * @param ordered			Profile and candidates must be in the same order
	 * @param allowPartialMatch	Allow superflous candidates in the comparison target list
	 * @return True if match, otherwise false.
	 */
	public static <T> boolean compareProfile(Profile<T>[] profileClasses, T[] candidates, Class rightParentClass, boolean ordered, boolean allowPartialMatch)
	{
		if (profileClasses == null)
			return true;	//Not set means we shouold ignore and assume match.

		if (ordered)
			return compareProfileOrdered(profileClasses, candidates, rightParentClass,allowPartialMatch);
		else
			return compareProfileUnordered(profileClasses, candidates, rightParentClass, allowPartialMatch);
	}
	
	private static <T> boolean compareProfileOrdered(Profile<T>[] profileClasses, T[] inputClasses, Class rightParentClass, boolean allowPartialMatch)
	{
		if (profileClasses.length != inputClasses.length)
		{
			if (allowPartialMatch && profileClasses.length <= inputClasses.length)
			{
				//...
			}
			else
			{
				if (ClassHunter.DEBUG_COMPARISON)
				{
					Log.i(TAG, "Length mismatch: "+profileClasses.length+" vs "+inputClasses.length);
				}
				
				return false;
			}
		}	
		
		for (int i = 0; i < profileClasses.length; i++)
		{
			Profile<T> currentLeft = profileClasses[i];
			
			if (!(currentLeft.compareTo(inputClasses[i],rightParentClass) ))
			{	
				if (ClassHunter.DEBUG_COMPARISON)
				{
					Log.i(TAG, "#"+i+"  -  Profile mismatch. Left: "+currentLeft.toString()+", Right: "+inputClasses[i].toString());
				}

				return false;
			}
		}
		return true;
	}


	/**
	 * Class wrapping a profile type (field,method,constructor,class) and its similarity to a profile.
	 */
	public static class ProfileSimilarity<T>
	{
		T clazz;
		float similarity = 0;

		ProfileSimilarity(T clazz, float sim)
		{
			this.clazz = clazz;
			this.similarity = sim;
		}

		public static final Comparator<ProfileSimilarity> CLASS_SIMILARITY_COMPARATOR = new Comparator<ProfileSimilarity>()
		{
			@Override
			public int compare(ProfileSimilarity o1, ProfileSimilarity o2)
			{
				if (o1.similarity == o2.similarity)
					return 0;
				if (o1.similarity > o2.similarity)
					return 1;
				else
					return -1;
			}
		};
	}

	/**
	 * Compare the candidates to the input profile and sort them in descending order.
	 * @param profile			The profile to compare with
	 * @param candidates		The objects to compare to
	 * @param rightParentClass  The parent class of the objects, if they belong to a class.
	 * @return	A list of ProfileSimilarity objects in descending order of similarity
	 */
	public static <T> List<ProfileSimilarity<T>> getSimilarityRanking(Profile profile, T[] candidates, Class rightParentClass)
	{
		if (candidates.length < 1)
		{
			if (ClassHunter.DEBUG_SIMILARITY_RANKING)
			{
				Log.i(TAG, "Empty candidate list passed to getSimilarityRanking");
			}
			return new ArrayList<>();
		}


		ArrayList<ProfileSimilarity<T>> similarities = new ArrayList<>();
		for (T canClass : candidates)
		{
			ProfileSimilarity newItem = new ProfileSimilarity(canClass, profile.getSimilarity(canClass, rightParentClass));

			//Items with a similarity of <=0 should be excluded. Scores will never be 0 or below unless the REQUIRED modifier is used.
			if (newItem.similarity > 0)
			{
				similarities.add( newItem );
			}
		}

		Collections.sort(similarities, ProfileSimilarity.CLASS_SIMILARITY_COMPARATOR);
		Collections.reverse(similarities);

		if (ClassHunter.DEBUG_SIMILARITY_RANKING)
		{
			if (!similarities.isEmpty())
			{
				Log.i(TAG, "Most similar: "+similarities.get(0).clazz.toString()+", Similarity: "+similarities.get(0).similarity);
			}
			else
			{
				Log.e(TAG, "No similar?. Should never happen");
			}


			Log.i(TAG, "Other similar: ");
			int counter  = 0;
			for (ProfileSimilarity canClass : similarities)
			{
				counter++;
				if (counter == 1)
					continue;

				Log.i(TAG, canClass.clazz.toString()+", Similarity: "+canClass.similarity);


				if (counter > 10)
					break;

			}
		}

		return similarities;

	}

	/**
	 * Returns the most similar candidate, and all other candidates with the similarity score.
	 * @param profile	Profile to compare with
	 * @param candidates	Candidates to compare to
	 * @param rightParentClass The parent class of the candidates, if they belong to a class.
	 * @return The most similar candidate, and any candidate with the same similarity score.
	 */
	public static <T> List<T> findAllMostSimilar(Profile<T> profile, T[] candidates, Class rightParentClass)
	{
		ArrayList<T> results = new ArrayList<>();

		if (candidates.length < 1)
			return results;

		List<ProfileSimilarity<T>> sims = getSimilarityRanking(profile,candidates, rightParentClass);

		float lastScore = 0;
		for (int i = 0; i < sims.size(); i++)
		{
			ProfileSimilarity<T> sim = sims.get(i);
			if (sim.similarity >= lastScore)
			{
				results.add(sim.clazz);
				lastScore = sim.similarity;
			}
			else
				break;

		}

		return results;
	}

	/**
	 *Find the most similar candidates, and return the top <code>count</code>
	 * @param profile			The profile to compare with
	 * @param candidates		The candidates to compare to
	 * @param rightParentClass 	The parent class of the objects, if they belong to a class.
	 * @param count				The number of candidates to return
	 * @return
	 */
	public static <T> List<T> findMostSimilar(Profile<T> profile, T[] candidates, Class rightParentClass, int count)
	{
		ArrayList<T> results = new ArrayList<>();

		if (candidates.length < 1)
			return results;

		List<ProfileSimilarity<T>> sims = getSimilarityRanking(profile,candidates, rightParentClass);

		for (int i = 0; i < count && i < sims.size(); i++)
		{
			results.add(sims.get(i).clazz);
		}

		return results;
	}

	/**
	 *Find the most similar candidate.
	 * @param profile			The profile to compare with
	 * @param candidates		The candidates to compare to
	 * @param rightParentClass 	The parent class of the objects, if they belong to a class.
	 * @return
	 */
	@Nullable public static <T> T findMostSimilar(Profile<T> profile, T[] candidates, Class rightParentClass)
	{
		if (candidates.length < 1)
			return null;

		List<ProfileSimilarity<T>> sims = getSimilarityRanking(profile,candidates, rightParentClass);
		if (!sims.isEmpty())
			return sims.get(0).clazz;
		return null;
	}

	private static <T> boolean compareProfileUnordered(Profile<T>[] leftMethods, T[] rightMethods, Class rightParentClass, boolean allowPartialMatch)
	{
		if (leftMethods.length != rightMethods.length)
		{
			if ( !(allowPartialMatch && rightMethods.length >= leftMethods.length) )
			{
				if (leftMethods.length < rightMethods.length)
				{
					if (ClassHunter.DEBUG_COMPARISON)
					{
						Log.i(TAG, "Length mismatch: "+leftMethods.length+" vs "+rightMethods.length);
					}
					return false;
				}	
			}
		}	
		
		//Keep track of right fields we have matched against
		boolean[] consumedList = new boolean[rightMethods.length];
		Arrays.fill(consumedList, false);
		
		int consumedCount = 0;

		for (int i = 0; i < leftMethods.length; i++)
		{
			boolean found = false;
			
			Profile<T> currentLeft = leftMethods[i];
			
			for (int j = 0; j < rightMethods.length; j++)
			{
				//Already matched to something, skip.
				if (consumedList[j])
					continue;
				
				//If matched, mark field as consumed and break lower loop
				if (currentLeft.compareTo(rightMethods[j],rightParentClass))
				{
					consumedList[j] = true;
					consumedCount++;
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				if (ClassHunter.DEBUG_COMPARISON)
					Log.i(TAG, "No match found for method at index: "+i);
			}
		}

		return consumedCount == leftMethods.length;
	}


	/**
	 * Return the index of the first instance of <code>pattern</code> in <code>classes</code>
	 * @param pattern	The class to look for
	 * @param classes	The class to look through
	 * @return	The first index of the class, or -1
	 */
	public static int findFirstClassIndex(Class pattern, Class[] classes)
	{
		
		for (int i = 0; i < classes.length; i++)
		{
			if (classes[i] == pattern)
				return i;
		}	
		
		return -1;
	}

	/**
	 * Return the index of the first match for <code>pattern</code> in <code>classes</code>
	 * @param pattern		The class to look for
	 * @param classes		The class to look through
	 * @param parentClass 	The parent class of the objects, if they belong to a class.
	 * @return	The first index of the class, or -1
	 */
	public static int findFirstClassIndex(ClassItem pattern, Class[] classes, Class parentClass)
	{
		for (int i = 0; i < classes.length; i++)
		{
			if (pattern.compareTo(classes[i], parentClass))
				return i;
		}

		return -1;
	}


	/**
	 * Compare a list of profiles to a list of candidates, and return the similarity of the two lists.
	 * @param patternItems The profiles to compare with
	 * @param candidates	The candidates to compare to
	 * @param parentClass	The parent class of the candidates, if they belong to a class.
	 * @param ordered		Whether the order of objects should be considered
	 * @return				The similarity score of the two lists
	 */
	public static <T> float getProfileSimilarity(Profile<T>[] patternItems, T[] candidates, Class parentClass, boolean ordered)
	{
		//TODO Instead of just comparing, get similarity for all pairs and combine top pairs as score. Would be more accurate, but very slow.

		//Null input means match anything
		if (patternItems == null)
			return 1;


		//Each pattern item is given a score based on matches in the search items.
		// 1   - Item found, and was in order expected (other items inbetween don't matter).
		// 0.75 - Item found, but in wrong order.
		// 0 - item not found

		//Any extra items should also be considered
		float[] patternScores = new float[patternItems.length];
		boolean[] searchScores = new boolean[candidates.length];
		Arrays.fill(patternScores, 0);
		Arrays.fill(searchScores, false);


		//First search for items in the right order
		if (ordered)
		{
			int lastSearchStart = 0;
			for (int i = 0; i < patternItems.length; i++)
			{
				for (int j = lastSearchStart; j < candidates.length; j++)
				{
					if (patternItems[i].compareTo(candidates[j],parentClass))
					{
						patternScores[i] = 1;
						searchScores[j] = true;
						lastSearchStart = j+1;
						break;
					}
				}

			}
		}

		//Then just look for matches for the rest
		for (int i = 0; i < patternItems.length; i++)
		{
			if (patternScores[i] < 1)
			{
				for (int j = 0; j < candidates.length; j++)
				{
					if (!searchScores[j])
					{
						if (patternItems[i].compareTo(candidates[j],parentClass))
						{
							if (ordered)
								patternScores[i] = 0.75f;
							else
								patternScores[i] = 1;
							searchScores[j] = true;
							break;
						}
					}
				}
			}
		}



		float similarity = 0;

		for (int i = 0; i < patternScores.length; i++)
		{
			similarity += patternScores[i];

			//Not found, check for required flag
			if (patternScores[i] <= 0)
			{
				int mods = patternItems[i].getModifiers();
				if (mods != -1 && Modifiers.isRequired(mods ))
				{
					//Required item was not found. Return -1 as score
					return Integer.MIN_VALUE;
				}

			}

		}

		if (patternScores.length <= 0)
			similarity = 1;
		else
			similarity /= (float)patternScores.length;


		//For each extra argument in the list we are comparing against,
		//multiply the result by 0.98
		for (int i = 0; i < searchScores.length; i++)
		{
			//How should extra items in right list affect the score...
			if (!searchScores[i])
			{
				similarity *= 0.98f;	//Reasonable?
			}

		}

		return similarity;
	}


	/**
	 * Find all candidates that match the profile
	 * @param pattern		The profile to comapre with
	 * @param candidates	The candidates to compare to
	 * @param parentClass	The parent class of the candidates, if they belong to a class.
	 * @return				A list of all candidates that match the profile
	 */
	public static <T> List<T> findAllProfileMatches(Profile<T> pattern, T[] candidates, Class parentClass)
	{
		ArrayList<T> matches = new ArrayList<T>();
		
		for (int i = 0; i < candidates.length; i++)
		{
			if (pattern.compareTo(candidates[i], parentClass))
			{
				matches.add(candidates[i]);
			}
		}	
		
		return matches;
	}

	/**
	 * Find the first candidate that matches the profile
	 * @param pattern		The profile to compare with
	 * @param candidates	The candidates to compare to
	 * @param parentClass	The parent class of the candidates, if they belong to a class.
	 * @return				The first candidate that matches the profile.
	 */
	@Nullable public static <T> T findFirstProfileMatch(Profile<T> pattern, T[] candidates, Class parentClass)
	{
		
		for (int i = 0; i < candidates.length; i++)
		{
			if (pattern.compareTo(candidates[i], parentClass))
			{
				return candidates[i];
			}
		}	
		
		return null;
	}

	/**
	 * Find the index of the first candidate that matches the profile
	 * @param pattern		The profile to compare with
	 * @param candidates	The candidates to compare to
	 * @param parentClass	The parent class of the candidates, if they belong to a class.
	 * @return				The index of the first candidate that matches the profile, or -1
	 */
	public static <T> int findFirstProfileMatchIndex(Profile<T> pattern, T[] candidates, Class parentClass)
	{
		
		for (int i = 0; i < candidates.length; i++)
		{
			if (pattern.compareTo(candidates[i], parentClass))
			{
				return i;
			}
		}	
		
		return -1;
	}

	/**
	 * Find the first declared constructor that takes the specified number of parameters
	 * @param clazz			The class to look in
	 * @param paramCount	The number of parameters to look for
	 * @return	The first declared constructor with the specified number of parameters
	 */
	@Nullable public static Constructor findFirstDeclaredConstructorWithParamCount(Class clazz, int paramCount)
	{
		Constructor[] constructors = clazz.getDeclaredConstructors();
		
    	for (Constructor currentConstructor : constructors)
    	{
    		//Incase there are more
    		if (currentConstructor.getParameterTypes().length == paramCount)
    		{
    			return currentConstructor;
    		}
    	}
    	return null;
	}


	/**
	 * Find the method that takes the greatest number of parameters
	 * @param methods	The methods to look through
	 * @return	The method that takes the greatest number of parameters
	 */
	@Nullable public static Method findMethodWithGreatestParamCount(Method[] methods)
	{
		Method topMethod = null;
		int topCount = -1;

		for (Method currentMethod : methods)
		{
			//Incase there are more
			if (currentMethod.getParameterTypes().length > topCount)
			{
				topMethod = currentMethod;
				topCount = currentMethod.getParameterTypes().length;
			}
		}
		return topMethod;
	}

	/**
	 * Find the first method that takes the specified number of parameters
	 * @param methods		The methods to look through
	 * @param paramCount	The number of parameters to look for
	 * @return	The first method with the specified number of parameters
	 */
	@Nullable public static Method findFirstMethodWithParamCount(Method[] methods, int paramCount)
	{

    	for (Method currentMethod : methods)
    	{
    		//Incase there are more
    		if (currentMethod.getParameterTypes().length == paramCount)
    		{
    			return currentMethod;
    		}
    	}
    	return null;
	}

	/**
	 * Find the first declared field with the type <code>clazz</code>
	 * @param pattern The class to look for
	 * @param clazz	The class to look in
	 * @return	The first declared field matching the pattern
	 */
	@Nullable public static Field findFirstDeclaredFieldWithType(Class pattern, Class clazz)
	{
		Field[] fields = clazz.getDeclaredFields();
		
    	for (Field currentField : fields)
    	{
    		//Incase there are more
    		if (currentField.getType() == pattern)
    		{
    			return currentField;
    		}
    	}
    	return null;
	}

	/**
	 * Find all declared fields with the given type
	 * @param pattern	The class type to look for
	 * @param clazz		The class to look in
	 * @return	A list containing all fields matching the pattern
	 */
	public static List<Field> findAllDeclaredFieldsWithType(Class pattern, Class clazz)
	{
		ArrayList<Field> returnFields = new ArrayList<>();

		Field[] fields = clazz.getDeclaredFields();

		for (Field currentField : fields)
		{
			//Incase there are more
			if (currentField.getType() == pattern)
			{
				returnFields.add(currentField);
			}
		}
		return returnFields;
	}

	/**
	 * Find the <code>nth Field</code> with a class type matching <code>pattern</code>
	 * @param clazz		The class to look in
	 * @param pattern	The class type to look for
	 * @param nthField	The number of the field to return, starting from 0
	 * @return
	 */
	@Nullable public static Field findNthDeclaredFieldWithType(Class clazz, Class pattern, int nthField)
	{
		List<Field> fields = findAllDeclaredFieldsWithType(pattern, clazz);

		if (nthField < fields.size())
		{
			return fields.get(nthField);
		}
		else
			return null;
	}

	/**
	 * Find all methods with the specified return type
	 * @param pattern	The return type to look for
	 * @param methods	The methods to look through
	 * @return	A list containing all methods that return <code>pattern</code>
	 */
	public static List<Method> findAllMethodsWithReturnType(Class pattern, Method[] methods)
	{
		ArrayList<Method> returnMethods = new ArrayList<Method>();

    	for (Method currentMethod : methods)
    	{
    		//Incase there are more
    		if (currentMethod.getReturnType() == pattern)
    		{
    			returnMethods.add(currentMethod);
    		}
    	}
    	return returnMethods;
	}

	/**
	 * Find all classes that are enums
	 * @param clazzes	The array of classes to look through
	 * @return	A list containing all enum classes
	 */
	public static List<Class> findAllEnums(Class[] clazzes)
	{
		ArrayList<Class> enumClazzes = new ArrayList<>();

		for (Class clazz : clazzes)
		{
			if (clazz.isEnum())
				enumClazzes.add(clazz);
		}

		return enumClazzes;
	}

	/**
	 * Given a list of enum classes, find the one with the most constants
	 * @param clazzes	The list of classes to look through
	 * @return	The enum with the most constants, or null if input contained no enums.
	 */
	@Nullable public static Class findEnumWithMostValues(Class[] clazzes)
	{
		List<Class> enumClazzes = findAllEnums(clazzes);

		Class topClazz = null;
		int topCount = -1;
		for (Class clazz : enumClazzes)
		{
			if (clazz.isEnum())
			{
				int newCount = clazz.getEnumConstants().length;
				if (newCount > topCount)
				{
					topCount = newCount;
					topClazz = clazz;
				}

			}
		}

		return topClazz;
	}

	/**
	 * Given a list of enum classes, find the one with the fewest constants
	 * @param clazzes	The list of classes to look through
	 * @return	The enum with the fewest constants, or null if input contained no enums.
	 */
	@Nullable public static Class findEnumWithFewestValues(Class[] clazzes)
	{
		List<Class> enumClazzes = findAllEnums(clazzes);

		Class topClazz = null;
		int minCount = Integer.MAX_VALUE;
		for (Class clazz : enumClazzes)
		{
			if (clazz.isEnum())
			{
				int newCount = clazz.getEnumConstants().length;
				if (newCount < minCount)
				{
					minCount = newCount;
					topClazz = clazz;
				}
			}
		}

		return topClazz;
	}

	/**
	 * Given an array of enums, returns the enum with the specified name
	 * @param enums A list of enums to search
	 * @param searchString	The name of the enum to look for
	 * @return	The matching enum, or null.
	 */
	public static Enum findEnumByName(Enum[] enums, String searchString)
	{
		for (Enum currentEnum : enums)
		{
			if (currentEnum.name().equals(searchString))
				return currentEnum;
		}

		return null;
	}


	/**
	 * Find a method by name
	 * @param methods	The methods to look through
	 * @param name		The name to look for
	 * @return	The first method with the name, or null
	 */
	@Nullable public static Method firstMethodByName(Method[] methods, String name)
	{
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].getName().equals(name))
				return methods[i];
		}
		return null;
	}


	/**
	 * Get a set containing the names of all methods in <code>methods</code>
	 * @param methods	The methods to look through
	 * @return	A set of method names
	 */
	public static Set<String> findUniqueMethodNames(Method[] methods)
	{
		HashSet<String> names = new HashSet<String>();

		for (Method currentMethod : methods)
		{
			names.add(currentMethod.getName());
		}

		return names;

	}


	/**
	 * Find the first method with a given name
	 * @param methods 	The methods to look through
	 * @param name		The name to look for
	 * @return	The first method with the name, or null
	 */
	@Nullable public static Method findFirstMethodByName(Method[] methods, String name)
	{
		for (Method currentMethod : methods)
		{
			if (currentMethod.getName().equals(name))
				return currentMethod;
		}

		return null;
	}

	/**
	 * Find the first method with a given name that also matches the input profile
	 * @param profile		The profile to compare with
	 * @param methods		The methods to look through
	 * @param parentClass	The parent class of the methods
	 * @param name			The name to look for
	 * @return
	 */
	@Nullable public static Method findFirstMatchingMethodWithName(MethodProfile profile, Method[] methods, Class parentClass, String name)
	{
		for (Method currentMethod : methods)
		{
			if (currentMethod.getName().equals(name))
			{
				if (profile.compareTo(currentMethod, parentClass))
					return currentMethod;
			}
		}
		return null;
	}


	/**
	 * Search and load a
	 * @param profile	The profile to search with
	 * @param count		The number of classes to return
	 * @param param		A populated PackageTree containing all classes
	 * @return			The top matches for the profile
	 */
	public static List<Class> loadProfiledClasses(ClassProfile profile, int count, PackageTree param)
	{
		return ProfileSearch.loadProfiledClasses(profile,count,param);
	}

	/**
	 * Search for and load a class using a ClassProfile
	 * @param profile	The profile to search with
	 * @param param		A populated PackageTree containing all classes
	 * @return			The top match for the profile
	 */
	@Nullable public static Class loadProfiledClass(ClassProfile profile, PackageTree param)
	{
		return ProfileSearch.loadProfiledClass(profile,param);
	}


	/**
	 * Check for a special NOT_FOUND item returned when the fields or parameters
	 * of a class cannot be loaded.
	 * @param profiles The profiles array to check
	 * @return	Whether the profiles contain a single NOT_FOUND item
	 */
	public static <T> boolean CheckNotFoundMatch(Profile<T>[] profiles)
	{
		if (profiles == null)
			return true;
		if (profiles.length == 1)
			return ( !Modifiers.isFound(profiles[0].getModifiers()));
		return false;
	}

	/**
	 * Check for a special NOT_FOUND item returned when the fields or parameters
	 * of a class cannot be loaded.
	 * @param profile Whether the profile is a NOT_FOUND item
	 * @return Whether the profile is a NOT_FOUND item
	 */
	public static  <T> boolean CheckNotFoundMatch(Profile<T> profile)
	{
		if (profile == null)
			return true;
		return ( !Modifiers.isFound(profile.getModifiers()));
	}


	/**
	 * For each each class in the pattern array, find the position of a matching class in the target array.
	 * The position will be stored in the returned int[] array at the same position as the pattern searched with.
	 * In the case of duplicate entries in either array, the returned array will always use the first value found.
	 * @param pattern
	 * @param target
	 * @return	An int[] array matching classes in <code>pattern</code> to clases in <code>target</code>
	 */
	public static int[] findParameterPositions(Class[] pattern, Class[] target)
	{
		int[] returnArray = new int[pattern.length];
		Arrays.fill(returnArray, -1);

		for (int i = 0; i < pattern.length; i++)
		{
			Class searchClazz = pattern[i];

			for (int j = 0; j < target.length; j++)
			{
				if (searchClazz == target[j])
				{
					returnArray[i] = j;
					break;
				}
			}
		}
		return returnArray;
	}

}
