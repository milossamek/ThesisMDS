package com.MDS.ThesisMDS.frontend.gwt.client.objects;

public enum NodeType {
    ntRoot("root"), ntLeftSon("left son"), ntRightSon("right son"), ntFinish("finish"), ntNW("north West"), ntNE("north East"), ntSW("south West"), ntSE("south East"), ntParent("parent"), ntRemove("remove");

    private String name;

    NodeType(String name) {
        this.name = name;
    }

    public static NodeType GetNodeTypeByString(String nodeType) {
        switch (nodeType) {
            case "ntRoot":
                return ntRoot;
            case "ntLeftSon":
                return ntLeftSon;
            case "ntRightSon":
                return ntRightSon;
            case "ntFinish":
                return ntFinish;
            case "ntNW":
                return ntNW;
            case "ntNE":
                return ntNE;
            case "ntSE":
                return ntSE;
            case "ntSW":
                return ntSW;
            case "ntParent":
                return ntParent;
            case "ntRemove":
                return ntRemove;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
