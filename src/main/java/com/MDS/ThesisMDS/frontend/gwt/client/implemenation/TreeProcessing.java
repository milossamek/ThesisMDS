package com.MDS.ThesisMDS.frontend.gwt.client.implemenation;

import com.MDS.ThesisMDS.frontend.gwt.client.objects.Color;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.DrawNode;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.EventType;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.ProcessingHelper;
import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingCodeBase;

import java.util.List;
import java.util.logging.Logger;

public class TreeProcessing extends ProcessingCodeBase {
    protected int width = 1024;
    protected int height = 600;


    protected List<DrawNode> nodeList;
    protected List<DrawNode> eventList;
    protected EventType eventType;


    protected boolean animationRunning;
    protected int framerate = 25;

    protected Logger loggerGlobal;

    public TreeProcessing(int width, int height, List<DrawNode> nodeList, List<DrawNode> eventList, EventType eventType) {
        this.width = width;
        this.height = height;
        this.nodeList = nodeList;
        this.eventList = eventList;
        this.eventType = eventType;
        this.loggerGlobal = Logger.getLogger("Logger");
    }

    @Override
    public void mouseClicked() {
        animationRunning = ProcessingHelper.setAnimation(pro, animationRunning);
        super.mouseClicked();
    }

    @Override
    public void keyPressed() {
        setFramerate(ProcessingHelper.setFramerate(pro, pro.getKey(), getFramerate()));
        super.keyPressed();
    }

    public void setColor(Color color) {
        pro.color(color.getR(), color.getG(), color.getB());
    }

    public void fill(Color color) {
        pro.fill(color.getR(), color.getG(), color.getB());
    }

    public void stroke(Color color) {
        pro.stroke(color.getR(), color.getG(), color.getB());
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<DrawNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<DrawNode> nodeList) {
        this.nodeList = nodeList;
    }

    public List<DrawNode> getEventList() {
        return eventList;
    }

    public void setEventList(List<DrawNode> eventList) {
        this.eventList = eventList;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public boolean isAnimationRunning() {
        return animationRunning;
    }

    public void setAnimationRunning(boolean animationRunning) {
        this.animationRunning = animationRunning;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }
}
