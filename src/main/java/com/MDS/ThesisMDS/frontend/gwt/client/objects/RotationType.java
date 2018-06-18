package com.MDS.ThesisMDS.frontend.gwt.client.objects;

public enum RotationType {
    rtLeftRotation,
    rtRightRotation,
    rtNoRotation;

    public static RotationType toEvent(String eventType) {
        switch (eventType) {
            case "rtLeftRotation":
                return rtLeftRotation;
            case "rtRightRotation":
                return rtRightRotation;
            case "rtNoRotation":
                return rtNoRotation;

        }
        return rtNoRotation;
    }


}
