package com.simonepugliese.vecchio.core.db;

public interface ConnectionToDB {
    void open(String db);
    void close();
}


