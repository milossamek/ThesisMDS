package com.MDS.ThesisMDS.backend.implementation.interfaces;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.implementation.objects.Data;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;

import java.awt.*;
import java.util.List;

public interface ITreeImplementation {
    void build(List<TreeNodeDB> nodes);

    void bulkLoad(Integer datasetIndex);

    Data removeNode(Point location);

    void addNode(TreeNodeDB node);

    Data findNode(Point location);

    void clear();

    void clearEvent();

    //funkce vraci stav stromu po aktualni operaci
    List<TreeNode> getTree();

    //akce provedene na stromu
    List<TreeNode> getEvent();

    //list nodu bez navigacnich prvku
    List<TreeNode> getBasicNodes();

    //funkce vraci stav stromu pred aktualni operaci
    List<TreeNode> getLastDrawView();
}
