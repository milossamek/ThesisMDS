package com.MDS.ThesisMDS.backend.implementation.objects;

import com.MDS.ThesisMDS.backend.implementation.interfaces.IMTData;

public class Data implements IMTData<String> {
    private String data;

    public Data(String data) {
        this.data = data;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String Data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }
}
