package com.mayulive.xposed.classhunter;


/**
 * Containers bitfield modifier values and methods for parsing them
 * The standard values mirror that of {@link java.lang.reflect.Modifier}.
 * In addition there are some values only used by this library, stored in the latter 2 bytes.
 */
public class Modifiers
{

	/**
	 * Some bits are reused, the alternate forms only being used
	 * for methods and constructors.
	 */
	public enum ModifierType
	{
		DEFAULT,METHOD
	}

	public static final int NO_MODIFIERS = 0x0;



	//The standard 16 (19 with duplicate values) modifiers, stored in the first 2 bytes
	//These are identical to the modifiers found in the reflection lib.
	//Some of these are duplicate values, and hold different meanings for methods
	public static final int PUBLIC = 		0x0001;
	public static final int PRIVATE =		0x0002;
	public static final int PROTECTED = 	0x0004;
	public static final int STATIC = 		0x0008;
	public static final int FINAL = 		0x0010;
	public static final int SUPER = 		0x0020;	public static final int SYNCHRONIZED = 	0x0020;	//Methods
	public static final int VOLATILE = 		0x0040; public static final int BRIDGE = 	0x0040;		//Methods
	public static final int TRANSIENT = 	0x0080; public static final int VARARGS = 	0x0080;		//Methods
	public static final int NATIVE = 		0x0100;
	public static final int INTERFACE =		0x0200;
	public static final int ABSTRACT = 	 	0x0400;
	public static final int STRICT =	 	0x0800;
	public static final int SYNTHETIC =	 	0x1000;
	public static final int ANNOTATION = 	0x2000;
	public static final int ENUM =		 	0x4000;

	//Some weird unexposed values. They may or may not be generated at dex-time,
	//and are therefore ignored during comparison.
	public static final int DEFAULT =	 	0x00400000;
	public static final int CONSTRUCTOR =	0x00010000;

	//Stuff used by classSearch, stored in the next 2 bytes. Make sure to avoid overriding existing values.
	//I was under the impression that only the first 2 bytes were used. Maybe we should move to a long. Hmmmmm
	public static final int THIS = 			0x10000000;	//For method args and fields, must match containing class.
	public static final int ARRAY = 		0x20000000;	//Is an array.
	public static final int EXACT = 		0x40000000;	//All modifiers must match, probably only used by auto-parser.
	public static final int NOT_FOUND = 	0x80000000;	//Class could not be loaded. Used as a dummy item to identify such cases.
	public static final int REQUIRED = 		0x00800000;	//When comparing lists, target must contain the marked item.
														//Similarity will otherwise be set to Integer.MIN_VALUE;. Use with care.


	/**
	 * //Get just the standard flags in the first 2 bytes.
	 * @param target The bitfield
	 * @return
	 */
	public static int getStandard (int target)
	{
		return  0xFFFF & target;
	}

	/**
	 * Get only the flags specific to this library
	 * @param target
	 * @return
	 */
	public static int getInternal(int target)
	{
		return  0xFFFF0000 & target;
	}


	public static boolean is( int pattern, int target)
	{
		return (target & pattern) != 0;
	}

	public static boolean isFound(int target)
	{
		return ! ((target & NOT_FOUND) != 0);
	}

	public static boolean isExact(int target)
	{
		return (target & EXACT) != 0;
	}

	public static boolean isThis(int target)
	{
		return (target & THIS) != 0;
	}

	public static boolean isArray(int target)
	{
		return (target & ARRAY) != 0;
	}

	public static boolean isRequired(int target)
	{
		return (target & REQUIRED) != 0;
	}

	/**
	 * Target must match pattern exactly.
	 * This is called if the internal EXACT bit is set.
	 * @param pattern	Pattern to compare with
	 * @param target	Bitfield to compare to
	 * @return	If the target matches the pattern
	 */
	private static boolean compareExact(int pattern, int target)
	{
		return pattern == target;
	}

	/**
	 * Target must only contain the bits set in the pattern.
	 * Will return true even if the target has bits set that are not in the pattern.
	 * @param pattern	Pattern to compare with
	 * @param target	The bitfield to compare to
	 * @return	If the target matches the pattern
	 */
	private static boolean compareLoose(int pattern, int target)
	{
		return (pattern & target) == pattern;
	}

	//Compares a profile modifier to a target
	//second byte flags are stripped from both

	/**
	 * Compare a modifier to a pattern.
	 * We check for the extra EXACT bit first, then
	 * strip anything but the standard first-byte values before comparing.
	 * If EXACT is set they must be a perfect match, otherwise the target
	 * is only required to have the same bits set as the pattern.
	 * @param pattern	The pattern to compare with
	 * @param target	The bitfield to compare to
	 * @return
	 */
	public static boolean compare(int pattern, int target)
	{
		if (isExact(pattern))
		{
			pattern = getStandard(pattern);
			target  = getStandard(target);
			return compareExact(pattern,target);
		}
		else
		{
			pattern = getStandard(pattern);
			target  = getStandard(target);
			return compareLoose(pattern,target);
		}
	}

	public static int[] values = new int[]
	{
		PUBLIC,
		PRIVATE,
		PROTECTED,
		STATIC,
		FINAL,
		SUPER,
		SYNCHRONIZED,
		VOLATILE,
		TRANSIENT,
		NATIVE,
		INTERFACE,
		ABSTRACT,
		STRICT,
		SYNTHETIC,
		ANNOTATION,
		ENUM,

		DEFAULT,
		CONSTRUCTOR,

		THIS,
		ARRAY,
		EXACT
	};

	/**
	 * Get the bitfield in string form, delimited by |
	 * @param target	The bitfield to get the values for
	 * @param type		The type of modifier. See {@link ModifierType}
	 * @return			The bitfield in |-delimited string form
	 */
	public static String getModifierString(int target, ModifierType type)
	{
		StringBuilder builder = new StringBuilder();

		for (int flag : values)
		{
			if (is(flag,target))
			{
				builder.append(getFlagString(flag, type));
				builder.append(" | ");
			}
		}

		if (builder.length() != 0)
		{
			builder.setLength(builder.length()-2);
		}
		else
		{
			//Constructors often require /something/
			builder.append("NO_MODIFIERS");
		}

		return builder.toString();
	}


	/**
	 * Get the string representation of a single modifier
	 * @param flag 		The modifier to get the string representation of
	 * @return			The string representation of the modifier
	 */
	public static String getFlagString(int flag)
	{
		return getFlagString(flag,ModifierType.DEFAULT);
	}

	/**
	 * Get the string representation of a single modifier
	 * @param flag 		The modifier to get the string representation of
	 * @param type		The type of modifier. See {@link ModifierType}
	 * @return			The string representation of the modifier
	 */
	public static String getFlagString(int flag, ModifierType type)
	{
		switch(flag)
		{

			case PUBLIC:
				return "PUBLIC";
			case PRIVATE:
					return "PRIVATE";
			case PROTECTED:
					return "PROTECTED";
			case STATIC:
					return "STATIC";
			case FINAL:
				return "FINAL";
			case SUPER:	//Or synchronized
			{
				switch(type)
				{
					case METHOD:
						return "SYNCHRONIZED";
					default:
						return "SUPER";

				}
			}
			case VOLATILE:	//Or bridge
			{
				switch(type)
				{
					case METHOD:
						return "BRIDGE";
					default:
						return "VOLATILE";
				}
			}
			case TRANSIENT:	//Or varargs
			{
				switch(type)
				{
					case METHOD:
						return "VARARGS";
					default:
						return "TRANSIENT";
				}
			}
			case NATIVE:
				return "NATIVE";
			case INTERFACE:
				return "INTERFACE";
			case ABSTRACT:
				return "ABSTRACT";
			case STRICT:
				return "STRICT";
			case SYNTHETIC:
				return "SYNTHETIC";
			case ANNOTATION:
				return "ANNOTATION";
			case ENUM:
				return "ENUM";


			case CONSTRUCTOR:
				return "CONSTRUCTOR";
			case DEFAULT:
				return "DEFAULT";


			case THIS:
				return "THIS";
			case ARRAY:
				return "ARRAY";
			case EXACT:
				return "EXACT";
		}

		return "";
	}
}
