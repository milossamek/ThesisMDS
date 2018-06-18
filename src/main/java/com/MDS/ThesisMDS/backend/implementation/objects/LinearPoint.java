package com.MDS.ThesisMDS.backend.implementation.objects;

import java.awt.*;
import java.util.ArrayList;

public class LinearPoint {
    private Point dataPoint;
    private Point drawPoint;

    public LinearPoint(Point dataPoint, Point drawPoint) {
        this.dataPoint = dataPoint;
        this.drawPoint = drawPoint;
    }

    public static ArrayList<Point> toList(ArrayList<LinearPoint> inList) {
        ArrayList<Point> points = new ArrayList<>();

        for (LinearPoint linearPoint : inList) {
            points.add(linearPoint.getDrawPoint());
        }

        return points;
    }

    public Point getDataPoint() {
        return dataPoint;
    }

    public void setDataPoint(Point dataPoint) {
        this.dataPoint = dataPoint;
    }

    public Point getDrawPoint() {
        return drawPoint;
    }

    public void setDrawPoint(Point drawPoint) {
        this.drawPoint = drawPoint;
    }
}
