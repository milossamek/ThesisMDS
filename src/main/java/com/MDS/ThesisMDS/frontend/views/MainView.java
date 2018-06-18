package com.MDS.ThesisMDS.frontend.views;

import com.MDS.ThesisMDS.MyVaadinUI;
import com.MDS.ThesisMDS.backend.database.Database;
import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.frontend.designs.MainDesign;
import com.MDS.ThesisMDS.frontend.forms.TreeDetailForm;
import com.MDS.ThesisMDS.frontend.views.tree.PSTView;
import com.MDS.ThesisMDS.frontend.views.tree.PointQuadView;
import com.MDS.ThesisMDS.frontend.views.tree.RangeView;
import com.MDS.ThesisMDS.frontend.views.tree.TrieQuadView;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringComponent
@UIScope
public class MainView extends MainDesign implements View {
    private static final int MAXSHOWROWS = 7;
    @Autowired
    private Database database;
    @Autowired
    private TreeDetailForm detailForm;
    @Autowired
    private PSTView pstView;
    @Autowired
    private RangeView rangeView;
    @Autowired
    private TrieQuadView trieQuadView;
    @Autowired
    private PointQuadView pointQuadView;

    private Window win;

    public MainView() {
        super();
        setupView();
        createInstances();
        addListeners();
    }


    private void setupView() {
        gridOperations.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridOperations.addColumn(a -> VaadinIcons.PENCIL.getHtml(), new HtmlRenderer()).setCaption("Show details").setSortable(false).setId("showDetails");
        gridOperations.addColumn(a -> VaadinIcons.TRASH.getHtml(), new HtmlRenderer()).setCaption("Remove").setSortable(false).setId("deleteOperation");
    }

    private void createInstances() {
        win = new Window();
    }

    private void addListeners() {
        gridOperations.addItemClickListener((ItemClickListener<OperationsDB>) event -> {
            switch (event.getColumn().getId()) {
                case "showDetails":
                    showOperationDetail(event.getItem());
                    break;
                case "deleteOperation":
                    askDeleteOperation(event.getItem());
                    break;
            }
        });

        btnAction.addClickListener((Button.ClickListener) event -> fireEvent(event));
        btnPST.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.PSTREE));
        btnRange.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.RANGETREE));
        btnQuad.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.TRIEQUADTREE));
        btnPointQuad.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.POINTQUADTREE));
        btnHilbert.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.HILBERTVIEW));
        btnZORder.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.ZORDERVIEW));
        btnPeano.addClickListener((Button.ClickListener) event -> ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.PEANOVIEW));
    }

    private void askDeleteOperation(OperationsDB operation) {
        String question = "Do you really want to remove operation : " + operation.getType().getName() + " with id :" + operation.getId();

        ConfirmDialog.show(MyVaadinUI.getCurrent(), "Please Confirm remove operation",
                question,
                "Yes", "No",
                (ConfirmDialog.Listener) dialog -> {
                    if (dialog.isConfirmed()) {
                        database.deleteOperation(operation);
                        Notification.show("Operation with id: " + operation.getId() + " has been deleted", Notification.Type.TRAY_NOTIFICATION);
                        fillGrid();
                    } else {
                        Notification.show("Canceled", Notification.Type.TRAY_NOTIFICATION);
                    }
                });
    }

    private void fireEvent(Button.ClickEvent event) {
        if (gridOperations.getSelectedItems().iterator().hasNext()) {
            OperationsDB operation = gridOperations.getSelectedItems().iterator().next();
            TreeDB tree = operation.getNode().getTree();


            switch (tree.getType()) {
                case ttPrioritySearchTree:
                    pstView.setOperation(operation);
                    ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.PSTREE);
                    break;
                case ttQuadTree:
                    trieQuadView.setOperation(operation);
                    ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.TRIEQUADTREE);
                    break;
                case ttRangeTree:
                    rangeView.setOperation(operation);
                    ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.RANGETREE);
                    break;
                case ttQuadTreePoint:
                    pointQuadView.setOperation(operation);
                    ((MyVaadinUI) MyVaadinUI.getCurrent()).changeView(MyVaadinUI.POINTQUADTREE);
                    break;
            }


        } else Notification.show("Please select operation", Notification.Type.ERROR_MESSAGE);
    }

    @PostConstruct
    private void fillGrid() {
        List<OperationsDB> allOperations = database.findAllOperations();
        ListDataProvider<OperationsDB> dataProvider = DataProvider.ofCollection(allOperations);
        gridOperations.setDataProvider(dataProvider);
        gridOperations.setVisible(allOperations.size() > 0);
        btnAction.setVisible(allOperations.size() > 0);
        lblOperations.setVisible(allOperations.size() > 0);
        if (gridOperations.isVisible()) {
            gridOperations.setHeightMode(HeightMode.ROW);
            gridOperations.setHeightByRows(allOperations.size() < MAXSHOWROWS ? allOperations.size() : MAXSHOWROWS);
        }
    }


    private void showOperationDetail(OperationsDB operation) {
        detailForm.setOperation(operation);
        detailForm.openInModalPopup();


    }

    private void deleteSelectedOperation(OperationsDB operation) {

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        fillGrid();
    }
}
