package com.mayulive.xposed.classhunter.packagetree;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mayulive.xposed.classhunter.ClassHunter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;


/**
 * A tree of classes and packages loaded from a dexfile.
 * For convenience' sake it also wraps a classloader, so you can pass them together.
 * A package in this context is anything that has nested classes, so all classes are
 * treated as such. This includes nested anonymous classes.
 */
public class PackageTree extends PackageEntry
{
	private static final String TAG = ClassHunter.getLogTag( PackageTree.class );

	protected Map<String, PackageEntry> mPackageMap = new HashMap<>();	//Map package/classes to entry containing inner/sub classes
	protected ClassLoader mClassLoader;
	protected NestedResolver mResolver = null;							//Used to resolve paths containing $.
																		//Problematic with nested anonymous classes,
																		//Even worse if there are $ characters in class and package names.

	/**
	 * Constructor a PackageTree from a dex file.
	 * Additional dex files may be added afterwards.
	 * @param dexFile		The dex file to load, usually apk of the app we are running in.
	 * @param classLoader	The corresponding ClassLoader for convenience.
	 */
	public PackageTree(String dexFile, @Nullable ClassLoader classLoader)
	{
		super("");
		mClassLoader = classLoader;
		mPackageMap.put("",this);

		addDexFile(dexFile);
	}



	/**
	 * Multi-dex is a thing. Does that literally mean multiple dex files? apks?
	 * I have no idea, but if so you can add more here.
	 * @param path	The path to a dex file (usually an apk)
	 */
	public void addDexFile(String path)
	{
		mResolver = new NestedResolver();

		try
		{
			DexFile df = new DexFile(path);

			for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); )
			{
				addClass(iter.nextElement());
			}

		}
		catch (Exception ex)
		{
			Log.e(TAG, "Failed to load or parse dex file. This is fatal.");
			ex.printStackTrace();
		}

		mResolver.resolve(this);
		mResolver = null;
	}


	/**
	 *
	 * @return The ClassLoader passed when constructed
	 */
	public ClassLoader getClassLoader()
	{
		return mClassLoader;
	}


	/**
	 * Get the HashMap of Path:PackageEntry pairs
	 * @return	The internal HashMap of Path:PackageEntry pairs
	 */
	public Map<String, PackageEntry> getMap()
	{
		return mPackageMap;
	}


	/**
	 * Get the PackageEntry associated with the input path
	 * Classes are also given package entries, if the path
	 * refers to a class, the returned entry will list any
	 * nested classes, and <code>parent</code> will be its
	 * package or outer class.
	 * @param path	The path to get the <code>PackageEntry</code> for
	 * @return	The requested <Code>PackageEntry</Code>, or null.
	 */
	public @Nullable PackageEntry getPackage(String path)
	{
		return mPackageMap.get(path);
	}


	/**
	 * Add a single class. This method assumes that <code>pack</code> already exists in <code>mPackageMap</code>
	 * This should only be used by the nested resolver.
 	 * @param pack					The package or parent class the class resides in
	 * @param className				The name of the class
	 * @param loadableClassPath		The full path (name) of the class as it appears in the dexfile.
	 */
	protected void addClass(String pack, String className, String loadableClassPath)
	{
		PackageEntry entry = getPackage(pack,null);
		entry.classes.add(loadableClassPath);

		getPackage(loadableClassPath, entry);
	}

	/**
	 * Add a class that does not belong to any packages.
	 * I do not think this is even possible.
	 * @param className	The name of the class as it appears in the dexfile.
	 */
	protected void addRootClass(String className)
	{
		this.classes.add(className);
		getPackage(className, this);
	}

	/**
	 * Add a single class.
	 * If the path includes $ anywhere it will be passed to the nested resolver for further ... resolving.
	 * @param clazzPath	The full path of the class to add
	 */
	protected void addClass(String clazzPath)
	{
		if (clazzPath.contains("$"))
		{
			mResolver.addToNestedClasses(clazzPath);
		}
		else
		{
			// For simplicity, reconsider if slow
			// Update: not slow.
			// update2: Now handled by nested resolver
			//clazzPath = clazzPath.replace("$",".");

			String packagePath = stripLast(clazzPath);
			PackageEntry entry = getPackage(packagePath,null);

			entry.classes.add(clazzPath);

			//Also add a package entry for the class itself,
			//even if it does not have any nested classes.
			getPackage(clazzPath,null);

		}
	}

	/**
	 * Get the PackageEntry for a package.
	 * If it does not exist it will be created.
	 * If a new package is created its parent will be parsed from the path,
	 * or the parent provided to this method will be used.
	 * @param packagePath	The path of the package to be created
	 * @param parent		The parent to use if the package does not already exist.
	 * @return
	 */
	protected PackageEntry getPackage(String packagePath, @Nullable PackageEntry parent)
	{
		PackageEntry entry = mPackageMap.get(packagePath);
		if (entry == null)
		{
			entry = new PackageEntry(packagePath);
			addPackage(entry, parent);
		}

		return entry;
	}


	/**
	 * Add a new package to the map. If no parent is provided it will be parsed from the path of the new entry.
	 * @param newEntry	The package to add to the map
	 * @param parent	The parent of the new package. If null will be parsed from package path.
	 */
	protected void addPackage(PackageEntry newEntry, @Nullable PackageEntry parent)
	{
		mPackageMap.put(newEntry.packagePath, newEntry);

		//Attempt to get parent. This will recursively trigger the
		//creation of packages that do not yet exist in the map
		//Note that we treat $ as .
		if (parent == null)
		{
			newEntry.parent = getPackage(stripLast(newEntry.packagePath), parent);
		}
		else
		{
			newEntry.parent = parent;
		}

		newEntry.parent.packages.add(newEntry);


	}

	/**
	 * Called recursively to traverse the PackageTree.
	 * If <code>currentDepth</code> is <code>>=minDepth && <=maxDepth</code>, all class contained in
	 * <code>currentRoot</code> will be added to <code>packages</code>. if <code> < maxDepth </code>
	 * this method will be called with any nested packages.
	 * @param packages		The packages at the depth we are looking for
	 * @param currentRoot	The <code>PackageEntry</code> to return classes from, or to traverse deeper.
	 * @param currentDepth	The depth at this iteration
	 * @param minDepth		The min depth to return classes from
	 * @param maxDepth		The max depth to return classes from
	 */
	private void getClassesIfDepthMatch(ArrayList<PackageEntry> packages, PackageEntry currentRoot, int currentDepth, int minDepth, int maxDepth)
	{
		if (currentDepth <= maxDepth && currentDepth >= minDepth)
		{
			packages.add(currentRoot);
		}

		if (currentDepth < maxDepth)
		{
			currentDepth++;
			for (PackageEntry nestedPackage : currentRoot.packages)
			{
				getClassesIfDepthMatch(packages,nestedPackage, currentDepth, minDepth, maxDepth);
			}
		}
	}

	/**
	 * Get all classes at a given depth from <code>startPath</code>.
	 * @param startPath	The path (Package or class with nested classes) to start searching from.
	 * @param minDepth	The minimum depth to return classes from (0 for classes inside <code>startPath</code>)
	 * @param maxDepth	The max depth to return classes from (0 for classes inside <code>startPath</code>)
	 * @return			A list of all classes at the specified depth
	 */
	public List<Class> getAllAtDepth(String startPath, int minDepth, int maxDepth)
	{
		//mine depth must be >= 0
		if (minDepth > 0)
			minDepth = 0;
		if (maxDepth < minDepth)
			maxDepth = minDepth;

		return getAllAtDepth( getPackage(startPath,null), minDepth, maxDepth);
	}


	/**
	 * Get all classes at a given depth from <code>rootEntry</code>.
	 * @param rootEntry	The <code>PackageEntry</code> (Package or class with nested classes) to start searching from.
	 * @param minDepth	The minimum depth to return classes from (0 for classes inside <code>rootEntry</code>)
	 * @param maxDepth	The max depth to return classes from (0 for classes inside <code>rootEntry</code>)
	 * @return			A list of all classes at the specified depth
	 */
	public List<Class> getAllAtDepth(PackageEntry rootEntry, int minDepth, int maxDepth)
	{
		//mine depth must be >= 0
		if (minDepth > 0)
			minDepth = 0;
		if (maxDepth < minDepth)
			maxDepth = minDepth;

		ArrayList<Class> classList = new ArrayList<>();

		ArrayList<PackageEntry> targetPackages = new ArrayList<>();
		getClassesIfDepthMatch(targetPackages, rootEntry, 0, minDepth,maxDepth);

		for (PackageEntry entry : targetPackages)
		{

			for (String classPath : entry.classes)
			{

				Class clazz = ClassHunter.loadClass(classPath, mClassLoader);
				if (clazz != null)
				{
					classList.add(clazz);
				}
				else
				{
					Log.e(TAG, "Failed to load class: "+classPath);
				}
			}
		}

		return classList;
	}

	/**
	 * Trim the last path segment delimieted by a period
	 */
	private String stripLast(String path)
	{
		int lastIndex = path.lastIndexOf('.');
		if (lastIndex == -1)
			return "";
		else
		{
			return path.substring(0,lastIndex);
		}


	}


	/**
	 * Get the number of path segments from root this package is.
	 * Very similar to depth, but 0 would be root, 0 would be in root etc
	 * @param entry	The <code>PackageEntry</code> to find the hop count for
	 * @return	The number of hops from root
	 */
	public int getPackageSegmentsFromRoot(PackageEntry entry)
	{
		int counter = 0;

		while(entry != this)
		{
			counter++;
			entry = entry.parent;
		}

		return counter;
	}

	/**
	 * Get the depth of a class relative to its containing package
	 * If the class is contained within the package, this number is 0.
	 * @param pack		The package to count depth from
	 * @param fullpath	The full path of the class we are getting the depth for
	 * @return			The depth of the class relative to the package
	 */
	public int getDepth(String pack, String fullpath)
	{
		PackageEntry knownEntry = mPackageMap.get(pack);
		PackageEntry targetPackage = mPackageMap.get(fullpath);

		if (knownEntry == null || targetPackage == null)
		{
			return -1;
		}

		int knownDepth = getPackageSegmentsFromRoot(knownEntry);
		int targetDepth = getPackageSegmentsFromRoot(targetPackage);

		return targetDepth - knownDepth - 1;
	}

	/**
	 * Get the number of branches in a path, assuming that all periods and
	 * dollars signs separate packages and classes.
	 * @param path
	 * @return
	 */
	public static int getSimpleSegmentCount(String path)
	{
		if (path == null || path.isEmpty())
			return 0;

		String[] split = path.split("\\.|\\$");
		return split.length;
	}

	/**
	 * Get the first <code>segments</code> segments of the path,
	 * delimieted by . or $
	 * @param path		The path
	 * @param segments	The number of segments to return
	 * @return	The path limited to <code>segments</code> segments
	 */
	public static String getSegmentedPath(String path, int segments)
	{

		if (path == null || path.isEmpty())
			return "";

		String[] split = path.split("\\.|\\$");

		if (segments >= split.length)
			return path;

		int charCounter = 0;
		for (int i = 0; i < segments; i++)
		{
			charCounter += split[i].length()+1;
		}
		if (charCounter > 0)
			charCounter--;	//no delimiter on last

		return path.substring(0, charCounter);
	}

	/**
	 * Get the depth of <code>knownPath</code> relative to <code>fullPath</code>
	 * 0 means contained inside knownPath, -1 means paths are identical.
	 * Simple depth assumes packages and classes are delimited by . and $, and that these
	 * characters never appear in package or class names. If they do see {@link #getDepth(String, String)}
	 * @param fullpath	The full path of the class
	 * @param knownPath	The known path of the class
	 * @return
	 */
	public static int getSimpleDepth(String fullpath, String knownPath )
	{
		int fullCount = fullpath.length() - fullpath.replaceAll("\\.", "").replaceAll("\\$","").length();
		int knownCount = knownPath.length() - ( knownPath.replaceAll("\\.", "").replaceAll("\\$","").length() );

		//If full is:   	 com.nordskog.a.b.c
		//and known is:		 com.nordskog
		//Then the depth is: 		      0 1 2

		//If full is:  		 com.nordskog.a.b.c
		//and known is:
		//Then the depth is:  0     1	  2 3 4

		//If full is:  		 com.nordskog.a.b.c
		//and known is:		 com
		//Then the depth is:        0	  1 2 3

		//So basically...
		if (knownPath.isEmpty())
		{
			Log.i(TAG, "Return: "+fullCount);
			return fullCount;
		}
		else
		{
			Log.i(TAG, "Return: "+(fullCount - knownCount - 1));
			return fullCount - knownCount - 1;
		}

	}
}
