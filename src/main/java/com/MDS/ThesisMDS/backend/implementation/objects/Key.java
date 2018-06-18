package com.MDS.ThesisMDS.backend.implementation.objects;

import com.MDS.ThesisMDS.backend.implementation.interfaces.IMTKey;

public class Key implements IMTKey<Integer> {
    private Integer x;
    private Integer y;

    public Key(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Integer getX() {
        return this.x;
    }

    @Override
    public void setX(Integer X) {
        this.x = X;
    }

    @Override
    public Integer getY() {
        return this.y;
    }

    @Override
    public void setY(Integer Y) {
        this.y = Y;
    }

    @Override
    public int compareX(Integer x) {
        return this.x.compareTo(x);
    }

    @Override
    public int compareY(Integer y) {
        return this.y.compareTo(y);
    }

    @Override
    public int compareTo(Integer o) {
        return 0;
    }

    @Override
    public String toString() {
        return "" + x + ";" + y;
    }
}
