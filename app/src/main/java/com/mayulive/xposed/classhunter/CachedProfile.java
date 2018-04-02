package com.mayulive.xposed.classhunter;


/**
 * An entry used by ProfileCache
 */
public class CachedProfile
{
	private String mTargetPath;		//Path when the profile was created
	private String mClassPath;		//Path it ended up being
	private int mHookVersion;
	private int mModuleVersion;
	private float mSimilarity = 0;
	private boolean mUsed = false;	//Set to true if cached profile is used, so we can ditch old entries.

	public CachedProfile(){};

	public CachedProfile(String targetPath, String classPath, int hookVersion, int moduleVersion, float similarity)
	{
		this.mTargetPath = targetPath;
		this.mClassPath = classPath;
		this.mHookVersion = hookVersion;
		this.mModuleVersion = moduleVersion;
		this.mSimilarity = similarity;
	}

	public Class load(ClassLoader loader)
	{
		return ClassHunter.loadClass(mClassPath, loader);
	}

	//////////////
	//Getty setty
	//////////////

	public int getModuleVersion()
	{
		return mModuleVersion;
	}

	public void setModuleVersion(int moduleVersion)
	{
		this.mModuleVersion = moduleVersion;
	}

	public int getHookVersion()
	{
		return mHookVersion;
	}

	public void setHookVersion(int hookVersion)
	{
		this.mHookVersion = hookVersion;
	}

	public String getClassPath()
	{
		return mClassPath;
	}

	public void setClassPath(String mClassPath)
	{
		this.mClassPath = mClassPath;
	}

	public String getTargetPath()
	{
		return mTargetPath;
	}

	public void setTargetPath(String mTargetPath)
	{
		this.mTargetPath = mTargetPath;
	}

	public float getSimilarity()
	{
		return mSimilarity;
	}

	public void setSimilarity(float similarity)
	{
		this.mSimilarity = similarity;
	}

	public void setUsed(boolean used)
	{
		mUsed = used;
	}

	public boolean getUsed()
	{
		return mUsed;
	}
}
