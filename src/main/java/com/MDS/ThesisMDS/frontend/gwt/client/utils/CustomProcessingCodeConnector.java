package com.MDS.ThesisMDS.frontend.gwt.client.utils;


import com.MDS.ThesisMDS.frontend.components.DrawProcessingExtensionComponent;
import com.MDS.ThesisMDS.frontend.gwt.client.implemenation.*;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.DrawNode;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.EventType;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.Point;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingCode;
import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingCodeConnector;
import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingCodeState;

import java.util.List;

@Connect(DrawProcessingExtensionComponent.class)
public class CustomProcessingCodeConnector extends ProcessingCodeConnector {
    private PointProcessing pointProcessing;


    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
    }

    @Override
    public ProcessingCodeState getState() {
        return super.getState();
    }

    @Override
    public ProcessingCode getProcessingJavaCode(String codeClass) {
        String className = "";
        int width = 0;
        int height = 0;
        EventType eventType = EventType.etBulkLoadingAnimated;
        List<DrawNode> tree = null;
        List<DrawNode> event = null;
        List<Point> pointList = null;

        ObjectCreator objectCreator = new ObjectCreator();
        if (objectCreator.isHashed(codeClass)) {
            className = objectCreator.unhashClassName(codeClass);
            width = objectCreator.unhashWidth(codeClass);
            height = objectCreator.unhashHeight(codeClass);
            tree = objectCreator.unhashTree(codeClass);
            event = objectCreator.unhashEvent(codeClass);
            eventType = objectCreator.unhashEventType(codeClass);
            pointList = objectCreator.unhashPointList(codeClass);
        } else {
            className = codeClass;
        }

        if (PSTProcessing.class.getName().equals(className)) {
            PSTProcessing PSTProcessing = new PSTProcessing(width, height, tree, event, eventType);
            return PSTProcessing;
        } else if (RangeTreeProcessing.class.getName().equals(className)) {
            RangeTreeProcessing rangeTreeProcessing = new RangeTreeProcessing(width, height, tree, event, eventType);
            return rangeTreeProcessing;
        } else if (QuadTreeProcessing.class.getName().equals(className)) {
            QuadTreeProcessing quadTreePro = new QuadTreeProcessing(width, height, tree, event, eventType);
            return quadTreePro;
        } else if (PointProcessing.class.getName().equals(className)) {
            if (pointProcessing == null) {
                pointProcessing = new PointProcessing(width, height, pointList);
                return pointProcessing;
            } else {
                pointProcessing.setWidth(width);
                pointProcessing.setHeight(height);
                pointProcessing.setPointList(pointList);
                pointProcessing.setup();
                return pointProcessing;
            }
        } else if (EmptyProcessing.class.getName().equals(className)) {
            return new EmptyProcessing();
        }

        return null;
    }


}

