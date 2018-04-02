package com.mayulive.xposed.classhunter.profiles;

import android.util.Log;

import com.mayulive.xposed.classhunter.ClassHunter;
import com.mayulive.xposed.classhunter.Modifiers;
import com.mayulive.xposed.classhunter.ProfileHelpers;

import java.util.HashSet;
import java.util.Set;

import static com.mayulive.xposed.classhunter.ProfileHelpers.compareProfile;
import static com.mayulive.xposed.classhunter.profiles.ClassProfile.CLASS_DATA.*;


public class ClassProfile implements Profile<Class>
{
	/**
	 * Designates a single type of data stored in a ClassProfile used to compare it to a class
	 */
	public enum CLASS_DATA
	{
		MODIFIERS,

		TYPE_PARAM_COUNT,

		INTERFACES,

		SUPER_CLASS,

		NESTED_CLASS,

		ENUM_VALUES,
		DECLARED_FIELDS,
		DECLARED_METHODS,
		DECLARED_CONSTRUCTORS,
		
		PUBLIC_FIELDS,
		PUBLIC_METHODS
	}

	private static final CLASS_DATA[] comparisons = new CLASS_DATA[]
	{
			MODIFIERS,
			ENUM_VALUES,
			DECLARED_CONSTRUCTORS,
			DECLARED_FIELDS,
			DECLARED_METHODS,
			PUBLIC_FIELDS,
			PUBLIC_METHODS,
			TYPE_PARAM_COUNT,
			INTERFACES,
			SUPER_CLASS,
			NESTED_CLASS
	};

	private static final String TAG = ClassHunter.getLogTag( ClassProfile.class );

	private String mFullPath = "";
	private String mKnownPath = "";

	//Depth branch to search starting from known path
	private int mMinDepth = 0;
	private int mMaxDepth = 0;

	private int  mModifiers = -1;

	private int mTypeParamCount = -1;

	private ClassItem[] mInterfaces;
	private ClassItem mSuperClass;

	private ClassItem[] mNestedClasses;

	private String[] mEnumValues;// = new String[0];

	private FieldItem[] mPublicFields;// = new FieldItem[0];
	private FieldItem[] mDeclaredFields;// = new FieldItem[0];
	
	private MethodProfile[] mPublicMethods;// = new MethodProfile[0];
	private MethodProfile[] mDeclaredMethods;// = new MethodProfile[0];

	private ConstructorProfile[] mDeclaredConstructors;// = new ConstructorProfile[0];

	private boolean mInverted = false;
	
	public ClassProfile()
	{

	}

	public boolean compareTo(Class inputClass, Class rightParentClass)
	{
		if (inputClass == null)
			return mInverted ? true : false;

		//Was going to do something with these, never did.
		//You really only want to compare a class profile if you are checking
		//that it matches 100%, anything else should should use similarity scores.
		boolean ordered = true;
		boolean allowPartial = false;

		boolean[] matched = new boolean[comparisons.length];
		boolean allMatched = true;

		for (int i = 0; i < comparisons.length; i++)
		{
			matched[i] = compareToSingle(comparisons[i],inputClass,ordered,allowPartial);
			allMatched &= matched[i];
			if (ClassHunter.DEBUG_CLASS_COMPARISON)
			{
				Log.i(TAG, "Class comparison "+ comparisons[i].toString()+": "+matched[i]);
			}
		}

		return mInverted ? !allMatched : allMatched;
	}

	/**
	 * Compare enum values in this profile to that of an input enum class
	 * This is a loose comparison, and will return true as long as the input
	 * class contants at least all the enum constants in the profile.
	 * @param rightClass	The enum class to compare to
	 * @return				Whether the enum class matches the profile
	 */
	private boolean compareEnumValues( Class rightClass)
	{
		if (mEnumValues == null)
			return true;

		if (!rightClass.isEnum())
			return false;

		Object[] rightEnumValues = rightClass.getEnumConstants();
		Set<String> enumMap = new HashSet<String> ();
		for (Object enumVal : rightEnumValues)
		{
			enumMap.add(enumVal.toString());
		}

		for (Object enumVal : mEnumValues)
		{
			if (!enumMap.contains(enumVal))
			{
				if(ClassHunter.DEBUG_COMPARISON)
					Log.i(TAG, "Could not find enum: "+enumVal.toString());

				return false;
			}
		}

		return true;
	}

	/**
	 * Get the similarity score of the enum constants in this profile compared to another enum class
	 * @param rightClass	The enum class to compare to
	 * @return				The similarity score of the two
	 */
	private float getEnumSimilarity( Class rightClass)
	{
		//Null means ignore
		if (mEnumValues == null)
			return 1;

		if (!rightClass.isEnum())
			return 0;

		float foundCount = 0;

		Object[] rightEnumValues = rightClass.getEnumConstants();
		Set<String> enumMap = new HashSet<String> ();
		for (Object enumVal : rightEnumValues)
		{
			enumMap.add(enumVal.toString());
		}

		for (Object enumVal : mEnumValues)
		{

			if (enumMap.contains(enumVal))
			{
				foundCount++;
			}
		}

		float similarity = 1;
		if (mEnumValues.length > 0)
			similarity = foundCount / mEnumValues.length;

		return similarity;
	}


	/**
	 * Get whether the profile matches another class for a single {@link CLASS_DATA} data type
	 * @param dataType		The data type to compare
	 * @param rightClass	The class to compare to
	 * @param ordered		Whether values must be in the same order (where applicable)
	 * @param allowPartial	Whether a partial match will be allowed (where applicable)
	 * @return				Whether the profile matches the class
	 */
	public boolean compareToSingle(CLASS_DATA dataType, Class rightClass, boolean ordered, boolean allowPartial)
	{
		try
		{
			switch (dataType)
			{
				case TYPE_PARAM_COUNT:
				{
					return mTypeParamCount == -1 ? true : mTypeParamCount == rightClass.getTypeParameters().length;
				}

				case INTERFACES:
				{
					return compareProfile(getInterfaces(), rightClass.getInterfaces(), rightClass, ordered, allowPartial);
				}

				case NESTED_CLASS:
				{
					return compareProfile(getNestedClasses(), rightClass.getDeclaredClasses(), rightClass, ordered, allowPartial);
				}

				case SUPER_CLASS:
				{
					return mSuperClass == null ? true : mSuperClass.compareTo(rightClass.getSuperclass(), rightClass.getSuperclass());
				}

				case MODIFIERS:
					return mModifiers == -1 ? true : Modifiers.compare(mModifiers, rightClass.getModifiers());

				case ENUM_VALUES:
					return compareEnumValues(rightClass);

				case DECLARED_CONSTRUCTORS:
					return compareProfile(getDeclaredConstructors(), rightClass.getDeclaredConstructors(), rightClass, ordered, allowPartial);

				case DECLARED_FIELDS:
				{
					return compareProfile(getDeclaredFields(), rightClass.getDeclaredFields(), rightClass, ordered, allowPartial);
				}

				case DECLARED_METHODS:
					return compareProfile(getDeclaredMethods(), rightClass.getDeclaredMethods(), rightClass, ordered, allowPartial);

				case PUBLIC_FIELDS:
					return compareProfile(getPublicFields(), rightClass.getFields(), rightClass, ordered, allowPartial);

				case PUBLIC_METHODS:
					return ProfileHelpers.compareProfile(getPublicMethods(), rightClass.getMethods(), rightClass, ordered, allowPartial);
				default:
					return true;
			}
		}
		catch (NoClassDefFoundError ex)
		{
			//Log.e("###", "Caught NoClassDefFoundError while comparing class: "+ProfileUtil.getName(rightClass)+", type: "+dataType.toString());
			//ex.printStackTrace();
			return handleClassDefNotFoundError(dataType);
		}
	}

	/**
	 * Handle a ClassDefNotFoundError when attempting to compare to a class
	 * @param dataType
	 * @return
	 */
	private boolean handleClassDefNotFoundError(CLASS_DATA dataType)
	{
		//This actually happens from time to time, usually when trying to
		//fetch the list of fields, methods (return type fails), or constructors (params).
		//Presumably some leftover unit test stuff?
		//When the profiler encounters one of these we add a dummy item with
		//a modifier bit set to NOT_FOUND. If a dummy item is set and we catch
		//this exception, return true. If profile is null return true.

		switch (dataType)
		{
			case TYPE_PARAM_COUNT:
				return true;	//I don't think this should ever happen?

			case INTERFACES:
				return ProfileHelpers.CheckNotFoundMatch(getInterfaces());


			case NESTED_CLASS:
				return ProfileHelpers.CheckNotFoundMatch(getNestedClasses());


			case SUPER_CLASS:
				return ProfileHelpers.CheckNotFoundMatch(getSuperClass());


			case MODIFIERS:
				return true;	//Also impossible

			case ENUM_VALUES:
				return true;	//Also impossible

			case DECLARED_CONSTRUCTORS:
				return ProfileHelpers.CheckNotFoundMatch(getDeclaredConstructors());	//Should be handled by constructor profile


			case DECLARED_FIELDS:
				return ProfileHelpers.CheckNotFoundMatch(getDeclaredFields());


			case DECLARED_METHODS:
				return ProfileHelpers.CheckNotFoundMatch(getDeclaredMethods());

			case PUBLIC_FIELDS:
				return ProfileHelpers.CheckNotFoundMatch(getPublicFields());

			case PUBLIC_METHODS:
				return ProfileHelpers.CheckNotFoundMatch(getPublicMethods());

			default:
				return true;
		}
	}

	public float getSimilarity(Class rightClass, Class rightParentClass)
	{
		float similarity = 0;

		for (int i = 0; i < comparisons.length; i++)
		{
			float sim = getSimilaritySingle(comparisons[i],rightClass);
			similarity += sim;
			if (ClassHunter.DEBUG_CLASS_SIMILARITY)
			{
				Log.i(TAG, "Similarity "+ comparisons[i].toString()+": "+sim);
			}
		}

		similarity = similarity / (float) comparisons.length;

		if (mInverted)
			return 1f - similarity;
		else
			return similarity;
	}


	/**
	 * Get the similarity to another class for a single {@link CLASS_DATA} data type
	 * @param dataType		The data type to compare
	 * @param rightClass	The class to compare to
	 * @return				The similarity score for the single data type
	 */
	public float getSimilaritySingle(CLASS_DATA dataType, Class rightClass)
	{
		try
		{
			switch(dataType)
			{
				case TYPE_PARAM_COUNT:
				{
					if (mTypeParamCount == -1)
					{
						return 1;
					}
					else
					{
						//Well that got complicated
						int selfCount = mTypeParamCount;
						int otherCount = rightClass.getTypeParameters().length;

						if (selfCount == 0 && otherCount == 0)
							return 1;
						else if (otherCount == 0)
							return 0;

						if (selfCount > otherCount)
						{
							int temp = selfCount;
							selfCount = otherCount;
							otherCount = temp;
						}

						return selfCount / otherCount;
					}
				}

				case INTERFACES:
				{
					return ProfileHelpers.getProfileSimilarity(getInterfaces(), rightClass.getInterfaces(), rightClass, false);
				}

				case SUPER_CLASS:
				{
					return mSuperClass == null ? 1 : (mSuperClass.compareTo(rightClass.getSuperclass(), null) ? 1 : 0);
				}

				case NESTED_CLASS:
				{
					return ProfileHelpers.getProfileSimilarity(getNestedClasses(), rightClass.getDeclaredClasses(), rightClass, false);
				}

				case MODIFIERS:
					return (mModifiers == -1 ? true : Modifiers.compare(mModifiers,rightClass.getModifiers()) ) ? 1 : 0;

				case ENUM_VALUES:
					return getEnumSimilarity(rightClass);

				case DECLARED_CONSTRUCTORS:
					return ProfileHelpers.getProfileSimilarity(getDeclaredConstructors(), rightClass.getDeclaredConstructors(), rightClass, false);

				case DECLARED_FIELDS:
				{
					return ProfileHelpers.getProfileSimilarity(getDeclaredFields(), rightClass.getDeclaredFields(), rightClass, false);
				}

				case DECLARED_METHODS:
					return ProfileHelpers.getProfileSimilarity(getDeclaredMethods(), rightClass.getDeclaredMethods(), rightClass, false);

				case PUBLIC_FIELDS:
					return ProfileHelpers.getProfileSimilarity(getPublicFields(), rightClass.getFields(), rightClass, false);

				case PUBLIC_METHODS:
					return ProfileHelpers.getProfileSimilarity(getPublicMethods(), rightClass.getMethods(), rightClass, false);

				default:
					return 1;
			}
		}
		catch (NoClassDefFoundError ex)
		{
			//Log.i(TAG, "Caught NoClassDefFoundError while comparing class: "+ProfileUtil.getName(rightClass));
			return handleClassDefNotFoundError(dataType) ? 1 : 0;
		}

	}

	/**
	 *
	 * @param depth The minimum depth below knownPath to search
	 */
	public void setMinDepth(int depth)
	{
		mMinDepth = depth;
	}

	/**
	 *
	 * @param depth The maximum depth below knownPath to search
	 */
	public void setMaxDepth(int depth)
	{
		mMaxDepth = depth;
	}

	public int getMinDepth()
	{
		return mMinDepth;
	}

	public int getMaxDepth()
	{
		return mMaxDepth;
	}


	public void setFullPath(String fullPath)
	{
		mFullPath = fullPath;

	}
	public String getFullPath()
	{
		return mFullPath;
	}

	public void setKnownPath(String knownPath)
	{
		mKnownPath = knownPath;
	}

	public String getKnownPath()
	{
		return mKnownPath;
	}

	public void setModifiers(int modifiers)
	{
		mModifiers = modifiers;
	}

	public int getModifiers()
	{
		return mModifiers;
	}

	@Override
	public Profile<Class> setInverted(boolean inverted)
	{
		mInverted = inverted;
		return this;
	}

	public boolean getInverted()
	{
		return mInverted;
	}

	public void setTypeParamCount(int count)
	{
		mTypeParamCount = count;
	}

	public int getTypeParamCount()
	{
		return mTypeParamCount;
	}

	public void setInterfaces(ClassItem[] interfaces)
	{
		mInterfaces = interfaces;
	}

	public ClassItem[] getInterfaces()
	{
		return mInterfaces;
	}

	public void setSuperClass(ClassItem superClass)
	{
		mSuperClass = superClass;
	}

	public ClassItem getSuperClass()
	{
		return mSuperClass;
	}

	public void setEnumValues(String[] values)
	{
		mEnumValues = values;
	}

	public String[] getEnumValues()
	{
		return mEnumValues;
	}


	public ClassItem[] getNestedClasses()
	{
		return mNestedClasses;
	}

	public void setNestedClasses(ClassItem[] nestedClasses)
	{
		this.mNestedClasses = nestedClasses;
	}

	public ConstructorProfile[] getDeclaredConstructors() {
		return mDeclaredConstructors;
	}



	public void setDeclaredConstructors(ConstructorProfile[] declaredConstructors) {
		this.mDeclaredConstructors = declaredConstructors;
	}



	public MethodProfile[] getDeclaredMethods() {
		return mDeclaredMethods;
	}



	public void setDeclaredMethods(MethodProfile[] declaredMethods) {
		this.mDeclaredMethods = declaredMethods;
	}



	public FieldItem[] getDeclaredFields() {
		return mDeclaredFields;
	}



	public void setDeclaredFields(FieldItem[] declaredFields) {
		this.mDeclaredFields = declaredFields;
	}

	public void setDeclaredFields(ClassItem[] declaredFields) {
		this.mDeclaredFields = ClassToField(declaredFields);
	}

	public MethodProfile[] getPublicMethods() {
		return mPublicMethods;
	}

	public void setPublicMethods(MethodProfile[] publicMethods) {
		this.mPublicMethods = publicMethods;
	}



	public FieldItem[] getPublicFields() {
		return mPublicFields;
	}


	public void setPublicFields(FieldItem[] publicFields) {
		this.mPublicFields = publicFields;
	}


	public void setPublicFields(ClassItem[] publicFields) {
		this.mPublicFields = ClassToField(publicFields);
	}

	private static FieldItem[] ClassToField(ClassItem[] items )
	{
		FieldItem[] fieldItems = new FieldItem[items.length];
		for (int i = 0; i < items.length; i++)
		{
			fieldItems[i] = new FieldItem(items[i]);
		}

		return fieldItems;
	}


	/**
	 *
	 * @return A shallow copy of this instance
	 */
	public ClassProfile copy()
	{
		ClassProfile newProfile = new ClassProfile();

		newProfile.setMaxDepth(getMaxDepth());
		newProfile.setMinDepth(getMinDepth());

		newProfile.setInverted(getInverted());

		newProfile.setFullPath( getFullPath());
		newProfile.setKnownPath(getFullPath());
		newProfile.setModifiers(getModifiers());

		newProfile.setDeclaredFields(getDeclaredFields());
		newProfile.setPublicFields(getPublicFields());

		newProfile.setDeclaredMethods(getDeclaredMethods());
		newProfile.setPublicMethods(getPublicMethods());

		newProfile.setDeclaredConstructors(getDeclaredConstructors());

		newProfile.setTypeParamCount(getTypeParamCount());
		newProfile.setInterfaces(getInterfaces());
		newProfile.setSuperClass(getSuperClass());

		newProfile.setNestedClasses(getNestedClasses());

		return newProfile;
	}

}









