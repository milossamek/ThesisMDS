package com.MDS.ThesisMDS.frontend.forms;

import com.MDS.ThesisMDS.backend.types.BulkEventType;
import com.MDS.ThesisMDS.frontend.Interfaces.IPopupForm;
import com.MDS.ThesisMDS.frontend.designs.BulkLoadingDesign;
import com.MDS.ThesisMDS.frontend.events.BulkLoadingEvent;
import com.MDS.ThesisMDS.frontend.events.CloseFormEvent;
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
public class BulkLoadingForm extends BulkLoadingDesign implements IPopupForm {
    private static final int DATASETNOTSELECTED = -1;
    private static final int DATASET_1 = 1;
    private static final int DATASET_2 = 2;
    private static final int DATASET_3 = 3;
    @Autowired
    EventBus.UIEventBus eventBus;
    private PopupForm popupForm;
    private Button _selectedButton;
    private Boolean closedByEvent;

    public BulkLoadingForm() {
        super();
        addListeners();
        popupForm = new PopupForm("Bulk Loading ");
        popupForm.addComponent(this);
        popupForm.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent closeEvent) {
                if (!closedByEvent) {
                    eventBus.publish(EventScope.UI, this, new CloseFormEvent());
                }
            }
        });
    }

    private void addListeners() {
        btnBulk1.addClickListener((Button.ClickListener) event -> setButtonDown(event));
        btnBulk2.addClickListener((Button.ClickListener) event -> setButtonDown(event));
        btnBulk3.addClickListener((Button.ClickListener) event -> setButtonDown(event));
        btnBulkAnimate.addClickListener((Button.ClickListener) event -> animateAction(event));
        btnBulkInstant.addClickListener((Button.ClickListener) event -> instantAction(event));
        btnCancel.addClickListener((Button.ClickListener) event -> cancel(event));
    }

    private void setButtonDown(Button.ClickEvent clickEvent) {
        SetBulkButtonState(true);
        clickEvent.getButton().setEnabled(false);
        _selectedButton = clickEvent.getButton();
    }

    private void SetBulkButtonState(Boolean enabled) {
        btnBulk1.setEnabled(enabled);
        btnBulk2.setEnabled(enabled);
        btnBulk3.setEnabled(enabled);
    }

    private Integer getDataSet(Button aButton) {
        if (aButton == null) return DATASETNOTSELECTED;

        if (aButton == btnBulk1) return DATASET_1;
        if (aButton == btnBulk2) return DATASET_2;
        if (aButton == btnBulk3) return DATASET_3;

        return DATASETNOTSELECTED;
    }

    private void instantAction(Button.ClickEvent clickEvent) {
        if (_selectedButton != null) {
            closedByEvent = true;
            eventBus.publish(EventScope.UI, this, new BulkLoadingEvent(BulkEventType.blInstant, getDataSet(_selectedButton)));
        } else Notification.show("Select one of bulkLoadings");
    }

    private void animateAction(Button.ClickEvent clickEvent) {
        if (_selectedButton != null) {
            closedByEvent = true;
            eventBus.publish(EventScope.UI, this, new BulkLoadingEvent(BulkEventType.blAnimate, getDataSet(_selectedButton)));
        } else Notification.show("Select one of bulkLoadings");
    }

    private void cancel(Button.ClickEvent clickEvent) {
        closePopup();
    }

    @Override
    public Window openInModalPopup() {
        closedByEvent = false;
        SetBulkButtonState(true);
        return popupForm.openInModalPopup();
    }

    @Override
    public void closePopup() {
        popupForm.closePopup();
    }


}
