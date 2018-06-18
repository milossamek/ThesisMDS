package com.MDS.ThesisMDS.backend.implementation;

import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.implementation.datastructures.TrieBasedQuadTree;
import com.MDS.ThesisMDS.backend.implementation.interfaces.IMTNode;
import com.MDS.ThesisMDS.backend.implementation.interfaces.ITreeImplementation;
import com.MDS.ThesisMDS.backend.implementation.objects.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component(value = "QUAD")
public class TrieBasedQTImplementation implements ITreeImplementation {
    private TrieBasedQuadTree<Key, Data> tree;
    private List<TreeNode> basicNodes;
    private Integer lastHorizontal = 0;
    private Integer lastVeritcal = 0;
    private List<TreeNode> lastTreeView;

    public TrieBasedQTImplementation() {
        tree = new TrieBasedQuadTree<>(100, 100);
        basicNodes = new ArrayList<>();
        lastTreeView = new ArrayList<>();
    }

    @Override
    public void build(List<TreeNodeDB> nodes) {
        lastTreeView.clear();
        tree.clear();
        basicNodes.clear();
        List<IMTNode<Key, Data>> nodeList = new ArrayList<>();
        int maximum = 0;

        for (TreeNodeDB node : nodes) {
            nodeList.add(new Node(new Key((int) node.getLoc().getX(), (int) node.getLoc().getY()), new Data(node.getData())));

            if (node.getLoc().getX() > maximum) {
                maximum = (int) node.getLoc().getX();
            }

            if (node.getLoc().getY() > maximum) {
                maximum = (int) node.getLoc().getY();
            }
        }

        if (lastVeritcal == 0 || lastHorizontal == 0) {
            tree.setHorizontalBound(maximum + 1 / maximum);
            tree.setVerticalBound(maximum + 1 / maximum);
            setLastHorizontalAndVertical(maximum + 1 / maximum, maximum + 1 / maximum);
        }

        tree.build(nodeList);
    }


    @Override
    public void bulkLoad(Integer datasetIndex) {
        lastTreeView.clear();
        tree.clear();
        basicNodes.clear();
        List<IMTNode<Key, Data>> nodeList = DataCreator.createNodeList(datasetIndex);

        switch (datasetIndex) {
            case 2:
                tree.setHorizontalBound(100);
                tree.setVerticalBound(100);
                break;
            case 3:
                tree.setHorizontalBound(100);
                tree.setVerticalBound(100);
                break;
            default:
                tree.setHorizontalBound(100);
                tree.setVerticalBound(100);
                break;
        }

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
        lastTreeView.clear();
        basicNodes.clear();
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

    private void setLastHorizontalAndVertical(int hori, int verti) {
        this.lastHorizontal = hori;
        this.lastVeritcal = verti;
    }
}
