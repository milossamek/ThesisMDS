package com.MDS.ThesisMDS.frontend.gwt.client.implemenation;

import org.vaadin.tltv.vprocjs.gwt.client.ui.ProcessingCodeBase;

public class EmptyProcessing extends ProcessingCodeBase {
    @Override
    public void setup() {
        pro.size(1024, 600);
        pro.background(255, 255, 255);
        pro.text("TREE IS EMPTY", 0, 0);
        super.setup();
    }

    @Override
    public void draw() {
        super.draw();
    }
}
