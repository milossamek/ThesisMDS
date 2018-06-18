package com.MDS.ThesisMDS.frontend.forms;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.types.NodeSortType;
import com.MDS.ThesisMDS.frontend.Interfaces.IPopupForm;
import com.MDS.ThesisMDS.frontend.designs.CreateNodeDesign;
import com.MDS.ThesisMDS.frontend.events.AddNodeEvent;
import com.MDS.ThesisMDS.frontend.events.EditNodeEvent;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;

@SpringComponent
@UIScope
@Scope("prototype")
public class CreateNodeForm extends CreateNodeDesign implements IPopupForm {
    @Autowired
    EventBus.UIEventBus eventBus;
    private PopupForm popupForm;
    private TreeNodeDB node;
    private boolean isEditForm;
    private ListDataProvider<TreeNodeDB> tree;

    public CreateNodeForm() {
        super();
        this.popupForm = new PopupForm("Create node");
        popupForm.addComponent(this);
        addListeners();
        this.isEditForm = false;
    }

    private void addListeners() {
        btnOK.addClickListener((Button.ClickListener) event -> add(event));
        btnCancel.addClickListener((Button.ClickListener) event -> cancel(event));
    }

    private void add(Button.ClickEvent event) {
        Integer x = 0;
        Integer y = 0;
        try {
            x = Integer.parseInt(edtX.getValue());
            y = Integer.parseInt(edtY.getValue());

            if (edtData.getValue().length() > 20) {
                Notification.show("Maximum size of data caption is 20 characters, please set shorter name");
                return;
            }

            node = new TreeNodeDB(new ExtendedPoint(x, y), edtData.getValue(), null, NodeSortType.stX);
            if (node != null) {
                if (isEditForm) eventBus.publish(EventScope.UI, this, new EditNodeEvent(node));
                else {
                    for (TreeNodeDB treeNodeDB : tree.getItems()) {
                        if (treeNodeDB.getLoc().getX() == x && treeNodeDB.getLoc().getY() == y){
                            node = null;
                            Notification.show("Selected key: " + x + ";" + y+ " already exist in structure: " + treeNodeDB.getData());
                            return;
                        }
                    }
                    eventBus.publish(EventScope.UI, this, new AddNodeEvent(node));
                }
                closePopup();
            }
            node = null;
        } catch (NumberFormatException nfe) {
            node = null;
            Notification.show("Data are not valid");
        }

    }

    private void cancel(Button.ClickEvent event) {
        closePopup();
    }

    public PopupForm getPopupForm() {
        return popupForm;
    }

    public void setPopupForm(PopupForm popupForm) {
        this.popupForm = popupForm;
    }

    public TreeNodeDB getNode() {
        return node;
    }

    public void setNode(TreeNodeDB node) {
        isEditForm = true;
        this.node = node;
    }

    public void setTree(ListDataProvider<TreeNodeDB> tree) {
        this.tree = tree;
    }

    private void fillForm() {
        if (node == null || isEditForm) {
            edtX.setValue("");
            edtY.setValue("");
            edtData.setValue("");
            return;
        }


        edtX.setValue(Double.toString(node.getLoc().getX()));
        edtY.setValue(Double.toString(node.getLoc().getX()));
        edtData.setValue(node.getData());
    }

    @Override
    public Window openInModalPopup() {
        fillForm();
        return popupForm.openInModalPopup();
    }

    @Override
    public void closePopup() {
        isEditForm = false;
        popupForm.closePopup();
    }
}
