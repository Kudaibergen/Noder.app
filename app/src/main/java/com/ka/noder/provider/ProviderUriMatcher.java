package com.ka.noder.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

class ProviderUriMatcher {
    private UriMatcher mUriMatcher;
    private SparseArray<UriEnum> mEnumsMap;

    ProviderUriMatcher(){
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mEnumsMap = new SparseArray<>();
        buildUriMatcher();
    }

    private void buildUriMatcher(){
        String authority = Contract.CONTENT_AUTHORITY;
        UriEnum[] uris = UriEnum.values();
        for (UriEnum uri : uris) {
            mUriMatcher.addURI(authority, uri.getPath(), uri.getCode());
        }
        buildEnumsMap();
    }

    private void buildEnumsMap(){
        UriEnum[] uris = UriEnum.values();
        for (UriEnum uri : uris) {
            mEnumsMap.put(uri.getCode(), uri);
        }
    }

    UriEnum matchUri(Uri uri){
        int code = mUriMatcher.match(uri);
        return mEnumsMap.get(code);
    }
}