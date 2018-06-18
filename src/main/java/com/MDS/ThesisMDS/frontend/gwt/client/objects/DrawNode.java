package com.MDS.ThesisMDS.frontend.gwt.client.objects;

import java.util.List;


public class DrawNode {
    private static final int STEPINCREMENT = 5;
    private String data;
    private Point dataLocation;
    private Point actualDrawLocation;
    private Point finalDrawLocation;
    private DrawNode parent;
    private NodeType nodeType;
    private Integer level;
    private NodeSortType sortType;
    private boolean drawIt;
    private Integer childCount = 0;
    private Integer bound;
    private String uid;
    private boolean isRotating;
    private RotationType rotationType;
    private boolean isChanged = false;
    private Integer rotationIndex = 0;

    public DrawNode() {
        this.nodeType = NodeType.ntRoot;
        this.sortType = NodeSortType.stX;
        this.drawIt = true;
        this.isRotating = false;
        this.isChanged = false;
        this.rotationIndex = 0;
    }

    public DrawNode(String data, Point dataLocation, Point actualDrawLocation, Point finalDrawLocation, DrawNode parent, NodeType nodeType) {
        this.data = data;
        this.dataLocation = dataLocation;
        this.actualDrawLocation = actualDrawLocation;
        this.finalDrawLocation = finalDrawLocation;
        this.parent = parent;
        this.nodeType = nodeType;
        this.drawIt = true;
        this.isRotating = false;
        this.isChanged = false;
        this.rotationIndex = 0;
    }

    public static DrawNode copy(DrawNode node, Boolean uid, Boolean bound) {
        DrawNode nodeOut = copy(node);

        if (uid) {
            nodeOut.setUid(node.getUid());
        }

        if (bound) {
            nodeOut.setBound(node.getBound());
        }

        return nodeOut;
    }

    public static DrawNode copy(DrawNode node) {
        DrawNode drawNode = new DrawNode();
        String data = node.getData();
        if (data != null) drawNode.setData(data);
        if (node.getDataLocation() != null) {
            drawNode.setDataLocation(new Point(node.getDataLocation().getX(), node.getDataLocation().getY()));
        }

        if (node.getActualDrawLocation() != null) {
            drawNode.setActualDrawLocation(new Point(node.getActualDrawLocation().getX(), node.getActualDrawLocation().getY()));
        }

        if (node.getFinalDrawLocation() != null) {
            drawNode.setFinalDrawLocation(new Point(node.getFinalDrawLocation().getX(), node.getFinalDrawLocation().getY()));
        }
        if (node.getParent() != null) {
            drawNode.setParent(node.getParent());
        }

        if (node.getNodeType() != null) {
            drawNode.setNodeType(node.getNodeType());
        }

        drawNode.setDrawIt(node.isDrawIt());
        return drawNode;
    }

    public static DrawNode findByKey(List<DrawNode> list, Point key) {
        DrawNode findNode = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY())) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByKeyAndDraw(List<DrawNode> list, Point key) {
        DrawNode findNode = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY()) && list.get(i).isDrawIt()) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }


    public static DrawNode findByKeyAndDataAndDraw(List<DrawNode> list, Point key, String data) {
        DrawNode findNode = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY()) && list.get(i).isDrawIt() && list.get(i).getData().equals(data)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByKeyAndDataAndDrawAndSort(List<DrawNode> list, Point key, String data, NodeSortType type) {
        DrawNode findNode = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY()) && list.get(i).isDrawIt() && list.get(i).getData().equals(data) && list.get(i).getSortType().equals(type)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }


    public static DrawNode findByKeyAndSortType(List<DrawNode> list, Point key, NodeSortType type) {
        DrawNode findNode = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY()) && list.get(i).getSortType().equals(type)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByUID(List<DrawNode> list, DrawNode node) {
        DrawNode findNode = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getUid() != null && list.get(i).getUid().equals(node.getUid())) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }


    public static DrawNode findByUIDToIndex(List<DrawNode> list, DrawNode node, Integer index) {
        DrawNode findNode = null;
        if (index > list.size()) {
            index = list.size();
        }

        for (int i = 0; i < index; i++) {
            if (list.get(i).getUid() != null && list.get(i).getUid().equals(node.getUid())) {
                findNode = list.get(i);
            }
        }

        return findNode;
    }

    public static void findByUIDAndChangeDrawLocations(List<DrawNode> list, DrawNode node, Point actual, Point finalP) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUid() != null && list.get(i).getUid().equals(node.getUid())) {
                list.get(i).setActualDrawLocation(new Point(actual));
                list.get(i).setFinalDrawLocation(new Point(finalP));
            }
        }
    }

    public static DrawNode findNodeByParentUIDAndDrawIt(List<DrawNode> list, DrawNode node) {
        DrawNode findNode = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getParent() != null && list.get(i).getParent().getUid() != null && list.get(i).getParent().getUid().equals(node.getUid()) && list.get(i).isDrawIt() && !list.get(i).equals(node)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findNodeByParentUID(List<DrawNode> list, DrawNode node) {
        DrawNode findNode = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getParent() != null && list.get(i).getParent().getUid() != null && list.get(i).getParent().getUid().equals(node.getUid()) && !list.get(i).equals(node)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByUIDAndDraw(List<DrawNode> list, DrawNode node) {
        DrawNode findNode = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getUid() != null && list.get(i).getUid().equals(node.getUid()) && list.get(i).isDrawIt()) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByUIDAndDrawToIndex(List<DrawNode> list, DrawNode node, Integer index) {
        DrawNode findNode = null;
        for (int i = 0; i < index; i++) {
            if (list.get(i).getUid() != null && list.get(i).getUid().equals(node.getUid()) && list.get(i).isDrawIt()) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByKeyAndSortTypeFromIndex(List<DrawNode> list, Point key, NodeSortType type, Integer index) {
        DrawNode findNode = null;
        for (int i = index; i < list.size(); i++) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY()) && list.get(i).getSortType().equals(type)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static DrawNode findByKeyAndSortTypeFromToIndex(List<DrawNode> list, Point key, NodeSortType type, Integer index, Integer size) {
        DrawNode findNode = null;
        for (int i = index; i < size; i++) {
            if (list.get(i).getDataLocation().getX().equals(key.getX()) && list.get(i).getDataLocation().getY().equals(key.getY()) && list.get(i).getSortType().equals(type)) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public static int getSTEPINCREMENT() {
        return STEPINCREMENT;
    }

    private Boolean isRange(Integer x, Integer y, Integer range) {
        if (x > y) {
            if ((x - y) <= range) {
                return true;
            }
        } else {
            if ((y - x) <= range) {
                return true;
            }
        }

        return false;
    }

    public boolean increment() {
        boolean lcontinue = false;
        if (actualDrawLocation == null) actualDrawLocation = new Point(0, 0);
        if (finalDrawLocation == null) return false;

        if (isRotating) {
            if (isRange(actualDrawLocation.getX(), finalDrawLocation.getX(), STEPINCREMENT)) {
                actualDrawLocation.x = finalDrawLocation.getX();
            }

            if (isRange(actualDrawLocation.getY(), finalDrawLocation.getY(), STEPINCREMENT)) {
                actualDrawLocation.y = finalDrawLocation.getY();
            }

            if (actualDrawLocation.getX() < finalDrawLocation.getX()) {
                actualDrawLocation.x += STEPINCREMENT;
            } else if (actualDrawLocation.getX() > finalDrawLocation.getX()) {
                actualDrawLocation.x -= STEPINCREMENT;
            }

            if (actualDrawLocation.getY() < finalDrawLocation.getY()) {
                actualDrawLocation.y += STEPINCREMENT;
            } else if (actualDrawLocation.getY() > finalDrawLocation.getY()) {
                actualDrawLocation.y -= STEPINCREMENT;
            }

            if (actualDrawLocation.getX().equals(finalDrawLocation.getX()) && actualDrawLocation.getY().equals(finalDrawLocation.getY())) {
                return false;
            }

            return true;
        }

        switch (nodeType) {
            case ntRoot:
                if (actualDrawLocation.y < finalDrawLocation.y) {
                    actualDrawLocation.y += STEPINCREMENT;
                    lcontinue = true;
                } else actualDrawLocation.y = finalDrawLocation.y;
                actualDrawLocation.x = finalDrawLocation.x;
                break;
            case ntLeftSon:
            case ntNW:
            case ntNE:
                if (actualDrawLocation.y < finalDrawLocation.y) {
                    actualDrawLocation.y += STEPINCREMENT;
                    lcontinue = true;
                } else actualDrawLocation.y = finalDrawLocation.y;
                if (actualDrawLocation.x > finalDrawLocation.x) {
                    actualDrawLocation.x -= STEPINCREMENT;
                    lcontinue = true;
                } else actualDrawLocation.x = finalDrawLocation.x;
                break;
            case ntRightSon:
            case ntSW:
            case ntSE:
                if (actualDrawLocation.y < finalDrawLocation.y) {
                    actualDrawLocation.y += STEPINCREMENT;
                    lcontinue = true;
                } else actualDrawLocation.y = finalDrawLocation.y;
                if (actualDrawLocation.x < finalDrawLocation.x) {
                    actualDrawLocation.x += STEPINCREMENT;
                    lcontinue = true;
                } else actualDrawLocation.x = finalDrawLocation.x;
                break;
        }
        return lcontinue;
    }

    public void setDataLocation(String location) {

    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Point getDataLocation() {
        return dataLocation;
    }

    public void setDataLocation(Point dataLocation) {
        this.dataLocation = dataLocation;
    }

    public Point getActualDrawLocation() {
        return actualDrawLocation;
    }

    public void setActualDrawLocation(Point actualDrawLocation) {
        this.actualDrawLocation = actualDrawLocation;
    }

    public Point getFinalDrawLocation() {
        return finalDrawLocation;
    }

    public void setFinalDrawLocation(Point finalDrawLocation) {
        this.finalDrawLocation = finalDrawLocation;
    }

    public DrawNode getParent() {
        return parent;
    }

    public void setParent(DrawNode parent) {
        this.parent = parent;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public NodeSortType getSortType() {
        return sortType;
    }

    public void setSortType(NodeSortType sortType) {
        this.sortType = sortType;
    }

    public boolean isDrawIt() {
        return drawIt;
    }

    public void setDrawIt(boolean drawIt) {
        this.drawIt = drawIt;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public Integer getBound() {
        return bound;
    }

    public void setBound(Integer bound) {
        this.bound = bound;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isRotating() {
        return isRotating;
    }

    public void setRotating(boolean rotating) {
        isRotating = rotating;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public void setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public Integer getRotationIndex() {
        return rotationIndex;
    }

    public void setRotationIndex(Integer rotationIndex) {
        this.rotationIndex = rotationIndex;
    }

    @Override
    public String toString() {
        return "DrawNode{" +
                "data='" + data + '\'' +
                ", dataLocation=" + dataLocation +
                ", actualDrawLocation=" + actualDrawLocation +
                ", finalDrawLocation=" + finalDrawLocation +
                ", parent=" + parent +
                ", nodeType=" + nodeType +
                ", level=" + level +
                ", sortType=" + sortType +
                ", drawIt=" + drawIt +
                ", childCount=" + childCount +
                ", bound=" + bound +
                ", uid='" + uid + '\'' +
                '}';
    }
}


