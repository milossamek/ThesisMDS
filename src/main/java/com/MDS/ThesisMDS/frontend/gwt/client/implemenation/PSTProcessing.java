package com.MDS.ThesisMDS.frontend.gwt.client.implemenation;


import com.MDS.ThesisMDS.frontend.gwt.client.objects.*;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.ProcessingHelper;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.TreePosition;

import java.util.List;

public class PSTProcessing extends TreeProcessing {
    private final int NODE_WIDTH = 50;
    private final int NODE_HEIGHT = 25;

    private Color textColor = new Color(0, 0, 0);
    private Color nodeColor = new Color(117, 244, 121);
    private Color nodeLineColor = new Color(2, 104, 5);
    private Color nodeEventColor = new Color(213, 151, 131);
    private Color nodeEventLineColor = new Color(213, 232, 27);

    private DrawNode nodeForDelete;
    private Integer actualRotation = 0;

    public PSTProcessing(int width, int height, List<DrawNode> nodeList, List<DrawNode> eventList, EventType eventType) {
        super(width, height, nodeList, eventList, eventType);
    }

    @Override
    public void setup() {
        ProcessingHelper.prepareProcessing(pro, width, height, framerate, ProcessingHelper.CL_WHITE);

        if (nodeList != null && nodeList.size() > 0) {
            if (eventList != null && eventList.size() > 0) {
                TreePosition.ProcessPSTTree(width, height, NODE_WIDTH, NODE_HEIGHT, nodeList, EventType.etBulkLoading);
                TreePosition.ProcessPSTEvent(nodeList, eventList, eventType, width, height, NODE_WIDTH, NODE_HEIGHT);
            } else {
                TreePosition.ProcessPSTTree(width, height, NODE_WIDTH, NODE_HEIGHT, nodeList, eventType);
            }
        } else if (eventList != null && (eventList.size() > 0)) {
            TreePosition.ProcessPSTTree(width, height, NODE_WIDTH, NODE_HEIGHT, eventList, EventType.etBulkLoadingAnimated);
        } else {
            printEmpty();
        }

        pro.loop();
        animationRunning = true;
    }

    @Override
    public void draw() {
        pro.background(255, 255, 255);
        drawTree();
        drawEvent();
    }

    private boolean printEmpty() {
        if ((nodeList == null || nodeList.size() == 0) && (eventList == null || eventList.size() == 0)) {
            ProcessingHelper.printEmptyMessage(pro, width, height, nodeColor);
            return true;
        }
        return false;
    }

    private void drawEvent() {
        for (int i = 0; i < eventList.size(); i++) {
            DrawNode treeNode = eventList.get(i);
            if (treeNode.isRotating()) {
                if (treeNode.getRotationIndex() > 0) {
                    if (!treeNode.isChanged() && treeNode.getRotationIndex() > 1) {//changing default locations of nodes after rotation change
                        DrawNode byUIDToIndex = DrawNode.findByUIDToIndex(eventList, treeNode, i);
                        if (byUIDToIndex != null) {
                            treeNode.setActualDrawLocation(new Point(byUIDToIndex.getFinalDrawLocation()));
                        }
                        treeNode.setChanged(true);
                    }
                    actualRotation = i;
                }

                hideNodeFromDrawList(treeNode); //hiding node from drawList, keeping it in event List
                hideNodeFromEventListToIndex(treeNode, i);

                //calculating node Position by parent location
                if (treeNode.getParent() != null) {
                    DrawNode parentNode = DrawNode.findByUIDToIndex(eventList, treeNode.getParent(), i);
                    treeNode.setParent(parentNode);
                    int level = parentNode.getLevel() + 1;
                    if (level == 1 && parentNode.getNodeType() != NodeType.ntRoot) level = treeNode.getLevel();
                    treeNode.setFinalDrawLocation(TreePosition.getNodeChildernPosition(parentNode, treeNode.getNodeType(), width, NODE_HEIGHT * 4, level, true));
                }

            } else if (actualRotation > 0 && i > actualRotation) {
                if (!treeNode.isChanged()) {
                    DrawNode byUID = DrawNode.findByUID(eventList, treeNode);
                    treeNode.setActualDrawLocation(byUID.getFinalDrawLocation());
                    treeNode.setChanged(true);
                }
                DrawNode parentNode = DrawNode.findByUIDToIndex(eventList, treeNode.getParent(), i);
                treeNode.setParent(parentNode);
                treeNode.setFinalDrawLocation(TreePosition.getNodeChildernPosition(parentNode, treeNode.getNodeType(), width, NODE_HEIGHT * 4, parentNode.getLevel() + 1, true));
            }


            if (treeNode.isDrawIt()) {
                drawNode(treeNode, nodeEventColor, nodeEventLineColor, eventType != EventType.etFind);
            }

            if (treeNode.increment()) {
                break;
            }

            if ((i == (eventList.size() - 1))) {
                if (eventType == EventType.etDelete && nodeForDelete == null) {
                    nodeForDelete = treeNode;
                    break;
                }
                pro.noLoop();
            }
        }
    }


    private void hideNodeFromEventListToIndex(DrawNode treeNode, Integer index) {
        for (int i = 0; i < eventList.size(); i++) {
            if (i >= index) break;
            if (treeNode.getUid().equals(eventList.get(i).getUid())) {
                eventList.get(i).setDrawIt(false);
            }
        }
    }

    private DrawNode hideNodeFromDrawList(DrawNode treeNode) {
        DrawNode drawNode = DrawNode.findByUID(nodeList, treeNode);
        drawNode.setDrawIt(false);
        return drawNode;
    }


    private void hideNode(DrawNode node) {
        DrawNode byKeyAndDataAndDraw = DrawNode.findByKeyAndDataAndDraw(nodeList, node.getDataLocation(), node.getData());
        while (byKeyAndDataAndDraw != null) {
            byKeyAndDataAndDraw.setDrawIt(false);
            byKeyAndDataAndDraw = DrawNode.findByKeyAndDataAndDraw(nodeList, node.getDataLocation(), node.getData());
        }
        byKeyAndDataAndDraw = DrawNode.findByKeyAndDataAndDraw(eventList, node.getDataLocation(), node.getData());
        while (byKeyAndDataAndDraw != null) {
            byKeyAndDataAndDraw.setDrawIt(false);
            byKeyAndDataAndDraw = DrawNode.findByKeyAndDataAndDraw(eventList, node.getDataLocation(), node.getData());
        }
    }

    private void drawTree() {
        if (nodeForDelete != null) {
            hideNode(nodeForDelete);
        }

        for (int i = 0; i < nodeList.size(); i++) {
            DrawNode treeNode = nodeList.get(i);
            if (!treeNode.isDrawIt()) continue;
            drawNode(treeNode, nodeColor, nodeLineColor, true);

            if (treeNode.increment()) {
                break;
            }

            if (treeNode.increment() && (i == (nodeList.size() - 1)) && eventList.size() == 0) {
                pro.noLoop();
                animationRunning = false;
            }
        }
    }

    public void drawNode(DrawNode node, Color nodeColor, Color lineColor, Boolean drawLine) {
        if (node.getParent() != null && drawLine && node.getParent().getActualDrawLocation() != null) {
            int startPositionx = node.getParent().getActualDrawLocation().getX() + NODE_WIDTH / 2;
            int startPositiony = node.getParent().getActualDrawLocation().getY() + NODE_HEIGHT * 2;


            switch (node.getNodeType()) {
                case ntLeftSon:
                    pro.line(startPositionx, startPositiony, node.getActualDrawLocation().getX() + NODE_WIDTH / 2, node.getActualDrawLocation().getY());
                    break;
                case ntRightSon:
                    pro.line(startPositionx, startPositiony, node.getActualDrawLocation().getX() + NODE_WIDTH / 2, node.getActualDrawLocation().getY());
                    break;
            }
        }

        fill(nodeColor);
        pro.rect(node.getActualDrawLocation().getX(), node.getActualDrawLocation().getY(), NODE_WIDTH, NODE_HEIGHT * 2);
        setNodeText(node);
        drawXRect(node, nodeColor);
    }

    private void drawXRect(DrawNode node, Color nodeColor) {
        pro.line(node.getActualDrawLocation().getX() + NODE_WIDTH / 2, node.getActualDrawLocation().getY() + NODE_HEIGHT * 2, node.getActualDrawLocation().getX() + NODE_WIDTH / 2, node.getActualDrawLocation().getY() + NODE_HEIGHT * 2 + NODE_HEIGHT / 2);
        fill(nodeColor);
        pro.rect(node.getActualDrawLocation().getX(), node.getActualDrawLocation().getY() + NODE_HEIGHT * 2 + NODE_HEIGHT / 2, NODE_WIDTH, NODE_HEIGHT);
        fill(new Color(174, 46, 46));
        if (node.getBound() != null) {
            pro.text(node.getBound().toString(), calculateXPosition(node.getActualDrawLocation().getX().toString(), node.getActualDrawLocation().getX(), NODE_WIDTH), node.getActualDrawLocation().getY() + NODE_HEIGHT * 2 + NODE_HEIGHT / 2 + 15);
        }
    }

    public void setNodeText(DrawNode node) {
        int xPosition = node.getActualDrawLocation().getX();

        fill(textColor);
        pro.text(node.getData(), calculateXPosition(node.getData(), xPosition, NODE_WIDTH), node.getActualDrawLocation().getY() + 15);
        pro.text(node.getDataLocation().getX().toString(), calculateXPosition(node.getDataLocation().getX().toString(), xPosition, NODE_WIDTH), node.getActualDrawLocation().getY() + 30);
        fill(new Color(174, 46, 46));
        pro.text(node.getDataLocation().getY().toString(), calculateXPosition(node.getDataLocation().getY().toString(), xPosition, NODE_WIDTH), node.getActualDrawLocation().getY() + 45);
    }

    private int calculateXPosition(String data, int position, int nodeWidth) {
        if (data.length() > 4) {
            return position;
        } else {
            return position + nodeWidth / 3;
        }
    }
}
