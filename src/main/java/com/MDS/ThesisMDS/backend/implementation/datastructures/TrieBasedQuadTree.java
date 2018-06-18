package com.MDS.ThesisMDS.backend.implementation.datastructures;


/*

    Region Quad Tree implementation.
    Splitting region in half of quad
    Bulk loading - sort order by ZCurve
    Key must implements IMTKey interface with x and y coordination (integer)
    Data must implements IMTData with string value for print, generics for data
 */

import com.MDS.ThesisMDS.backend.events.EventsBuffer;
import com.MDS.ThesisMDS.backend.implementation.interfaces.*;
import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.NodeSortType;
import com.MDS.ThesisMDS.backend.types.TreeNodeType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TrieBasedQuadTree<K extends IMTKey<Integer>, T extends IMTData<String>> implements IAnimatableStructure<K,T> {
    private static final int NW = 0;
    private static final int NE = 1;
    private static final int SW = 2;
    private static final int SE = 3;
    private int horizontalBound = 1024;
    private int verticalBound = 1024;
    private QNode<K, T> root;
    private EventsBuffer buffer;
    private int nodeCount;


    public TrieBasedQuadTree(int horizontal, int vertical) {
        this.horizontalBound = horizontal;
        this.verticalBound = vertical;
        this.root = null;
        this.buffer = new EventsBuffer();
    }

    @Override
    public void clear() {
        root = null;
        clearEventBuffer();
        clearDrawingBuffer();
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
        return root == null;
    }

    @Override
    public void build(List<IMTNode<K, T>> nodeList) {
        if ((nodeList != null) && (nodeList.size() == 0)) return;

        if (nodeList.size() == 1) {
            root = new QNode<>(nodeList.get(0), null, true);
        } else {
            bulkLoad(nodeList);
        }
    }

    @Override
    public void insert(IMTNode<K, T> node) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, null, 0, 0);
        }

        if (root != null) addToEventList(root, null, 0, TreeNodeType.ntRoot);
        insert(node, root, 0, false);
    }

    @Override
    public Integer size() {
        return 0;
    }

    @Override
    public T find(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, null, 0, 0);
        }

        addToEventList(root, null, 0, TreeNodeType.ntRoot);
        IMTNode<K, T> imtNode = find(key, root);
        if (imtNode != null) return imtNode.getData();
        return null;
    }

    @Override
    public T remove(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, null, 0, 0);
        }

        addToEventList(root, null, 0, TreeNodeType.ntRoot);
        return remove(key, root, null, 0);
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


    private void bulkLoad(List<IMTNode<K, T>> nodeList) {
        if ((nodeList != null) && (nodeList.size() == 0)) return;
        List<QNode<K, T>> nodeZORDER = new ArrayList<>();

        for (IMTNode<K, T> node : nodeList) {
            QNode<K, T> ktqNode = new QNode<>(node, null, true);
            ktqNode.zOrder = calculateZOrderValue(ktqNode);
            nodeZORDER.add(ktqNode);
        }

        Collections.sort(nodeZORDER, Comparator.comparing(o -> o.zOrder));

        for (QNode<K, T> ktqNode : nodeZORDER) {
            insert(ktqNode.getNode(), root, 0, true);
        }
    }

    private int calculateZOrderValue(QNode<K, T> node) {
        Integer xValue = node.getNode().getKey().getX();
        Integer yValue = node.getNode().getKey().getY();
        String binaryX = Integer.toBinaryString(xValue);
        String binaryY = Integer.toBinaryString(yValue);
        String joinedBinaryValue = "";

        if (binaryX.length() != binaryY.length()) {
            binaryX = StringUtils.leftPad(binaryX, 16, "0");
            binaryY = StringUtils.leftPad(binaryY, 16, "0");
        }

        for (int i = 0; i < binaryX.length() - 1; i++) {
            joinedBinaryValue += binaryX.charAt(i);
            joinedBinaryValue += binaryY.charAt(i);
        }

        return Integer.parseInt(joinedBinaryValue, 2);
    }

    private IMTNode<K, T> insert(IMTNode<K, T> node, QNode<K, T> actualNode, int level, boolean bulkLoading) {
        if (actualNode == null || root == null) {
            root = new QNode(node, null, true);
            if (bulkLoading) {
                addToDrawList(root, null, level, TreeNodeType.ntRoot);
            } else {
                addToEventList(root, null, level, TreeNodeType.ntRoot);
            }
            return root.getNode();
        }

        if (actualNode.isLeafNode()) {
            IMTNode<K, T> ktimtNode = node.cloneIt();
            ktimtNode.getKey().setX(horizontalBound / 2);
            ktimtNode.getKey().setY(verticalBound / 2);
            QNode newNode = new QNode(ktimtNode, null, false);
            newNode.getNode().setData(null);
            QNode oldNode = actualNode;
            root = newNode;
            if (bulkLoading) addToDrawList(newNode, null, level, TreeNodeType.ntRoot);
            else addToEventList(newNode, null, level, TreeNodeType.ntRoot);

            insert(oldNode.getNode(), root, 0, bulkLoading);
            return insert(node, root, 0, bulkLoading);

        } else {
            IMTNode<K, T> tempNode = actualNode.getNode().cloneIt();
            int xSplit = (new Double(horizontalBound / Math.pow(2, level + 2))).intValue(); //Bisection method
            int ySplit = (new Double(verticalBound / Math.pow(2, level + 2))).intValue();   //Bisection method
            int newXCoord = 0;
            int newYCoord = 0;
            int myChild = 0;

            if (node.getKey().getX() >= tempNode.getKey().getX()) { // 2 x 4 quad
                if (node.getKey().getY() >= tempNode.getKey().getY()) { //2
                    myChild = NE;
                    newXCoord = tempNode.getKey().getX() + xSplit;
                    newYCoord = tempNode.getKey().getY() + ySplit;

                } else { //4
                    myChild = SE;
                    newXCoord = tempNode.getKey().getX() + xSplit;
                    newYCoord = tempNode.getKey().getY() - ySplit;
                }
            } else { // 1 x 3 quad
                if (node.getKey().getY() >= tempNode.getKey().getY()) { //1
                    myChild = NW;
                    newXCoord = tempNode.getKey().getX() - xSplit;
                    newYCoord = tempNode.getKey().getY() + ySplit;

                } else { // 3
                    myChild = SW;
                    newXCoord = tempNode.getKey().getX() - xSplit;
                    newYCoord = tempNode.getKey().getY() - ySplit;
                }
            }

            if (actualNode.getChildern()[myChild] == null) {
                actualNode.getChildern()[myChild] = new QNode<>(node, actualNode, true);

                if (bulkLoading)
                    addToDrawList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));
                else
                    addToEventList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));

                return actualNode.getChildern()[myChild].getNode();
            } else {
                if (actualNode.getChildern()[myChild].isLeafNode()) {
                    IMTNode<K, T> tempKey = actualNode.getChildern()[myChild].getNode().cloneIt();
                    tempNode.getKey().setX(newXCoord);
                    tempNode.getKey().setY(newYCoord);
                    actualNode.getChildern()[myChild] = new QNode<>(tempNode, actualNode, false);
                    actualNode.getChildern()[myChild].getNode().setData(null); //only leaf nodes store data

                    if (bulkLoading)
                        addToDrawList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));
                    else
                        addToEventList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));

                    insert(tempKey, actualNode.getChildern()[myChild], level + 1, bulkLoading);
                    return insert(node, actualNode.getChildern()[myChild], level + 1, bulkLoading);
                } else {
                    if (!bulkLoading)
                        addToEventList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));

                    return insert(node, actualNode.getChildern()[myChild], level + 1, bulkLoading);
                }
            }
        }
    }

    private T remove(K key, QNode<K, T> node, QNode<K, T> parentNode, int nodeIndex) {
        if (node == null) return null;

        if (node.isLeafNode()) {
            if (node.getNode().getKey().getX().equals(key.getX()) && node.getNode().getKey().getY().equals(key.getY())) {
                addToEventList(node, parentNode, 0, TreeNodeType.ntRemove);
                T deletedNode = node.getNode().getData();
                node = null;
                if (parentNode != null & parentNode.getChildern()[nodeIndex] != null)
                    parentNode.getChildern()[nodeIndex] = null;

                //zruseni navigacniho prvku, pokud ma jiz jen jednoho potomka
                if (parentNode.getChildCount() == 1) {
                    removeNavigationNode(parentNode);
                }

                return deletedNode;
            }
        } else {
            if (key.getX() >= node.getNode().getKey().getX()) { // 2 x 4 quad
                if (key.getY() >= node.getNode().getKey().getY()) { //2
                    addToEventList(node.getChildern()[NE], node, 0, TreeNodeType.ntNE);
                    return remove(key, node.getChildern()[NE], node, NE);
                } else { //4
                    addToEventList(node.getChildern()[SE], node, 0, TreeNodeType.ntSE);
                    return remove(key, node.getChildern()[SE], node, SE);
                }
            } else { // 1 x 3 quad
                if (key.getY() >= node.getNode().getKey().getY()) { //1
                    addToEventList(node.getChildern()[NW], node, 0, TreeNodeType.ntNW);
                    return remove(key, node.getChildern()[NW], node, NW);
                } else { // 3
                    addToEventList(node.getChildern()[SW], node, 0, TreeNodeType.ntSW);
                    return remove(key, node.getChildern()[SW], node, SW);
                }
            }
        }

        return null;
    }

    private void removeNavigationNode(QNode<K, T> parentNode) {
        for (int i = 0; i < parentNode.getChildern().length; i++) {
            if (parentNode.getChildern()[i] != null) {
                //pokud parentNode neni root pouze posunume
                if (parentNode.getParentNode() != null) {
                    for (int j = 0; j < parentNode.getParentNode().getChildern().length; j++) {
                        if (parentNode.getParentNode().getChildern()[j] != null && parentNode.getParentNode().getChildern()[j].equals(parentNode)) {
                            parentNode.getParentNode().getChildern()[j] = parentNode.getChildern()[i];
                            parentNode.getParentNode().getChildern()[j].setParentNode(parentNode.getParentNode());
                            addToEventList(parentNode.getParentNode().getChildern()[j], parentNode.getParentNode(), 0, getNodeType(j));
                            addChildernToEventList(parentNode.getParentNode().getChildern()[j]);
                            break;
                        }
                    }
                } else {
                    //mazany navigacni prvek je koren stromu
                    root = parentNode.getChildern()[i];
                    root.parentNode = null;
                    addToEventList(root, null, 0, TreeNodeType.ntRoot);
                }

                QNode<K, T> oldParentNode = parentNode;
                parentNode = parentNode.getChildern()[i];
                parentNode.setParentNode(oldParentNode.getParentNode());
                break;
            }
        }
    }

    private void addChildernToEventList(QNode<K, T> parentNode) {
        QNode<K, T>[] childern = parentNode.getChildern();
        for (int i = 0; i < childern.length; i++) {
            if (childern[i] != null) {
                addToEventList(childern[i], parentNode, 0, getNodeType(i));
                addChildernToEventList(childern[i]);
            }
        }
    }

    private IMTNode<K, T> find(K key, QNode<K, T> node) {
        if (node == null) {
            return null;
        }

        if (node.isLeafNode()) {
            if (node.getNode().getKey().getX().equals(key.getX()) && node.getNode().getKey().getY().equals(key.getY())) {
                addToEventList(node, null, 0, TreeNodeType.ntFinish);
                return node.getNode();
            } else {
                return null;
            }
        } else {
            if (key.getX() >= node.getNode().getKey().getX()) { // 2 x 4 quad
                if (key.getY() >= node.getNode().getKey().getY()) { //2
                    addToEventList(node.getChildern()[NE], node, 0, TreeNodeType.ntNE);
                    return find(key, node.getChildern()[NE]);
                } else { //4
                    addToEventList(node.getChildern()[SE], node, 0, TreeNodeType.ntSE);
                    return find(key, node.getChildern()[SE]);
                }
            } else { // 1 x 3 quad
                if (key.getY() >= node.getNode().getKey().getY()) { //1
                    addToEventList(node.getChildern()[NW], node, 0, TreeNodeType.ntNW);
                    return find(key, node.getChildern()[NW]);
                } else { // 3
                    addToEventList(node.getChildern()[SW], node, 0, TreeNodeType.ntSW);
                    return find(key, node.getChildern()[SW]);
                }
            }
        }
    }

    private void addToDrawList(QNode<K, T> node, QNode<K, T> parentNode, Integer nodeLevel, TreeNodeType nodeType) {
        addDraw(createBufferNode(node, parentNode, nodeLevel, nodeType));
    }


    private void addToEventList(QNode<K, T> node, QNode<K, T> parentNode, Integer nodeLevel, TreeNodeType nodeType) {
        addEvent(createBufferNode(node, parentNode, nodeLevel, nodeType));
    }

    private TreeNode createBufferNode(QNode<K, T> node, QNode<K, T> parentNode, Integer nodeLevel, TreeNodeType nodeType) {
        if (node == null) return null;
        if (nodeLevel == null) nodeLevel=0;


        TreeNode parent = null;
        String data = "null";
        NodeSortType parentSortType = NodeSortType.stX;
        if (!nodeType.equals(TreeNodeType.ntRoot)) nodeLevel++;

        if (parentNode != null) {
            if (parentNode.getNode().getData() != null) {
                data = parentNode.getNode().getData().toString();
            }

            parent = new TreeNode(new ExtendedPoint(parentNode.getNode().getKey().getX(), parentNode.getNode().getKey().getY()),
                    data, null, null, nodeLevel - 1, parentSortType);
            parent.setChildCount(parentNode.getChildCount());
            parent.setUID(parentNode.UID);
        }

        data = "null";
        if (node.getNode().getData() != null) {
            data = node.getNode().getData().toString();
        }


        TreeNode e = new TreeNode(new ExtendedPoint(node.getNode().getKey().getX(), node.getNode().getKey().getY()),
                data, parent, nodeType, nodeLevel, NodeSortType.stX);
        e.setChildCount(node.getChildCount());
        e.setUID(node.UID);
        return e;
    }

    private List<IMTNode<K, T>> sortData(List<IMTNode<K, T>> data, SortType type) {
        switch (type) {
            case stX:
                data.sort((o1, o2) -> -1 * (o1.getKey().compareX(o2.getKey().getX())));
                break;
            case stY:
                data.sort((o1, o2) -> -1 * (o1.getKey().compareY(o2.getKey().getY())));
                break;
        }

        return data;
    }

    private TreeNodeType getNodeType(int index) {
        switch (index) {
            case 0:
                return TreeNodeType.ntNW;
            case 1:
                return TreeNodeType.ntNE;
            case 2:
                return TreeNodeType.ntSW;
            case 3:
                return TreeNodeType.ntSE;
        }

        return TreeNodeType.ntRoot;
    }

    private void iterate(QNode<K, T> startNode, QNode<K, T> parent, int level, int index) {
        if (parent == null) {
            addToDrawList(startNode, null, level, TreeNodeType.ntRoot);
        } else {
            addToDrawList(startNode, parent, level - 1, getNodeType(index));
        }

        if (startNode.getChildern()[0] != null) iterate(startNode.getChildern()[0], startNode, level + 1, 0);
        if (startNode.getChildern()[1] != null) iterate(startNode.getChildern()[1], startNode, level + 1, 1);
        if (startNode.getChildern()[2] != null) iterate(startNode.getChildern()[2], startNode, level + 1, 2);
        if (startNode.getChildern()[3] != null) iterate(startNode.getChildern()[3], startNode, level + 1, 3);
    }

    public int getHorizontalBound() {
        return horizontalBound;
    }

    public void setHorizontalBound(int horizontalBound) {
        this.horizontalBound = horizontalBound;
    }

    public int getVerticalBound() {
        return verticalBound;
    }

    public void setVerticalBound(int verticalBound) {
        this.verticalBound = verticalBound;
    }

    private enum SortType {stX, stY}

    private class QNode<K extends IMTKey<Integer>, T extends IMTData<String>> implements Comparable<K> {
        private IMTNode<K, T> node;
        private boolean isLeafNode;
        private QNode<K, T> parentNode;
        private QNode<K, T>[] childern;
        private Integer zOrder;
        private String UID;

        public QNode(IMTNode<K, T> node, QNode<K, T> parentNode) {
            this.node = node;
            this.parentNode = parentNode;
            initChild();
            UID = UUID.randomUUID().toString();
        }

        public QNode(IMTNode<K, T> node, QNode<K, T> parentNode, boolean isLeafNode) {
            this.node = node;
            this.isLeafNode = isLeafNode;
            this.parentNode = parentNode;
            initChild();
            UID = UUID.randomUUID().toString();
        }

        private void initChild() {
            this.childern = new QNode[4];
            for (int i = 0; i < 4; i++) {
                childern[i] = null;
            }
        }

        public IMTNode<K, T> getNode() {
            return node;
        }

        public void setNode(IMTNode<K, T> node) {
            this.node = node;
        }

        public boolean isLeafNode() {
            return isLeafNode;
        }

        public void setLeafNode(boolean leafNode) {
            isLeafNode = leafNode;
        }

        public QNode<K, T>[] getChildern() {
            return childern;
        }

        public void setChildern(QNode<K, T>[] childern) {
            this.childern = childern;
        }


        public QNode<K, T> getParentNode() {
            return parentNode;
        }

        public void setParentNode(QNode<K, T> parentNode) {
            this.parentNode = parentNode;
        }

        public boolean equals(IMTNode<K, T> node) {
            return (this.node.getKey().getX().equals(node.getKey().getX()) && this.node.getKey().getY().equals(node.getKey().getY()));
        }

        private int getChildCount() {
            int childCount = 0;
            for (QNode<K, T> ktqNode : childern) {
                if (ktqNode != null) {
                    childCount++;
                }
            }

            return childCount;
        }


        @Override
        public int compareTo(K o) {
            return 0;
        }

        @Override
        public String toString() {
            return node.getKey() + ";" + node.getData();
        }
    }
}
