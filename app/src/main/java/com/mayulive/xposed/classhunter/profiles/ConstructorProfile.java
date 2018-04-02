package com.mayulive.xposed.classhunter.profiles;

import android.util.Log;

import com.mayulive.xposed.classhunter.ClassHunter;
import com.mayulive.xposed.classhunter.Modifiers;
import com.mayulive.xposed.classhunter.ProfileHelpers;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ConstructorProfile implements Profile<Constructor>
{
	private static final String TAG = ClassHunter.getLogTag( ConstructorProfile.class );

	private ClassItem[] mParameters = null;
	private int mModifiers = -1;
	private boolean mInverted = false;

	
	public ConstructorProfile(Class...  parameters)
	{
		mParameters = new ClassItem[parameters.length];
		for (int i = 0; i < mParameters.length; i++)
		{
			mParameters[i] = new ClassItem(parameters[i]);
		}
	}
	
	public ConstructorProfile()
	{
		//No params!
	}
	
	public ConstructorProfile( ClassItem...  parameters)
	{
		mParameters = parameters;
	}

	public ConstructorProfile(int modifiers, ClassItem...  parameters)
	{
		mModifiers = modifiers;
		mParameters = parameters;
	}
	
	public ConstructorProfile(int modifiers)
	{
		mModifiers = modifiers;
	}

	public int getModifiers()
	{
		return mModifiers;
	}

	@Override
	public Profile<Constructor> setInverted(boolean inverted)
	{
		mInverted = inverted;
		return this;
	}


	public boolean _compareTo(Constructor inputConstructor, Class rightParentClass)
	{

		if (mModifiers != -1)
		{
			if (!Modifiers.compare(mModifiers,inputConstructor.getModifiers()))
			{
				if (ClassHunter.DEBUG_COMPARISON)
				{
					Log.i(TAG, "Constructor Mod comp failed: Left: "+Modifiers.getStandard(mModifiers)+", right: "+inputConstructor.getModifiers());

				}
				return false;
			}
		}

		//Constructors are somewhat unique in that you can get them from a class even if they include unloadable parameters.
		//Getting fields will throw immediately, as will methods because they try to load the return type.
		boolean paramMatch = false;
		try
		{
			//Method params should always be in the same order ... think.
			paramMatch = ProfileHelpers.compareProfile(mParameters, inputConstructor.getParameterTypes(),rightParentClass,true,false);
		}
		catch (NoClassDefFoundError ex)
		{
			paramMatch = ProfileHelpers.CheckNotFoundMatch(mParameters);
		}


		if (paramMatch)
		{
			return true;
		}
		else
		{
			if (ClassHunter.DEBUG_COMPARISON)
			{
				Log.i(TAG, "Constructor param comp failed: Left: "+ Arrays.toString(mParameters)+", right: "+inputConstructor.getParameterTypes());
			}
			return false;
		}
	}

	@Override
	public boolean compareTo(Constructor inputConstructor, Class rightParentClass)
	{
		if (mInverted)
			return !_compareTo(inputConstructor,rightParentClass);
		else
			return _compareTo(inputConstructor,rightParentClass);
	}

		@Override
	public float getSimilarity(Constructor right, Class rightParentClass)
	{
		float[] similarities = new float[2];

		//Constructors are somewhat unique in that you can get them from a class even if they include unloadable parameters.
		//Getting fields will throw immediately, as will methods because they try to load the return type.
		try
		{
			similarities[0] = ProfileHelpers.getProfileSimilarity(mParameters, right.getParameterTypes(), rightParentClass, true);
		}
		catch (NoClassDefFoundError ex)
		{
			similarities[0] =  ProfileHelpers.CheckNotFoundMatch(mParameters) ? 1 : 0;
		}

		similarities[1] = mModifiers != -1 ?   ( Modifiers.compare(mModifiers, right.getModifiers()) ? 1 : 0 )   :  1;

		float similarity = 0;
		for (float sim : similarities)
		{
			similarity += sim;
		}

		similarity /= 2f;

		if (mInverted)
			return 1f - similarity;
		else
			return similarity;
	}
}