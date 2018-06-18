package com.MDS.ThesisMDS.backend.database.model;

import com.MDS.ThesisMDS.backend.types.OperationType;

import javax.persistence.*;

@Entity
public class OperationsDB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    @Enumerated
    private OperationType type;
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private TreeNodeDB node;
    @Column
    private String details;

    public OperationsDB() {
    }

    public OperationsDB(OperationType type, TreeNodeDB node, String details) {
        this.type = type;
        this.node = node;
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public TreeNodeDB getNode() {
        return node;
    }

    public void setNode(TreeNodeDB node) {
        this.node = node;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
