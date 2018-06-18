package com.MDS.ThesisMDS.backend.implementation.datastructures;


import com.MDS.ThesisMDS.backend.events.EventsBuffer;
import com.MDS.ThesisMDS.backend.implementation.interfaces.*;
import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.RotationType;
import com.MDS.ThesisMDS.backend.types.TreeNodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
    Priority Search Tree
    Combination of Binary Search Tree + Binary Heap (Max - Heap)
    X key - BST rules
    Y key - Binary Heap rules
    Key must implements IMTKey interface with x and y coordination (integer)
    Data must implements IMTData with string value for print, generics for data
 */

public class PrioritySearchTree<K extends IMTKey<Integer>, T extends IMTData<String>> implements IAnimatableStructure<K,T> {
    private PSTNode<K, T> root;
    private Integer nodeCount;
    private EventsBuffer buffer;
    private Integer rotationIndex;


    //* PUBLIC PART**//
    public PrioritySearchTree() {
        buffer = new EventsBuffer();
        rotationIndex = 0;
        clear();
    }

    @Override
    public Boolean isEmpty() {
        return nodeCount == 0 && root == null;
    }

    @Override
    public void clear() {
        root = null;
        nodeCount = 0;
        rotationIndex = 0;
        clearEventBuffer();
        clearDrawingBuffer();
    }

    @Override
    public void clearEventBuffer() {
        rotationIndex = 0;
        buffer.flushEvent();
    }

    @Override
    public void clearDrawingBuffer() {
        buffer.flushDraw();
    }

    @Override
    public void build(List<IMTNode<K, T>> data) {
        List<IMTNode<K, T>> lLeftNodes = new ArrayList<>();
        List<IMTNode<K, T>> lRightNodes = new ArrayList<>();

        if (!isEmpty()) return;
        if (data == null || data.size() == 0) return;
        nodeCount = data.size();

        //Y Sort
        data = sortDataSet(data, SortType.stY);
        root = new PSTNode<>(data.get(data.size() - 1));
        root.setLevel(0);
        data.remove(data.size() - 1);

        if (data.size() == 0) {
            root.setBound(root.getKey());
            root.getBound().setX(root.getKey().getX());
            return;
        }

        //X Sort
        data = sortDataSet(data, SortType.stX);
        root.setBound(data.get(data.size() / 2).getKey());
        addToDrawList(root, TreeNodeType.ntRoot, 0);

        for (IMTNode<K, T> node : data) {
            if (node.getKey().compareX(root.getBound().getX()) == -1) {
                lLeftNodes.add(node);
            } else {
                lRightNodes.add(node);
            }
        }

        build(lLeftNodes, root, NodeType.ntLeft, 1);
        build(lRightNodes, root, NodeType.ntRight, 1);
    }

    @Override
    public T find(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, TreeNodeType.ntRoot, 0);
        }

        PSTNode<K, T> p = findP(key);
        if (p != null) {
            return p.getNode().getData();
        }

        return null;
    }

    @Override
    public T remove(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, TreeNodeType.ntRoot, 0);
        }


        PSTNode<K, T> p = findP(key);
        if (p != null) {
            return remove(p);
        }

        return null;
    }

    @Override
    public void insert(IMTNode<K, T> node) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, TreeNodeType.ntRoot, 0);
        }

        if (root != null) addToEventList(root, TreeNodeType.ntRoot, 0);
        insert(node, root, 0);
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

    /*Bulk loading*/
    private void build(List<IMTNode<K, T>> data, PSTNode<K, T> parent, NodeType type, Integer nodeLevel) {
        List<IMTNode<K, T>> lLeftNodes = new ArrayList<>();
        List<IMTNode<K, T>> lRightNodes = new ArrayList<>();

        if (data.size() == 0) return;

        data = sortDataSet(data, SortType.stY);

        switch (type) {
            case ntLeft:
                PSTNode<K, T> leftSon = new PSTNode<>(data.get(data.size() - 1));
                leftSon.setLevel(nodeLevel);
                leftSon.setParent(parent);
                parent.setLeftSon(leftSon);
                //            addToDrawList(leftSon, TreeNodeType.ntLeftSon, nodeLevel);
                break;
            case ntRight:
                PSTNode<K, T> rightSon = new PSTNode<>(data.get(data.size() - 1));
                parent.setRightSon(rightSon);
                rightSon.setLevel(nodeLevel);
                rightSon.setParent(parent);
                //             addToDrawList(rightSon, TreeNodeType.ntRightSon, nodeLevel);
                break;
        }
        data.remove(data.size() - 1);

        if (data.size() == 0) {
            switch (type) {
                case ntLeft:
                    parent.getLeftSon().setBound(parent.getLeftSon().getKey());
                    parent.getLeftSon().setParent(parent);
                    addToDrawList(parent.getLeftSon(), TreeNodeType.ntLeftSon, nodeLevel);
                    break;
                case ntRight:
                    parent.getRightSon().setBound(parent.getRightSon().getKey());
                    parent.getRightSon().setParent(parent);
                    addToDrawList(parent.getRightSon(), TreeNodeType.ntRightSon, nodeLevel);
                    break;
            }
            return;
        }

        data = sortDataSet(data, SortType.stX);


        for (IMTNode<K, T> node : data) {
            if (node.getKey().compareX(data.get(data.size() / 2).getKey().getX()) == -1) {
                lLeftNodes.add(node);
            } else {
                lRightNodes.add(node);
            }
        }

        switch (type) {
            case ntLeft:
                parent.getLeftSon().setBound(data.get(data.size() / 2).getKey());
                parent.getLeftSon().setParent(parent);
                addToDrawList(parent.getLeftSon(), TreeNodeType.ntLeftSon, nodeLevel);
                build(lLeftNodes, parent.getLeftSon(), NodeType.ntLeft, nodeLevel + 1);
                build(lRightNodes, parent.getLeftSon(), NodeType.ntRight, nodeLevel + 1);
                break;
            case ntRight:
                parent.getRightSon().setBound(data.get(data.size() / 2).getKey());
                parent.getRightSon().setParent(parent);
                addToDrawList(parent.getRightSon(), TreeNodeType.ntRightSon, nodeLevel);
                build(lLeftNodes, parent.getRightSon(), NodeType.ntLeft, nodeLevel + 1);
                build(lRightNodes, parent.getRightSon(), NodeType.ntRight, nodeLevel + 1);

                break;
        }

    }

    private T insert(IMTNode<K, T> node, PSTNode<K, T> actualNode, int level) {
        if (actualNode == null) {
            root = new PSTNode<>(node);
            addToEventList(root, TreeNodeType.ntRoot, 0);
            return node.getData();
        }

        if (node.getKey().getY() > actualNode.getKey().getY()) {
            TreeNodeType nodeType = TreeNodeType.ntRoot;
            PSTNode aNewNode = new PSTNode<K, T>(node);
            aNewNode.setBound(actualNode.getBound());

            if (actualNode.getParent() != null) {
                aNewNode.setParent(actualNode.getParent());
                if (actualNode.getParent().getLeftSon() != null && actualNode.getParent().getLeftSon().equals(actualNode)) {
                    nodeType = TreeNodeType.ntLeftSon;
                    actualNode.getParent().setLeftSon(aNewNode);
                } else {
                    nodeType = TreeNodeType.ntRightSon;
                    actualNode.getParent().setRightSon(aNewNode);
                }
            } else {
                root = aNewNode;
            }

            if (actualNode.getLeftSon() != null) {
                aNewNode.setLeftSon(actualNode.getLeftSon());
                actualNode.getLeftSon().setParent(aNewNode);
                actualNode.setLeftSon(null);
            }

            if (actualNode.getRightSon() != null) {
                aNewNode.setRightSon(actualNode.getRightSon());
                actualNode.getRightSon().setParent(aNewNode);
                actualNode.setRightSon(null);
            }
            aNewNode.setLevel(actualNode.getLevel());
            addToEventList(aNewNode, nodeType, level);
            insert(actualNode.getNode(), aNewNode, level+1);
            nodeCount++;
            return node.getData();
        } else {
            if (actualNode.getKey().getX() >= node.getKey().getX()) {
                if (actualNode.getLeftSon() != null) {
                    addToEventList(actualNode.getLeftSon(), TreeNodeType.ntLeftSon, level + 1);
                    return insert(node, actualNode.getLeftSon(), level+1);
                } else {
                    PSTNode aNewNode = new PSTNode<K, T>(node);
                    aNewNode.setParent(actualNode);
                    aNewNode.setLevel(level -1);
                    actualNode.setLeftSon(aNewNode);
                    aNewNode.setBound(aNewNode.getKey());
                    addToEventList(aNewNode, TreeNodeType.ntLeftSon, level + 1);
                }
            } else {
                if (actualNode.getRightSon() != null) {
                    addToEventList(actualNode.getRightSon(), TreeNodeType.ntRightSon, level + 1);
                    return insert(node, actualNode.getRightSon(), level+1);
                } else {
                    PSTNode aNewNode = new PSTNode<K, T>(node);
                    aNewNode.setLevel(level -1);
                    aNewNode.setParent(actualNode);
                    actualNode.setRightSon(aNewNode);
                    aNewNode.setBound(aNewNode.getKey());
                    nodeCount++;
                    addToEventList(aNewNode, TreeNodeType.ntRightSon, level + 1);
                }
            }
        }


        return null;
    }

    private T remove(PSTNode<K, T> removeNode) {
        //removing leaf node
        nodeCount--;
        if (removeLeafNode(removeNode)) {
            addToEventList(removeNode, TreeNodeType.ntFinish, 0);
            return removeNode.getNode().getData();
        }

        while (removeNode.getLeftSon() != null || removeNode.getRightSon() != null) {
            if (removeNode.getLeftSon() == null ||
                    (removeNode.getLeftSon() != null && removeNode.getLeftSon().getLeftSon() == null && removeNode.getLeftSon().getRightSon() == null && removeNode.getRightSon() == null) ||
                    (removeNode.getRightSon() != null && removeNode.getRightSon().getLeftSon() == null && removeNode.getRightSon().getRightSon() == null && removeNode.getLeftSon() == null) ||
                    (removeNode.getRightSon() != null && removeNode.getRightSon().getLeftSon() == null && removeNode.getRightSon().getRightSon() == null) && removeNode.getLeftSon() != null && removeNode.getLeftSon().getLeftSon() == null && removeNode.getLeftSon().getRightSon() == null) {
                if (removeLeafNode(removeNode)) return removeNode.getNode().getData();
                else if (removeNode.getLeftSon() != null && removeNode.getRightSon() != null) {
                    if (removeNode.getLeftSon().getKey().getY() > removeNode.getRightSon().getKey().getY()) {
                        removeNode.getLeftSon().setParent(removeNode.getParent());
                        removeNode.getLeftSon().setRightSon(removeNode.getRightSon());
                        removeNode.getRightSon().setParent(removeNode.getLeftSon());
                        if (removeNode.getParent() == null) {
                            root = removeNode.getLeftSon();
                            addToEventList(root, TreeNodeType.ntRoot, 0);
                        } else {
                            if (removeNode.equals(removeNode.getParent().getLeftSon())) {
                                removeNode.getParent().setLeftSon(removeNode.getLeftSon());
                                addToEventList(removeNode.getLeftSon(), TreeNodeType.ntLeftSon, removeNode.getLeftSon().getLevel());
                                addChildernsToEventBuffer(removeNode.getLeftSon(), RotationType.rtNoRotation, removeNode.getLeftSon().getLevel(), true);
                            } else {
                                removeNode.getParent().setRightSon(removeNode.getLeftSon());
                                addToEventList(removeNode.getLeftSon(), TreeNodeType.ntRightSon, removeNode.getLeftSon().getLevel());
                                addChildernsToEventBuffer(removeNode.getLeftSon(), RotationType.rtNoRotation, removeNode.getLeftSon().getLevel(), true);
                            }
                        }
                    } else {
                        removeNode.getRightSon().setParent(removeNode.getParent());
                        removeNode.getRightSon().setLeftSon(removeNode.getLeftSon());
                        removeNode.getLeftSon().setParent(removeNode.getRightSon());
                        if (removeNode.getParent() == null) {
                            root = removeNode.getRightSon();
                            addToEventList(root, TreeNodeType.ntRoot, 0);
                        } else {
                            if (removeNode.equals(removeNode.getParent().getLeftSon())) {
                                removeNode.getParent().setLeftSon(removeNode.getRightSon());
                                addToEventList(removeNode.getRightSon(), TreeNodeType.ntLeftSon, removeNode.getRightSon().getLevel() - 1);
                                if (removeNode.getRightSon().getNode().getKey().getX() < removeNode.getLeftSon().getNode().getKey().getX()) {
                                    removeNode.getLeftSon().setLeftSon(removeNode.getRightSon());
                                    addToEventList(removeNode.getLeftSon(), TreeNodeType.ntLeftSon, removeNode.getLeftSon().getLevel() + 1);
                                } else {
                                    removeNode.getLeftSon().setRightSon(removeNode.getRightSon());
                                    addToEventList(removeNode.getLeftSon(), TreeNodeType.ntRightSon, removeNode.getLeftSon().getLevel() + 1);
                                }

                            } else {
                                removeNode.getParent().setRightSon(removeNode.getRightSon());
                                addToEventList(removeNode.getRightSon(), TreeNodeType.ntRightSon, removeNode.getRightSon().getLevel() - 1);
                                if (removeNode.getLeftSon().getNode().getKey().getX() < removeNode.getRightSon().getNode().getKey().getX()) {
                                    removeNode.getRightSon().setLeftSon(removeNode.getLeftSon());
                                    addToEventList(removeNode.getLeftSon(), TreeNodeType.ntLeftSon, removeNode.getLeftSon().getLevel() + 1);
                                } else {
                                    removeNode.getRightSon().setRightSon(removeNode.getLeftSon());
                                    addToEventList(removeNode.getLeftSon(), TreeNodeType.ntRightSon, removeNode.getLeftSon().getLevel() + 1);
                                }

                            }
                        }
                    }

                } else if (removeNode.getLeftSon() == null) { //remove node with only right son
                    removeNode.getRightSon().setParent(removeNode.getParent());
                    if (removeNode.getParent() == null) root = removeNode.getRightSon();
                    if (removeNode.equals(removeNode.getParent().getLeftSon())) { //removing leaf node
                        removeNode.getParent().setLeftSon(removeNode.getRightSon());
                        addToEventList(removeNode.getRightSon(), TreeNodeType.ntLeftSon, removeNode.getRightSon().getLevel() - 1);
                        addChildernsToEventBuffer(removeNode.getRightSon(), RotationType.rtNoRotation, removeNode.getRightSon().getLevel() - 1, false);
                    } else {
                        removeNode.getParent().setRightSon(removeNode.getRightSon());
                        addToEventList(removeNode.getRightSon(), TreeNodeType.ntRightSon, removeNode.getRightSon().getLevel() - 1);
                        addChildernsToEventBuffer(removeNode.getRightSon(), RotationType.rtNoRotation, removeNode.getRightSon().getLevel() - 1, false);
                    }

                } else { //remove node with only left son
                    removeNode.getLeftSon().setParent(removeNode.getParent());
                    if (removeNode.getParent() == null) root = removeNode.getLeftSon();
                    if (removeNode.equals(removeNode.getParent().getLeftSon())) { //removing leaf node
                        removeNode.getParent().setLeftSon(removeNode.getLeftSon());
                        addToEventList(removeNode.getLeftSon(), TreeNodeType.ntLeftSon, removeNode.getLeftSon().getLevel() - 1);
                        addChildernsToEventBuffer(removeNode.getLeftSon(), RotationType.rtNoRotation, removeNode.getLeftSon().getLevel() - 1, false);
                    } else {
                        removeNode.getParent().setRightSon(removeNode.getLeftSon());
                        addToEventList(removeNode.getLeftSon(), TreeNodeType.ntRightSon, removeNode.getLeftSon().getLevel() - 1);
                        addChildernsToEventBuffer(removeNode.getLeftSon(), RotationType.rtNoRotation, removeNode.getLeftSon().getLevel() - 1, false);

                    }
                }

                addToEventList(removeNode, TreeNodeType.ntFinish, removeNode.getLevel());
                return removeNode.getNode().getData();
            }
            if (removeNode.getLeftSon() != null && removeNode.getRightSon() != null) {
                if (removeNode.getLeftSon().getKey().getY() > removeNode.getRightSon().getKey().getY())
                    rightRotation(removeNode);
                else leftRotation(removeNode);
            } else if (removeNode.getLeftSon() == null) leftRotation(removeNode);
            else rightRotation(removeNode);
        }

        if (removeNode.getParent() == null) root = null;
        else if (removeNode.equals(removeNode.getParent().getLeftSon())) removeNode.getParent().setLeftSon(null);
        else removeNode.getParent().setRightSon(null);
        addToEventList(removeNode, TreeNodeType.ntFinish, removeNode.getLevel());
        return removeNode.getNode().getData();
    }

    private boolean removeLeafNode(PSTNode<K, T> removeNode) {
        if (removeNode.getLeftSon() == null && removeNode.getRightSon() == null) {
            if (removeNode.getParent() == null) { //root without childs
                root = null;
                return true;
            }

            if (removeNode.equals(removeNode.getParent().getLeftSon())) { //removing leaf node
                removeNode.getParent().setLeftSon(null);
            } else {
                removeNode.getParent().setRightSon(null);
            }
            return true;
        }
        return false;
    }


    /*Finding*/
    private PSTNode<K, T> findP(K searchPoint) {
        PSTNode<K, T> actualNode = root;
        addToEventList(actualNode, TreeNodeType.ntRoot, 0);

        while (actualNode != null) {
            K keyValue = actualNode.getKey();
            if (keyValue.getX().equals(searchPoint.getX()) && keyValue.getY().equals(searchPoint.getY())) {
                addToEventList(actualNode, TreeNodeType.ntFinish, actualNode.getLevel());
                return actualNode;
            }

            if (searchPoint.getY() <= keyValue.getY()) {
                if (actualNode.getBound().getX() <= searchPoint.getX()) {
                    if (actualNode.getRightSon() == null) return null;
                    actualNode = actualNode.getRightSon();
                    addToEventList(actualNode, TreeNodeType.ntRightSon, actualNode.getLevel());
                } else {
                    if (actualNode.getLeftSon() == null) return null;
                    actualNode = actualNode.getLeftSon();
                    addToEventList(actualNode, TreeNodeType.ntLeftSon, actualNode.getLevel());
                }

                continue;
            }
            return null; //node doesnt not exist
        }
        return null;//root doesnt not exist
    }

    private void leftRotation(PSTNode<K, T> node) {
        rotationIndex++;
        if (node == null) return;
        if (node.getParent() == null) {
            root = node.getRightSon();
            node.getRightSon().setParent(null);
            addToEventList(root, TreeNodeType.ntRoot, 0, RotationType.rtLeftRotation, rotationIndex);
            addChildernsToEventBuffer(root, RotationType.rtLeftRotation, 0, true);
        } else {
            if (node.equals(node.getParent().getLeftSon())) {
                node.getParent().setLeftSon(node.getRightSon());
                node.getRightSon().setParent(node.getParent());
                node.getParent().getRightSon().setLevel(node.getParent().getRightSon().getLevel() - 1);
                addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntLeftSon, node.getParent().getRightSon().getLevel(), RotationType.rtLeftRotation, rotationIndex);
                if (node.getParent().getRightSon().getLeftSon() != null) {
                    node.getParent().getRightSon().getLeftSon().setLevel(node.getParent().getRightSon().getLeftSon().getLevel() - 1);
                    addToEventList(node.getParent().getRightSon().getLeftSon(), TreeNodeType.ntLeftSon, node.getLevel(), RotationType.rtLeftRotation, rotationIndex);
                }
            } else {
                node.getParent().setRightSon(node.getRightSon());
                node.getRightSon().setParent(node.getParent());
                node.getParent().getRightSon().setLevel(node.getParent().getRightSon().getLevel() - 1);
                addToEventList(node.getParent().getRightSon(), TreeNodeType.ntRightSon, node.getParent().getRightSon().getLevel(), RotationType.rtLeftRotation, rotationIndex);
                if (node.getParent().getRightSon().getRightSon() != null) {
                    node.getParent().getRightSon().getRightSon().setLevel(node.getParent().getRightSon().getRightSon().getLevel() - 1);
                    addToEventList(node.getParent().getRightSon().getRightSon(), TreeNodeType.ntRightSon, node.getParent().getRightSon().getRightSon().getLevel(), RotationType.rtLeftRotation, rotationIndex);
                }
            }
        }

        if (node.getRightSon() != null) node.setParent(node.getRightSon());
        else node.setParent(null);

        if (node.getParent() != null && node.getParent().getLeftSon() != null) {
            node.setRightSon(node.getParent().getLeftSon());
            node.getParent().setLeftSon(null);
        } else node.setRightSon(null);

        if (node.getRightSon() != null) {
            node.getRightSon().setParent(node);
        }
        if (node.getParent() != null) node.getParent().setLeftSon(node);
        node.setLevel(node.getLevel() + 1);
        addToEventList(node, TreeNodeType.ntLeftSon, node.getLevel(), RotationType.rtLeftRotation, rotationIndex);
        if (node.getParent() != null) {
            addChildernsToEventBuffer(node.getParent(), RotationType.rtLeftRotation, node.getLevel(), true);
        } else addChildernsToEventBuffer(node, RotationType.rtLeftRotation, node.getLevel(), true);
        //addToEventList(node.getRightSon(),TreeNodeType.ntRightSon,0,RotationType.rtLeftRotation);
    }

    private void addChildernsToEventBuffer(PSTNode<K, T> node, RotationType rotType, Integer level, Boolean increaseLevel) {
        if (node == null) return;
        if (level == null) level=0;
        if (increaseLevel) level++;

        if (node.getLeftSon() != null) {
            node.getLeftSon().setLevel(level);
            addToEventList(node.getLeftSon(), TreeNodeType.ntLeftSon, node.getLeftSon().getLevel(), rotType, rotationIndex);
            addChildernsToEventBuffer(node.getLeftSon(), rotType, level, true);
        }

        if (node.getRightSon() != null) {
            node.getRightSon().setLevel(level);
            addToEventList(node.getRightSon(), TreeNodeType.ntRightSon, node.getRightSon().getLevel(), rotType, rotationIndex);
            addChildernsToEventBuffer(node.getRightSon(), rotType, level, true);
        }
    }

    private void rightRotation(PSTNode<K, T> node) {
        rotationIndex++;
        if (node == null) return;
        if (node.getParent() == null) {
            root = node.getLeftSon();
            node.getLeftSon().setParent(null);
            addToEventList(root, TreeNodeType.ntRoot, 0, RotationType.rtRightRotation, rotationIndex);
            addChildernsToEventBuffer(root, RotationType.rtLeftRotation, 0, true);
        } else {
            if (node.equals(node.getParent().getLeftSon())) {
                node.getParent().setLeftSon(node.getLeftSon());
                node.getLeftSon().setParent(node.getParent());
                node.getParent().getLeftSon().setLevel(node.getParent().getLeftSon().getLevel() - 1);
                addToEventList(node.getParent().getLeftSon(), TreeNodeType.ntLeftSon, node.getParent().getLeftSon().getLevel(), RotationType.rtRightRotation, rotationIndex);
                addChildernsToEventBuffer(node.getParent().getLeftSon(), RotationType.rtRightRotation, node.getParent().getLeftSon().getLevel(), true);
                if (node.getParent().getLeftSon().getLeftSon() != null) {
                    node.getParent().getLeftSon().getLeftSon().setLevel(node.getParent().getLeftSon().getLeftSon().getLevel() - 1);
                    addToEventList(node.getParent().getLeftSon().getLeftSon(), TreeNodeType.ntLeftSon, node.getParent().getLeftSon().getLeftSon().getLevel(), RotationType.rtRightRotation, rotationIndex);
                }
            } else {
                node.getParent().setRightSon(node.getLeftSon());
                node.getLeftSon().setParent(node.getParent());
                node.getParent().getRightSon().setLevel(node.getParent().getRightSon().getLevel() - 1);
                addToEventList(node.getParent().getRightSon(), TreeNodeType.ntRightSon, node.getParent().getRightSon().getLevel(), RotationType.rtRightRotation, rotationIndex);
                addChildernsToEventBuffer(node.getParent().getRightSon(), RotationType.rtRightRotation, node.getParent().getRightSon().getLevel(), true);
                if (node.getParent().getRightSon().getRightSon() != null) {
                    node.getParent().getRightSon().getRightSon().setLevel(node.getParent().getRightSon().getRightSon().getLevel() - 1);
                    addToEventList(node.getParent().getRightSon().getRightSon(), TreeNodeType.ntRightSon, node.getParent().getRightSon().getRightSon().getLevel(), RotationType.rtRightRotation, rotationIndex);
                }
            }

        }
        if (node.getLeftSon() != null) node.setParent(node.getLeftSon());
        else node.setParent(null);

        if (node.getParent() != null && node.getParent().getRightSon() != null) {
            node.setLeftSon(node.getParent().getRightSon());
            node.getParent().setRightSon(null);
        } else node.setLeftSon(null);

        if (node.getLeftSon() != null) {
            node.getLeftSon().setParent(node);

        }
        if (node.getParent() != null) node.getParent().setRightSon(node);
        node.setLevel(node.getLevel() + 1);
        addToEventList(node, TreeNodeType.ntRightSon, node.getLevel(), RotationType.rtRightRotation, rotationIndex);
        if (node.getParent() != null) {
            addChildernsToEventBuffer(node.getParent(), RotationType.rtRightRotation, node.getParent().getLevel(), true);
        } else addChildernsToEventBuffer(node, RotationType.rtRightRotation, node.getLevel(), true);

        //  addToEventList(node.getLeftSon(),TreeNodeType.ntLeftSon,0,RotationType.rtRightRotation);
    }

    private void iterate(PSTNode<K, T> startNode, TreeNodeType nodeType, int level) {
        if (startNode.getParent() == null) {
            addToDrawList(startNode, TreeNodeType.ntRoot, 0);
        } else {
            addToDrawList(startNode, nodeType, level);
        }

        if (startNode.getLeftSon() != null) iterate(startNode.getLeftSon(), TreeNodeType.ntLeftSon, level + 1);
        if (startNode.getRightSon() != null) iterate(startNode.getRightSon(), TreeNodeType.ntRightSon, level + 1);

    }

    /*Helpers*/
    private List<IMTNode<K, T>> sortDataSet(List<IMTNode<K, T>> data, SortType type) {
        switch (type) {
            case stX:
                data.sort((IMTNode<K, T> o1, IMTNode<K, T> o2) -> {
                    return o1.getKey().compareX(o2.getKey().getX());
                });
                break;
            case stY:
                data.sort((IMTNode<K, T> o1, IMTNode<K, T> o2) -> {
                    return o1.getKey().compareY(o2.getKey().getY());
                });
                break;
        }

        return data;
    }

    private void addToDrawList(PSTNode<K, T> node, TreeNodeType type, Integer nodeLevel) {
        addDraw(createBufferNode(node, type, nodeLevel, null, 0));
    }

    private void addToEventList(PSTNode<K, T> node, TreeNodeType type, Integer nodeLevel, RotationType rType, Integer rotationIndex) {
        addEvent(createBufferNode(node, type, nodeLevel, rType, rotationIndex));
    }

    private void addToEventList(PSTNode<K, T> node, TreeNodeType type, Integer nodeLevel) {
        addEvent(createBufferNode(node, type, nodeLevel, null, 0));
    }

    private TreeNode createBufferNode(PSTNode<K, T> node, TreeNodeType type, Integer nodeLevel, RotationType rType, Integer rotationIndex) {
        TreeNode parent = null;
        if (nodeLevel == null) nodeLevel=0;
        if (rotationIndex == null) rotationIndex=0;

        if (node.getParent() != null) {
            parent = new TreeNode(new ExtendedPoint(node.getParent().getKey().getX(), node.getParent().getKey().getY()),
                    node.getParent().getNode().getData().toString(),
                    null,
                    null,
                    nodeLevel - 1);
            parent.setUID(node.getParent().UID);
        }
        TreeNode e = new TreeNode(new ExtendedPoint(node.getKey().getX(), node.getKey().getY()),
                node.getValue().toString(), parent, type, nodeLevel);

        if (node.getBound() != null && node.getBound().getX() != 0) {
            e.setBound(node.getBound().getX());
        }

        e.setUID(node.UID);

        e.setRotationIndex(rotationIndex);

        if (rType != null) {
            e.setRotationType(rType);
        } else e.setRotationType(RotationType.rtNoRotation);

        return e;
    }

    private enum NodeType {ntLeft, ntRight}

    private enum SortType {stX, stY}

    private class PSTNode<K extends IMTKey<Integer>, T extends IMTData<String>> implements Comparable<K> {
        private PSTNode<K, T> parent;
        private PSTNode<K, T> leftSon;
        private PSTNode<K, T> rightSon;
        private IMTNode<K, T> node;
        private K bound;
        private String UID;
        private Integer level;


        public PSTNode(IMTNode<K, T> node) {
            this.node = node;
            UID = UUID.randomUUID().toString();
        }

        public PSTNode<K, T> getParent() {
            return parent;
        }

        public void setParent(PSTNode<K, T> parent) {
            this.parent = parent;
        }

        public PSTNode<K, T> getLeftSon() {
            return leftSon;
        }

        public void setLeftSon(PSTNode<K, T> leftSon) {
            this.leftSon = leftSon;
        }

        public PSTNode<K, T> getRightSon() {
            return rightSon;
        }

        public void setRightSon(PSTNode<K, T> rightSon) {
            this.rightSon = rightSon;
        }

        public K getKey() {
            return node.getKey();
        }

        public void setKey(K key) {
            this.node.setKey(key);
        }

        public T getValue() {
            return node.getData();
        }

        public void setValue(T value) {
            this.node.setData(value);
        }

        public K getBound() {
            return bound;
        }

        public void setBound(K bound) {
            this.bound = bound;
        }

        public IMTNode<K, T> getNode() {
            return node;
        }

        public void setNode(IMTNode<K, T> node) {
            this.node = node;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        @Override
        public int compareTo(K o) {
            return 0;
        }

        @Override
        public String toString() {
            return node.getData().toString() + ";" + node.getKey().getX() + ";" + node.getKey().getY();
        }
    }
}
