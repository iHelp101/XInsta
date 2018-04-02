package com.mayulive.xposed.classhunter.profiles;

public class ProfileUtil
{
	/**
	 * The only name method that also returns arrays as classname[]
	 * is getCanonicalName(). Unfortunately, some classes, such as anon,
	 * do not have canonical names.
	 * Solution: Try canonical, use getName if it fails.
	 * @param clazz The class to get the name of
	 * @return	The canonical name, the name-name, or an empty string
	 */

	public static String getName(Class clazz)
	{
		if (clazz == null)
			return "";
		String name = clazz.getCanonicalName();
		if (name == null)
			name = clazz.getName();
		return name;
	}

}
