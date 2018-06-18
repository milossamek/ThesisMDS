package com.MDS.ThesisMDS.backend.database.model;

import com.MDS.ThesisMDS.backend.types.TreeType;

import javax.persistence.*;

@Entity
public class TreeDB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private Integer nodeCount;
    @Column
    @Enumerated
    private TreeType type;
    @Column
    private Integer maxLevel;


    public TreeDB() {
    }

    public TreeDB(Integer nodeCount, TreeType type, Integer maxLevel) {
        this.nodeCount = nodeCount;
        this.type = type;
        this.maxLevel = maxLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }

    public TreeType getType() {
        return type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public String toString() {
        return "" + id;
    }
}
