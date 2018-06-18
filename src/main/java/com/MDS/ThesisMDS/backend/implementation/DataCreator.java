package com.MDS.ThesisMDS.backend.implementation;

import com.MDS.ThesisMDS.backend.implementation.interfaces.IMTNode;
import com.MDS.ThesisMDS.backend.implementation.objects.Data;
import com.MDS.ThesisMDS.backend.implementation.objects.Key;
import com.MDS.ThesisMDS.backend.implementation.objects.Node;

import java.util.ArrayList;
import java.util.List;

public class DataCreator {
    public static List<IMTNode<Key, Data>> createNodeList(Integer datasetIndex) {
        List<IMTNode<Key, Data>> nodeList = new ArrayList<>();
        switch (datasetIndex) {
            case 0:
            case 1:
                nodeList.add(new Node(new Key(57, 77), new Data("Toronto")));
                nodeList.add(new Node(new Key(31, 45), new Data("Denver")));
                nodeList.add(new Node(new Key(30, 20), new Data("Nashville")));
                nodeList.add(new Node(new Key(35, 16), new Data("Omaha")));
                nodeList.add(new Node(new Key(55, 42), new Data("Calgary")));
                nodeList.add(new Node(new Key(60, 50), new Data("Austin")));
                nodeList.add(new Node(new Key(52, 10), new Data("Mobile")));
                nodeList.add(new Node(new Key(41, 17), new Data("New York")));
                break;
            case 2:
                nodeList.add(new Node(new Key(57, 77), new Data("Toronto")));
                nodeList.add(new Node(new Key(31, 45), new Data("Denver")));
                nodeList.add(new Node(new Key(84, 65), new Data("Buffalo")));
                nodeList.add(new Node(new Key(35, 16), new Data("Omaha")));
                nodeList.add(new Node(new Key(35, 42), new Data("Chicago")));
                nodeList.add(new Node(new Key(85, 15), new Data("Atlanta")));
                nodeList.add(new Node(new Key(90, 5), new Data("Miami")));
                nodeList.add(new Node(new Key(52, 10), new Data("Mobile")));
                nodeList.add(new Node(new Key(55, 42), new Data("Calgary")));
                nodeList.add(new Node(new Key(41, 17), new Data("New York")));
                nodeList.add(new Node(new Key(38, 57), new Data("Seattle")));
                nodeList.add(new Node(new Key(33, 44), new Data("Portland")));
                break;
            case 3:
                nodeList.add(new Node(new Key(57, 77), new Data("Toronto")));
                nodeList.add(new Node(new Key(31, 45), new Data("Denver")));
                nodeList.add(new Node(new Key(84, 65), new Data("Buffalo")));
                nodeList.add(new Node(new Key(35, 16), new Data("Omaha")));
                nodeList.add(new Node(new Key(35, 42), new Data("Chicago")));
                nodeList.add(new Node(new Key(85, 15), new Data("Atlanta")));
                nodeList.add(new Node(new Key(90, 5), new Data("Miami")));
                nodeList.add(new Node(new Key(52, 10), new Data("Mobile")));
                nodeList.add(new Node(new Key(55, 42), new Data("Calgary")));
                nodeList.add(new Node(new Key(41, 17), new Data("New York")));
                nodeList.add(new Node(new Key(38, 57), new Data("Seattle")));
                nodeList.add(new Node(new Key(33, 44), new Data("Portland")));
                nodeList.add(new Node(new Key(40, 60), new Data("Aspen")));
                nodeList.add(new Node(new Key(30, 20), new Data("Nashville")));
                nodeList.add(new Node(new Key(60, 50), new Data("Austin")));
                nodeList.add(new Node(new Key(65, 64), new Data("Taos")));
                break;
            default:
                break;
        }

        return nodeList;
    }
}
