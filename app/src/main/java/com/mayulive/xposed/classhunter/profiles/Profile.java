package com.mayulive.xposed.classhunter.profiles;

import android.support.annotation.Nullable;

/**
 * An interface for comparing classes, fields, methods, and constructors.
 * The modifiers include a bit that signifies <code>this</code>, meaning <Code>T right</Code> should match the <code>RightParentClass</code> provided.
 */
public interface Profile<T>
{
	/**
	 * @param right The class to compare to
	 * @param  rightParentClass The parent class if a field, method, or constructor parameter.
	 * @return	Whether the profile matches <code>T right</code>
	 */
	public boolean compareTo(T right, @Nullable Class rightParentClass);


	/**
	 * @param right The class to compare to
	 * @param  rightParentClass The parent class if a field, method, or constructor parameter.
	 * @return	How similar the profile is to <code>T right</code>
	 */
	public float getSimilarity(T right, @Nullable Class rightParentClass);


	/**
	 *
	 * @return The full modifier bitfield of the profile, including flags specific to this library
	 */
	public int getModifiers();


	/**
	 * If true, the profile should return the opposite value when compared.
	 * True if no match, false if match.
	 * For similarity it will return 1f-similarity
	 * @param inverted
	 * @return
	 */
	public Profile<T> setInverted(boolean inverted);


}
