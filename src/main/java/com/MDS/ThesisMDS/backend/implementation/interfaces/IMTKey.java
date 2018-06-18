package com.MDS.ThesisMDS.backend.implementation.interfaces;

public interface IMTKey<K> extends Comparable<K> {
    K getX();

    void setX(K X);

    K getY();

    void setY(K Y);

    int compareX(K x);

    int compareY(K y);
}
