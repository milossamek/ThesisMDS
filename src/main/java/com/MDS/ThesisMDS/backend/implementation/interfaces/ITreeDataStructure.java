package com.MDS.ThesisMDS.backend.implementation.interfaces;

import java.util.List;

public interface ITreeDataStructure<K extends IMTKey<Integer>, T extends IMTData<String>> {
    Boolean isEmpty();

    void clear();

    void build(List<IMTNode<K, T>> data);

    T find(K key);

    T remove(K key);

    void insert(IMTNode<K, T> node);

    Integer size();
}
