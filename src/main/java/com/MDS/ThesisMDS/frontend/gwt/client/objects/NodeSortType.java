package com.MDS.ThesisMDS.frontend.gwt.client.objects;

public enum NodeSortType {
    stX("stX"), stY("stY");

    private String name;

    NodeSortType(String name) {
        this.name = name;
    }

    public static NodeSortType GetNodeTypeByString(String nodeType) {
        switch (nodeType) {
            case "stX":
                return stX;
            case "stY":
                return stY;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
