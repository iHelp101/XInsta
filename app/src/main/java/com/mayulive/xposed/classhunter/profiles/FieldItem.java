package com.mayulive.xposed.classhunter.profiles;

import com.mayulive.xposed.classhunter.Modifiers;

import java.lang.reflect.Field;


public class FieldItem implements Profile<Field>
{
	//Takes a class item and a modifier
	private int mModifiers = -1;
	private ClassItem mClassItem;
	private boolean mInverted = false;

	public FieldItem(ClassItem clazz)
	{
		mClassItem = clazz;
	}

	public FieldItem(ClassItem clazz, int modifiers)
	{
		mClassItem = clazz;
		mModifiers = modifiers;
	}


	public FieldItem(int modifiers, ClassItem clazz)
	{
		mClassItem = clazz;
		mModifiers = modifiers;
	}


	public FieldItem(int modifiers)
	{
		mModifiers = modifiers;
	}

	public int getModifiers()
	{
		return mModifiers;
	}

	@Override
	public Profile<Field> setInverted(boolean inverted)
	{
		mInverted = inverted;
		return this;
	}

	public boolean _compareTo(Field right, Class rightParentClass)
	{
		if (  !(mModifiers == -1 ? true : Modifiers.compare(mModifiers, right.getModifiers()) ) )
			return false;
		else
		{
			return mClassItem != null ?  mClassItem.compareTo(right.getType(), rightParentClass) : true;
		}
	}

	@Override
	public boolean compareTo(Field right, Class rightParentClass)
	{
		if (mInverted)
			return !_compareTo(right,rightParentClass);
		else
			return _compareTo(right,rightParentClass);
	}

	@Override
	public float getSimilarity(Field right, Class rightParentClass)
	{
		//TODO implement more proper similarity?
		if (mInverted)
			return compareTo(right,rightParentClass) ? 0 : 1;
		else
			return compareTo(right,rightParentClass) ? 1 : 0;
	}
}