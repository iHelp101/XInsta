package com.mayulive.xposed.classhunter.packagetree;

import java.util.ArrayList;

/**
 * A very simple Package tree entry, listing the contained packages and classes.
 * In the context of this library, classes are also treated as packages and given a package entry.
 * If a <code>PackageEntry</code> is for a class, it will also be listed in the <code>classes</code> list of its parent.
 */
public class PackageEntry
{

	/** The path of this entry */
	public String packagePath;

	/** The parent of this entry */
	public PackageEntry parent;

	/** The packages contained in this package */
	public ArrayList<PackageEntry> packages = new ArrayList<PackageEntry>();

	/** The classes contained in this package */
	public ArrayList<String> classes = new ArrayList<>();

	PackageEntry(String packageName)
	{
		packagePath = packageName;
	}
}
