package com.MDS.ThesisMDS.frontend.gwt.client.utils;

import com.MDS.ThesisMDS.frontend.gwt.client.objects.Color;
import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingJavascriptObject;

public class ProcessingHelper {
    public static final Color CL_WHITE = new Color(255, 255, 255);


    public static boolean setAnimation(ProcessingJavascriptObject processing, boolean animationRunning) {
        if (animationRunning) {
            processing.noLoop();
            return false;
        } else {
            processing.loop();
            return true;
        }
    }

    public static void printEmptyMessage(ProcessingJavascriptObject processing, Integer width, Integer height, Color color) {
        processing.text("Tree is empty", Math.round(width / 2), Math.round(height / 2));
        processing.fill(color);
    }

    public static void setColor(ProcessingJavascriptObject processing, Color color) {
        processing.color(color.getR(), color.getG(), color.getB());
    }

    public static void fill(ProcessingJavascriptObject processing, Color color) {
        processing.fill(color.getR(), color.getG(), color.getB());
    }

    public static void stroke(ProcessingJavascriptObject processing, Color color) {
        processing.stroke(color.getR(), color.getG(), color.getB());
    }

    public static int setFramerate(ProcessingJavascriptObject processing, int key, int framerate) {
        if (processing.getKey() == '+') {
            framerate++;
        } else if (processing.getKey() == '-') {
            framerate--;
        }
        processing.frameRate(framerate);

        return framerate;
    }

    public static void prepareProcessing(ProcessingJavascriptObject processing, int width, int height, int framerate, Color color) {
        processing.size(width, height);
        processing.frameRate(framerate);
        processing.background(color.getR(), color.getG(), color.getB());
    }
}
