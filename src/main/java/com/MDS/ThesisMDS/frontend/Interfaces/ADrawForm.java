package com.MDS.ThesisMDS.frontend.Interfaces;

import com.MDS.ThesisMDS.frontend.events.*;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

public abstract class ADrawForm extends VerticalLayout implements IDrawForm {
    @EventBusListenerMethod
    abstract public void onFindEvent(FindNodeEvent fndNode);

    @EventBusListenerMethod
    abstract public void OnBulkLoading(BulkLoadingEvent bulkLoadingEvent);

    @EventBusListenerMethod
    abstract public void onAddEvent(AddNodeEvent addNode);

    @EventBusListenerMethod
    abstract public void onEditEvent(EditNodeEvent node);

    @EventBusListenerMethod
    abstract public void onDeleteEvent(DeleteNodeEvent node);

    @EventBusListenerMethod
    abstract public void onCloseForm(CloseFormEvent event);
}
