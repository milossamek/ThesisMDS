package com.MDS.ThesisMDS.frontend.gwt.client.implemenation;


import com.MDS.ThesisMDS.frontend.gwt.client.objects.Color;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.Point;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.ProcessingHelper;
import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingCodeBase;

import java.util.List;
import java.util.logging.Logger;

public class PointProcessing extends ProcessingCodeBase {
    private int framerate = 25;

    private Color textColor = new Color(0, 0, 0);
    private Color nodeColor = new Color(117, 244, 121);
    private Color nodeLineColor = new Color(2, 104, 5);
    private Color nodeEventColor = new Color(213, 151, 131);
    private Color nodeEventLineColor = new Color(213, 232, 27);

    private int width = 1024;
    private int height = 600;

    private boolean animationRunning;
    private List<Point> pointList;
    private Integer lastIndex;
    private Logger loggerGlobal;


    public PointProcessing(int width, int height, List<Point> pointList) {
        this.width = width;
        this.height = height;
        this.pointList = pointList;
        animationRunning = false;
        this.lastIndex = 0;
        this.loggerGlobal = Logger.getLogger("Point logger");
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    @Override
    public void mouseClicked() {
        animationRunning = ProcessingHelper.setAnimation(pro, animationRunning);
        super.mouseClicked();
    }

    @Override
    public void keyPressed() {
        framerate = ProcessingHelper.setFramerate(pro, pro.getKey(), framerate);
        super.keyPressed();
    }

    @Override
    public void setup() {
        this.lastIndex = 0;
        ProcessingHelper.prepareProcessing(pro, width, height, framerate, ProcessingHelper.CL_WHITE);

        if (pointList.size() > 0) {
            pro.loop();
            animationRunning = true;
        }
    }

    @Override
    public void draw() {
        pro.background(255, 255, 255);

        for (int i = 0; i < pointList.size(); i++) {
            if (i < pointList.size() - 1) {
                pro.line(pointList.get(i).getX(), pointList.get(i).getY(), pointList.get(i + 1).getX(), pointList.get(i + 1).getY());

                if (lastIndex == i) {
                    lastIndex = i + 1;
                    break;
                }

            }
        }

    }


    private void setColor(Color color) {
        ProcessingHelper.setColor(pro, color);
    }

    private void fill(Color color) {
        ProcessingHelper.fill(pro, color);
    }

    private void stroke(Color color) {
        ProcessingHelper.stroke(pro, color);
    }
}
