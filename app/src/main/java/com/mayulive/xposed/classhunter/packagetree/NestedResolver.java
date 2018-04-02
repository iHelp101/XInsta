package com.mayulive.xposed.classhunter.packagetree;

import java.util.ArrayList;


/**
 * This class is responsible for resolving individual packages and classes from paths that
 * contain a dollar sign. Ideally we would be able to just delimit them by periods and dollar signs,
 * but someone decided it would be a good idea to make dollar signs a valid character for package and class names.
 * This greatly complicate matters.
 *
 */
public class NestedResolver
{

	/** Classes separated by the number of $-delimited segments they contain */
	private ArrayList<ArrayList<String>> mNestedPaths = new ArrayList<>();

	public NestedResolver()
	{
	}

	/**
	 * Add a $-containing path to be resolved later
	 * @param path The path to resolve later
	 */
	protected void addToNestedClasses(String path)
	{
		//Get number of $ branches
		int branches = path.length() - path.replace("$","").length();

		//Add to array with corresponding count
		ArrayList<String> pathsWithBranches = getPathsWithBranches(branches);
		pathsWithBranches.add(path);
	}

	/**
	 * Get the ArrayList for storing paths with the input number of branches
	 * @param branches the number of branches to look for
	 * @return	The ArrayList of paths corresponding to the branch count
	 */
	private ArrayList<String> getPathsWithBranches(int branches)
	{
		while (mNestedPaths.size() < branches+1)
		{
			mNestedPaths.add(new ArrayList<String>());
		}

		return mNestedPaths.get(branches);
	}


	/**
	 * Given a path and a PackageTree containing any parent packages or classes,
	 * resolve the name and package of the input class, and add it to the tree.
	 * The parent package or class must already exist in the tree.
	 * @param path	The path to resolve
	 * @param tree	A PackageTree containing the parent class or package.
	 */
	protected void resolveLastBranch(String path, PackageTree tree)
	{
		ArrayList<Integer> splits = new ArrayList<>();
		int periodPosition = -1;
		for (int i = path.length()-1; i >= 0; i--)
		{
			if (path.charAt(i) == '.')
			{
				//Hit package separator.
				//If nothing found assume before is package, after is class.
				periodPosition = i;
				break;
			}

			if (path.charAt(i) == '$')
			{
				splits.add(i);
			}
		}

		String clazzName = null;
		String packageName = null;

		for (int i = 0; i < splits.size(); i++)
		{
			Integer splitLocation = splits.get(i);
			String lookupString = path.substring(0,splitLocation);

			if (tree.mPackageMap.containsKey(lookupString))
			{
				clazzName = path.substring(splitLocation+1);
				packageName = lookupString;
				break;
			}
		}

		if (packageName == null && periodPosition == -1)
		{
			tree.addRootClass(path);
		}
		else
		{
			if (packageName == null)
			{
				packageName = path.substring(0,periodPosition);
				clazzName = path.substring(periodPosition+1);
			}

			tree.addClass(packageName, clazzName, path);
		}
	}

	/**
	 * Resolves the class and package names of the classed added to nested paths.,
	 * and adds them to the input <code>PackageTree</code>.
	 * Parent classes and packages must exist in the tree for children to be resolved,
	 * so we resolve them in order of $-delimieted segment count.
	 * @param tree	The <code>PackageTree</code> to resolve with and to.
	 */
	protected void resolve(PackageTree tree)
	{
		//Start with the paths that have fewer $ breaks
		for (ArrayList<String> paths : mNestedPaths)
		{
			for (String path : paths)
			{
				resolveLastBranch(path,tree);
			}
		}

		mNestedPaths.clear();
	}

}
