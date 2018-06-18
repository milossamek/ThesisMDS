package com.MDS.ThesisMDS.backend.implementation.interfaces;

import com.MDS.ThesisMDS.backend.events.EventsBuffer;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;

public interface IAnimatable {
    EventsBuffer getBuffer();

    void addDraw(TreeNode node);

    void addEvent(TreeNode node);

    void clearEventBuffer();

    void clearDrawingBuffer();
}
