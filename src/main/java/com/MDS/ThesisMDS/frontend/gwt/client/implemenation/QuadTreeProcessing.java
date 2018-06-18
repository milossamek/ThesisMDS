package com.MDS.ThesisMDS.frontend.gwt.client.implemenation;


import com.MDS.ThesisMDS.frontend.gwt.client.objects.*;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.ProcessingHelper;
import com.MDS.ThesisMDS.frontend.gwt.client.utils.TreePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class QuadTreeProcessing extends TreeProcessing {
    private final int NODE_HEIGHT = 20;
    private final int NODE_WIDTH = 30;

    private Color textColor = new Color(0, 0, 0);
    private Color nodeColor = new Color(117, 244, 121);
    private Color nodeLineColor = new Color(2, 104, 5);
    private Color nodeEventColor = new Color(213, 151, 131);
    private Color nodeEventLineColor = new Color(213, 232, 27);

    public QuadTreeProcessing(int width, int height, List<DrawNode> nodeList, List<DrawNode> eventList, EventType eventType) {
        super(width, height, nodeList, eventList, eventType);
    }

    @Override
    public void setup() {
        ProcessingHelper.prepareProcessing(pro, width, height, framerate, ProcessingHelper.CL_WHITE);

        if (nodeList != null && nodeList.size() > 0) {
            if (eventList != null && eventList.size() > 0) {
                loggerGlobal.log(Level.SEVERE,"Processing tree");
                TreePosition.ProcessQuadTree(width, height, NODE_WIDTH, NODE_HEIGHT, nodeList, EventType.etBulkLoading);
                TreePosition.ProcessQuadEvent(nodeList, eventList, eventType, width, NODE_WIDTH, NODE_HEIGHT);
                loggerGlobal.log(Level.SEVERE,"Tree calculated");
                loggerGlobal.log(Level.SEVERE,eventList.toString());
            } else {
                TreePosition.ProcessQuadTree(width, height, NODE_WIDTH, NODE_HEIGHT, nodeList, eventType);

            }
        } else if (eventList != null && (eventList.size() > 0)) {
            TreePosition.ProcessQuadTree(width, height, NODE_WIDTH, NODE_HEIGHT, eventList, EventType.etBulkLoadingAnimated);
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

    private void drawEvent() {
        Boolean afterRemove = false;
        for (int i = 0; i < eventList.size(); i++) {
            DrawNode treeNode = eventList.get(i);

            if (afterRemove) {
                hideSameNodes(treeNode, i);

                //pri upraveni struktury stromu udalosti, musime podivat do struktury stromu kolik aktualne ma node synu a poté jeste do event listu, listy spojime a vykreslime
                if (treeNode.getParent() != null && treeNode.isDrawIt()) {
                    List<NodeType> nodeChilds = getNodeChilds(nodeList, treeNode.getParent());
                    List<NodeType> nodeChildsEvent = getNodeChilds(eventList, treeNode.getParent());
                    nodeChilds.addAll(nodeChildsEvent);

                    if (treeNode.isDrawIt() && nodeChilds != null && nodeChilds.size() < 4) {
                        drawNodeNullChild(nodeChilds, treeNode.getParent());
                    }
                }

                if (treeNode.isDrawIt() && !treeNode.isChanged()) {
                    DrawNode parentNode = DrawNode.findByUIDToIndex(eventList, treeNode.getParent(), i);
                    DrawNode actualNode = DrawNode.findByUID(nodeList, treeNode);
                    treeNode.setRotating(true);
                    treeNode.setParent(parentNode);
                    treeNode.setFinalDrawLocation(TreePosition.getNodeChildernPosition(parentNode, treeNode.getNodeType(), width, NODE_HEIGHT * 4, actualNode.getLevel()-1,false));
                    treeNode.setChanged(true);
               }
             }

            if (treeNode.getNodeType() != NodeType.ntRemove) {
                if (treeNode.isDrawIt())
                    drawNode(treeNode, nodeEventColor, nodeEventLineColor, NODE_WIDTH, NODE_HEIGHT, true, 0);
                if (treeNode.increment()) {
                    break;
                }
            } else {
                afterRemove = true;
                if (hideSameNodes(treeNode, i)) break;
            }


            if (i == (eventList.size() - 1)) {
                loggerGlobal.log(Level.INFO, "Stoping loop: " + eventType);
                if (eventType == EventType.etAdd) {
                    nodeList.get(nodeList.size() - 1).setDrawIt(true);
                    break;
                }
                pro.noLoop();
            }
        }
    }

    private void drawTree() {
        for (int i = 0; i < nodeList.size(); i++) {
            DrawNode treeNode = nodeList.get(i);
            if (treeNode.isDrawIt()) drawNode(treeNode, nodeColor, nodeLineColor, NODE_WIDTH, NODE_HEIGHT, true, 0);
            if (treeNode.increment()) break;

            if (treeNode.getParent() != null) {
                List<NodeType> nodeChilds = getNodeChilds(nodeList, treeNode.getParent());
                if (treeNode.isDrawIt() && nodeChilds != null && nodeChilds.size() < 4) {
                    drawNodeNullChild(nodeChilds, treeNode.getParent());
                }
            }

            for (int j = 0; j < i; j++) {
                DrawNode treeLastNode = nodeList.get(j);
                if (treeLastNode.isDrawIt() && treeNode.getActualDrawLocation().getX().equals(treeLastNode.getActualDrawLocation().getX()) && treeNode.getActualDrawLocation().getY().equals(treeLastNode.getActualDrawLocation().getY())) {
                    nodeList.get(j).setDrawIt(false);
                }
            }

            if (treeNode.increment() && (i == (nodeList.size() - 1)) && eventList.size() == 0) {
                pro.noLoop();
                animationRunning = false;
            }
        }
    }

    private void printEmpty() {
        ProcessingHelper.printEmptyMessage(pro, width, height, nodeLineColor);
    }


    public void drawNode(DrawNode node, Color nodeColor, Color lineColor, Integer nodeWidth, Integer nodeHeight, Boolean drawText, Integer offset) {
        if (node.getParent() != null && node.getNodeType() != NodeType.ntRoot) {
            int startPositionx = node.getParent().getActualDrawLocation().getX();
            int startPositiony = node.getParent().getActualDrawLocation().getY() + nodeHeight;
            fill(nodeLineColor);

            switch (node.getNodeType()) {
                case ntNE:
                case ntNW:
                    pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, node.getActualDrawLocation().getX() + nodeWidth / 2, node.getActualDrawLocation().getY());
                    break;
                case ntSE:
                case ntSW:
                    pro.line(startPositionx + (nodeWidth / 2) + offset, startPositiony, node.getActualDrawLocation().getX() + nodeWidth / 2, node.getActualDrawLocation().getY());
                    break;
            }
        }

        fill(nodeColor);
        if (node.getActualDrawLocation() == null) {
            loggerGlobal.log(Level.SEVERE, "Node without actual draw location" + node);
        }
        pro.rect(node.getActualDrawLocation().getX(), node.getActualDrawLocation().getY(), nodeWidth, nodeHeight);
        if (drawText) setNodeText(node);
    }

    private boolean hideSameNodes(DrawNode treeNode, int index) {
        Boolean something = false;
        DrawNode nodeFromDrawList = DrawNode.findByUIDAndDraw(nodeList, treeNode);
        while (nodeFromDrawList != null) {
            something = true;
            nodeFromDrawList.setDrawIt(false);
            nodeFromDrawList = DrawNode.findByUIDAndDraw(nodeList, treeNode);
        }

        DrawNode nodeSameUID = DrawNode.findByUIDAndDrawToIndex(eventList, treeNode, index);
        while (nodeSameUID != null) {
            something = true;
            nodeSameUID.setDrawIt(false);
            nodeSameUID = DrawNode.findByUIDAndDraw(eventList, treeNode);
        }
        return something;
    }

    private void drawEmptyNode(Point parentLocation, NodeType type, Integer nodeWidth, Integer nodeHeight, Integer emptyNodeWidth, Integer emptyNodeHeight, Point endLocation) {
        int startPositionx = parentLocation.getX();
        int startPositiony = parentLocation.getY() + nodeHeight;

        switch (type) {
            case ntNE:
            case ntNW:
                pro.line(startPositionx + nodeWidth / 2, startPositiony, endLocation.getX() + emptyNodeWidth / 2, endLocation.getY());
                break;
            case ntSE:
            case ntSW:
                pro.line(startPositionx + nodeWidth / 2, startPositiony, endLocation.getX() + emptyNodeWidth / 2, endLocation.getY());
                break;
        }

        fill(new Color(0, 0, 0));
        pro.rect(endLocation.getX(), endLocation.getY(), emptyNodeWidth, emptyNodeHeight);
    }

    public void setNodeText(DrawNode node) {
        fill(textColor);
        String text = "";
        if (node.getData().equals("null")) {
            text = node.getDataLocation().toString();
        } else
            text = node.getData();

        pro.text(text, node.getActualDrawLocation().getX() + NODE_WIDTH / 20, node.getActualDrawLocation().getY() + (NODE_HEIGHT / 2));
    }

    private List<NodeType> getNodeChilds(List<DrawNode> nodeList, DrawNode parentNode) {
        if (parentNode == null) return null;

        ArrayList<NodeType> objects = new ArrayList<>();

        for (DrawNode drawNode : nodeList) {
            if (drawNode.getParent() == null) {
                continue;
            }
            if (drawNode.getParent().getUid().equals(parentNode.getUid())) {
                objects.add(drawNode.getNodeType());
            }
        }
        return objects;
    }

    private void drawNodeNullChild(List<NodeType> usedNodes, DrawNode selectedNode) {
        Boolean[] used = new Boolean[4];
        for (int i = 0; i < used.length; i++) {
            used[i] = false;
        }
        for (NodeType usedNode : usedNodes) {
            switch (usedNode) {
                case ntNW:
                    used[0] = true;
                    break;
                case ntNE:
                    used[1] = true;
                    break;
                case ntSW:
                    used[2] = true;
                    break;
                case ntSE:
                    used[3] = true;
                    break;
            }
        }


        Point nodePosition = null;

        if (!used[0]) {
            nodePosition = TreePosition.getNodeChildernPosition(selectedNode, NodeType.ntNW, width, NODE_HEIGHT * 2, selectedNode.getLevel() + 1, false);
            drawEmptyNode(selectedNode.getFinalDrawLocation(), NodeType.ntNW, NODE_WIDTH, NODE_HEIGHT, 3, 5, nodePosition);
        }

        if (!used[1]) {
            nodePosition = TreePosition.getNodeChildernPosition(selectedNode, NodeType.ntNE, width, NODE_HEIGHT * 2, selectedNode.getLevel() + 1, false);
            drawEmptyNode(selectedNode.getFinalDrawLocation(), NodeType.ntNE, NODE_WIDTH, NODE_HEIGHT, 3, 5, nodePosition);
        }

        if (!used[2]) {
            nodePosition = TreePosition.getNodeChildernPosition(selectedNode, NodeType.ntSW, width, NODE_HEIGHT * 2, selectedNode.getLevel() + 1, false);
            drawEmptyNode(selectedNode.getFinalDrawLocation(), NodeType.ntSW, NODE_WIDTH, NODE_HEIGHT, 3, 5, nodePosition);
        }

        if (!used[3]) {
            nodePosition = TreePosition.getNodeChildernPosition(selectedNode, NodeType.ntSE, width, NODE_HEIGHT * 2, selectedNode.getLevel() + 1, false);
            drawEmptyNode(selectedNode.getFinalDrawLocation(), NodeType.ntSE, NODE_WIDTH, NODE_HEIGHT, 3, 5, nodePosition);
        }
    }

    public DrawNode setNode(Point nodePosition, DrawNode parentNode, NodeType nodeType) {
        DrawNode newNode = new DrawNode();
        newNode.setActualDrawLocation(nodePosition);
        newNode.setFinalDrawLocation(nodePosition);
        newNode.setParent(parentNode);
        newNode.setNodeType(nodeType);
        return newNode;
    }

}
