package com.MDS.ThesisMDS.frontend.components;

import com.MDS.ThesisMDS.MyVaadinUI;
import com.MDS.ThesisMDS.backend.implementation.interfaces.ILinearOrdering;
import com.MDS.ThesisMDS.backend.implementation.objects.LinearPoint;
import com.MDS.ThesisMDS.frontend.events.CloseFormEvent;
import com.MDS.ThesisMDS.frontend.forms.SelectedPointForm;
import com.MDS.ThesisMDS.frontend.gwt.client.implemenation.PointProcessing;
import com.vaadin.contextmenu.ContextMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.hezamu.canvas.Canvas;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.tltv.vprocjs.component.Processing;

import java.awt.*;
import java.util.ArrayList;

public class LinearOrderingDrawComponent extends VerticalLayout implements View {
    private ILinearOrdering ordering;
    private SelectedPointForm selectedPointForm;

    private Integer width;
    private Integer height;
    private Integer square;
    private Integer dimension = 1;

    private DrawProcessingExtensionComponent codeExtension;
    private Canvas canvas;
    private Processing processing;

    private Button buttonDraw;
    private ContextMenu contextMenu;
    private com.vaadin.contextmenu.MenuItem lastMenuItem;

    private ArrayList<LinearPoint> _actualPointOrdering;
    private LinearPoint lastPoint = null;
    private Boolean selectionMode;

    public LinearOrderingDrawComponent(ILinearOrdering ordering, String name, SelectedPointForm selectedPointForm) {
        super();
        this.ordering = ordering;
        this.selectedPointForm = selectedPointForm;
        this.selectionMode = false;
        createInstances();
        addListeners();
        addContextMenuItems(contextMenu, name);

    }


    public void setupView() {
        MyVaadinUI myUI = (MyVaadinUI) MyVaadinUI.getCurrent();

        width = myUI.get_remainingWidth() - (myUI.get_remaingHeight() / 100) * 1;
        height = myUI.get_remaingHeight() - (myUI.get_remaingHeight() / 100) * 10;
        canvas.setWidth(width.toString());
        canvas.setHeight(height.toString());

        if (myUI.get_remainingWidth() < myUI.get_remaingHeight()) {
            square = myUI.get_remainingWidth() - (myUI.get_remaingHeight() / 100) * 1;
        } else {
            square = myUI.get_remaingHeight() - (myUI.get_remaingHeight() / 100) * 10;
        }
    }


    private void drawBorder(int bits, int aWidth, int aHeight, int margin) {
        int lineSize = 10;
        int n = 1 << bits;
        int cellSize = (aWidth - 2 * margin) / (n);
        canvas.setFillStyle(0, 0, 0);
        canvas.setFont("arial");


        canvas.beginPath();
        canvas.moveTo(margin + cellSize / 2, 0);
        canvas.lineTo(margin + cellSize / 2, lineSize);
        canvas.fillText(String.valueOf(0) + " ", margin + cellSize / 2 + lineSize / 2, lineSize * 1.5, aWidth);

        canvas.moveTo(0, margin + cellSize / 2);
        canvas.lineTo(lineSize, margin + cellSize / 2);
        canvas.fillText(String.valueOf(0) + " ", lineSize * 1.5, margin + cellSize / 2, aWidth);
        for (long i = 0; i < n; i++) {
            int x = (int) Math.round((double) i / (n - 1) * (aWidth - 2 * margin - cellSize) + margin) + cellSize / 2;
            canvas.moveTo(x, 0);
            canvas.lineTo(x, lineSize);
            canvas.fillText(String.valueOf(i) + " ", x + lineSize / 2, lineSize * 1.5, aWidth);

            int y = (int) Math.round((double) i / (n - 1) * (aHeight - 2 * margin - cellSize) + margin) + cellSize / 2;
            canvas.moveTo(0, y);
            canvas.lineTo(lineSize, y);
            canvas.fillText(String.valueOf(i) + " ", lineSize * 1.5, y, aWidth);
        }

        canvas.stroke();
    }

    private void createInstances() {
        canvas = new Canvas();
        buttonDraw = new Button("Action");


        addComponent(canvas);
        processing = new Processing();
        codeExtension = new DrawProcessingExtensionComponent();
        codeExtension.extend(processing);
        addComponent(processing);

        HorizontalLayout layout = new HorizontalLayout();
        contextMenu = new ContextMenu(buttonDraw, false);
        contextMenu.setAsContextMenuOf(buttonDraw);
        layout.addComponent(buttonDraw);
        addComponent(layout);

        processing.setVisible(false);
    }

    private Integer setDimensionMenu(com.vaadin.contextmenu.MenuItem button) {
        selectionMode = false;
        if (lastMenuItem == button) {
            dimension += 1;
            if (dimension > 4) {
                dimension = 1;
                return 1;
            }
            return dimension;
        }
        lastMenuItem = button;
        dimension = 1;
        return 1;
    }


    private void addContextMenuItems(ContextMenu menu, String orderingName) {
      /*  menu.addItem("Draw" + orderingName, e -> {
            processing.setVisible(false);
            canvas.setVisible(true);
            setupView();
            _actualPointOrdering = ordering.calculatePoints(width, height, setDimensionMenu(e), 10);
            DrawPoints(_actualPointOrdering, false, width, height, 10);
        });*/

        menu.addItem("Draw " + orderingName + " Square", e -> {
            processing.setVisible(false);
            canvas.setVisible(true);
            processing.setSizeUndefined();
            setupView();
            _actualPointOrdering = ordering.calculatePoints(square, square, setDimensionMenu(e), 10);
            DrawPoints(_actualPointOrdering, false, square, square, 10);
        });


       /* menu.addItem("Animate " + orderingName , e -> {
            Animated(width, height, e);

        });*/

        menu.addItem("Animate " + orderingName + " Square", e -> {
            Animated(square, square, e);
        });

        menu.addItem("Draw " + orderingName + " with selection", e -> {
            processing.setVisible(false);
            canvas.setVisible(true);
            processing.setSizeUndefined();
            setupView();
            _actualPointOrdering = ordering.calculatePoints(square, square, setDimensionMenu(e), 10);
            DrawPoints(_actualPointOrdering, true, square, square, 10);
            selectionMode = true;
        });

        menu.addItem("Reset", e -> {
            lastMenuItem = null;
            dimension = 1;
            processing.setVisible(false);
            canvas.setVisible(true);
            canvas.clear();
        });


    }


    private void addListeners() {
        buttonDraw.addClickListener((Button.ClickListener) event -> contextMenu.open(event.getClientX(), event.getClientY()));
        canvas.addMouseMoveListener(new Canvas.CanvasMouseMoveListener() {
            @Override
            public void onMove(MouseEventDetails mouseEventDetails) {
                synchronized (this) {
                    if (!selectionMode) return;
                    Point actualPoint = new Point(mouseEventDetails.getRelativeX(), mouseEventDetails.getRelativeY());
                    if (_actualPointOrdering == null) return;

                    LinearPoint drawPoint = inRange(_actualPointOrdering, actualPoint, 20);

                    if (drawPoint != lastPoint) {
                        DrawPoints(_actualPointOrdering, true, square, square, 10);
                        lastPoint = drawPoint;
                    }

                    if (drawPoint != null && canvas != null)
                        canvas.beginPath();
                    try {
                        canvas.setFillStyle("red");
                        canvas.fillRect(drawPoint.getDrawPoint().x, drawPoint.getDrawPoint().y, 10, 10);
                    } catch (Exception e) {
                        //Na prase
                    }

                    canvas.stroke();
                }
            }
        });


        canvas.addMouseDownListener(mouseEventDetails -> {
            if (lastPoint != null) {
                for (int i = 0; i < _actualPointOrdering.size(); i++) {
                    if (lastPoint.equals(_actualPointOrdering.get(i))) {
                        selectedPointForm.setIndex(i);
                        selectedPointForm.setPoint(lastPoint.getDataPoint());
                        selectedPointForm.openInModalPopup();
                    }
                }
            }
        });
    }

    private LinearPoint inRange(ArrayList<LinearPoint> point, Point actualPoint, int range) {
        for (LinearPoint point1 : point) {
            if (Math.abs(actualPoint.x - point1.getDrawPoint().x) <= range && (Math.abs(actualPoint.y - point1.getDrawPoint().y) <= range)) {
                return point1;
            }
        }

        return null;
    }

    private void Animated(Integer width, Integer height, com.vaadin.contextmenu.MenuItem button) {
        canvas.setVisible(false);
        processing.setVisible(true);
        setupView();
        ArrayList<LinearPoint> points = ordering.calculatePoints(width, height, setDimensionMenu(button), 1);
        codeExtension.set_pointList(LinearPoint.toList(points));
        codeExtension.set_width(width);
        codeExtension.set_height(height);
        codeExtension.setProcessingJavaCodeClass(PointProcessing.class.getName());
    }

    private void DrawPoints(ArrayList<LinearPoint> points, Boolean drawBorder, Integer aWidth, Integer aHeight, int margin) {
        canvas.clear();
        canvas.setFillStyle(255, 255, 255);
        canvas.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setStrokeStyle("#000000");

        canvas.beginPath();
        for (int i = 0; i < points.size() - 1; i++) {
            canvas.moveTo(points.get(i).getDrawPoint().x, points.get(i).getDrawPoint().y);
            canvas.lineTo(points.get(i + 1).getDrawPoint().x, points.get(i + 1).getDrawPoint().y);
        }

        canvas.stroke();

        if (drawBorder) drawBorder(dimension, aWidth, aHeight, margin);
        canvas.saveContext();
    }

    public void clearPoint() {
        if (!selectionMode) return;
        lastPoint = null;
        DrawPoints(_actualPointOrdering, true, square, square, 10);
    }

    @EventBusListenerMethod
    public void onCloseFormPointForm(CloseFormEvent event) {
        if (!selectionMode) return;
        lastPoint = null;
        DrawPoints(_actualPointOrdering, true, square, square, 10);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setupView();
    }
}
