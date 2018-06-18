package com.MDS.ThesisMDS.backend.implementation.datastructures;

import com.MDS.ThesisMDS.backend.events.EventsBuffer;
import com.MDS.ThesisMDS.backend.implementation.interfaces.*;
import com.MDS.ThesisMDS.backend.implementation.objects.ExtendedPoint;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.NodeSortType;
import com.MDS.ThesisMDS.backend.types.TreeNodeType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/*

    Point Quad Tree implementation.
    Splitting region in selected point
    Bulk loading - sort order by ZCurve
    Key must implements IMTKey interface with x and y coordination (integer)
    Data must implements IMTData with string value for print, generics for data
 */

public class PointBasedQuadTree<K extends IMTKey<Integer>, T extends IMTData<String>> implements IAnimatableStructure<K,T> {
    private static final int NW = 0;
    private static final int NE = 1;
    private static final int SW = 2;
    private static final int SE = 3;
    private QNode<K, T> root;
    private EventsBuffer buffer;
    private int nodeCount;


    public PointBasedQuadTree() {
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
            root = new QNode<>(nodeList.get(0), true);
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
        QNode<K, T> ktqNode = find(key, root);
        if (ktqNode != null) return ktqNode.getNode().getData();
        return null;
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


    public T remove(K key) {
        buffer.flushEvent();

        if (!isEmpty()) {
            buffer.flushDraw();
            iterate(root, null, 0, 0);
        }

        addToEventList(root, null, 0, TreeNodeType.ntRoot);
        QNode<K, T> ktqNode = find(key, root);
        if (ktqNode != null) {
            IMTNode<K, T> remove = remove(ktqNode);
            if (remove != null) return remove.getData();
        }
        return null;
    }

    //The Z-ordering can be used to efficiently build a quadtree for a set of points
    private void bulkLoad(List<IMTNode<K, T>> nodeList) {
        if ((nodeList != null) && (nodeList.size() == 0)) return;
        List<QNode<K, T>> nodeZORDER = new ArrayList<>();

        for (IMTNode<K, T> node : nodeList) {
            QNode<K, T> ktqNode = new QNode<>(node, true);
            ktqNode.zOrder = calculateZOrderValue(ktqNode);
            nodeZORDER.add(ktqNode);
        }

        Collections.sort(nodeZORDER, Comparator.comparing(o -> o.zOrder));

        for (QNode<K, T> ktqNode : nodeZORDER) {
            insert(ktqNode.getNode(), root, 0, true);
        }
    }


    //same as BST only 4 way comparsion by X & Y
    private IMTNode<K, T> insert(IMTNode<K, T> node, QNode<K, T> actualNode, int level, boolean bulkLoading) {
        if (root == null) { //empty tree
            root = new QNode(node, true);
            if (bulkLoading) {
                addToDrawList(root, null, level, TreeNodeType.ntRoot);
            } else {
                addToEventList(root, null, level, TreeNodeType.ntRoot);
            }
            return root.getNode();
        }

        int myChild = 0;
        //detect the position
        if (node.getKey().getX() >= actualNode.getNode().getKey().getX()) { // 2 x 4 quad
            if (node.getKey().getY() >= actualNode.getNode().getKey().getY()) { //2
                myChild = NE;
            } else { //4
                myChild = SE;
            }
        } else { // 1 x 3 quad
            if (node.getKey().getY() >= actualNode.getNode().getKey().getY()) { //1
                myChild = NW;
            } else { // 3
                myChild = SW;
            }
        }


        if (actualNode.getChildern()[myChild] == null) {
            if (actualNode.isLeafNode()) actualNode.setLeafNode(false);
            actualNode.getChildern()[myChild] = new QNode<>(node, true);
            actualNode.getChildern()[myChild].setParent(actualNode);
            if (bulkLoading)
                addToDrawList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));
            else
                addToEventList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));
            return actualNode.getChildern()[myChild].getNode();
        } else {
            if (!bulkLoading)
                addToEventList(actualNode.getChildern()[myChild], actualNode, level, TreeNodeType.getQuadNodeType(myChild));
            return insert(node, actualNode.getChildern()[myChild], level + 1, bulkLoading);
        }
    }


    //again BST only 4 way comparsion
    private QNode<K, T> find(K key, QNode<K, T> node) {
        if (node == null) {
            return null;
        }

        if (key.getX().equals(node.getNode().getKey().getX()) && key.getY().equals(node.getNode().getKey().getY())) {
            addToEventList(node, null, 0, TreeNodeType.ntFinish);
            return node;
        }

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

    private TreeNodeType getNodeRegion(int index) {
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

    private ArrayList<IMTNode<K, T>> toList(QNode<K, T> startNode, ArrayList<IMTNode<K, T>> outList) {
        if (outList == null) {
            outList = new ArrayList<IMTNode<K, T>>();
        }

        outList.add(startNode.getNode());

        if (startNode.getChildern()[0] != null) toList(startNode.getChildern()[0], outList);
        if (startNode.getChildern()[1] != null) toList(startNode.getChildern()[1], outList);
        if (startNode.getChildern()[2] != null) toList(startNode.getChildern()[2], outList);
        if (startNode.getChildern()[3] != null) toList(startNode.getChildern()[3], outList);

        return outList;
    }

    private void iterate(QNode<K, T> startNode, QNode<K, T> parent, int level, int index) {
        if (parent == null) {
            addToDrawList(startNode, null, level, TreeNodeType.ntRoot);
        } else {
            addToDrawList(startNode, parent, level - 1, getNodeRegion(index));
        }

        if (startNode.getChildern()[0] != null) iterate(startNode.getChildern()[0], startNode, level + 1, 0);
        if (startNode.getChildern()[1] != null) iterate(startNode.getChildern()[1], startNode, level + 1, 1);
        if (startNode.getChildern()[2] != null) iterate(startNode.getChildern()[2], startNode, level + 1, 2);
        if (startNode.getChildern()[3] != null) iterate(startNode.getChildern()[3], startNode, level + 1, 3);
    }

    private IMTNode<K, T> remove(QNode<K, T> node) {
        if (node == null) {
            return null;
        }

        if (node.isLeafNode()) {
            if (node.getParent() != null) {
                for (int i = 0; i < node.getParent().getChildern().length; i++) {
                    if (node.equals(node.getParent().getChildern()[i])) {
                        addToEventList(node, node.getParent(), 0, TreeNodeType.ntRemove);
                        node.getParent().getChildern()[i] = null;
                        if (node.getParent().getChildCount() == 0) {
                            node.getParent().setLeafNode(true);
                        }
                        return node.getNode();
                    }
                }
            }
        } else {
            ArrayList<IMTNode<K, T>> qNodes = toList(root, null);
            qNodes.remove(node.getNode());
            clear();
            build(qNodes);
        }

        return null;


        //NEBUDE POUZITO
        /*if (node.getParent() == null) { //removing root node
            QNode<K, T> bestFitNode = bestFitByCloserAxes(node);
            if (bestFitNode == null) bestFitNode = bestFitByManhattan(node);
            addToEventList(root, null, 0, TreeNodeType.ntRemove);
            for (int parentChildIndex = 0; parentChildIndex < root.childern.length; parentChildIndex++) { //set parent childern on position of removed node
                if (root.getChildern()[parentChildIndex] != null && root.getChildern()[parentChildIndex].equals(bestFitNode)) {
                    addToEventList(bestFitNode, root, 0, getNodeRegion(parentChildIndex));
                    break;
                }
            }
            root = bestFitNode;
            return node.getNode();

        } else if (node.getChildCount() == 0) {
            for (int parentChildIndex = 0; parentChildIndex < node.getParent().getChildern().length; parentChildIndex++) { //no childers we dont care
                QNode<K, T> actualChildern = node.getParent().getChildern()[parentChildIndex];
                if (actualChildern != null && actualChildern.equals(node)) {
                    addToEventList(node, node.getParent(), 0, TreeNodeType.ntRemove);
                    node.getParent().getChildern()[parentChildIndex] = null;
                    IMTNode<K, T> removedNode = node.getNode();
                    return removedNode;
                }
            }

        } else if (node.getChildCount() == 1) { //node with only 1 child
            for (int childIndex = 0; childIndex < node.getChildern().length; childIndex++) {
                QNode<K, T> swapNode = node.getChildern()[childIndex];
                if (swapNode != null) {
                    swapNode.setParent(node.getParent()); //new node set parent level +1
                    int index = 0;
                    for (int parentChildIndex = 0; parentChildIndex < node.getParent().getChildern().length; parentChildIndex++) { //set parent childern on position of removed node
                        QNode<K, T> actualChildern = node.getParent().getChildern()[parentChildIndex];
                        if (actualChildern != null && actualChildern.equals(node)) {
                            node.getParent().getChildern()[parentChildIndex] = swapNode;
                            index = parentChildIndex;
                            break;
                        }
                    }

                    addToEventList(node, node.getParent(), 0, TreeNodeType.ntRemove);
                    addToEventList(node.getParent().getChildern()[index], node.getParent(), 0, getNodeRegion(index));
                    node.setParent(null);
                    IMTNode<K, T> removedNode = node.getNode();
                    node = null;
                    return removedNode;
                }
            }

        } else { //while we have more childs //TODO nasli jsme pouze prvek, ktery se stane novym smazanym prvkem ale smazany prvek může mít vlastní syny a zároveň naleznutý prvek může mít svoje syny == až 7 nodu
            //try to find the best candidate by the closer to bordering axes or by  secondly is same side on axes
            QNode<K, T> bestFitNode = bestFitByCloserAxes(node);
            //if node not founds select node by City Block Metric
            if (bestFitNode == null) bestFitNode = bestFitByManhattan(node);

            int index = 0;
            if (bestFitNode != null) {
                bestFitNode.setParent(node.getParent());
                for (int parentChildIndex = 0; parentChildIndex < node.getParent().getChildern().length; parentChildIndex++) { //set parent childern on position of removed node
                    if (node.getParent().getChildern()[parentChildIndex] != null && node.getParent().getChildern()[parentChildIndex].equals(node)) {
                        node.getParent().getChildern()[parentChildIndex] = bestFitNode;
                        index = parentChildIndex;
                        break;
                    }
                }
                addToEventList(node, node.getParent(), 0, TreeNodeType.ntRemove);
                addToEventList(node.getParent().getChildern()[index], node.getParent(), 0, getNodeRegion(index));
                node.setParent(null);
                IMTNode<K, T> removedNode = node.getNode();
                node = null;
                return removedNode;
            }

        }
        */
    }

    private QNode<K, T> bestFitByCloserAxes(QNode<K, T> actualNode) {
        Integer minimumX = Integer.MAX_VALUE;
        Integer minimumY = Integer.MAX_VALUE;
        Integer actualNodeIndeX = -1;
        Integer actualNodeIndeY = -1;
        Integer xValue = actualNode.getNode().getKey().getX();
        Integer yValue = actualNode.getNode().getKey().getY();
        Integer multipleIndex = -1;

        for (int i = 0; i < actualNode.getChildern().length; i++) {
            QNode<K, T> actualChildern = actualNode.getChildern()[i];
            if (actualChildern != null) {
                //pred tim nez zkontroluju jestli je mensi
                if (minimumX == calculateDistance(actualChildern.getNode().getKey().getX(), xValue) && minimumY == calculateDistance(actualChildern.getNode().getKey().getX(), yValue)) {
                    multipleIndex = i;
                }

                if (minimumX > calculateDistance(actualChildern.getNode().getKey().getX(), xValue)) {
                    minimumX = calculateDistance(actualChildern.getNode().getKey().getX(), xValue);
                    actualNodeIndeX = i;
                    multipleIndex = -1;
                }

                if (minimumY > calculateDistance(actualChildern.getNode().getKey().getY(), yValue)) {
                    minimumY = calculateDistance(actualChildern.getNode().getKey().getY(), yValue);
                    actualNodeIndeY = i;
                    multipleIndex = -1;
                }
            }
        }

        if (multipleIndex == -1) { //there is no multiple candidates with same distance
            if (actualNodeIndeX.equals(actualNodeIndeY)) //only if both axes are closer
                return actualNode.getChildern()[actualNodeIndeX];
        }
        return null;
    }

    private QNode<K, T> bestFitByManhattan(QNode<K, T> actualNode) {
        int minimumDistance = Integer.MAX_VALUE;
        int nodeIndex = 0;

        for (int i = 0; i < actualNode.getChildern().length; i++) {
            QNode<K, T> actualChildern = actualNode.getChildern()[i];
            if (actualChildern != null) {
                if (minimumDistance > (Math.abs(actualNode.getNode().getKey().getX() - actualChildern.getNode().getKey().getX()) + Math.abs(actualNode.getNode().getKey().getY() - actualChildern.getNode().getKey().getX()))) {
                    minimumDistance = Math.abs(actualNode.getNode().getKey().getX() - actualChildern.getNode().getKey().getX()) + Math.abs(actualNode.getNode().getKey().getY() - actualChildern.getNode().getKey().getX());
                    nodeIndex = i;
                }
            }
        }

        return actualNode.getChildern()[nodeIndex];
    }

    private Integer calculateDistance(Integer one, Integer two) {
        if (one > two) {
            return one - two;
        } else {
            return two - one;
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
            parent.setUID(parentNode.getUID());
        }

        data = "null";
        if (node.getNode().getData() != null) {
            data = node.getNode().getData().toString();
        }


        TreeNode e = new TreeNode(new ExtendedPoint(node.getNode().getKey().getX(), node.getNode().getKey().getY()),
                data, parent, nodeType, nodeLevel, NodeSortType.stX);
        e.setChildCount(node.getChildCount());
        e.setUID(node.getUID());
        return e;
    }

    private class QNode<K extends IMTKey<Integer>, T extends IMTData<String>> implements Comparable<K> {
        private IMTNode<K, T> node;
        private boolean isLeafNode;
        private QNode<K, T>[] childern;
        private Integer zOrder;
        private QNode<K, T> parent;
        private String UID;

        public QNode(IMTNode<K, T> node) {
            this.node = node;
            initChild();
            UID = UUID.randomUUID().toString();
        }

        public QNode(IMTNode<K, T> node, boolean isLeafNode) {
            this.node = node;
            this.isLeafNode = isLeafNode;
            initChild();
            UID = UUID.randomUUID().toString();
        }

        private void initChild() {
            this.childern = new QNode[4];
            for (int i = 0; i < 4; i++) {
                childern[i] = null;
            }
        }


        public Integer getzOrder() {
            return zOrder;
        }

        public void setzOrder(Integer zOrder) {
            this.zOrder = zOrder;
        }

        public QNode<K, T> getParent() {
            return parent;
        }

        public void setParent(QNode<K, T> parent) {
            this.parent = parent;
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

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
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
