package com.MDS.ThesisMDS.frontend.components;

import com.MDS.ThesisMDS.MyVaadinUI;
import com.MDS.ThesisMDS.backend.database.Database;
import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.implementation.interfaces.ITreeImplementation;
import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.OperationType;
import com.MDS.ThesisMDS.backend.types.TreeType;
import com.MDS.ThesisMDS.frontend.Interfaces.ADrawForm;
import com.MDS.ThesisMDS.frontend.events.*;
import com.MDS.ThesisMDS.frontend.forms.BulkLoadingForm;
import com.MDS.ThesisMDS.frontend.forms.EditTreeForm;
import com.MDS.ThesisMDS.frontend.gwt.client.implemenation.EmptyProcessing;
import com.MDS.ThesisMDS.frontend.gwt.client.objects.EventType;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import org.vaadin.tltv.vprocjs.component.Processing;

import java.util.List;


/*

Stara se o handlovaní stromů mezi java scriptem. Předavání infa + databaze

 */

public class DataStructureDrawComponent extends ADrawForm {
    private BulkLoadingForm _bulkForm;
    private EditTreeForm _editForm;
    private Database database;

    private ITreeImplementation tree;

    private Panel layoutPanel;
    private Button controlButton;
    private DrawProcessingExtensionComponent codeExtension;
    private Processing processing;
    private OperationsDB lastOperation;
    private OperationsDB operation;
    private ContextMenu contextMenu;
    private TreeDB databaseTree;
    private TreeType treeType;
    private String processingClass;
    private Boolean operationDrawed;

    public DataStructureDrawComponent(BulkLoadingForm _bulkForm, EditTreeForm _editForm, Database database, ITreeImplementation tree, TreeType treeType, String processingClass) {
        super();
        this.setMargin(false);
        this._bulkForm = _bulkForm;
        this._editForm = _editForm;
        this.database = database;
        this.tree = tree;
        this.treeType = treeType;
        this.processingClass = processingClass;
        this.operationDrawed = false;
        setupView();
        createInstances();
        addListeners();
    }

    private void reset() {
        codeExtension.setProcessingJavaCodeClass(EmptyProcessing.class.getName());
    }

    private void createInstances() {
        lastOperation = null;
    }

    private void addListeners() {
        controlButton.addClickListener((Button.ClickListener) event -> contextMenu.open(event.getClientX(), event.getClientY()));
        addContextMenuItems(contextMenu);
    }

    private void bulkLoading(EventType type, Integer aDataset) {
        refresh();
        tree.bulkLoad(aDataset);
        codeExtension.set_event(null);
        codeExtension.set_eventType(type);
        startBuilding(type, false);
        setProcessing();
    }

    private void startBuilding(EventType type, Boolean fromDatabase) {
        codeExtension.set_tree(tree.getTree());

        if (!fromDatabase) {
            TreeDB tree = new TreeDB(this.tree.getTree().size(), treeType, 0);
            database.addOperation(new OperationsDB(OperationType.otBulkLoading, this.tree.getBasicNodes().get(0), "Bulk loading from dataset"), tree, this.tree.getBasicNodes());
            databaseTree = tree;
        } else {
            databaseTree = operation.getNode().getTree();
        }
    }


    private void setupView() {
        setMargin(new MarginInfo(false, false, false, false));
        layoutPanel = new Panel();
        layoutPanel.setSizeFull();

        processing = new Processing();
        processing.setSizeUndefined();
        codeExtension = new DrawProcessingExtensionComponent();
        codeExtension.extend(processing);

        layoutPanel.setContent(processing);
        addComponent(layoutPanel);

        controlButton = new com.vaadin.ui.Button("Tree actions");
        contextMenu = new ContextMenu(controlButton, false);
        contextMenu.setAsContextMenuOf(controlButton);
        addComponent(controlButton);
    }

    private void addContextMenuItems(ContextMenu menu) {
        menu.addItem("Bulk Loading", e -> {
            reset();
            _bulkForm.openInModalPopup();
        });

        menu.addItem("Edit Tree", e -> {
            reset();
            _editForm.setVisibleButtons(new String[]{EditTreeForm.buttonFind, EditTreeForm.buttonAdd, EditTreeForm.buttonDelete});
            _editForm.setTree(databaseTree);
            _editForm.openInModalPopup();
        });


        menu.addItem("Clear Tree", e -> {
            refresh();
            tree.clear();
            databaseTree = null;
            reset();

        });


    }

    private void drawOperation() {
        if (operation == null) {
            reset();
            return;
        }

        TreeDB tree = operation.getNode().getTree();
        List<TreeNodeDB> nodes = database.findAllByTree(tree);
        this.tree.build(nodes);

        switch (operation.getType()) {

            case otBulkLoading:
                startBuilding(EventType.etBulkLoading, true);
                codeExtension.set_event(null);
                codeExtension.set_eventType(EventType.etBulkLoading);
                if (!codeExtension.getLastProcessingJavaClass().equals(processingClass)) {
                    codeExtension.setProcessingJavaCodeClass(processingClass);
                }
                break;
            case otBulkLoadingAnimated:
                startBuilding(EventType.etBulkLoadingAnimated, true);
                TreeNodeDB test1 = new TreeNodeDB(new ExtendedPoint(0, 1), "test");
                this.tree.findNode(test1.getLoc());
                codeExtension.set_event(null);
                codeExtension.set_eventType(EventType.etBulkLoadingAnimated);
                if (!codeExtension.getLastProcessingJavaClass().equals(processingClass)) {
                    codeExtension.setProcessingJavaCodeClass(processingClass);
                }
                break;
            case otInsert:
                startBuilding(EventType.etBulkLoading, true);
                onAddEvent(new AddNodeEvent(operation.getNode()));
                break;
            case otDelete:
                startBuilding(EventType.etBulkLoading, true);
                onDeleteEvent(new DeleteNodeEvent(operation.getNode()));
                break;
            case otSearch:
                startBuilding(EventType.etBulkLoading, true);
                onFindEvent(new FindNodeEvent(operation.getNode()));
                break;
        }

        operation=null;
    }

    public void setTree(ITreeImplementation tree) {
        this.tree = tree;
    }

    @Override
    public void onFindEvent(FindNodeEvent fndNode) {
        _editForm.closePopup();
        if (tree != null) {
            tree.findNode(fndNode.getNode().getLoc());
            prepareProcessing(EventType.etFind, tree.getEvent(), tree.getTree());
            if (operation == null) {
                OperationsDB operation = new OperationsDB(OperationType.otSearch, fndNode.getNode(), "Searching node in tree");
                database.addOperationDB(operation, databaseTree, database.findAllByTree(databaseTree));
            }
        }
        setProcessing();
    }

    @Override
    public void OnBulkLoading(BulkLoadingEvent bulkLoadingEvent) {
        _bulkForm.closePopup();
        switch (bulkLoadingEvent.getEventType()) {
            case blAnimate:
                bulkLoading(EventType.etBulkLoadingAnimated, bulkLoadingEvent.getDataSet());
                break;
            case blInstant:
                bulkLoading(EventType.etBulkLoading, bulkLoadingEvent.getDataSet());
                break;
            case blNone:
                refresh();
        }
    }

    @Override
    public void onAddEvent(AddNodeEvent addNode) {
        if (ClosePopUp()) return;
        tree.addNode(addNode.getNode());

        prepareProcessing(EventType.etAdd, tree.getEvent(), tree.getTree());
        codeExtension.set_tree(tree.getTree());

        if (databaseTree == null) {
            databaseTree = database.createTree(tree.getTree().size(), treeType, 0);
        }
        database.addNodeToTree(addNode.getNode(), databaseTree);
        if (lastOperation == null) {
            createOperation(OperationType.otInsert, addNode.getNode(), "Adding node  to the tree");
        }
        lastOperation.getNode().setTree(databaseTree);
        setProcessing();
    }

    @Override
    public void onEditEvent(EditNodeEvent node) {

    }

    @Override
    public void onDeleteEvent(DeleteNodeEvent node) {
        if (ClosePopUp()) return;
        tree.removeNode(node.getNode().loc);
        prepareProcessing(EventType.etDelete, tree.getEvent(), tree.getTree());

        if (databaseTree == null) {
            databaseTree = database.createTree(this.tree.getTree().size(), treeType, 0);
        }

        node.getNode().setDeleted(true);
        database.addNodeToTree(node.getNode(), databaseTree);
        if (lastOperation == null) {
            createOperation(OperationType.otDelete, node.getNode(), "Removing node from the tree");
        }
        lastOperation.getNode().setTree(databaseTree);

        setProcessing();
    }

    @Override
    public void onCloseForm(CloseFormEvent event) {
        if (tree.getTree() != null && tree.getTree().size() > 0) {
            codeExtension.set_tree(tree.getTree());
            codeExtension.set_eventType(EventType.etBulkLoading);
            codeExtension.set_event(null);
            setProcessing();
        } else reset();
    }

    @Override
    public void setOperation(OperationsDB operation) {
        this.operation = operation;
        operationDrawed = false;
    }

    @Override
    public void refresh() {
        MyVaadinUI myUI = (MyVaadinUI) MyVaadinUI.getCurrent();
        codeExtension.set_width(myUI.get_remainingWidth());
        codeExtension.set_height(myUI.get_remaingHeight());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        refresh();
        drawOperation();
        super.enter(event);
    }

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        reset();
        super.beforeLeave(event);
    }

    private boolean ClosePopUp() {
        if (_editForm.isOpen()) _editForm.closePopup();
        if (tree == null) return true;
        return false;
    }

    private void prepareProcessing(EventType eventType, List<TreeNode> eventList, List<TreeNode> tree) {
        codeExtension.set_eventType(eventType);
        codeExtension.set_event(eventList);
        codeExtension.set_tree(tree);
    }

    private void createOperation(OperationType type, TreeNodeDB node, String title) {
        lastOperation = new OperationsDB(type, node, title);
        database.addOperationDB(lastOperation, databaseTree, database.findAllByTree(databaseTree));
    }

    private void setProcessing() {
        codeExtension.setProcessingJavaCodeClass(processingClass);
    }
}
