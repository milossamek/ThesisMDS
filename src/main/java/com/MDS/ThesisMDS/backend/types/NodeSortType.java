package com.MDS.ThesisMDS.backend.types;

public enum NodeSortType {
    stX("stX"),
    stY("stY");

    private String name;

    NodeSortType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
