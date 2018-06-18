package com.MDS.ThesisMDS.backend.implementation.objects;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.types.NodeDrawType;
import com.MDS.ThesisMDS.backend.types.NodeSortType;
import com.MDS.ThesisMDS.backend.types.RotationType;
import com.MDS.ThesisMDS.backend.types.TreeNodeType;

import java.awt.*;
import java.util.List;

public class TreeNode extends TreeNodeDB {
    private static final int STEPINCREMENT = 5;

    private Integer nodeLevel;
    private TreeNode parent;
    private TreeNodeType nodeType;
    private String UID;
    private NodeDrawType drawEventType;
    private Integer childCount;
    private Integer bound;
    private RotationType rotationType;
    private Integer rotationIndex;

    public TreeNode() {
    }

    public TreeNode(ExtendedPoint _loc, String _text, Integer nodeLevel) {
        super(_loc, _text, null, NodeSortType.stX);
        this.nodeLevel = nodeLevel;
        this.childCount = 0;
        this.rotationType = RotationType.rtNoRotation;
        this.rotationIndex = 0;
        this.bound = 0;
        this.UID = "";
    }

    public TreeNode(ExtendedPoint _loc, String data, TreeNode parent, TreeNodeType type, Integer nodeLevel) {
        super(_loc, data, null, NodeSortType.stX);
        this.parent = parent;
        this.nodeType = type;
        this.nodeLevel = nodeLevel;
        this.childCount = 0;
        this.rotationType = RotationType.rtNoRotation;
        this.rotationIndex = 0;
        this.bound = 0;
        this.UID = "";
    }

    public TreeNode(ExtendedPoint _loc, String data, TreeNode leftSon, TreeNode rightSon, TreeNode parent, TreeNodeType nodeType, Integer nodeLevel) {
        super(_loc, data, null, NodeSortType.stX);
        this.parent = parent;
        this.nodeType = nodeType;
        this.nodeLevel = nodeLevel;
        this.childCount = 0;
        this.rotationType = RotationType.rtNoRotation;
        this.rotationIndex = 0;
        this.bound = 0;
        this.UID = "";
    }

    public TreeNode(ExtendedPoint _loc, String data, TreeNode parent, TreeNodeType type, Integer nodeLevel, NodeSortType nodeSortType) {
        super(_loc, data, null, nodeSortType);
        this.parent = parent;
        this.nodeType = type;
        this.nodeLevel = nodeLevel;
        this.rotationType = RotationType.rtNoRotation;
        this.rotationIndex = 0;
        this.bound = 0;
        this.childCount = 0;
        this.UID = "";
    }

    public static TreeNode findByKey(List<TreeNode> list, Point key) {
        TreeNode findNode = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLoc().x == key.x && list.get(i).getLoc().y == key.y) {
                findNode = list.get(i);
                break;
            }
        }

        return findNode;
    }

    public TreeNode clone() {
        TreeNode treeNode = new TreeNode();
        treeNode.setRotationIndex(new Integer(this.getRotationIndex()));
        treeNode.nodeLevel = new Integer(this.nodeLevel);
        treeNode.nodeType = this.nodeType;
        treeNode.UID = new String(this.UID);
        treeNode.drawEventType = this.drawEventType;
        treeNode.childCount = new Integer(this.childCount);
        treeNode.rotationType = this.rotationType;
        if (this.parent != null) {
            treeNode.parent = this.parent.clone();
        }
        treeNode.bound = new Integer(this.bound);

        //TreeNodeD|B
        treeNode.setData(new String(this.data));
        treeNode.setLoc(new ExtendedPoint(this.loc));
        treeNode.setTree(this.getTree());
        treeNode.setNodeSortType(this.getNodeSortType());

        return treeNode;
    }

    public TreeNodeType getNodeType() {
        return nodeType;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public NodeDrawType getDrawEventType() {
        return drawEventType;
    }

    public void setDrawEventType(NodeDrawType drawEventType) {
        this.drawEventType = drawEventType;
    }


    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }


    public Integer getBound() {
        return bound;
    }

    public void setBound(Integer bound) {
        this.bound = bound;
    }


    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public void setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
    }

    public Integer getRotationIndex() {
        return rotationIndex;
    }

    public void setRotationIndex(Integer rotationIndex) {
        this.rotationIndex = rotationIndex;
    }

    @Override
    public String toString() {
        if (parent != null) {
            return getData() + " {" + getLoc().x + ";" + getLoc().y + "}" + nodeType + ";" + nodeLevel + ";" + getNodeSortType() + ";" + UID + ";" + getParent().getData() + ";" + getParent().loc.toString() + ";" + getParent().getUID() + ";" + rotationType;
        } else {
            return getData() + " {" + getLoc().x + ";" + getLoc().y + "}" + nodeType + ";" + nodeLevel + ";" + getNodeSortType() + ";" + UID + ";" + rotationType;
        }
    }
}
