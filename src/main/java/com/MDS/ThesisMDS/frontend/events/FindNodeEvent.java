package com.MDS.ThesisMDS.frontend.events;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;

import java.io.Serializable;

public class FindNodeEvent implements Serializable {

    private final TreeNodeDB node;

    public FindNodeEvent(TreeNodeDB p) {
        this.node = p;
    }

    public TreeNodeDB getNode() {
        return node;
    }
}
