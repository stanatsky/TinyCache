package com.example.tinycache.utils;

import org.openjdk.jol.info.GraphLayout;

public class Weigher {
    public int weigh(Object value) {
        return (int) GraphLayout.parseInstance(value).totalSize();
    }
}
