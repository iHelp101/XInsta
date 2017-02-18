package com.ihelp101.instagram;

public interface Listen {
    public abstract void init();

    public abstract boolean shouldUserUpdate(String s, int i, String s1);

    public abstract boolean canAutoUpdate(String s, int i);
}
