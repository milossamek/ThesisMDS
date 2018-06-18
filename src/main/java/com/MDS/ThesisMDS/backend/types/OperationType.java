package com.MDS.ThesisMDS.backend.types;

public enum OperationType {
    otBulkLoading("Bulk loading"),
    otBulkLoadingAnimated("Bulk loading animated"),
    otInsert("Insert"),
    otDelete("Delete"),
    otSearch("Search");

    private String name;

    OperationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


