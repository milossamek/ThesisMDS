package com.MDS.ThesisMDS.frontend.gwt.client.utils;


import com.MDS.ThesisMDS.frontend.gwt.client.objects.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.MDS.ThesisMDS.frontend.gwt.client.objects.NodeType.ntFinish;
import static com.MDS.ThesisMDS.frontend.gwt.client.objects.NodeType.ntRoot;

public class TreePosition {

    //funkce na vypocet pozic
    public static Point getNodeChildernPosition(DrawNode node, NodeType childType, Integer width, Integer displaceY, Integer level, Boolean boostRoot) {
        switch (childType) {
            case ntRoot:
                return new Point(node.getFinalDrawLocation().getX(), node.getFinalDrawLocation().getY() + displaceY);
            case ntLeftSon:
                if (node.getNodeType() == NodeType.ntRoot && boostRoot) {
                    return new Point(node.getFinalDrawLocation().getX() - (width / 4), node.getFinalDrawLocation().getY() + (displaceY + displaceY / 2));
                } else {
                    return new Point(node.getFinalDrawLocation().getX() - (int) ((double) width / 5 / ((double) level * Math.max(1.0, (double) level - 2.5))), node.getFinalDrawLocation().getY() + displaceY);
                }
            case ntRightSon:
                if (node.getNodeType() == NodeType.ntRoot && boostRoot) {
                    return new Point(node.getFinalDrawLocation().getX() + (width / 4), node.getFinalDrawLocation().getY() + (displaceY + displaceY / 2));
                } else {
                    return new Point(node.getFinalDrawLocation().getX() + (int) ((double) width / 5 / ((double) level * Math.max(1.0, (double) level - 2.5))), node.getFinalDrawLocation().getY() + displaceY);
                }
            case ntNW: //1
                return new Point((int) ((double) node.getFinalDrawLocation().getX() - 3.0 * ((double) width / Math.pow((double) level, 2.0) / 8.0)), node.getFinalDrawLocation().getY() + displaceY);
            case ntNE: //2
                return new Point((int) ((double) node.getFinalDrawLocation().getX() - (double) width / Math.pow((double) level, 2.0) / 8.0), node.getFinalDrawLocation().getY() + displaceY);
            case ntSW: //3e
                return new Point((int) ((double) node.getFinalDrawLocation().getX() + (double) width / Math.pow((double) level, 2.0) / 8.0), node.getFinalDrawLocation().getY() + displaceY);
            case ntSE: //4
                return new Point((int) ((double) node.getFinalDrawLocation().getX() + 3.0 * ((double) width / Math.pow((double) level, 2.0) / 8.0)), node.getFinalDrawLocation().getY() + displaceY);
        }
        return null;
    }

    public static void ProcessPSTTree(Integer width, Integer height, Integer nodeWidth, Integer nodeHeight, List<DrawNode> nodeList, EventType eventType) {
        Logger logger = Logger.getLogger("ProcessRangeTree");
        if (nodeList.size() == 0) return;
        DrawNode parentNode;

        for (int nodeIndex = 0; nodeIndex < nodeList.size(); nodeIndex++) {
            DrawNode node = nodeList.get(nodeIndex);
            switch (node.getNodeType()) {
                case ntRoot:
                    setRootDefaultPosition(width, height, node);
                    break;
                case ntLeftSon:
                case ntRightSon:
                    if (ParentIsNull(logger, node)) {
                        continue;
                    }
                    setNodePosition(width, nodeHeight, nodeList, node, nodeHeight * 4);
                    break;
            }

            if (eventType == EventType.etBulkLoading) {
                node.setActualDrawLocation(node.getFinalDrawLocation());
            }
        }
    }

    public static void ProcessRangeTree(Integer width, Integer height, Integer nodeWidth, Integer nodeHeight, List<DrawNode> nodeList, EventType eventType) {
        Logger logger = Logger.getLogger("ProcessRangeTree");
        if (nodeList.size() == 0) return;
        DrawNode parentNode;
        int lastRootIndex = 0;
        for (int nodeIndex = 0; nodeIndex < nodeList.size(); nodeIndex++) {
            DrawNode node = nodeList.get(nodeIndex);
            switch (node.getSortType()) {
                case stX:
                    switch (node.getNodeType()) {
                        case ntRoot:
                            lastRootIndex = nodeIndex;
                            setRootDefaultPosition(width, height, node);
                            break;
                        case ntLeftSon:
                        case ntRightSon:
                            if (ParentIsNull(logger, node)) {
                                continue;
                            }
                            setNodePosition(width, nodeHeight, nodeList, node, nodeHeight * 6);
                            break;
                    }
                    break;
                case stY:
                    switch (node.getNodeType()) {
                        case ntRoot:
                            lastRootIndex = nodeIndex;
                            if (ParentIsNull(logger, node)) {
                                continue;
                            }
                            parentNode = DrawNode.findByKeyAndSortType(nodeList, node.getParent().getDataLocation(), NodeSortType.stX);
                            node.setActualDrawLocation(new Point(parentNode.getFinalDrawLocation().getX(), parentNode.getFinalDrawLocation().getY()));
                            node.setFinalDrawLocation(new Point(parentNode.getFinalDrawLocation().getX(), parentNode.getFinalDrawLocation().getY() + nodeHeight * 2));
                            node.setParent(parentNode);
                            node.setLevel(parentNode.getLevel() + 2);
                            break;
                        case ntLeftSon:
                        case ntRightSon:
                            if (ParentIsNull(logger, node)) {
                                continue;
                            }
                            parentNode = DrawNode.findByKeyAndSortTypeFromToIndex(nodeList, node.getParent().getDataLocation(), NodeSortType.stY, lastRootIndex - 1, nodeIndex + 1);
                            node.setActualDrawLocation(new Point(parentNode.getFinalDrawLocation().getX(), parentNode.getFinalDrawLocation().getY()));
                            node.setLevel(parentNode.getLevel() + 1);
                            node.setFinalDrawLocation(getNodeChildernPosition(parentNode, node.getNodeType(), width, nodeHeight * 2, node.getLevel(), false));
                            node.setParent(parentNode);
                            break;
                    }
                    break;
            }
            if (eventType == EventType.etBulkLoading) {
                node.setActualDrawLocation(node.getFinalDrawLocation());
            }
        }
    }


    public static void ProcessQuadTree(Integer width, Integer height, Integer nodeWidth, Integer nodeHeight, List<DrawNode> nodeList, EventType eventType) {
        Logger logger = Logger.getLogger("ProcessRangeTree");
        if (nodeList.size() == 0) return;
        for (int nodeIndex = 0; nodeIndex < nodeList.size(); nodeIndex++) {
            DrawNode node = nodeList.get(nodeIndex);
            switch (node.getNodeType()) {
                case ntRoot:
                    setRootDefaultPosition(width, height, node);
                    break;
                case ntNW:
                case ntSE:
                case ntSW:
                case ntNE:
                    if (ParentIsNull(logger, node)) {
                        continue;
                    }
                    DrawNode parentNode = DrawNode.findByUID(nodeList, node.getParent());
                    setDrawLocatAndParent(node, new Point(parentNode.getFinalDrawLocation().getX(), parentNode.getFinalDrawLocation().getY()), getNodeChildernPosition(parentNode, node.getNodeType(), width, nodeHeight * 4, node.getLevel(), false), parentNode);
                    break;
            }

            if (eventType == EventType.etBulkLoading) {
                node.setActualDrawLocation(node.getFinalDrawLocation());
            }
        }

    }

    //*******************************************************KONEC ROZLOZENI STROMU****************************************************/
    private static void setNodePosition(Integer width, Integer nodeHeight, List<DrawNode> nodeList, DrawNode node, int displaceY) {
        DrawNode parentNode;
        parentNode = DrawNode.findByKeyAndSortType(nodeList, node.getParent().getDataLocation(), NodeSortType.stX);
        node.setActualDrawLocation(new Point(parentNode.getFinalDrawLocation().getX(), parentNode.getFinalDrawLocation().getY()));
        node.setFinalDrawLocation(getNodeChildernPosition(parentNode, node.getNodeType(), width, displaceY, node.getLevel(), true));
        node.setParent(parentNode);
        node.setLevel(parentNode.getLevel() + 1);
    }

    private static void setRootDefaultPosition(Integer width, Integer height, DrawNode node) {
        int canvasMiddlePoint = (width / 2);
        node.setActualDrawLocation(new Point(canvasMiddlePoint, 0));
        node.setFinalDrawLocation(new Point(canvasMiddlePoint, height / 16));
        node.setLevel(0);
    }


    //*********************************************************************** EVENTY*************************************************************************************************************
    public static void ProcessPSTEvent(List<DrawNode> drawList, List<DrawNode> eventList, EventType type, Integer width, Integer height, Integer nodeWidth, Integer nodeHeight) {
        Logger logger = Logger.getLogger("Calculate event");
        if (eventList.size() == 0) return;
        DrawNode lastNode = null;

        switch (type) {
            case etFind:
                for (DrawNode treeNode : eventList) {
                    DrawNode byKey = DrawNode.findByKeyAndSortType(drawList, treeNode.getDataLocation(), NodeSortType.stX);
                    if (byKey != null) {
                        switch (byKey.getNodeType()) {
                            case ntRoot:
                                treeNode.setActualDrawLocation(new Point(byKey.getFinalDrawLocation()));
                                lastNode = treeNode;
                                break;
                            case ntLeftSon:
                            case ntRightSon:
                            case ntFinish:
                                treeNode.setParent(byKey.getParent());
                                treeNode.setActualDrawLocation(new Point(lastNode.getFinalDrawLocation()));
                                lastNode = treeNode;
                                break;
                        }
                        treeNode.setFinalDrawLocation(new Point(byKey.getFinalDrawLocation()));
                    }
                }
                break;
            case etDelete:
                //FindingAction - searching node before remove
                Integer foundedNodeIndex = -1;
                for (int i = 0; i < eventList.size(); i++) {
                    DrawNode treeNode = eventList.get(i);
                    DrawNode nodeFromTreeStructure = DrawNode.findByUID(drawList, treeNode);
                    if (nodeFromTreeStructure != null) {
                        switch (treeNode.getNodeType()) {
                            case ntRoot:
                                treeNode.setActualDrawLocation(new Point(nodeFromTreeStructure.getFinalDrawLocation()));
                                lastNode = treeNode;
                                break;
                            case ntLeftSon:
                            case ntRightSon:
                            case ntFinish:
                                if (nodeFromTreeStructure.getParent() != null) {
                                    treeNode.setParent(nodeFromTreeStructure.getParent());
                                }
                                treeNode.setActualDrawLocation(new Point(lastNode.getFinalDrawLocation()));
                                lastNode = treeNode;
                                if (treeNode.getNodeType() == ntFinish) {
                                    foundedNodeIndex = i;
                                }
                                break;
                        }
                        treeNode.setFinalDrawLocation(new Point(nodeFromTreeStructure.getFinalDrawLocation()));
                    }
                    if (foundedNodeIndex != -1) break;
                }


                //Deleting action
                for (int i = foundedNodeIndex + 1; i < eventList.size(); i++) {
                    DrawNode treeNodeD = eventList.get(i);
                    treeNodeD.setDrawIt(true);
                    DrawNode byUID = DrawNode.findByUID(drawList, treeNodeD);

                    if (byUID != null) {
                        switch (treeNodeD.getNodeType()) {
                            case ntRoot:
                                setNodeLocationAndParent(treeNodeD, null, byUID.getActualDrawLocation(), new Point(((width / 2) - nodeWidth) + nodeWidth, height / 16), true);
                                treeNodeD.setLevel(0);
                                break;
                            case ntLeftSon:
                            case ntRightSon:
                                DrawNode parentByUID = DrawNode.findByUID(drawList, treeNodeD.getParent());
                                if (treeNodeD.getRotationType() == RotationType.rtNoRotation) {
                                    //upravuju prvky bez rotace
                                    setNodeLocationAndParent(treeNodeD, parentByUID, byUID.getFinalDrawLocation(), getNodeChildernPosition(parentByUID, treeNodeD.getNodeType(), width, nodeHeight * 4, parentByUID.getLevel() + 1, true), true);
                                } else {
                                    //upravuju prvky s rotaci
                                    setNodeLocationAndParent(treeNodeD, parentByUID, byUID.getFinalDrawLocation(), byUID.getFinalDrawLocation(), true);
                                }
                                break;
                            case ntFinish:
                                DrawNode parentByUID1 = DrawNode.findByUID(drawList, treeNodeD.getParent());
                                DrawNode finishPos = DrawNode.findByUID(drawList, treeNodeD);
                                setNodeLocationAndParent(treeNodeD, parentByUID1, finishPos.getFinalDrawLocation(), finishPos.getFinalDrawLocation(), false);
                                break;
                        }
                    }
                }
                break;
            case etAdd:
                for (DrawNode treeNode : eventList) {
                    DrawNode byKey = DrawNode.findByUID(drawList, treeNode);
                    if (byKey != null) {
                        switch (byKey.getNodeType()) {
                            case ntRoot:
                                treeNode.setActualDrawLocation(new Point(byKey.getFinalDrawLocation()));
                                lastNode = treeNode;
                                break;
                            case ntLeftSon:
                            case ntRightSon:
                            case ntFinish:
                                DrawNode byUID = DrawNode.findByUID(drawList, byKey.getParent());
                                if (byUID != null) treeNode.setParent(byUID);
                                else treeNode.setParent(byKey.getParent());
                                treeNode.setActualDrawLocation(new Point(lastNode.getFinalDrawLocation()));
                                lastNode = treeNode;
                        }
                        treeNode.setFinalDrawLocation(new Point(byKey.getFinalDrawLocation()));
                    } else {
                        if (treeNode.getParent() != null) {
                            DrawNode parentByUid = DrawNode.findByUID(drawList, treeNode.getParent());
                            if (parentByUid != null) {
                                DrawNode newDrawNode = DrawNode.copy(treeNode, true, true);
                                newDrawNode.setLevel(parentByUid.getLevel() + 1);
                                newDrawNode.setDrawIt(false);
                                setNodeLocationAndParent(newDrawNode, parentByUid, parentByUid.getFinalDrawLocation(), getNodeChildernPosition(parentByUid, newDrawNode.getNodeType(), width, nodeHeight * 4, parentByUid.getLevel() + 1, true), true);
                                drawList.add(newDrawNode);

                                setNodeLocationAndParent(treeNode, parentByUid, newDrawNode.getActualDrawLocation(), newDrawNode.getFinalDrawLocation(), true);
                                treeNode.setLevel(parentByUid.getLevel() + 1);
                                lastNode = treeNode;
                            }
                        } else {
                            if (treeNode.getNodeType() == NodeType.ntRoot) {
                                treeNode.setActualDrawLocation(new Point(((width / 2) - nodeWidth) + nodeWidth, height / 16));
                                treeNode.setFinalDrawLocation(new Point(((width / 2) - nodeWidth) + nodeWidth, height / 16));
                                treeNode.setLevel(0);


                                DrawNode newDrawNode = DrawNode.copy(treeNode, true, true);
                                setNodeLocationAndParent(newDrawNode, null, treeNode.getActualDrawLocation(), treeNode.getFinalDrawLocation(), true);
                                newDrawNode.setLevel(0);
                                newDrawNode.setDrawIt(false);
                                drawList.add(newDrawNode);
                                lastNode = treeNode;
                            }
                        }
                    }
                }
                break;
        }
    }

    private static void setNodeLocationAndParent(DrawNode node, DrawNode parent, Point actualLocation, Point finalLocation, Boolean rotating) {
        node.setActualDrawLocation(new Point(actualLocation));
        node.setFinalDrawLocation(new Point(finalLocation));
        node.setParent(parent);
        node.setRotating(rotating);
    }

    public static void ProcessRangeEvent(List<DrawNode> drawList, List<DrawNode> eventList, EventType eventType, Integer width, Integer nodeWidth, Integer nodeHeight) {
        Logger logger = Logger.getLogger("Calculate event position");
        if (eventList.size() == 0) return;
        DrawNode lastNode = null;
        Boolean nodeKeyChanged = false;
        DrawNode newDrawNode = null;


        switch (eventType) {
            case etFind:
                ProcessPSTEvent(drawList, eventList, eventType, width, 0, nodeWidth, nodeHeight);
                break;
            case etDelete:
                for (int i = 0; i < eventList.size(); i++) {
                    DrawNode node = eventList.get(i);

                    switch (node.getNodeType()) {
                        case ntRoot:
                            DrawNode drawedRootNode = DrawNode.findByUIDAndDraw(drawList, node);
                            if (drawedRootNode != null) {
                                if (drawedRootNode.getParent() != null) {
                                    node.setParent(drawedRootNode.getParent());
                                }
                                node.setActualDrawLocation(new Point(drawedRootNode.getFinalDrawLocation()));
                                node.setFinalDrawLocation(new Point(drawedRootNode.getFinalDrawLocation()));
                                node.setLevel(drawedRootNode.getLevel());
                            }
                            break;
                        case ntLeftSon:
                        case ntRightSon:
                            DrawNode drawedNode = DrawNode.findByUID(drawList, node);
                            if (drawedNode != null) {
                                DrawNode parentNode = DrawNode.findByUIDToIndex(eventList, node.getParent(), i);
                                if (parentNode != null) {
                                    //      node.setLevel(parentNode.getLevel() + 1);
                                    setNodeLocationAndParent(node, parentNode, drawedNode.getFinalDrawLocation(), getNodeChildernPosition(parentNode, node.getNodeType(), width,
                                            node.getSortType() == NodeSortType.stY ? nodeHeight * 2 : nodeHeight * 6, node.getLevel(), false), true);
                                } else {
                                    parentNode = DrawNode.findByUID(drawList, node.getParent());
                                    // node.setLevel(parentNode.getLevel() + 1);
                                    setNodeLocationAndParent(node, parentNode, drawedNode.getFinalDrawLocation(), getNodeChildernPosition(parentNode, node.getNodeType(), width,
                                            node.getSortType() == NodeSortType.stY ? nodeHeight * 2 : nodeHeight * 6, node.getLevel(), false), true);
                                }
                            }
                            break;
                        case ntFinish:
                            DrawNode drawNode = DrawNode.findByUID(drawList, node);
                            if (drawNode != null) {
                                setNodeLocationAndParent(node, drawNode.getParent(), drawNode.getActualDrawLocation(), drawNode.getFinalDrawLocation(), false);
                            }
                            break;
                        case ntParent:
                            DrawNode changedNode = DrawNode.findByUIDAndDraw(drawList, node);
                            DrawNode parentNode = null;
                            if (changedNode != null) {
                                if (changedNode.getParent() != null) {
                                    parentNode = DrawNode.findByUID(drawList, changedNode.getParent());
                                    if (parentNode == null) {
                                        parentNode = changedNode.getParent();
                                    }
                                }
                                setNodeLocationAndParent(node, parentNode, changedNode.getFinalDrawLocation(), changedNode.getFinalDrawLocation(), false);
                            }
                            break;
                        case ntRemove:
                            DrawNode drawNode1 = DrawNode.findByUID(drawList, node);
                            if (drawNode1 != null) {
                                setNodeLocationAndParent(node, node.getParent(), drawNode1.getActualDrawLocation(), drawNode1.getFinalDrawLocation(), false);
                            }
                            break;
                    }
                }
                break;
            case etAdd:
                //prochazim hlavni strom
                for (DrawNode treeNode : eventList) {
                    if (treeNode.getSortType() == NodeSortType.stY) continue;
                    DrawNode byKey = DrawNode.findByUID(drawList, treeNode);
                    if (byKey == null || nodeKeyChanged) {
                        if (treeNode.getNodeType() == ntRoot) {
                            newDrawNode = DrawNode.copy(treeNode, true, false);
                            newDrawNode.setLevel(0);
                            newDrawNode.setActualDrawLocation(new Point(width / 2 , 0));
                            newDrawNode.setFinalDrawLocation(drawList.get(0).getFinalDrawLocation());
                            drawList.add(newDrawNode);
                            copyLocations(newDrawNode,treeNode);
                            lastNode = newDrawNode;
                            nodeKeyChanged=true;
                        } else if (!nodeKeyChanged) {
                            DrawNode parentByKey = DrawNode.findByUID(drawList, treeNode.getParent());
                            nodeKeyChanged = true;
                            if (parentByKey != null) {
                                newDrawNode = DrawNode.copy(treeNode, true, false);
                                newDrawNode.setLevel(parentByKey.getLevel() + 1);
                                newDrawNode.setDrawIt(false);
                                setNodeLocationAndParent(newDrawNode, parentByKey, parentByKey.getFinalDrawLocation(), getNodeChildernPosition(parentByKey, newDrawNode.getNodeType(), width, newDrawNode.getSortType() == NodeSortType.stY ? nodeHeight * 2 : nodeHeight * 6, parentByKey.getLevel() + 1, false), false);
                                drawList.add(newDrawNode);
                                setNodeLocationAndParent(treeNode, parentByKey, newDrawNode.getActualDrawLocation(), newDrawNode.getFinalDrawLocation(), false);
                                lastNode = newDrawNode;
                            } else {
                                setDrawLocatAndParent(treeNode, lastNode.getParent().getFinalDrawLocation(), getNodeChildernPosition(lastNode.getParent(), treeNode.getNodeType(), width, treeNode.getSortType() == NodeSortType.stY ? nodeHeight * 2 : nodeHeight * 6, lastNode.getLevel(), false), lastNode.getParent());
                                lastNode = treeNode.getParent();
                                if (newDrawNode == null) {
                                    newDrawNode = DrawNode.copy(treeNode, true, false);
                                    newDrawNode.setDrawIt(false);
                                    setNodeLocationAndParent(newDrawNode, treeNode.getParent(), treeNode.getActualDrawLocation(), treeNode.getFinalDrawLocation(), false);
                                    newDrawNode.setLevel(treeNode.getLevel() + 1);
                                    drawList.add(newDrawNode);
                                }
                            }
                        } else {
                            setDrawLocatAndParent(treeNode, newDrawNode.getFinalDrawLocation(), getNodeChildernPosition(newDrawNode, treeNode.getNodeType(), width, treeNode.getSortType() == NodeSortType.stY ? nodeHeight * 2 : nodeHeight * 6, treeNode.getLevel(), false), newDrawNode);
                        }
                    } else {
                        setNodePosition(treeNode, byKey, lastNode);
                        lastNode = treeNode;
                    }
                }

                //prochazim vedlejsi stromy
                for (DrawNode treeNode : eventList) {
                    if (treeNode.getSortType() == NodeSortType.stX) continue;
                    switch (treeNode.getNodeType()) {
                        case ntRoot:
                            DrawNode parentByKey = DrawNode.findByUID(drawList, treeNode.getParent());
                            if (parentByKey != null) {
                                setDrawLocatAndParent(treeNode, new Point(parentByKey.getFinalDrawLocation().getX(), parentByKey.getFinalDrawLocation().getY()),
                                        new Point(parentByKey.getFinalDrawLocation().getX(), parentByKey.getFinalDrawLocation().getY() + nodeHeight * 2),
                                        parentByKey);
                                treeNode.setLevel(parentByKey.getLevel() + 2);
                            }
                            break;
                        case ntLeftSon:
                        case ntRightSon:
                            DrawNode parentY = DrawNode.findByUID(eventList, treeNode.getParent());
                            if (parentY != null) {
                                treeNode.setLevel(parentY.getLevel() + 1);
                                setDrawLocatAndParent(treeNode, new Point(parentY.getFinalDrawLocation().getX(), parentY.getFinalDrawLocation().getY()), getNodeChildernPosition(parentY, treeNode.getNodeType(), width, treeNode.getSortType() == NodeSortType.stY ? nodeHeight * 2 : nodeHeight * 6, treeNode.getLevel(), false), parentY);
                            }
                            break;
                    }
                }
                break;
        }
    }

    private static int calculateNodeLevel(DrawNode node) {
        DrawNode actualNode = node;
        int level = 0;
        while (actualNode.getNodeType() == NodeType.ntRoot) {
            level++;
            actualNode = actualNode.getParent();
        }

        return level;
    }

    private static void copyLocations(DrawNode from, DrawNode to) {
        to.setActualDrawLocation(from.getActualDrawLocation());
        to.setFinalDrawLocation(from.getFinalDrawLocation());
    }


    public static void ProcessQuadEvent(List<DrawNode> drawList, List<DrawNode> eventList, EventType eventType, Integer width, Integer nodeWidth, Integer nodeHeight) {
        if (eventList.size() == 0) return;
        DrawNode lastNode = null;
        Boolean afterRemove = false;
        Integer maxIndex = 0;

        for (DrawNode treeNode : eventList) {
            if (treeNode.getSortType() == NodeSortType.stY) continue;
            DrawNode byKey = DrawNode.findByUID(drawList, treeNode);

            //jestlize v puvodni strukture nebyl prvek, musime ho vytvorit.. pozor prvek nemusi mit otce a může to být kořen
            if (byKey == null && (eventType == EventType.etAdd || eventType == EventType.etDelete)) {
                if (treeNode.getParent() != null) {
                    DrawNode parentByKey = DrawNode.findByUID(drawList, treeNode.getParent());

                    if (parentByKey == null) {
                        parentByKey = DrawNode.findByUIDToIndex(eventList, treeNode.getParent(),maxIndex);
                        if (parentByKey == null) {
                            parentByKey = lastNode;
                        }
                    }

                    DrawNode newDrawNode = insertNewNode(treeNode, parentByKey.getFinalDrawLocation(), getNodeChildernPosition(parentByKey, treeNode.getNodeType(), width, nodeHeight * 4, parentByKey.getLevel() + 1, false), parentByKey);
                    newDrawNode.setDrawIt(eventType == EventType.etDelete);
                    drawList.add(newDrawNode);
                    setDrawLocatAndParent(treeNode, newDrawNode.getActualDrawLocation(), newDrawNode.getFinalDrawLocation(), parentByKey);
                } else {
                    if (treeNode.getNodeType() == ntRoot) {
                        setRootDefaultPosition(width, nodeHeight, treeNode);
                        treeNode.setFinalDrawLocation(drawList.get(0).getFinalDrawLocation());
                        DrawNode newDrawNode = insertNewNode(treeNode, treeNode.getActualDrawLocation(), drawList.get(0).getFinalDrawLocation(), null);
                        drawList.add(newDrawNode);
                    }
                }

                lastNode = treeNode;
                maxIndex++;
                continue;
            }

            if (byKey != null) {
                switch (treeNode.getNodeType()) {
                    case ntRoot:
                        treeNode.setActualDrawLocation(new Point(byKey.getFinalDrawLocation()));
                        treeNode.setFinalDrawLocation(new Point(byKey.getFinalDrawLocation()));
                        lastNode = treeNode;
                        break;
                    case ntNE:
                    case ntNW:
                    case ntSE:
                    case ntSW:
                        if (!afterRemove) {
                            treeNode.setParent(byKey.getParent());
                            treeNode.setActualDrawLocation(new Point(lastNode.getFinalDrawLocation()));
                            treeNode.setFinalDrawLocation(new Point(byKey.getFinalDrawLocation()));
                            lastNode = treeNode;
                        } else {
                            DrawNode parentByKey = DrawNode.findByUID(drawList, treeNode.getParent());
                            treeNode.setParent(parentByKey);
                            treeNode.setActualDrawLocation(new Point(byKey.getFinalDrawLocation()));
                            treeNode.setFinalDrawLocation(new Point(getNodeChildernPosition(parentByKey, treeNode.getNodeType(), width, nodeHeight * 4, parentByKey.getLevel() + 1, false)));
                            treeNode.setRotating(true);
                            lastNode = treeNode;
                        }
                        break;
                    case ntFinish:
                    case ntRemove:
                        treeNode.setParent(byKey.getParent());
                        treeNode.setActualDrawLocation(new Point(byKey.getFinalDrawLocation()));
                        treeNode.setFinalDrawLocation(new Point(byKey.getFinalDrawLocation()));
                        lastNode = treeNode;
                        afterRemove = true;
                        break;

                }
                maxIndex++;
            }
        }
    }


    public static boolean ParentIsNull(Logger logger, DrawNode node) {
        if (node.getParent() == null) {
            logger.log(Level.SEVERE, "Parent is null " + node);
            return true;
        } else return false;
    }

    private static void setNodePosition(DrawNode treeNode, DrawNode byKey, DrawNode lastNode) {
        switch (byKey.getNodeType()) {
            case ntRoot:
                treeNode.setActualDrawLocation(new Point(byKey.getFinalDrawLocation()));
                break;
            case ntLeftSon:
            case ntRightSon:
                treeNode.setParent(byKey.getParent());
                treeNode.setActualDrawLocation(new Point(lastNode.getFinalDrawLocation()));
                break;
            case ntFinish:
                treeNode.setParent(byKey.getParent());
                treeNode.setActualDrawLocation(new Point(lastNode.getFinalDrawLocation()));
                break;
        }

        treeNode.setFinalDrawLocation(new Point(byKey.getFinalDrawLocation()));
    }

    private static void setDrawLocatAndParent(DrawNode treeNode, Point actualLoc, Point finalLoc, DrawNode parentNode) {
        treeNode.setActualDrawLocation(new Point(actualLoc));
        treeNode.setFinalDrawLocation(new Point(finalLoc));
        treeNode.setParent(parentNode);
    }

    private static DrawNode insertNewNode(DrawNode treeNode, Point actualLoc, Point finalLoc, DrawNode parentNode) {
        DrawNode newDrawNode = DrawNode.copy(treeNode);
        newDrawNode.setActualDrawLocation(new Point(actualLoc));
        newDrawNode.setFinalDrawLocation(new Point(finalLoc));
        newDrawNode.setParent(parentNode);
        if (newDrawNode.getUid() != null) {
            newDrawNode.setUid(treeNode.getUid());
        }
        return newDrawNode;
    }

    private static Integer getMaxLevel(List<DrawNode> tree) {
        int maxLevel = 0;
        for (DrawNode treeNode : tree) {
            if (treeNode.getLevel() > maxLevel) {
                maxLevel = treeNode.getLevel();
            }
        }

        return maxLevel;
    }

}
