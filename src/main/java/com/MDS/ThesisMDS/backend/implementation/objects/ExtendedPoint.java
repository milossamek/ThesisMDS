package com.MDS.ThesisMDS.backend.implementation.objects;

import java.awt.*;

public class ExtendedPoint extends Point {

    public ExtendedPoint() {
    }

    public ExtendedPoint(Point p) {
        super(p);
    }

    public ExtendedPoint(int x, int y) {
        super(x, y);
    }


    @Override
    public String toString() {
        return x + ";" + y;
    }
}
