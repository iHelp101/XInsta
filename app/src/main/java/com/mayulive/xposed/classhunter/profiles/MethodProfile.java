package com.mayulive.xposed.classhunter.profiles;

import android.util.Log;

import com.mayulive.xposed.classhunter.ClassHunter;
import com.mayulive.xposed.classhunter.Modifiers;
import com.mayulive.xposed.classhunter.ProfileHelpers;

import java.lang.reflect.Method;

public class MethodProfile implements Profile<Method>
{
	private static final String TAG = ClassHunter.getLogTag( MethodProfile.class );

	private ClassItem mReturnType = null;
	private ClassItem[] mParameters = null;
	private int mModifiers = -1;
	private boolean mInverted = false;
	
	
	public MethodProfile setReturnType(Class type)
	{
		mReturnType = new ClassItem(type);
		return this;
	}
	
	public MethodProfile(Class returnType, Class...  parameters)
	{
		mReturnType = new ClassItem(returnType);
		
		mParameters = new ClassItem[parameters.length];
		for (int i = 0; i < mParameters.length; i++)
		{
			mParameters[i] = new ClassItem(parameters[i]);
		}
	}
	
	public MethodProfile(ClassItem returnType, ClassItem...  parameters)
	{
		mReturnType = returnType;
		mParameters = parameters;
}

	public MethodProfile(int modifiers)
	{
		mModifiers = modifiers;
	}
	
	public MethodProfile(int modifiers, ClassItem returnType, ClassItem...  parameters)
	{
		mModifiers = modifiers;
		mReturnType = returnType;
		mParameters = parameters;
	}
	
	public MethodProfile(Class returnType)
	{
		mReturnType =  new ClassItem(returnType);
	}
	
	public MethodProfile(int modifiers, ClassItem returnType)
	{
		mModifiers = modifiers;
		mReturnType =  returnType;
	}

	public int getModifiers()
	{
		return mModifiers;
	}

	@Override
	public Profile<Method> setInverted(boolean inverted)
	{
		mInverted = inverted;
		return this;
	}

	private boolean _compareTo(Method inputMethod, Class rightParentClass)
	{
		
		if (mModifiers != -1)
		{
			if (!Modifiers.compare(mModifiers, inputMethod.getModifiers()))
				return false;
		}		
		
		//Compare return type
		if (mReturnType != null && !mReturnType.compareTo(inputMethod.getReturnType(),rightParentClass))
		{
			if (ClassHunter.DEBUG_COMPARISON)
			{
				Log.i(TAG, "Comparison failed at method return type");
				Log.i(TAG, "Left: "+mReturnType.getName()+", Right: "+inputMethod.getReturnType().getName());
			}
			return false;
		}
	
		//Method params should always be in the same order ... think.
		return ProfileHelpers.compareProfile(mParameters, inputMethod.getParameterTypes(),rightParentClass,true,false);
	}

	@Override
	public boolean compareTo(Method inputMethod, Class rightParentClass)
	{
		if (mInverted)
			return !_compareTo(inputMethod,rightParentClass);
		else
			return _compareTo(inputMethod,rightParentClass);
	}

	@Override
	public float getSimilarity(Method right, Class rightParentClass)
	{

		float[] similarities = new float[3];

		similarities[0] = ProfileHelpers.getProfileSimilarity(mParameters, right.getParameterTypes(), rightParentClass, true);
		similarities[1] = mReturnType.compareTo(right.getReturnType(),rightParentClass) ? 1 : 0;
		similarities[2] = mModifiers != -1 ?   ( Modifiers.compare(mModifiers, right.getModifiers()) ? 1 : 0 )   :  1;

		float similarity = 0;
		for (float sim : similarities)
		{
			similarity += sim;
		}

		similarity /= 3f;

		if (mInverted)
			return 1f - similarity;
		else
			return similarity;
	}


}
