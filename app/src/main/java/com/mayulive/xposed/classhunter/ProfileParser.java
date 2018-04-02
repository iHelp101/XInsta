package com.mayulive.xposed.classhunter;

import android.util.Log;

import com.mayulive.xposed.classhunter.packagetree.PackageTree;
import com.mayulive.xposed.classhunter.profiles.ProfileUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileParser
{

	private static final String TAG = ClassHunter.getLogTag( ProfileParser.class );

	private Set<String> mKnownPathIncludes = new HashSet<>();	//Set of paths that should be treated as resolvable
	private Map<String,String> mObfuscatedMap = null;			//Maps obfuscated paths to the known portion of the path

	private Set<String> mKnownClassList = new HashSet<>();		//Paths treated by the parser as known during last parse.
	private Set<String> mUnknownClassList = new HashSet<>();	//Paths treated by the parser as unknown during last parse.


	/**
	 * Set the list of paths that should be treated as resolvable, and mappings of
	 * obfuscated paths to the known portion of their paths.
	 * @param pathIncludes		The list of known, resolvable paths
	 * @param obfuscatedPath	A map of fullpath-knownpath pairs
	 */
	public void setPaths(String[] pathIncludes, Map<String,String> obfuscatedPath)
	{
		mKnownPathIncludes.clear();
		mKnownPathIncludes.addAll(Arrays.asList(pathIncludes));

		mObfuscatedMap = obfuscatedPath;
	}

	/**
	 * Get a list of the paths that were treated as unknown (unresolvable) during the last parse
	 */
	public List<String> getUnknownClassList()
	{
		ArrayList<String> paths = new ArrayList<>();
		paths.addAll(mUnknownClassList);

		Collections.sort(paths);
		return paths;
	}

	/**
	 * Get a list of the paths that were treated as known (resolvable) during the last parse
	 */
	public List<String> getKnownClassList()
	{
		ArrayList<String> paths = new ArrayList<>();
		paths.addAll(mKnownClassList);

		Collections.sort(paths);
		return paths;
	}

	//Use for classes
	private static String getModifiers( Class clazz, int additionalFlags)
	{
		return Modifiers.getModifierString(  clazz.getModifiers()|additionalFlags, Modifiers.ModifierType.DEFAULT);
	}

	private static String getModifiers(Field field)
	{
		return Modifiers.getModifierString(field.getModifiers() | Modifiers.EXACT, Modifiers.ModifierType.DEFAULT);
	}

	private static String getModifiers(Method method)
	{
		return Modifiers.getModifierString(method.getModifiers() | Modifiers.EXACT, Modifiers.ModifierType.METHOD);
	}

	private static String getModifiers(Constructor constructor)
	{
		return Modifiers.getModifierString(constructor.getModifiers() | Modifiers.EXACT, Modifiers.ModifierType.METHOD);
	}


	private String getClassProfileName(Class clazz, Class ParentClass)
	{

		String name = ProfileUtil.getName(clazz);

		//Primitives and anything in java.lang should always be known.
		boolean known = false;

		//an array is not a primitive, so check component type instead.
		//Check for primitives and java.*
		if (clazz.isPrimitive() || ( clazz.isArray() && clazz.getComponentType().isPrimitive() ))
			known = true;
		else
		{
			known = name.startsWith("java.");
		}

		//For informing page of what classes are used.
		//This should be before we check what the user has configured.
		if(known)
			mKnownClassList.add(name);
		else
			mUnknownClassList.add(name);


		//Check user input
		if (mKnownPathIncludes.contains(name))
			known = true;

		//If still unknown, check for obfuscated paths
		int additionalFlags = Modifiers.EXACT;	//Auto-profiled classes should always be exact.

		if (!known)
		{
			String unofuscatedName = mObfuscatedMap.get(name);

			if (clazz.isArray())
			{
				//The class at this point is actually the array class,
				//which is pretty much empty. For the sake of obtaining
				//a meaningful match, we want the modifiers of the contained class.
				clazz = clazz.getComponentType();
				name = ProfileUtil.getName(clazz);
				additionalFlags |= Modifiers.ARRAY;
			}

			if (unofuscatedName != null)
				name = unofuscatedName;

		}

		if (ParentClass != null && clazz == ParentClass)
		{
			if (!known)
				additionalFlags |= Modifiers.THIS;
			//return "thizDummyClass.class, "+"\""+name+"\"";
		}

		if (known)
			return name+".class";
		else
		{
			return "\""+name+"\" , "+getModifiers(clazz,additionalFlags);
		}
	}
	
	private String parseClass(Class clazz, Class parentClass, boolean includeComma)
	{
		String name = getClassProfileName(clazz,parentClass);

		return "	new ClassItem("+name+")"+(includeComma ? "," : "") ;
	}


	
	
	private String parseField(Field field, Class parentClass)
	{

		return "	new FieldItem( "+getModifiers(field)+", "+parseClass(field.getType(),parentClass,false)+")";
	}
	
	
	private String parseMethod(Method method, int index, Class parentClass)
	{

		StringBuilder builder = new StringBuilder();
		
		builder.append("	//Method #"+index+": "+method.getName()+"\n");
		
		builder.append("	new MethodProfile\n");
		builder.append("	(\n");
		
		builder.append("		"+getModifiers(method)+",\n");


		Class returnType = method.getReturnType();
		builder.append("	"+parseClass(returnType,parentClass,false));

		Class[] params = method.getParameterTypes();

		if (params.length > 0)
			builder.append(",\n");
		builder.append("\n");

		for (int i = 0; i < params.length; i++)
		{
			builder.append( "	"+parseClass(params[i],parentClass, i+1<params.length )+"\n" );
		}

		builder.append("\n	),\n");
		
		return builder.toString();
	}

	
	private String parseConstructor(Constructor constructor, int index, Class parentClass)
	{

		StringBuilder builder = new StringBuilder();
		
		builder.append("	//Constructor #"+index+"\n");
		
		builder.append("	new ConstructorProfile\n");
		builder.append("	(");
		builder.append("		"+getModifiers(constructor)+",\n");
		
		builder.append("\n");

		try
		{
			Class[] params = constructor.getParameterTypes();
			for (int i = 0; i < params.length; i++)
			{
				builder.append( "	"+parseClass(params[i],parentClass,i+1<params.length)+"\n" );
			}

			//No params, but syntax requires no space after modifier, or empty array.
			if (params.length == 0)
			{
				builder.append( "	new ClassItem[0]\n");
			}
		}
		catch (NoClassDefFoundError ex)
		{
			builder.append("new ClassItem(NOT_FOUND)\n");
		}

		builder.append("\n	),\n");

		//builder.append("#")
		
		
		//return "	new ClassItem("+getClassProfileName(field.getType(),null)+"),";		
		
		return builder.toString();
	}


	private String parseEnumValues(Class clazz, String itemName)
	{

		if (!clazz.isEnum())
			return "";


		StringBuilder builder = new StringBuilder();

		builder.append("/////////////////////////\n");
		builder.append("//Enum Values\n");
		builder.append("/////////////////////////\n");

		builder.append(itemName+".setEnumValues(new String[]\n");


		builder.append("{\n");

		Object[] enums;
		enums = clazz.getEnumConstants();


		for (int i = 0; i < enums.length; i++)
		{
			builder.append("\""+enums[i].toString()+"\","+"\n");
		}

		builder.append("\n");
		builder.append("});\n");

		return builder.toString();
	}

	private String parseFields(Class clazz, boolean publicFields, String itemName)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("/////////////////////////\n");
		if (publicFields)
			builder.append("//Public fields\n");
		else
			builder.append("//Declared fields\n");
		builder.append("/////////////////////////\n");

		builder.append(itemName);
		if (publicFields)
			builder.append(".setPublicFields(new FieldItem[]\n");
		else
			builder.append(".setDeclaredFields(new FieldItem[]\n");
		
		builder.append("{\n");

		//Like methods, just getting the fields will throw
		try
		{
			Field[] fields;
			if (publicFields)
				fields= clazz.getFields();
			else
				fields = clazz.getDeclaredFields();

			for (int i = 0; i < fields.length; i++)
			{
				builder.append(parseField(fields[i],clazz));

				builder.append(",");
				builder.append("	//");
				builder.append(fields[i].getName());
				builder.append("\n");
			}
		}
		catch (NoClassDefFoundError ex)
		{
			builder.append("new FieldItem(NOT_FOUND)");
		}

		builder.append("\n");
		builder.append("});\n");
		
		return builder.toString();
	}

	private String parseMethods(Class clazz, boolean publicFields, String itemName)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("/////////////////////////\n");
		if (publicFields)
			builder.append("//Public Methods\n");
		else
			builder.append("//Declared Methods\n");
		builder.append("/////////////////////////\n");
		
		if (publicFields)
			builder.append(itemName+".setPublicMethods(new MethodProfile[]\n");
		else
			builder.append(itemName+".setDeclaredMethods(new MethodProfile[]\n");

		builder.append("{\n");


		//getDeclared/Methods throws because it also tries to get return type, which may not exist on dexpath.
		try
		{
			Method[] methods;
			if (publicFields)
				methods= clazz.getMethods();
			else
				methods = clazz.getDeclaredMethods();

			for (int i = 0; i < methods.length; i++)
			{
				builder.append(parseMethod(methods[i], i, clazz) +"\n");
			}
		}
		catch (NoClassDefFoundError ex)
		{
			builder.append("new MethodProfile(NOT_FOUND)\n");
		}

		
		builder.append("\n");
		builder.append("});\n");
		
		return builder.toString();
	}


	private String parseConstructors(Class clazz, String itemName)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("/////////////////////////\n");
		builder.append("//Declared Constructors\n");
		builder.append("/////////////////////////\n");

		builder.append(itemName+".setDeclaredConstructors(new ConstructorProfile[]\n");
		
		builder.append("{\n");

		Constructor[] constructors;
		constructors = clazz.getDeclaredConstructors();
		for (int i = 0; i < constructors.length; i++)
		{
				builder.append(parseConstructor(constructors[i], i, clazz) + "\n");
		}

		builder.append("\n");
		builder.append("});\n");
		
		return builder.toString();
	}

	private String parseModifiers(Class clazz, String profileName)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(profileName+".setModifiers("+getModifiers(clazz,0)+");\n");
		builder.append("\n");

		return builder.toString();
	}

	private static String getPaths(String profileName, String fullPath, String knownPath)
	{
		return "\n"+profileName+".setFullPath(\""+fullPath+"\");\n"+profileName+".setKnownPath(\""+knownPath+"\");\n\n";
	}



	private String parseInterfaces(Class clazz, String itemName)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("/////////////////////////\n");
		builder.append("//Interfaces\n");
		builder.append("/////////////////////////\n");

		builder.append(itemName+".setInterfaces(new ClassItem[]\n");

		builder.append("{\n");

		try
		{
			Class[] classes = clazz.getInterfaces();
			for (int i = 0; i < classes.length; i++)
			{
				builder.append(parseClass(classes[i],null, i < classes.length-1));
				builder.append("\n");
			}
		}
		catch (NoClassDefFoundError ex)
		{
			builder.append("new ClassItem(NOT_FOUND)\n");
		}

		builder.append("\n");
		builder.append("});\n");

		return builder.toString();
	}


	private String parseNestedClasses(Class clazz, String itemName)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("/////////////////////////\n");
		builder.append("//Nested Classes\n");
		builder.append("/////////////////////////\n");

		builder.append(itemName+".setNestedClasses(new ClassItem[]\n");

		builder.append("{\n");

		try
		{
			Class[] classes = clazz.getDeclaredClasses();

			for (int i = 0; i < classes.length; i++)
			{
				builder.append(parseClass(classes[i],null, i < classes.length-1));
				builder.append("\n");
			}
		}
		catch (NoClassDefFoundError ex)
		{
			builder.append("new ClassItem(NOT_FOUND)\n");
		}

		builder.append("\n");
		builder.append("});\n");

		return builder.toString();
	}

	private String parseSuperClass(Class clazz, String itemName)
	{
		//Primitives, interfaces, voids and such do not have a superclass
		if (clazz.getSuperclass() == null)
			return "";

		StringBuilder builder = new StringBuilder();

		builder.append(itemName+".setSuperClass(");

		builder.append(parseClass(clazz.getSuperclass(), null, false));
		builder.append(");\n");

		return builder.toString();
	}

	private String parseTypeParamCount(Class clazz, String itemName)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(itemName+".setTypeParamCount(");
		builder.append(clazz.getTypeParameters().length);
		builder.append(");\n");

		return builder.toString();
	}

	private String getDepth(String itemName, int depth)
	{
		StringBuilder builder = new StringBuilder();

		builder.append(itemName+".setMinDepth("+depth);
		builder.append(");\n");
		builder.append(itemName+".setMaxDepth("+depth);
		builder.append(");\n");

		return builder.toString();
	}

	/**
	 * Creates a profile for a class, but naively estimates depth. See {@link #getClassProfile(Class,String,String,int,boolean)}.
	 */
	public String getClassProfile(Class clazz, String profileName, String knownPath, boolean includePublic)
	{
		int depth = PackageTree.getSimpleDepth(clazz.getName(), knownPath);
		return getClassProfile(clazz,profileName,knownPath, depth, includePublic);
	}


	/**
	 * Generate a profile of the input class.
	 * The profile is standard Java code.
	 * <code>depth</code> is the number of levels below the knownPath the class resides at.
	 * A class <code>com.nordskog.abc</code> with the known path <code>com.nordskog</code> would have a depth of 0.
	 * If the full path is known the depth is -1. If no packages or class names are expected to contain the symbol <code>$</code>,
	 * you may omit the depth and have it resolved for you: {@link #getClassProfile(Class,String,String,boolean)}. Otherwise a populated {@link PackageTree} is required to resolve it.
	 * @param clazz			The class to profile
	 * @param profileName	The full path (name) of the class
	 * @param knownPath		The known path of the class, if partially obfuscated
	 * @param depth			The number of package or class levels from the knownPath the class is located at
	 * @param includePublic Whether public fields and methods should be included in the profile.
	 * @return				A string containing the profile definition in Java code
	 */
	public String getClassProfile(Class clazz, String profileName, String knownPath, int depth, boolean includePublic)
	{
		String fullPath = clazz.getName();

		mKnownClassList.clear();
		mUnknownClassList.clear();

		try
		{
			StringBuilder builder = new StringBuilder();

			builder.append("public static ClassProfile getProfile()\n{\n");

			builder.append( "ClassProfile "+profileName+" = new ClassProfile();\n" );

			builder.append(getPaths(profileName,fullPath, knownPath));

			builder.append(getDepth(profileName, depth));

			builder.append( parseModifiers(clazz,profileName) );

			builder.append(parseTypeParamCount(clazz,profileName));
			builder.append(parseSuperClass(clazz,profileName));
			builder.append(parseInterfaces(clazz,profileName));

			builder.append(parseNestedClasses(clazz,profileName));

			builder.append( parseEnumValues(clazz, profileName) );

			if (includePublic)
				builder.append(  parseFields(clazz, true, profileName) );
			builder.append( parseFields(clazz, false, profileName) );

			if (includePublic)
				builder.append(  parseMethods(clazz, true, profileName) );
			builder.append( parseMethods(clazz, false, profileName ) );

			builder.append( parseConstructors(clazz, profileName) );

			builder.append("\nreturn "+profileName+";");

			builder.append("\n}");

			return builder.toString();
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Parsing failed.");
			ex.printStackTrace();
			return "Parsing Failed";
		}
	}
}

