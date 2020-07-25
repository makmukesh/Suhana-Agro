package com.vpipl.suhanaagro.Utils;

import android.support.v4.util.LruCache;

/**
 * Created by admin on 08-06-2017.
 */

@SuppressWarnings("ALL")
public class Cache {
    private static Cache instance;
    int cacheSize = 100 * 1024 * 1024;
    private LruCache<Object, Object> lru;

    private Cache() {

        lru = new LruCache<>(cacheSize);

    }

    public static Cache getInstance() {

        if (instance == null) {

            instance = new Cache();
        }

        return instance;

    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }
}