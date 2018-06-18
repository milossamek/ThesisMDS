package com.MDS.ThesisMDS.backend.implementation.datastructures;

import com.MDS.ThesisMDS.backend.events.EventsBuffer;
import com.MDS.ThesisMDS.backend.implementation.interfaces.*;
import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.NodeSortType;
import com.MDS.ThesisMDS.backend.types.TreeNodeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/*
     RangeTree implementation with Binary Search Tree
     Main BST sort by X, secondary node BSTs sort by Y
     Key must implements IMTKey interface with x and y coordination (integer)
     Data must implements IMTData with string value for print, generics for data
 */

public class RangeTree<K extends IMTKey<Integer>, T extends IMTData<String>> implements IAnimatableStructure<K,T> {
    private static final int MAXSON = 2; //binary search tree
    private RNode<K, T> root; //root of main BST
    private RNode<K, T> lastLeafNode;
    private Integer nodeCount;
    private EventsBuffer buffer;


    //* PUBLIC PART**//
    public RangeTree() {
        nodeCount = 0;
        buffer = new EventsBuffer();
    }

    @Override
    public void clear() {
        root = null;
        nodeCount = 0;
        buffer.flushDraw();
        buffer.flushEvent();
    }

    @Override
    public void clearEventBuffer() {
        buffer.flushEvent();
    }

    @Override
    public void clearDrawingBuffer() {
        buffer.flushDraw();
    }

    @Override
    public Boolean isEmpty() {
        return nodeCount == 0 && root == null;
    }

    @Override
    public void build(List<IMTNode<K, T>> nodeList) {
        buffer.flushDraw();
        if ((nodeList != null) && (nodeList.size() == 0)) return;

        nodeCount = nodeList.size();
        root = build(nodeList, null, SortType.stX);
        root.isRootNode = true;
        addToDrawList(root, TreeNodeType.ntRoot, 0, SortType.stX);
        Collections.reverse(getBuffer().getDrawList());
    }

    @Override
    public void insert(IMTNode<K, T> node) {
        buffer.flushEvent();
        if (root == null) {
            root = new RNode<>(node);
            root.isLeafNode = true;
            root.isRootNode = true;
            nodeCount++;
            addToEventList(root, TreeNodeType.ntRoot, 0, SortType.stX);
            return;
        }

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, TreeNodeType.ntRoot, 0, SortType.stX);
        }

        addToEventList(root, TreeNodeType.ntRoot, 0, SortType.stX);
        insert(root, node, 0, SortType.stX);
        nodeCount++;
    }

    @Override
    public T find(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, TreeNodeType.ntRoot, 0, SortType.stX);
        }

        return findNode(key);
    }

    @Override
    public T remove(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, TreeNodeType.ntRoot, 0, SortType.stX);
        }

        RNode<K, T> delete = remove(key, root, SortType.stX, 0);
        if (delete == null) {
            return null;
        } else return delete.getNode().getData();
    }

    @Override
    public Integer size() {
        return nodeCount;
    }


    @Override
    public EventsBuffer getBuffer() {
        return buffer;
    }

    @Override
    public void addDraw(TreeNode node) {
        buffer.addDraw(node);
    }

    @Override
    public void addEvent(TreeNode node) {
        buffer.addEvent(node);
    }

    /*Bulk loading - recursive*/
    private RNode<K, T> build(List<IMTNode<K, T>> nodeList, RNode<K, T> secondaryTreeRoot, SortType type) {
        Integer level = 0;
        RNode<K, T> lastNode = null;
        RNode<K, T> actualNode = buildSecondaryTree(nodeList, null, lastNode, level, type);
        if (actualNode != null) {
            actualNode.parent = secondaryTreeRoot;
        }
        return actualNode;
    }

    private RNode<K, T> buildSecondaryTree(List<IMTNode<K, T>> nodeList, RNode<K, T> parent, RNode<K, T> lastNode, Integer level, SortType type) {
        level++;
        RNode<K, T> actualNode;
        if (nodeList.size() >= MAXSON)
            actualNode = createNavigationNode(nodeList, parent, lastNode, level, type);
        else {
            actualNode = createLeaf(nodeList, lastLeafNode);
            lastLeafNode = actualNode;
        }
        if (actualNode == null) return null;


        actualNode.parent = parent != null ? parent : null;
        if (type == SortType.stX && !actualNode.isLeafNode) {
            RNode<K, T> actualNode2 = lastLeafNode;
            actualNode.secondaryBST = build(nodeList, null, SortType.stY);
            actualNode.secondaryBST.parent = actualNode;
            actualNode.secondaryBST.isRootNode = true;
            actualNode.secondaryBST.isSecondaryBST = true;
            addToDrawList(actualNode.secondaryBST, TreeNodeType.ntRoot, level, SortType.stY);
            lastLeafNode = actualNode2;
        }
        return actualNode;
    }

    private RNode<K, T> createLeaf(List<IMTNode<K, T>> nodeList, RNode<K, T> lastNode) {
        if (nodeList.size() == 0) return null;

        RNode<K, T> leafNode = new RNode<>(nodeList.get(0), true);
        if (lastNode != null) {
            lastNode.next = leafNode;
            leafNode.previous = lastNode;
        }
        return leafNode;
    }

    private RNode<K, T> createNavigationNode(List<IMTNode<K, T>> nodeList, RNode<K, T> parent, RNode<K, T> lastNode, Integer level, SortType type) {
        RNode<K, T> node;
        sortData(nodeList, type);
        Integer x = 0;
        Integer y = 0;

        switch (type) {
            case stX:
                x = nodeList.get(0).getKey().getX();
                y = nodeList.get(nodeList.size() - 1).getKey().getX();
                break;
            case stY:
                x = nodeList.get(0).getKey().getY();
                y = nodeList.get(nodeList.size() - 1).getKey().getY();
                break;
        }
        node = new RNode<>(changeKey(nodeList.get(0), x, y));
        node.node.setData(null);
        node.isSecondaryBST = type.equals(SortType.stY);
        List<IMTNode<K, T>> firstHalf = new ArrayList<>();
        List<IMTNode<K, T>> secondHalf = new ArrayList<>();
        splitNodes(nodeList, firstHalf, secondHalf, type);
        node.leftSon = buildSecondaryTree(firstHalf, node, lastNode, level, type);
        node.leftSon.isSecondaryBST = type.equals(SortType.stY);
        switch (type) {
            case stX:
                addToDrawList(node.leftSon, TreeNodeType.ntLeftSon, level, SortType.stX);
                break;
            case stY:
                addToDrawList(node.leftSon, TreeNodeType.ntLeftSon, level, SortType.stY);
                break;
        }
        node.rightSon = buildSecondaryTree(secondHalf, node, lastNode, level, type);
        node.rightSon.isSecondaryBST = type.equals(SortType.stY);
        switch (type) {
            case stX:
                addToDrawList(node.rightSon, TreeNodeType.ntRightSon, level, SortType.stX);
                break;
            case stY:
                addToDrawList(node.rightSon, TreeNodeType.ntRightSon, level, SortType.stY);
                break;
        }

        return node;
    }

    private void splitNodes(List<IMTNode<K, T>> nodeList, List<IMTNode<K, T>> firstHalf, List<IMTNode<K, T>> secondHalf, SortType type) {
        Integer splitIndex = (nodeList.size() % 2 == 0) ? (nodeList.size() / 2) : (nodeList.size() / 2 + 1);

        //find index for split
        for (int i = splitIndex; i < nodeList.size() - 1; i++) {
            Boolean xMatch = true;
            if (type == SortType.stX) {
                if (nodeList.get(splitIndex).getKey().compareX(nodeList.get(splitIndex + 1).getKey().getX()) == 0) {
                    xMatch = false;
                }
            }

            if (!xMatch) {
                splitIndex++;
                continue;
            }


            Boolean yMatch = true;
            if (type == SortType.stX) {
                if (nodeList.get(splitIndex).getKey().compareY(nodeList.get(splitIndex + 1).getKey().getY()) == 0) {
                    yMatch = false;
                }
            }

            if (yMatch) {
                break;
            }
            splitIndex++;
        }


        for (int i = 0; i < nodeList.size(); i++) {
            if (i < splitIndex) {
                firstHalf.add(nodeList.get(i).cloneIt());
            } else {
                secondHalf.add(nodeList.get(i).cloneIt());
            }
        }
    }

    /*Inserting - recursive*/
    private void insert(RNode<K, T> parent, IMTNode<K, T> node, Integer level, SortType type) {
        if (type == SortType.stX) level++;

        if (parent == null) {
            parent = new RNode<>(node);
            return;
        }

        if (parent.isLeafNode) {
            insertToLeafNode(parent, node, level, type);
            return;
        } else {
            insertNode(parent, node, level, type);
        }

    }


    private void insertToLeafNode(RNode<K, T> parentNode, IMTNode<K, T> newNode, Integer level, SortType type) {
        RNode<K, T> parentParentNode = parentNode.parent;
        RNode<K, T> newNodeDupl = new RNode<>(newNode);
        newNodeDupl.setLeafNode(true);
        RNode<K, T> newTreeNode;
        RNode<K, T> newYRootNode;


        //set correct wires to sons & parents & next & previous
        switch (type) {
            case stX:
                if (newNode.getKey().getX() <= parentNode.getNode().getKey().getX()) {
                    newTreeNode = new RNode<>(changeKey(newNode, newNode.getKey().getX(), parentNode.getNode().getKey().getX()));
                    newTreeNode.getNode().setData(null);
                    newNodeDupl.setLeafNode(true);
                    parentNode.setLeafNode(true);
                    createNodeConnections(parentNode, parentParentNode, newNodeDupl, newTreeNode, level, type, false);

                } else {
                    newTreeNode = new RNode<>(changeKey(newNode, parentNode.getNode().getKey().getX(), newNode.getKey().getX()));
                    newTreeNode.getNode().setData(null);
                    newNodeDupl.setLeafNode(true);
                    parentNode.setLeafNode(true);
                    createNodeConnections(parentNode, parentParentNode, newNodeDupl, newTreeNode, level, type, true);
                }

                //Secondary BSTree for ActualNode
                newNodeDupl = new RNode<K, T>(newNode.cloneIt());
                parentNode = new RNode<K, T>(parentNode.getNode().cloneIt());
                if (newNodeDupl.getNode().getKey().getY().compareTo(parentNode.getNode().getKey().getY()) <= 0) {
                    newYRootNode = new RNode<K, T>(changeKey(newNode, parentNode.getNode().getKey().getY(), newNodeDupl.getNode().getKey().getY()));
                    newYRootNode.getNode().setData(null);
                    newYRootNode.setNeightbours(newTreeNode, parentNode, newNodeDupl);
                    parentNode.setParent(newYRootNode);
                    newNodeDupl.setParent(newYRootNode);
                    parentNode.setNext(newNodeDupl);
                    newNodeDupl.setPrevious(parentNode);


                    newYRootNode.setRootNode(true);
                    newYRootNode.isSecondaryBST = true;
                    newNodeDupl.isSecondaryBST = true;
                    parentNode.isSecondaryBST = true;
                    newNodeDupl.setLeafNode(true);
                    parentNode.setLeafNode(true);
                    addToEventList(newYRootNode, TreeNodeType.ntRoot, 0, SortType.stY);
                    addToEventList(newNodeDupl, TreeNodeType.ntRightSon, 1, SortType.stY);
                    addToEventList(parentNode, TreeNodeType.ntLeftSon, 1, SortType.stY);
                } else {
                    newYRootNode = new RNode<K, T>(changeKey(newNode, newNodeDupl.getNode().getKey().getY(), parentNode.getNode().getKey().getY()));
                    newYRootNode.getNode().setData(null);
                    newYRootNode.setNeightbours(newTreeNode, newNodeDupl, parentNode);
                    parentNode.parent = newYRootNode;
                    newNodeDupl.parent = newYRootNode;
                    parentNode.previous = newNodeDupl;
                    newNodeDupl.next = parentNode;

                    newYRootNode.isRootNode = true;
                    newYRootNode.isSecondaryBST = true;
                    newNodeDupl.isSecondaryBST = true;
                    parentNode.isSecondaryBST = true;
                    newNodeDupl.setLeafNode(true);
                    parentNode.setLeafNode(true);
                    addToEventList(newYRootNode, TreeNodeType.ntRoot, 0, SortType.stY);
                    addToEventList(newNodeDupl, TreeNodeType.ntLeftSon, 1, SortType.stY);
                    addToEventList(parentNode, TreeNodeType.ntRightSon, 1, SortType.stY);
                }

                newTreeNode.secondaryBST = newYRootNode;
                if (parentParentNode == null) {
                    root = newTreeNode;
                }
                level++;
                break;
            case stY:
                if (newNode.getKey().getY().compareTo(parentNode.getNode().getKey().getY()) <= 0) {
                    newTreeNode = new RNode<>(changeKey(newNode, parentNode.getNode().getKey().getY(), newNodeDupl.getNode().getKey().getY()));
                    newTreeNode.getNode().setData(null);
                    newNodeDupl.setLeafNode(true);
                    parentNode.setLeafNode(true);
                    createNodeConnections(parentNode, parentParentNode, newNodeDupl, newTreeNode, level, type, true);
                } else {
                    newTreeNode = new RNode<>(changeKey(newNode, newNodeDupl.getNode().getKey().getY(), parentNode.getNode().getKey().getY()));
                    newTreeNode.getNode().setData(null);
                    newNodeDupl.setLeafNode(true);
                    parentNode.setLeafNode(true);
                    createNodeConnections(parentNode, parentParentNode, newNodeDupl, newTreeNode, level, type, false);
                }
                break;
        }
    }

    private void insertNode(RNode<K, T> parent, IMTNode<K, T> node, Integer level, SortType type) {
        //set position
        setParentInterval(parent, node, type);

        //insert node to correct position, get X or Y by insert type
        int dimensionKeyValue = 0;
        Boolean byX = null;
        switch (type) {
            case stX:
                byX = true;
                dimensionKeyValue = node.getKey().getX();
                break;
            case stY:
                byX = false;
                dimensionKeyValue = node.getKey().getY();
                break;
        }

        K leftSonKey = null;
        K rightSonKey = null;

        if (parent.getLeftSon() != null) leftSonKey = parent.getLeftSon().getNode().getKey();
        if (parent.getLeftSon() != null) rightSonKey = parent.getRightSon().getNode().getKey();
        if (isNodeNotNullAndNotLeaf(parent.leftSon) && dimensionKeyValue <= leftSonKey.getY()) {
            //correct non leaf node by left son
            addToEventList(parent.leftSon, TreeNodeType.ntLeftSon, level, type);
            insert(parent.leftSon, node, level, type);

        } else if (isNodeNotNullAndNotLeaf(parent.rightSon) && dimensionKeyValue >= rightSonKey.getX()) {
            //correct non leaf node by right son
            addToEventList(parent.rightSon, TreeNodeType.ntRightSon, level, type);
            insert(parent.rightSon, node, level, type);

        } else if (isNodeNotNullAndLeaf(parent.leftSon) &&
                (byX ? (leftSonKey.getX() >= dimensionKeyValue) : (leftSonKey.getY() >= dimensionKeyValue))) {
            //correct leaf node by left son
            addToEventList(parent.leftSon, TreeNodeType.ntLeftSon, level, type);
            insert(parent.leftSon, node, level, type);

        } else if (isNodeNotNullAndLeaf(parent.rightSon) &&
                (byX ? (dimensionKeyValue >= rightSonKey.getX()) : (dimensionKeyValue >= rightSonKey.getY()))) {
            //corrent leaf node by right son
            addToEventList(parent.rightSon, TreeNodeType.ntRightSon, level, type);
            insert(parent.rightSon, node, level, type);

        } else {
            addToEventList(parent.leftSon, TreeNodeType.ntLeftSon, level, type);
            insert(parent.leftSon, node, level, type);
        }

        //inserting to main tree, need insert to parent secondary tree, recursive
        if (type == SortType.stX && parent.getSecondaryBST() != null) {
            addToEventList(parent.getSecondaryBST(), TreeNodeType.ntRoot, level, SortType.stY);
            insert(parent.getSecondaryBST(), node, level, SortType.stY);
        }
    }

    private void createNodeConnections(RNode<K, T> parent, RNode<K, T> parentParent, RNode<K, T> actualNode, RNode<K, T> newTreeNode, Integer level, SortType type, Boolean match) {
        boolean isLeftSon = true;
        boolean isRootNode = false;

        if (parentParent != null) {
            if (parentParent.leftSon == parent) {
                parentParent.leftSon = newTreeNode;
            } else {
                parentParent.rightSon = newTreeNode;
                isLeftSon = false;
            }
        } else {
            parent.setRootNode(true);
            isRootNode=true;
        }

        if (match) {
            newTreeNode.setNeightbours(parentParent, parent, actualNode); //different
            parent.parent = newTreeNode;
            actualNode.parent = newTreeNode;

            actualNode.next = parent.next;
            if (actualNode.next != null) actualNode.next.previous = actualNode;
            actualNode.previous = parent;
            parent.next = actualNode;

        } else {
            newTreeNode.setNeightbours(parentParent, actualNode, parent); //different
            parent.parent = newTreeNode;
            actualNode.parent = newTreeNode;

            newTreeNode.previous = parent.previous;
            if (newTreeNode.previous != null) newTreeNode.previous.next = actualNode;
            actualNode.next = parent;
            parent.previous = actualNode;
        }

        if (isRootNode) {
            addToEventList(newTreeNode, TreeNodeType.ntRoot, level, type);
        } else if (isLeftSon) {
            addToEventList(newTreeNode, TreeNodeType.ntLeftSon, level, type);
        } else {
            addToEventList(newTreeNode, TreeNodeType.ntRightSon, level, type);
        }

        if (match) {
            addToEventList(parent, TreeNodeType.ntLeftSon, level, type);
            addToEventList(actualNode, TreeNodeType.ntRightSon, level, type);
        } else {
            addToEventList(parent, TreeNodeType.ntRightSon, level, type);
            addToEventList(actualNode, TreeNodeType.ntLeftSon, level, type);
        }
    }

    private void setParentInterval(RNode<K, T> parent, IMTNode<K, T> node, SortType type) {
        switch (type) {
            case stX:
                if (parent.getNode().getKey().getX() > node.getKey().getX()) { // p.X <> k.X
                    parent.getNode().getKey().setX(new Integer(node.getKey().getX()));
                    break;
                }

                if (parent.getNode().getKey().getY() < node.getKey().getX()) { // p.Y <> k.X
                    parent.getNode().getKey().setY(new Integer(node.getKey().getX()));
                    break;
                }
                break;
            case stY:
                if (parent.getNode().getKey().getX() > node.getKey().getY()) { // p.X <> k.Y
                    parent.getNode().getKey().setX(new Integer(node.getKey().getY()));
                    break;
                }

                if (parent.getNode().getKey().getY() < node.getKey().getY()) { // p.Y <> k.Y
                    parent.getNode().getKey().setY(new Integer(node.getKey().getY()));
                    break;
                }
                break;
        }
    }

    /*Finding*/
    private T findNode(K key) {
        int level = 1;
        addToEventList(root, TreeNodeType.ntRoot, level++, SortType.stX);
        if (root != null && root.isLeafNode) {
            if (root.getNode().getKey().compareX(key.getX()) == 0 &&
                    root.getNode().getKey().compareY(key.getY()) == 0)
                return root.getNode().getData();
        }

        RNode<K, T> actualNode = root;
        while (actualNode != null) {
            if (isNodeNotNullAndLeaf(actualNode.leftSon)) {
                if (actualNode.getLeftSon().getNode().getKey().compareX(key.getX()) == 0 &&
                        actualNode.getLeftSon().getNode().getKey().compareY(key.getY()) == 0) {
                    addToEventList(actualNode.getLeftSon(), TreeNodeType.ntLeftSon, level++, SortType.stX);
                    return actualNode.getLeftSon().getNode().getData();
                }
            }

            if (isNodeNotNullAndLeaf(actualNode.rightSon)) {
                if (actualNode.getRightSon().getNode().getKey().compareX(key.getX()) == 0 &&
                        actualNode.getRightSon().getNode().getKey().compareY(key.getY()) == 0) {
                    addToEventList(actualNode.getRightSon(), TreeNodeType.ntRightSon, level++, SortType.stX);
                    return actualNode.getRightSon().getNode().getData();
                }
            }

            if (isNodeNotNullAndNotLeaf(actualNode.leftSon) &&
                    (actualNode.getLeftSon().getNode().getKey().getX() <= key.getX()) &&
                    (key.getX() <= actualNode.getLeftSon().getNode().getKey().getY())) {
                actualNode = actualNode.leftSon;
                addToEventList(actualNode, TreeNodeType.ntLeftSon, level++, SortType.stX);
                continue;
            } else {

                if (!isNodeNotNullAndNotLeaf(actualNode.rightSon) ||
                        (!(actualNode.getRightSon().getNode().getKey().getX() <= key.getX())) ||
                        (!(key.getX() <= actualNode.getRightSon().getNode().getKey().getY()))) {
                    return null;
                }
                actualNode = actualNode.rightSon;
                addToEventList(actualNode, TreeNodeType.ntRightSon, level++, SortType.stX);
            }
        }

        return null;
    }

    /*Removing - recursive*/
    private RNode<K, T> remove(K key, RNode<K, T> node, SortType type, Integer level) {
        if (node == null) return null;
        RNode<K, T> actualNode = null;

        if (node.isLeafNode()) { //in node is leaf && key equals to node
            if (node.getNode().getKey().compareX(key.getX()) == 0 && node.getNode().getKey().compareY(key.getY()) == 0) {
                if (type == SortType.stX) {
                    nodeCount--;
                }
                removeLeafNode(node, type, level);
                return node;
            }
        }

        switch (type) {
            case stX:
                if ((node.getNode().getKey().getX() <= key.getX()) && (key.getX() <= node.getNode().getKey().getY())) {
                    remove(key, node.getSecondaryBST(), SortType.stY, level + 2);
                    actualNode = remove(key, node.getLeftSon(), SortType.stX, level + 1);
                    if (actualNode != null) {
                        return actualNode;
                    }
                    actualNode = remove(key, node.getRightSon(), SortType.stX, level + 1);


                }
                break;
            case stY:
                if ((node.getNode().getKey().getX() <= key.getY()) && (key.getY() <= node.getNode().getKey().getY())) {
                    {
                        actualNode = remove(key, node.getLeftSon(), SortType.stY, level + 1);
                        if (actualNode != null) {
                            return actualNode;
                        }
                        actualNode = remove(key, node.getRightSon(), SortType.stY, level + 1);
                    }
                    break;
                }
        }

        return actualNode;
    }


    private void removeLeafNode(RNode<K, T> node, SortType type, Integer level) {
        // node without parent
        if (node.getParent() == null || node.isRootNode) {
            removeLeafNodeWithoutParent(node, type);
            return;
        }
        //node without secondary parent
        if (node.getParent().getParent() == null || node.getParent().isRootNode) {
            removeLeafNodeWithoutSecondaryParent(node, type, level);
            return;
        }
        //parent of deleted node equals left son of secondary parent
        if (node.getParent().equals(node.getParent().getParent().getLeftSon())) {
            removeLeafNodeEqualsSecondaryParentLeftSon(node, type, level);
            return;
        }
        //node is left son of parent
        if (node.equals(node.getParent().getLeftSon())) {
            removeNodeEqualsLeftSon(node, type, level);
            return;
        }

        //node is right son of parent
        node.getPrevious().setNext(node.getNext());
        if (node.getPrevious().getNext() != null) {
            node.getNext().setPrevious(node.getPrevious());
        }

        addToEventList(node, TreeNodeType.ntFinish, level, type);
        if (node.getParent().getLeftSon().isLeafNode())
            addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntRemove, level, type);

        addToEventList(node.getParent(), TreeNodeType.ntRemove, level, type);
        if (node.getParent().getSecondaryBST() != null) {
            addToEventList(node.getParent().getSecondaryBST(), TreeNodeType.ntRemove, level, type);
            removeNodeSubtree(node.getParent().getSecondaryBST(), level, type);
        }
        node.getParent().getParent().setRightSon(node.getParent().getLeftSon());
        node.getParent().getLeftSon().setParent(node.getParent().getParent());
        RNode<K, T> actualNode = node.getParent().getLeftSon();

        int upperLevel = level - 1;
        addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntRightSon, upperLevel, type);
        sendChildToEventList(node.getParent().getLeftSon(), upperLevel, type);
        if (node.getParent().getLeftSon().getSecondaryBST() != null) {
            addToEventList(node.getParent().getLeftSon().getSecondaryBST(), TreeNodeType.ntRoot, upperLevel + 2, SortType.stY);
            sendChildToEventList(node.getParent().getLeftSon().getSecondaryBST(), upperLevel + 2, SortType.stY);
        }

        //nejprve prvni prvek
        if (actualNode.isLeafNode && type == SortType.stX) {
            if (actualNode.getNode().getKey().getX() < actualNode.getParent().getNode().getKey().getY()) {
                actualNode.getParent().getNode().getKey().setY(actualNode.getNode().getKey().getX());
                addToEventList(actualNode.getParent(), TreeNodeType.ntParent, level, actualNode.getParent().isSecondaryBST ? SortType.stY : SortType.stX); //changing control node key
            }
        } else {
            if (actualNode.getNode().getKey().getY() < actualNode.getParent().getNode().getKey().getY()) {
                actualNode.getParent().getNode().getKey().setY(actualNode.getNode().getKey().getY());
                addToEventList(actualNode.getParent(), TreeNodeType.ntParent, 0, actualNode.getParent().isSecondaryBST ? SortType.stY : SortType.stX); //changing control node key
            }
        }

        if (actualNode.getParent() != null) actualNode=actualNode.getParent(); else actualNode=null;
        //pote traverzujeme
        while (actualNode != null) {
            if (actualNode.getParent() == null) {
                actualNode = null;
                continue;
            }

            if (actualNode.isLeafNode && type == SortType.stX) {
                if (actualNode.getNode().getKey().getX() > actualNode.getParent().getNode().getKey().getY()) {
                    actualNode.getParent().getNode().getKey().setY(actualNode.getNode().getKey().getX());
                    addToEventList(actualNode.getParent(), TreeNodeType.ntParent, level, actualNode.getParent().isSecondaryBST ? SortType.stY : SortType.stX); //changing control node key
                    actualNode = actualNode.getParent();
                    continue;
                }
            } else {
                if (actualNode.getNode().getKey().getY() > actualNode.getParent().getNode().getKey().getY()) {
                    actualNode.getParent().getNode().getKey().setY(actualNode.getNode().getKey().getY());
                    addToEventList(actualNode.getParent(), TreeNodeType.ntParent, 0, actualNode.getParent().isSecondaryBST ? SortType.stY : SortType.stX); //changing control node key
                    actualNode = actualNode.getParent();
                    continue;
                }
            }
            actualNode = null;
        }

    }

    private void removeLeafNodeEqualsSecondaryParentLeftSon(RNode<K, T> node, SortType type, Integer level) {
        if (node.equals(node.getParent().getLeftSon())) {
            if (node.getNext() != null) {
                node.getNext().setPrevious(node.getPrevious());
                if (node.getNext().getPrevious() != null) {
                    node.getPrevious().setNext(node.getNext());
                }
            }

            addToEventList(node, TreeNodeType.ntFinish, level, type);
            if (node.getParent().getRightSon().isLeafNode())
                addToEventList(node.getParent().getRightSon(), TreeNodeType.ntRemove, level, type);
            if (node.getParent().getSecondaryBST() != null)
                addToEventList(node.getParent().getSecondaryBST(), TreeNodeType.ntRemove, level, type);
            addToEventList(node.getParent().getParent().getLeftSon(), TreeNodeType.ntRemove, level, type);
            if (node.getParent().getParent().getLeftSon().getSecondaryBST() != null) {
                addToEventList(node.getParent().getParent().getLeftSon().getSecondaryBST(), TreeNodeType.ntRemove, level, type);
                removeNodeSubtree(node.getParent().getParent().getLeftSon().getSecondaryBST(), level, type);
            }
            node.getParent().getParent().setLeftSon(node.getParent().getRightSon());
            node.getParent().getRightSon().setParent(node.getParent().getParent());

            int upperLevel = level - 1;
            addToEventList(node.getParent().getRightSon(), TreeNodeType.ntLeftSon, upperLevel, type);
            sendChildToEventList(node.getParent().getRightSon(), upperLevel, type);
            if (node.getParent().getRightSon().getSecondaryBST() != null) {
                addToEventList(node.getParent().getRightSon().getSecondaryBST(), TreeNodeType.ntRoot, upperLevel + 2, SortType.stY);
                sendChildToEventList(node.getParent().getRightSon().getSecondaryBST(), upperLevel + 2, SortType.stY);
            }

            //       addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntLeftSon, level, type);
            //            addToEventList(node.getParent().getRightSon().getParent(), TreeNodeType.ntRightSon, level, type);
            RNode<K, T> actualNode = node.getParent().getRightSon();
            while (actualNode != null) {
                if (actualNode.isLeafNode && type == SortType.stY) {
                    if (actualNode.getParent() != null && actualNode.getNode().getKey().getY() > actualNode.getParent().getNode().getKey().getX()) {
                        actualNode.getParent().getNode().getKey().setX(actualNode.getNode().getKey().getY());
                        addToEventList(actualNode.getParent(), TreeNodeType.ntParent, 0, actualNode.getParent().isSecondaryBST ? SortType.stY : SortType.stX); //changing control node key
                        actualNode = actualNode.getParent();
                        continue;
                    }
                } else {
                    if (actualNode.getParent() != null && actualNode.getNode().getKey().getX() > actualNode.getParent().getNode().getKey().getX()) {
                        actualNode.getParent().getNode().getKey().setX(actualNode.getNode().getKey().getX());
                        addToEventList(actualNode.getParent(), TreeNodeType.ntParent, 0, actualNode.getParent().isSecondaryBST ? SortType.stY : SortType.stX); //changing control node key
                        actualNode = actualNode.getParent();
                        continue;
                    }
                }
                actualNode = null;
            }


        } else {
            if (node.getPrevious() != null) {
                node.getPrevious().setNext(node.getNext());
                if (node.getPrevious().getNext() != null) {
                    node.getNext().setPrevious(node.getPrevious());
                }
                addToEventList(node, TreeNodeType.ntFinish, level, type);
                if (node.getParent().getLeftSon().isLeafNode())
                    addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntRemove, level, type);

                addToEventList(node.getParent().getParent().getLeftSon(), TreeNodeType.ntRemove, level, type);
                if (node.getParent().getParent().getLeftSon().getSecondaryBST() != null) {
                    addToEventList(node.getParent().getParent().getLeftSon().getSecondaryBST(), TreeNodeType.ntRemove, level, type);
                    removeNodeSubtree(node.getParent().getParent().getLeftSon().getSecondaryBST(), level, type);
                }

                node.getParent().getParent().setLeftSon(node.getParent().getLeftSon());
                node.getParent().getLeftSon().setParent(node.getParent().getParent());

                int upperLevel = level - 1;
                addToEventList(node.getParent().getParent().getLeftSon(), TreeNodeType.ntLeftSon, upperLevel, type);
                sendChildToEventList(node.getParent().getParent().getLeftSon(), upperLevel, type);
                if (node.getParent().getParent().getLeftSon().getSecondaryBST() != null) {
                    addToEventList(node.getParent().getParent().getLeftSon().getSecondaryBST(), TreeNodeType.ntRoot, upperLevel + 2, SortType.stY);
                    sendChildToEventList(node.getParent().getParent().getLeftSon().getSecondaryBST(), upperLevel + 2, SortType.stY);
                }
                //         addToEventList(node.getParent().getLeftSon().getParent(), TreeNodeType.ntRightSon, level, type);
            }
        }
    }

    private void sendChildToEventList(RNode<K, T> node, Integer level, SortType type) {
        if (node == null) return;
        level++;
        if (node.getLeftSon() != null) {
            addToEventList(node.getLeftSon(), TreeNodeType.ntLeftSon, level, type);
            sendChildToEventList(node.getLeftSon(), level, type);
        }

        if (node.getRightSon() != null) {
            addToEventList(node.getRightSon(), TreeNodeType.ntRightSon, level, type);
            sendChildToEventList(node.getRightSon(), level, type);
        }
    }

    private void removeNodeSubtree(RNode<K, T> node, Integer level, SortType type) {
        if (node.getLeftSon() != null) {
            addToEventList(node.getLeftSon(), TreeNodeType.ntRemove, level, type);
            removeNodeSubtree(node.getLeftSon(), level + 1, type);
        }
        if (node.getRightSon() != null) {
            addToEventList(node.getRightSon(), TreeNodeType.ntRemove, level, type);
            removeNodeSubtree(node.getRightSon(), level + 1, type);
        }
    }

    private void removeNodeEqualsLeftSon(RNode<K, T> node, SortType type, Integer level) {
        if (node.getNext() != null) {
            node.getNext().setPrevious(node.getPrevious());
            if (node.getNext().getPrevious() != null) {
                node.getPrevious().setNext(node.getNext());
            }

            addToEventList(node, TreeNodeType.ntFinish, level, type);
            if (node.getParent().getRightSon().isLeafNode())
                addToEventList(node.getParent().getRightSon(), TreeNodeType.ntRemove, level, type);

            addToEventList(node.getParent(), TreeNodeType.ntRemove, level, type);

            if (node.getParent().getSecondaryBST() != null) {
                addToEventList(node.getParent().getSecondaryBST(), TreeNodeType.ntRemove, level, type);
                removeNodeSubtree(node.getParent().getSecondaryBST(), level, type);
            }


            node.getParent().getParent().setRightSon(node.getParent().getRightSon());
            node.getParent().getRightSon().setParent(node.getParent().getParent());
            int upperLevel = level - 1;
            addToEventList(node.getParent().getRightSon(), TreeNodeType.ntRightSon, upperLevel, type);
            sendChildToEventList(node.getParent().getRightSon(), upperLevel, type);
            if (node.getParent().getRightSon().getSecondaryBST() != null) {
                addToEventList(node.getParent().getRightSon().getSecondaryBST(), TreeNodeType.ntRoot, upperLevel + 2, SortType.stY);
                sendChildToEventList(node.getParent().getRightSon().getSecondaryBST(), upperLevel + 2, SortType.stY);
            }
            //      addToEventList(node.getParent().getRightSon().getParent(), TreeNodeType.ntLeftSon, level, type);
        }
    }

    private void removeLeafNodeWithoutParent(RNode<K, T> node, SortType type) {
        switch (type) {
            case stX:
                addToEventList(root, TreeNodeType.ntFinish, 0, type);
                root = null;
                break;
            case stY:
                if (node.getParent() != null) {
                    addToEventList(node, TreeNodeType.ntFinish, 0, type);
                    node.getParent().setSecondaryBST(null);
                }
                break;
        }
    }


    //Pokud existuje jiz jenom jedna uroven nad odebiranym listovy prvek smazeme leveho syna a pravy syn se korenem nebo opacne smazeme praveho syna a levy syn se stane korenem
    private void removeLeafNodeWithoutSecondaryParent(RNode<K, T> node, SortType type, Integer level) {
        //    RNode<K, T> tempNode =

        if (node.getParent().getLeftSon() != null && node.equals(node.getParent().getLeftSon())) {
            switch (type) {
                case stX:
                    if (node.getNext() != null) {
                        node.getNext().setPrevious(node.getPrevious());
                        if (node.getNext().getPrevious() != null) {
                            node.getPrevious().setNext(node.getNext());
                        }
                        addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntFinish, level, type);
                        root = node.getParent().getRightSon();
                        root.setParent(null);
                        addToEventList(root, TreeNodeType.ntRoot, level, type);

                    }
                    break;
                case stY:
                    if (node.getNext() != null) {
                        node.getNext().setPrevious(node.getPrevious());
                        if (node.getNext().getPrevious() != null) {
                            node.getPrevious().setNext(node.getNext());
                        }
                        addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntFinish, level, type);
                        node = node.getParent().getRightSon();
                        node.setParent(node.getParent().getParent());
                        addToEventList(node, TreeNodeType.ntRoot, level, type);
                    }
                    break;
            }
        } else {
            switch (type) {
                case stX:
                    if (node.getPrevious() != null) {
                        node.getPrevious().setNext(node.getNext());
                        if (node.getPrevious().getNext() != null) {
                            node.getNext().setPrevious(node.getPrevious());
                        }
                        addToEventList(node.getParent().getRightSon(), TreeNodeType.ntFinish, level, type);
                        //          addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntRightSon, level, type);
                        root = node.getParent().getLeftSon();
                        root.setParent(null);
                        addToEventList(root, TreeNodeType.ntRoot, 0, type);
                    }
                    break;
                case stY:
                    if (node.getPrevious() != null) {
                        node.getPrevious().setNext(node.getNext());
                        if (node.getPrevious().getNext() != null) {
                            node.getNext().setPrevious(node.getPrevious());
                        }
                        addToEventList(node.getParent().getRightSon(), TreeNodeType.ntFinish, level, type);
                        //         addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntRightSon, level, type);
                        node = node.getParent().getLeftSon();
                        node.setParent(node.getParent().getParent());
                        addToEventList(node, TreeNodeType.ntRoot, level, type);
                    }
                    break;
            }
        }
    }

    private void addToDrawList(RNode<K, T> node, TreeNodeType type, Integer nodeLevel, SortType sortType) {
        addToList(buffer.getDrawList(), node, type, nodeLevel, sortType);
    }


    private void addToEventList(RNode<K, T> node, TreeNodeType type, Integer nodeLevel, SortType sortType) {
        addToList(buffer.getEventList(), node, type, nodeLevel, sortType);
    }

    private void addToList(List<TreeNode> aList, RNode<K, T> node, TreeNodeType type, Integer nodeLevel, SortType sortType) {
        TreeNode parent = null;
        String data = "null";
        NodeSortType parentSortType = NodeSortType.stX;
        if (nodeLevel == null) nodeLevel=0;


        if (node == null) return;

        NodeSortType nodeSortType;
        if (sortType == SortType.stX) {
            nodeSortType = NodeSortType.stX;
        } else nodeSortType = NodeSortType.stY;

        if (node.getParent() != null) {
            if (node.getParent().getNode().getData() != null) {
                data = node.getParent().getNode().getData().toString();
            }

            if (node.getParent().isSecondaryBST.equals(true)) {
                parentSortType = NodeSortType.stY;
            }

            parent = new TreeNode(new ExtendedPoint(node.getParent().getNode().getKey().getX(), node.getParent().getNode().getKey().getY()),
                    data, null, null, nodeLevel - 1, parentSortType);
            parent.setUID(node.getParent().UID);
        }


        data = "null";
        if (node.getNode().getData() != null) {
            data = node.getNode().getData().toString();
        }

        TreeNode e = new TreeNode(new ExtendedPoint(node.getNode().getKey().getX(), node.getNode().getKey().getY()),
                data, parent, type, nodeLevel, nodeSortType);
        e.setUID(node.UID);
        aList.add(e);
    }

    /*Helpers*/
    private IMTNode<K, T> changeKey(IMTNode<K, T> node, Integer x, Integer y) {
        IMTNode<K, T> ktimtNode = node.cloneIt();
        ktimtNode.getKey().setX(new Integer(x));
        ktimtNode.getKey().setY(new Integer(y));
        return ktimtNode;
    }


    private List<IMTNode<K, T>> sortData(List<IMTNode<K, T>> data, SortType type) {
        switch (type) {
            case stX:
                data.sort((o1, o2) -> {
                    return o1.getKey().compareX(o2.getKey().getX());
                });
                break;
            case stY:
                data.sort((o1, o2) -> {
                    return o1.getKey().compareY(o2.getKey().getY());
                });
                break;
        }

        return data;
    }


    private void iterate(RNode<K, T> startNode, TreeNodeType nodeType, int level, SortType sortType) {
        addToDrawList(startNode, nodeType, level, sortType);

        if (startNode.getSecondaryBST() != null)
            iterate(startNode.getSecondaryBST(), TreeNodeType.ntRoot, level, SortType.stY);
        if (startNode.getLeftSon() != null)
            iterate(startNode.getLeftSon(), TreeNodeType.ntLeftSon, level + 1, sortType);
        if (startNode.getRightSon() != null)
            iterate(startNode.getRightSon(), TreeNodeType.ntRightSon, level + 1, sortType);

    }


    private boolean isNodeNotNullAndNotLeaf(RNode<K, T> node) {
        if (node == null) return false;
        if (node.isLeafNode) return false;
        return true;
    }

    private boolean isNodeNotNullAndLeaf(RNode<K, T> node) {
        if (node == null) return false;
        if (!node.isLeafNode) return false;
        return true;
    }

    private enum SortType {stX, stY}

    private class RNode<K extends IMTKey<Integer>, T extends IMTData<String>> implements Comparable<K> {
        private RNode<K, T> parent;
        private RNode<K, T> leftSon;
        private RNode<K, T> rightSon;
        private RNode<K, T> next;
        private RNode<K, T> previous;
        private RNode<K, T> secondaryBST; //koren sekundarního binarního stromu
        private IMTNode<K, T> node;
        private Boolean isLeafNode;
        private Boolean isRootNode;
        private Boolean isSecondaryBST;
        private String UID;

        public RNode(IMTNode<K, T> node) {
            this.node = node;
            this.isLeafNode = false;
            this.isRootNode = false;
            this.isSecondaryBST = false;
            UID = UUID.randomUUID().toString();
        }

        public RNode(IMTNode<K, T> node, Boolean isLeafNode) {
            this.node = node;
            this.isLeafNode = isLeafNode;
            this.isRootNode = false;
            this.isSecondaryBST = false;
            UID = UUID.randomUUID().toString();
        }

        private void setNeightbours(RNode<K, T> parent, RNode<K, T> leftSon, RNode<K, T> rightSon) {
            this.parent = parent;
            this.leftSon = leftSon;
            this.rightSon = rightSon;
        }

        public RNode<K, T> getParent() {
            return parent;
        }

        public void setParent(RNode<K, T> parent) {
            this.parent = parent;
        }

        public RNode<K, T> getLeftSon() {
            return leftSon;
        }

        public void setLeftSon(RNode<K, T> leftSon) {
            this.leftSon = leftSon;
        }

        public RNode<K, T> getRightSon() {
            return rightSon;
        }

        public void setRightSon(RNode<K, T> rightSon) {
            this.rightSon = rightSon;
        }

        public RNode<K, T> getNext() {
            return next;
        }

        public void setNext(RNode<K, T> next) {
            this.next = next;
        }

        public RNode<K, T> getPrevious() {
            return previous;
        }

        public void setPrevious(RNode<K, T> previous) {
            this.previous = previous;
        }

        public RNode<K, T> getSecondaryBST() {
            return secondaryBST;
        }

        public void setSecondaryBST(RNode<K, T> secondaryBST) {
            this.secondaryBST = secondaryBST;
        }

        public IMTNode<K, T> getNode() {
            return node;
        }

        public void setNode(IMTNode<K, T> node) {
            this.node = node;
        }

        public Boolean isLeafNode() {
            return isLeafNode;
        }

        public void setLeafNode(Boolean leafNode) {
            isLeafNode = leafNode;
        }

        public Boolean getRootNode() {
            return isRootNode;
        }

        public void setRootNode(Boolean rootNode) {
            isRootNode = rootNode;
        }

        @Override
        public int compareTo(K o) {
            return 0;
        }
    }
}
