package com.MDS.ThesisMDS.backend.events;

import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class EventsBuffer {
    private ArrayList<TreeNode> drawList;
    private ArrayList<TreeNode> eventList;

    public EventsBuffer() {
        drawList = new ArrayList<>();
        eventList = new ArrayList<>();
    }

    public void flush() {
        drawList.clear();
        eventList.clear();
    }

    public void flushDraw() {
        drawList.clear();
    }

    public void flushEvent() {
        eventList.clear();
    }

    public void addDraw(TreeNode node) {
        drawList.add(node);
    }

    public void addEvent(TreeNode node) {
        eventList.add(node);
    }


    public List<TreeNode> getDrawList() {
        return drawList;
    }

    public List<TreeNode> getEventList() {
        return eventList;
    }
}
