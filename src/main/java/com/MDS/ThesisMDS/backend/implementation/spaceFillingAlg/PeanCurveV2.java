package com.MDS.ThesisMDS.backend.implementation.spaceFillingAlg;

import com.MDS.ThesisMDS.backend.implementation.interfaces.ILinearOrdering;
import com.MDS.ThesisMDS.backend.implementation.objects.LinearPoint;

import java.awt.*;
import java.util.ArrayList;

public class PeanCurveV2 implements ILinearOrdering {
    private int dimension;

    public PeanCurveV2(int dimension) {
        this.dimension = dimension;
    }

    private Point toPixel(int x, int y, int width, int height) {
        return new Point(x * width, y * height);
    }

    private ArrayList<LinearPoint> processPeano(Integer width, Integer height, Integer bits, Integer margin) {
        if (bits < 0 || bits > 64) {
            return null;
        }

        int n = (int) Math.pow(3, bits);
        int cellSize = (width - 2 * margin) / (n);
        ArrayList<LinearPoint> returnArray = new ArrayList<>();

        for (int i = 0; i < n * n; i++) {
            Point point = indexToPoint(i, n);
            Point secondPoint = toPixel(point.x, point.y, cellSize, cellSize);
            secondPoint.x = secondPoint.x + cellSize / 2;
            secondPoint.y = secondPoint.y + cellSize / 2;
            returnArray.add(new LinearPoint(new Point((int) point.x, (int) point.y), new Point(secondPoint.x, secondPoint.y)));
        }

        return returnArray;
    }


    private Point indexToPoint(int t, int n) {
        if ((t < 0) || (t > n * n)) {
            return null;
        }
        Point outPoint = null;
        int x = 0, y = 0;
        for (int i = 1; i < n; i = i * 3) {
            int s = t % 9;

            //rx jsou souradnice v subregionu
            int rx = s / 3;
            int ry = s % 3;

            if (rx == 1) {
                ry = 2 - ry;
            }

            //otacim do doby nez jsou spravne souradnice
            if (i > 1) {
                outPoint = rotate(i, x, y, s);
                x = outPoint.x;
                y = outPoint.y;
            }

            x += rx * i;
            y += ry * i;

            t = t / 9;

        }

        return new Point(x, y);
    }

    private Point rotate(int n, int x, int y, int s) {
        if (n == 1) {
            // Special case
            return new Point(x, y);
        }

        n = n - 1;
        switch (s) {
            case 0:
                return new Point(x, y); // normal
            case 1:
                return new Point(n - x, y);// flip horizontal
            case 2:
                return new Point(x, y);// normal
            case 3:
                return new Point(x, n - y); // flip vertical
            case 4:
                return new Point(n - x, n - y);// flip vertical and fliph horizontal
            case 5:
                return new Point(x, n - y);// flip vertical
            case 6:
                return new Point(x, y);// normal
            case 7:
                return new Point(n - x, y);// flip horizontal
            case 8:
                return new Point(x, y);// normal
        }

        return null;
    }


    @Override
    public ArrayList<LinearPoint> calculatePoints(Integer width, Integer height, Integer bits, Integer margin) {
        return processPeano(width, height, bits, margin);
    }
}
