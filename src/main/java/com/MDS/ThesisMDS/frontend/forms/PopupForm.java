package com.MDS.ThesisMDS.frontend.forms;


import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


@UIScope
public class PopupForm extends VerticalLayout {
    private Window popup;
    private String label;
    private Window.CloseListener closeListener;

    public PopupForm(String label) {
        this.label = label;
        this.closeListener = null;
    }


    public Window openInModalPopup() {
        this.popup = new Window(label, this);
        this.popup.setModal(true);
        UI.getCurrent().addWindow(this.popup);
        this.popup.setResizable(true);
        if (closeListener != null) {
            this.popup.addCloseListener(closeListener);
        }
        return this.popup;
    }


    public void closePopup() {
        if (this.popup != null) {
            this.popup.close();
        }

    }

    public void addCloseListener(Window.CloseListener close) {
        closeListener = close;
    }


}
