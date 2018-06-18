package com.MDS.ThesisMDS.backend.implementation.interfaces;

import java.io.Serializable;

public interface IMTNode<K extends IMTKey<Integer>, T extends IMTData<String>> extends Serializable {
    K getKey();

    void setKey(K key);

    T getData();

    void setData(T data);

    IMTNode<K, T> cloneIt();

}
