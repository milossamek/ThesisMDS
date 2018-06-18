package com.MDS.ThesisMDS.frontend.views.tree;

import com.MDS.ThesisMDS.MyVaadinUI;
import com.MDS.ThesisMDS.backend.database.Database;
import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.MDS.ThesisMDS.backend.implementation.PSTImplementation;
import com.MDS.ThesisMDS.backend.implementation.interfaces.ITreeImplementation;
import com.MDS.ThesisMDS.backend.types.TreeType;
import com.MDS.ThesisMDS.frontend.Interfaces.ADrawForm;
import com.MDS.ThesisMDS.frontend.components.DataStructureDrawComponent;
import com.MDS.ThesisMDS.frontend.events.*;
import com.MDS.ThesisMDS.frontend.forms.BulkLoadingForm;
import com.MDS.ThesisMDS.frontend.forms.EditTreeForm;
import com.MDS.ThesisMDS.frontend.gwt.client.implemenation.PSTProcessing;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

/**
 * Sample to show how the Processing component can be used to create Processing
 * sketch by its own Java API.
 *
 * @author Tltv
 */
@SpringComponent
@UIScope
public class PSTView extends ADrawForm {
    private DataStructureDrawComponent dataStructureDrawComponent;
    @Autowired
    private EventBus.UIEventBus _eventBus;
    @Qualifier("PST")
    private ITreeImplementation tree;
    @Autowired
    private BulkLoadingForm _bulkForm;
    @Autowired
    private EditTreeForm _editForm;
    @Autowired
    private Database database;

    public PSTView() {
        super();
        this.setMargin(false);
        tree = new PSTImplementation();
    }

    @PostConstruct
    private void postConstruct() {
        dataStructureDrawComponent = new DataStructureDrawComponent(_bulkForm, _editForm, database, tree, TreeType.ttPrioritySearchTree, PSTProcessing.class.getName());
        dataStructureDrawComponent.setMargin(false);
        addComponent(dataStructureDrawComponent);
    }

    @Override
    public void onFindEvent(FindNodeEvent fndNode) {
        dataStructureDrawComponent.onFindEvent(fndNode);
    }

    @Override
    public void OnBulkLoading(BulkLoadingEvent bulkLoadingEvent) {
        dataStructureDrawComponent.OnBulkLoading(bulkLoadingEvent);
    }

    @Override
    public void onAddEvent(AddNodeEvent addNode) {
        dataStructureDrawComponent.onAddEvent(addNode);
    }

    @Override
    public void onEditEvent(EditNodeEvent node) {
        dataStructureDrawComponent.onEditEvent(node);
    }

    @Override
    public void onDeleteEvent(DeleteNodeEvent node) {
        dataStructureDrawComponent.onDeleteEvent(node);
    }

    @Override
    public void onCloseForm(CloseFormEvent event) {
        dataStructureDrawComponent.onCloseForm(event);
    }

    @Override
    public void setOperation(OperationsDB operation) {
        dataStructureDrawComponent.setOperation(operation);
    }

    @Override
    public void refresh() {
        dataStructureDrawComponent.refresh();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        dataStructureDrawComponent.enter(event);
        super.enter(event);
    }

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        if (((MyVaadinUI) MyVaadinUI.getCurrent()).getLastView() != null && ((MyVaadinUI) MyVaadinUI.getCurrent()).getLastView().getClass().equals(PSTView.class)) {
            dataStructureDrawComponent.beforeLeave(event);
        }
    }
}
