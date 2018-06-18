package com.MDS.ThesisMDS.frontend.forms;

import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.database.services.TreeDBService;
import com.MDS.ThesisMDS.backend.database.services.TreeNodeDBService;
import com.MDS.ThesisMDS.frontend.Interfaces.IPopupForm;
import com.MDS.ThesisMDS.frontend.designs.TreeDetailDesign;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@SpringComponent
@UIScope
@Scope("prototype")
public class TreeDetailForm extends TreeDetailDesign implements IPopupForm {
    @Autowired
    private TreeDBService treeDBService;
    @Autowired
    private TreeNodeDBService treeNodeDBService;

    private OperationsDB selectedOperation;

    private PopupForm popupForm;

    public TreeDetailForm() {
        super();
        addListeners();
        popupForm = new PopupForm("Bulk Loading ");
        popupForm.addComponent(this);
    }

    private void addListeners() {
        btnClose.addClickListener((Button.ClickListener) event -> closePopup());
    }

    public void setOperation(OperationsDB operation) {
        selectedOperation = operation;
        fillComponents();
    }

    private void fillComponents() {
        TreeNodeDB selectedNode = treeNodeDBService.findById(selectedOperation.getNode().getId());
        TreeDB selectedTree = treeDBService.findById(selectedNode.getTree().getId());


        lblId.setValue(Integer.toString(selectedOperation.getId()));
        lblOperation.setValue(selectedOperation.getType().getName());
        lblNode.setValue(selectedOperation.getNode().toString());
        lblDetail.setValue(selectedOperation.getDetails());

        lblNodeId.setValue(Integer.toString(selectedNode.getId()));
        String location = selectedNode.getLoc().x + ";" + selectedNode.getLoc().y;
        lblLocation.setValue(location);
        lblData.setValue(selectedNode.getData());
        lblTreeId.setValue(Integer.toString(selectedNode.getTree().getId()));

        lblTreeId2.setValue(Integer.toString(selectedTree.getId()));
        lblTreeType.setValue(selectedTree.getType().getName());
        lblNodeCount.setValue(Integer.toString(selectedTree.getNodeCount()));
        lblMaxLevel.setValue(Integer.toString(selectedTree.getMaxLevel()));

        ListDataProvider<TreeNodeDB> dataProvider = DataProvider.ofCollection(treeNodeDBService.findAllByTree(selectedTree));
        grid.setDataProvider(dataProvider);
    }

    @Override
    public Window openInModalPopup() {
        return popupForm.openInModalPopup();
    }

    @Override
    public void closePopup() {
        popupForm.closePopup();
    }
}
