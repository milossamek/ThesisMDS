package com.MDS.ThesisMDS.backend.types;

public enum TreeNodeType {
    ntRoot("root"),
    ntLeftSon("left son"),
    ntRightSon("right son"),
    ntFinish("finish"),
    ntNW("north West"),
    ntNE("north East"),
    ntSW("south West"),
    ntSE("south East"),
    ntParent("parent"),
    ntRemove("remove");


    private String name;
    TreeNodeType(String name) {
        this.name = name;
    }

    public static TreeNodeType getQuadNodeType(Integer value) {
        switch (value) {
            case 0:
                return ntNW;
            case 1:
                return ntNE;
            case 2:
                return ntSW;
            case 3:
                return ntSE;

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
