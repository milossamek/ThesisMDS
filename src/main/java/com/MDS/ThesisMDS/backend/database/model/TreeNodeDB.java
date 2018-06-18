package com.MDS.ThesisMDS.backend.database.model;

import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.NodeSortType;

import javax.persistence.*;


@Entity
public class TreeNodeDB {
    @Column
    public ExtendedPoint loc;
    @Column
    public String data;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private TreeDB tree;
    @Column
    @Enumerated
    private NodeSortType nodeSortType;
    @Column(columnDefinition = "BOOLEAN")
    private Boolean deleted;


    public TreeNodeDB() {
        this.deleted = false;
    }

    public TreeNodeDB(ExtendedPoint loc, String data) {
        this.loc = loc;
        this.data = data;
        this.tree = null;
        this.nodeSortType = NodeSortType.stX;
        this.deleted = false;
    }

    public TreeNodeDB(ExtendedPoint loc, String data, TreeDB tree, NodeSortType type) {
        this.loc = loc;
        this.data = data;
        this.tree = tree;
        this.nodeSortType = type;
        this.deleted = false;
    }

    public static TreeNodeDB Copy(TreeNode node) {
        TreeNodeDB nodeOut = new TreeNodeDB();
        if (node.getId() != null && node.getId() != 0) {
            nodeOut.id = node.getId();
        }
        nodeOut.loc = node.getLoc();
        nodeOut.data = node.getData();
        nodeOut.tree = node.getTree();
        return nodeOut;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ExtendedPoint getLoc() {
        return loc;
    }

    public void setLoc(ExtendedPoint loc) {
        this.loc = loc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public TreeDB getTree() {
        return tree;
    }

    public void setTree(TreeDB tree) {
        this.tree = tree;
    }

    public NodeSortType getNodeSortType() {
        return nodeSortType;
    }

    public void setNodeSortType(NodeSortType nodeSortType) {
        this.nodeSortType = nodeSortType;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
