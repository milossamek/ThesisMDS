package com.MDS.ThesisMDS.frontend.events;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;

import java.io.Serializable;

public class DeleteNodeEvent implements Serializable {
    private final TreeNodeDB node;

    public DeleteNodeEvent(TreeNodeDB p) {
        this.node = p;
    }

    public TreeNodeDB getNode() {
        return node;
    }
}
