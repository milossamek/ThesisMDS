package com.MDS.ThesisMDS.backend.implementation;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.implementation.datastructures.RangeTree;
import com.MDS.ThesisMDS.backend.implementation.interfaces.IAnimatableStructure;
import com.MDS.ThesisMDS.backend.implementation.interfaces.IMTNode;
import com.MDS.ThesisMDS.backend.implementation.interfaces.ITreeImplementation;
import com.MDS.ThesisMDS.backend.implementation.objects.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component(value = "RANGE")
public class RangeTreeImplementation implements ITreeImplementation {
    private IAnimatableStructure<Key, Data> tree;
    private List<TreeNode> basicNodes;
    private List<TreeNode> lastTreeView;

    public RangeTreeImplementation() {
        tree = new RangeTree<>();
        basicNodes = new ArrayList<>();
        lastTreeView = new ArrayList<>();
    }

    @Override
    public void build(List<TreeNodeDB> nodes) {
        lastTreeView.clear();
        tree.clear();
        List<IMTNode<Key, Data>> customNodes = new ArrayList<>();

        for (TreeNodeDB node : nodes) {
            Node node1 = new Node(new Key(node.getLoc().x, node.getLoc().y), new Data(node.getData()));

            customNodes.add(node1);
        }


        tree.build(customNodes);
        lastTreeView = createDeepCopy(tree.getBuffer().getDrawList());
    }

    @Override
    public void bulkLoad(Integer datasetIndex) {
        lastTreeView.clear();
        tree.clear();
        basicNodes.clear();
        List<IMTNode<Key, Data>> nodeList = DataCreator.createNodeList(datasetIndex);

        for (IMTNode<Key, Data> keyDataIMTNode : nodeList) {
            basicNodes.add(getNode(keyDataIMTNode));
        }
        tree.build(nodeList);
    }

    @Override
    public Data removeNode(Point location) {
        lastTreeView = createDeepCopy(tree.getBuffer().getDrawList());
        Key searchPoint = new Key(location.x, location.y);
        return tree.remove(searchPoint);
    }

    @Override
    public void addNode(TreeNodeDB node) {
        lastTreeView = createDeepCopy(tree.getBuffer().getDrawList());
        tree.insert(new Node(new Key((int) node.getLoc().getX(), (int) node.getLoc().getY()), new Data(node.getData())));
    }

    @Override
    public Data findNode(Point location) {
        lastTreeView = createDeepCopy(tree.getBuffer().getDrawList());
        Key searchPoint = new Key(location.x, location.y);
        return tree.find(searchPoint);
    }

    private List<TreeNode> createDeepCopy(List<TreeNode> list) {
        ArrayList<TreeNode> objects = new ArrayList<>();
        for (TreeNode treeNode : list) {
            objects.add(treeNode.clone());
        }
        return objects;
    }

    @Override
    public void clear() {
        tree.clear();
    }

    @Override
    public void clearEvent() {
        tree.clearEventBuffer();
    }

    @Override
    public List<TreeNode> getTree() {
        return tree.getBuffer().getDrawList();
    }


    @Override
    public List<TreeNode> getEvent() {
        return tree.getBuffer().getEventList();
    }

    @Override
    public List<TreeNode> getBasicNodes() {
        return basicNodes;
    }

    @Override
    public List<TreeNode> getLastDrawView() {
        return lastTreeView;
    }

    private TreeNode getNode(IMTNode<Key, Data> node) {
        return new TreeNode(new ExtendedPoint(node.getKey().getX(), node.getKey().getY()), node.getData().getData(), 0);
    }
}
