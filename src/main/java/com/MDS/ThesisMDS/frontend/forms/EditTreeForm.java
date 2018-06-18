package com.MDS.ThesisMDS.frontend.forms;

import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.database.services.TreeDBService;
import com.MDS.ThesisMDS.backend.database.services.TreeNodeDBService;
import com.MDS.ThesisMDS.frontend.Interfaces.IPopupForm;
import com.MDS.ThesisMDS.frontend.designs.EditTreeDesign;
import com.MDS.ThesisMDS.frontend.events.CloseFormEvent;
import com.MDS.ThesisMDS.frontend.events.DeleteNodeEvent;
import com.MDS.ThesisMDS.frontend.events.FindNodeEvent;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.ItemClickListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;

@SpringComponent
@UIScope
@Scope("prototype")
public class EditTreeForm extends EditTreeDesign implements IPopupForm {
    public static final String buttonAdd = "btnAdd";
    public static final String buttonDelete = "btnDelete";
    public static final String buttonFind = "btnFind";
    public static final String buttonEdit = "btnEdit";
    @Autowired
    EventBus.UIEventBus eventBus;
    @Autowired
    TreeNodeDBService treeNodeDBService;
    @Autowired
    TreeDBService treeDBService;
    @Autowired
    CreateNodeForm nodeForm = new CreateNodeForm();
    private Boolean closedByEvent;
    private boolean isOpen;
    ListDataProvider<TreeNodeDB> dataProvider;

    private PopupForm popupForm;


    public EditTreeForm() {
        super();
        popupForm = new PopupForm("Edit tree");
        popupForm.addComponent(this);
        setupForm();
        addListeners();
        popupForm.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent closeEvent) {
                if (!closedByEvent) {
                    eventBus.publish(EventScope.UI, this, new CloseFormEvent());
                }
            }
        });
        this.isOpen = false;
    }

    public void setTree(TreeDB tree) {
        dataProvider = DataProvider.ofCollection(treeNodeDBService.findAllByTreeWithoutDeletedAndNull(tree));
        gridNodes.setDataProvider(dataProvider);
    }

    private void addListeners() {
        gridNodes.addItemClickListener(new ItemClickListener<TreeNodeDB>() {
            @Override
            public void itemClick(Grid.ItemClick<TreeNodeDB> event) {

            }
        });

        btnAdd.addClickListener((Button.ClickListener) event -> add(event));
        btnEdit.addClickListener((Button.ClickListener) event -> edit(event));
        btnDelete.addClickListener((Button.ClickListener) event -> delete(event));
        btnFind.addClickListener((Button.ClickListener) event -> find(event));
        btnCancel.addClickListener((Button.ClickListener) event -> close(event));
    }

    public void setVisibleButtons(String[] buttons) {
        btnAdd.setVisible(false);
        btnDelete.setVisible(false);
        btnFind.setVisible(false);
        btnEdit.setVisible(false);


        for (String button : buttons) {
            switch (button) {
                case buttonAdd:
                    btnAdd.setVisible(true);
                    break;
                case buttonDelete:
                    btnDelete.setVisible(true);
                    break;
                case buttonFind:
                    btnFind.setVisible(true);
                    break;
                case buttonEdit:
                    btnEdit.setVisible(true);
                    break;
            }
        }
    }

    private void find(Button.ClickEvent event) {
        if (selectNode()) {
            closedByEvent = true;
            eventBus.publish(EventScope.UI, this, new FindNodeEvent(gridNodes.getSelectedItems().iterator().next()));
        }
    }

    private void add(Button.ClickEvent event) {
        closedByEvent = true;
        nodeForm.setTree(dataProvider);
        nodeForm.openInModalPopup();
    }

    private void delete(Button.ClickEvent event) {
        if (selectNode()) {
            closedByEvent = true;
            eventBus.publish(EventScope.UI, this, new DeleteNodeEvent(gridNodes.getSelectedItems().iterator().next()));
        }
    }

    private void edit(Button.ClickEvent event) {
        if (selectNode()) {
            nodeForm.setNode(gridNodes.getSelectedItems().iterator().next());
            nodeForm.setTree(dataProvider);
            nodeForm.openInModalPopup();
        }
    }

    private void close(Button.ClickEvent event) {
        eventBus.publish(EventScope.UI, this, new CloseFormEvent());
        closePopup();
    }

    private boolean selectNode() {
        if (!gridNodes.getSelectedItems().iterator().hasNext()) {
            Notification.show("Please select node");
            return false;
        } else return true;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    private void setupForm() {
        gridNodes.removeColumn("tree");
    }

    @Override
    public Window openInModalPopup() {
        closedByEvent = false;
        isOpen = true;
        return popupForm.openInModalPopup();
    }

    @Override
    public void closePopup() {
        isOpen = false;
        popupForm.closePopup();
    }
}
