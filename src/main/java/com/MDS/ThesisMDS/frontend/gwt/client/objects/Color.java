package com.MDS.ThesisMDS.frontend.gwt.client.objects;

public class Color {
    int r;
    int g;
    int b;

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(Color color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
