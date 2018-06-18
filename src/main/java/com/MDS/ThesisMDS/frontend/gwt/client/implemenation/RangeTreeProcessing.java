package com.MDS.ThesisMDS.frontend.gwt.client.implemenation;


import com.MDS.ThesisMDS.frontend.gwt.client.objects.*;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.ProcessingHelper;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.TreePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RangeTreeProcessing extends TreeProcessing {
    private final int NODE_HEIGHT = 20;
    private final int NODE_WIDTH = 30;


    private Color textColor = new Color(0, 0, 0);
    private Color nodeColor = new Color(117, 244, 121);
    private Color nodeLineColor = new Color(2, 104, 5);
    private Color nodeEventColor = new Color(213, 151, 131);
    private Color nodeSecondColor = new Color(255, 250, 205);


    public RangeTreeProcessing(int width, int height, List<DrawNode> nodeList, List<DrawNode> eventList, EventType eventType) {
        super(width, height, nodeList, eventList, eventType);
    }

    @Override
    public void setup() {
        ProcessingHelper.prepareProcessing(pro, getWidth(), getHeight(), getFramerate(), ProcessingHelper.CL_WHITE);

        if (nodeList != null && nodeList.size() > 0) {
            if (eventList != null && eventList.size() > 0) {
                loggerGlobal.log(Level.SEVERE, "Preparing nodes");
                TreePosition.ProcessRangeTree(width, height, NODE_WIDTH, NODE_HEIGHT, nodeList, EventType.etBulkLoading);
                TreePosition.ProcessRangeEvent(nodeList, eventList, eventType, width, NODE_WIDTH, NODE_HEIGHT);
                loggerGlobal.log(Level.SEVERE, eventList.toString());
            } else {
                TreePosition.ProcessRangeTree(width, height, NODE_WIDTH, NODE_HEIGHT, nodeList, eventType);
            }
        } else if (eventList != null && (eventList.size() > 0)) {
            TreePosition.ProcessRangeTree(width, height, NODE_WIDTH, NODE_HEIGHT, eventList, EventType.etBulkLoadingAnimated);
        } else {
            drawEmptyMessage();
        }

        pro.loop();
        animationRunning = true;
    }

    @Override
    public void draw() {
        pro.background(255, 255, 255);

        drawLegend();
        drawTree();
        drawEvent();
    }

    private boolean drawEmptyMessage() {
        if ((nodeList == null || nodeList.size() == 0) && (eventList == null || eventList.size() == 0)) {
            ProcessingHelper.printEmptyMessage(pro, width, height, nodeColor);
            return true;
        }
        return false;
    }

    private boolean drawLegend() {
        Integer index = 0;
        ArrayList<String> legendList = new ArrayList<>();

        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).getData() != "null") {
                if (!legendList.contains(nodeList.get(i).getData())) {
                    pro.text(getNodeText(nodeList.get(i)) + " - " + nodeList.get(i).getData(), 10, 30 + 10 * index);
                    index++;
                    legendList.add(nodeList.get(i).getData());
                }
            }
        }
        return true;
    }

    private void drawEvent() {
        Boolean nodeDeleted = false;
        for (int i = 0; i < eventList.size(); i++) {
            DrawNode treeNode = eventList.get(i);

            if (eventType == EventType.etDelete) {
                if (hideSameNodes(i, treeNode)) break;
            }

            //chaning old node positions after tree locaton changes
            if (nodeDeleted && (treeNode.getNodeType() != NodeType.ntRemove && treeNode.getNodeType() != NodeType.ntParent) && !treeNode.isChanged()) {
                if (treeNode.getParent() != null) {
                    DrawNode parentNode = DrawNode.findByUIDToIndex(eventList, treeNode.getParent(), i);
                    if (parentNode != null) {
                        treeNode.setParent(parentNode);
                        treeNode.setFinalDrawLocation(new Point(TreePosition.getNodeChildernPosition(parentNode, treeNode.getNodeType(), width, treeNode.getSortType() == NodeSortType.stY ? NODE_HEIGHT * 2 : NODE_HEIGHT * 6, treeNode.getLevel(), false)));
                        treeNode.setRotating(true);
                        treeNode.setChanged(true);
                    }
                }
            }

            if (treeNode.getNodeType() == NodeType.ntRemove) {
                continue;
            }

            if (treeNode.isDrawIt()) {
                if (treeNode.getSortType() == NodeSortType.stY) {
                    drawNode(treeNode, nodeEventColor, nodeLineColor, NODE_WIDTH, NODE_HEIGHT, (NODE_WIDTH / 2) / 2);
                } else {
                    drawNode(treeNode, nodeEventColor, nodeLineColor, NODE_WIDTH + NODE_WIDTH / 2, NODE_HEIGHT, 0);
                }
            }

            if (treeNode.increment()) {
                break;
            }

            if (eventType != EventType.etDelete) {
                if (hideSameNodes(i, treeNode)) break;
            }

            if (treeNode.getNodeType() == NodeType.ntFinish) {
                treeNode.setDrawIt(false);
                nodeDeleted = treeNode.getSortType() == NodeSortType.stX && eventType == EventType.etDelete;
            }


            if (i == (eventList.size() - 1)) {
                pro.noLoop();
                animationRunning = false;
            }
        }
    }

    private boolean hideSameNodes(int i, DrawNode treeNode) {
        DrawNode nodeFromDrawList = DrawNode.findByUID(nodeList, treeNode);
        if (nodeFromDrawList != null) {
            if (nodeFromDrawList.isDrawIt()) {
                nodeFromDrawList.setDrawIt(false);
                return true;
            }
        }

        DrawNode nodeSameUID = DrawNode.findByUIDToIndex(eventList, treeNode, i);
        if (nodeSameUID != null) {
            nodeSameUID.setDrawIt(false);
        }
        return false;
    }


    private Boolean drawTree() {
        for (int i = 0; i < nodeList.size(); i++) {
            DrawNode treeNode = nodeList.get(i);
            if (treeNode.isDrawIt()) {
                if (treeNode.getSortType() == NodeSortType.stY) {
                    drawNode(treeNode, nodeSecondColor, nodeLineColor, NODE_WIDTH, NODE_HEIGHT, (NODE_WIDTH / 2) / 2);
                } else {
                    drawNode(treeNode, nodeColor, nodeLineColor, NODE_WIDTH + NODE_WIDTH / 2, NODE_HEIGHT, 0);
                }
            }

            if (treeNode.increment()) {
                return false;
            }

            if (treeNode.increment() && (i == (nodeList.size() - 1)) && eventList.size() == 0) {
                pro.noLoop();
                animationRunning = false;
            }
        }
        return true;
    }

    public void drawNode(DrawNode node, Color nodeColor, Color lineColor, Integer nodeWidth, Integer nodeHeight, Integer offset) {
        if (node.getParent() != null) {
            if (node.getParent().getActualDrawLocation() != null) {
                int startPositionx = node.getParent().getActualDrawLocation().getX();
                int startPositiony = node.getParent().getActualDrawLocation().getY() + nodeHeight;
                fill(nodeLineColor);

                switch (node.getNodeType()) {
                    case ntRoot:
                        pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, startPositionx + (nodeWidth / 2) + offset, node.getActualDrawLocation().getY());
                        break;
                    case ntLeftSon:
                        pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, node.getActualDrawLocation().getX() + nodeWidth + offset, node.getActualDrawLocation().getY());
                        break;
                    case ntRightSon:
                        pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, node.getActualDrawLocation().getX() + offset, node.getActualDrawLocation().getY());
                        break;
                    case ntFinish:
                        break;
                    case ntParent:
                        DrawNode realNode = DrawNode.findByUID(nodeList, node);
                        if (realNode != null) {
                            switch (realNode.getNodeType()) {
                                case ntRoot:
                                    pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, startPositionx + (nodeWidth / 2) + offset, node.getActualDrawLocation().getY());
                                    break;
                                case ntLeftSon:
                                    pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, node.getActualDrawLocation().getX() + nodeWidth + offset, node.getActualDrawLocation().getY());
                                    break;
                                case ntRightSon:
                                    pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, node.getActualDrawLocation().getX() + offset, node.getActualDrawLocation().getY());
                            }
                        }
                        break;
                }
            }

        }

        fill(nodeColor);
        pro.rect(node.getActualDrawLocation().getX() + offset, node.getActualDrawLocation().getY(), nodeWidth, nodeHeight);
        fill(nodeColor);
        setNodeText(node, nodeWidth, nodeHeight, offset);

    }

    public void setNodeText(DrawNode node, Integer nodeWidth, Integer nodeHeight, Integer offset) {
        fill(textColor);
        pro.text(getNodeText(node), node.getActualDrawLocation().getX() + nodeWidth / 20 + offset, node.getActualDrawLocation().getY() + (nodeHeight / 2));
    }

    private String getNodeText(DrawNode node) {
        if (node.getData().equals("null")) {
            return node.getDataLocation().toString();
        }

        switch (node.getSortType()) {
            case stX:
                return node.getData();
            case stY:
                return (node.getData().length() > 1) ? node.getData().substring(0, 2) : String.valueOf(node.getData().charAt(0));
        }

        return node.getData();
    }


}
