package com.MDS.ThesisMDS.frontend.views.orderings;

import com.MDS.ThesisMDS.backend.implementation.spaceFillingAlg.PeanCurveV2;
import com.MDS.ThesisMDS.frontend.components.LinearOrderingDrawComponent;
import com.MDS.ThesisMDS.frontend.events.CloseFormEvent;
import com.MDS.ThesisMDS.frontend.forms.SelectedPointForm;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

@SpringComponent
@UIScope
public class PeanoView extends VerticalLayout implements View {
    private LinearOrderingDrawComponent drawView;
    private PeanCurveV2 curve;
    @Autowired
    private SelectedPointForm _selectedPoint;
    @Autowired
    private EventBus.UIEventBus _eventBus;

    public PeanoView() {
        super();
        this.curve = new PeanCurveV2(2);
    }

    @PostConstruct
    private void postConstruct() {
        drawView = new LinearOrderingDrawComponent(curve, "Peano", _selectedPoint);
        drawView.setMargin(false);
        addComponent(drawView);
    }


    private void setupView() {
        drawView.setupView();
    }

    @EventBusListenerMethod
    public void onCloseFormPointForm(CloseFormEvent event) {
        drawView.clearPoint();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setupView();
    }
}
