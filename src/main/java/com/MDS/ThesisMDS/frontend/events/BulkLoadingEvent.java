package com.MDS.ThesisMDS.frontend.events;

import com.MDS.ThesisMDS.backend.types.BulkEventType;

import java.io.Serializable;

public class BulkLoadingEvent implements Serializable {

    private final BulkEventType event;
    private final Integer _bulkDataSet;

    public BulkLoadingEvent(BulkEventType event, Integer dataset) {
        this.event = event;
        this._bulkDataSet = dataset;
    }

    public BulkEventType getEventType() {
        return event;
    }

    public Integer getDataSet() {
        return _bulkDataSet;
    }
}
