package com.MDS.ThesisMDS.frontend.components;


import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vaadin.tltv.vprocjs.component.ProcessingCodeExtension;

import java.awt.*;
import java.util.List;

public class DrawProcessingExtensionComponent extends ProcessingCodeExtension {
    private static final String delimiter = "#!#";
    private Integer _width;
    private Integer _height;
    private List<TreeNode> _tree;
    private List<TreeNode> _event;
    private EventType _eventType;
    private List<Point> _pointList;
    private String lastProcessingJavaClass;

    public DrawProcessingExtensionComponent() {
        super();
        lastProcessingJavaClass="";
    }

    public static String getDelimiter() {
        return delimiter;
    }

    @Override
    public void setProcessingJavaCodeClass(String processingJavaCodeClass) {
        lastProcessingJavaClass=processingJavaCodeClass;
        String processingJavaCodeClass1 = hashClass(processingJavaCodeClass);
        super.setProcessingJavaCodeClass(processingJavaCodeClass1);
    }

    public String getLastProcessingJavaClass() {
        return lastProcessingJavaClass;
    }

    private String createJson(List<TreeNode> list) {
        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
            if (list == null || list.size() == 0) return "null";
            s = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    private String createJsonPoints(List<Point> nodeList) {
        ObjectMapper mapper = new ObjectMapper();
        String s = null;
        try {
            if (nodeList == null || nodeList.size() == 0) return "null";
            s = mapper.writeValueAsString(nodeList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }


    public Integer get_width() {
        return _width;
    }

    public void set_width(Integer _width) {
        this._width = _width;
    }

    public Integer get_height() {
        return _height;
    }

    public void set_height(Integer _height) {
        this._height = _height;
    }

    public List<TreeNode> get_tree() {
        return _tree;
    }

    public void set_tree(List<TreeNode> _tree) {
        this._tree = _tree;
    }

    public List<TreeNode> get_event() {
        return _event;
    }

    public void set_event(List<TreeNode> _event) {
        this._event = _event;
    }

    public EventType get_eventType() {
        return _eventType;
    }

    public void set_eventType(EventType _eventType) {
        this._eventType = _eventType;
    }

    public List<Point> get_pointList() {
        return _pointList;
    }

    public void set_pointList(List<Point> _pointList) {
        this._pointList = _pointList;
    }

    private String hashClass(String processingClass) {
        return processingClass + delimiter + _width + delimiter + _height + delimiter + _eventType + delimiter + createJson(_tree) + delimiter + createJson(_event) + delimiter + createJsonPoints(_pointList);
    }
}
