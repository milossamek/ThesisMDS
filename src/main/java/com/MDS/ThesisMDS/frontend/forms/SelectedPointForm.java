package com.MDS.ThesisMDS.frontend.forms;

import com.MDS.ThesisMDS.frontend.Interfaces.IPopupForm;
import com.MDS.ThesisMDS.frontend.designs.SelectPointDesign;
import com.MDS.ThesisMDS.frontend.events.CloseFormEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;

import java.awt.*;

@SpringComponent
@UIScope
@Scope("prototype")
public class SelectedPointForm extends SelectPointDesign implements IPopupForm {
    @Autowired
    EventBus.UIEventBus eventBus;
    private PopupForm popupForm;

    public SelectedPointForm() {
        popupForm = new PopupForm("Point");
        popupForm.addComponent(this);
        popupForm.addCloseListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent closeEvent) {
                eventBus.publish(EventScope.UI, this, new CloseFormEvent());
            }
        });

        addListeners();
    }

    private void addListeners() {

        btnCancel.addClickListener((Button.ClickListener) event -> closePopup());
    }

    @Override
    public Window openInModalPopup() {
        return popupForm.openInModalPopup();
    }

    @Override
    public void closePopup() {
        popupForm.closePopup();
    }


    public void setPoint(Point point) {
        lblLocation.setValue(point.x + "    ;   " + point.y);
    }

    public void setIndex(Integer index) {
        lblIndex.setValue(index.toString());
    }
}
