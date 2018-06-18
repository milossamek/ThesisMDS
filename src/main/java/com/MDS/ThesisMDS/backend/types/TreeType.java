package com.MDS.ThesisMDS.backend.types;

public enum TreeType {
    ttPrioritySearchTree("Priority search tree"),
    ttQuadTree("Quad tree - Trie based"),
    ttRangeTree("Range tree"),
    ttQuadTreePoint("Quad tree - Point based");

    private String name;

    TreeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
