package com.MDS.ThesisMDS.frontend.gwt.client.objects;

public enum EventType {
    etBulkLoading,
    etBulkLoadingAnimated,
    etFind,
    etDelete,
    etAdd;


    public static EventType toEvent(String eventType) {
        switch (eventType) {
            case "etBulkLoading":
                return etBulkLoading;
            case "etBulkLoadingAnimated":
                return etBulkLoadingAnimated;
            case "etFind":
                return etFind;
            case "etDelete":
                return etDelete;
            case "etAdd":
                return etAdd;

        }
        return etBulkLoading;
    }
}
