package com.MDS.ThesisMDS.backend.implementation.objects;

import com.MDS.ThesisMDS.backend.implementation.interfaces.IMTNode;

public class Node implements IMTNode<Key, Data> {
    private Key key;
    private Data data;

    public Node(Key key, Data data) {
        this.key = key;
        this.data = data;
    }


    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public IMTNode<Key, Data> cloneIt() {
        String tmpData = null;
        if (data != null) {
            tmpData = this.data.getData();
        }
        return new Node(new Key(key.getX(), key.getY()), new Data(tmpData));
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

