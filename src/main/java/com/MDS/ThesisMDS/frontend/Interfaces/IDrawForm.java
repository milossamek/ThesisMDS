package com.MDS.ThesisMDS.frontend.Interfaces;

import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.vaadin.navigator.View;

public interface IDrawForm extends View {
    void setOperation(OperationsDB operation);

    void refresh();
}
