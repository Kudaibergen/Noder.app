package com.ka.noder.provider;

enum UriEnum {
    NOTES(100, Contract.Notes.TABLE_NAME, Contract.Notes.CONTENT_TYPE),
    NOTES_ID(101, Contract.Notes.TABLE_NAME + "/*", Contract.Notes.CONTENT_ITEM_TYPE);

    private int code;
    private String path;
    private String contentType;

    UriEnum(int code, String path, String contentType){
        this.code = code;
        this.path = path;
        this.contentType = contentType;
    }

    int getCode() {
        return code;
    }

    String getPath() {
        return path;
    }

    String getContentType() {
        return contentType;
    }
}