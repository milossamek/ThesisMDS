package com.MDS.ThesisMDS.frontend.gwt.client.objects;


public class Point {
    Integer x;
    Integer y;

    public Point(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }
}
