package com.MDS.ThesisMDS.frontend.gwt.client.utils;

import com.MDS.ThesisMDS.frontend.gwt.client.objects.*;
import com.google.gwt.json.client.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Setting objects from backend to client-side front end
 **/

public class ObjectCreator {

    private static final String delimiter = "#!#";
    private List<DrawNode> tree;
    private List<DrawNode> eventList;

    public ObjectCreator() {
        tree = new ArrayList<>();
        eventList = new ArrayList<>();
    }

    private static String unhash(String hashed, HashIndex index) {
        String[] split = null;

        if (hashed.contains(delimiter)) {
            split = hashed.split(delimiter);
        } else {
            return hashed;
        }

        switch (index) {
            case hiClass:
                return split[0];
            case hiWidth:
                return split[1];
            case hiHeight:
                return split[2];
            case hiEventType:
                return split[3];
            case hiTree:
                return split[4];
            case hiEvent:
                return split[5];
            case hiNodes:
                return split[6];
        }

        return "null";
    }

    public String unhashClassName(String hashed) {
        return unhash(hashed, HashIndex.hiClass);
    }

    public Integer unhashWidth(String hashed) {
        return Integer.valueOf(unhash(hashed, HashIndex.hiWidth));
    }

    public Integer unhashHeight(String hashed) {

        return Integer.valueOf(unhash(hashed, HashIndex.hiHeight));
    }

    public List<Point> unhashPointList(String hashed) {
        String unhash = unhash(hashed, HashIndex.hiNodes);
        if (unhash.toLowerCase().trim().equals("null")) {
            return new ArrayList<>();
        }
        JSONValue json = JSONParser.parseStrict(unhash);
        if (json != null) {
            return JsonToNodeList(json);
        } else return new ArrayList<>();
    }

    private List<Point> JsonToNodeList(JSONValue json) {
        List<Point> points = new ArrayList<>();
        JSONArray array;
        JSONObject object;

        if ((array = json.isArray()) != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONValue object1 = array.get(i);
                if (object1 != null) {
                    object = object1.isObject();
                    if (object == null) {
                        continue;
                    }
                    Point point = createPoint(object);
                    if (point != null) points.add(point);
                }
            }
        } else if ((object = json.isObject()) != null) { //single node
            Point point = createPoint(object);
            if (point != null) points.add(point);
        }

        return points;
    }

    private Point createPoint(JSONObject object) {
        JSONValue x = object.get("x");
        JSONValue y = object.get("y");
        if (x != null && y != null) {
            Double v = x.isNumber().doubleValue();
            Double w = y.isNumber().doubleValue();
            Integer xValue = v.intValue();
            Integer yValue = w.intValue();
            return new Point(xValue, yValue);
        }
        return null;
    }

    public List<DrawNode> unhashTree(String hashed) {
        String unhash = unhash(hashed, HashIndex.hiTree);
        if (unhash.toLowerCase().trim().equals("null")) {
            return new ArrayList<>();
        }
        JSONValue json = JSONParser.parseStrict(unhash);
        if (json != null) {
            return JSONToDrawList(json, tree);
        } else return new ArrayList<>();
    }

    public List<DrawNode> unhashEvent(String hashed) {
        String unhash = unhash(hashed, HashIndex.hiEvent);
        if (unhash.toLowerCase().trim().equals("null")) {
            return new ArrayList<>();
        }
        JSONValue json = JSONParser.parseStrict(unhash);
        if (json != null) {
            return JSONToDrawList(json, eventList);
        } else return new ArrayList<>();
    }

    public EventType unhashEventType(String hashed) {
        String unhash = unhash(hashed, HashIndex.hiEventType);
        if (unhash.toLowerCase().trim().equals("null")) {
            return EventType.etBulkLoadingAnimated;
        }

        return EventType.toEvent(unhash);
    }


    public boolean isHashed(String hashed) {
        return hashed.contains(delimiter);
    }

    private List<DrawNode> JSONToDrawList(JSONValue json, List<DrawNode> nodeList) {
        JSONArray array;
        JSONObject object;

        //more nodes
        if ((array = json.isArray()) != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONValue object1 = array.get(i);
                if (object1 != null) {
                    object = object1.isObject();
                    if (object == null) {
                        continue;
                    }
                    createNode(object, nodeList);
                }
            }
        } else if ((object = json.isObject()) != null) { //single node
            createNode(object, nodeList);
        }

        return nodeList;
    }

    private DrawNode createNode(JSONObject node, List<DrawNode> nodeList) {
        DrawNode actualNode = new DrawNode();
        JSONValue value = null;

        //data
        value = node.get("data");
        if (value != null) {
            JSONString string = value.isString();
            if (string != null) {
                actualNode.setData(string.stringValue());
            }
        }

        //location
        value = node.get("loc");
        if (value != null) {
            JSONObject object1 = value.isObject();
            if (object1 != null) {
                JSONValue x = object1.get("x");
                JSONValue y = object1.get("y");
                if (x != null && y != null) {
                    Double v = x.isNumber().doubleValue();
                    Double w = y.isNumber().doubleValue();
                    Integer xValue = v.intValue();
                    Integer yValue = w.intValue();
                    Point point = new Point(xValue, yValue);
                    actualNode.setDataLocation(point);
                }
            }
        }

        //parent only 1 time
        if (nodeList != null) {
            value = node.get("parent");
            if (value != null) {
                JSONObject object = value.isObject();
                if (object != null) {
                    DrawNode parent = createNode(object, null);
                    if (parent != null) {
                        actualNode.setParent(parent);
                    }
                }
            }
        }

        //node type
        value = node.get("nodeType");
        if (value != null) {
            JSONString string = value.isString();
            if (string != null) {
                String nodeType = string.stringValue();
                if (nodeType != null) {
                    NodeType nodeType1 = NodeType.GetNodeTypeByString(nodeType);
                    if (nodeType1 != null) {
                        actualNode.setNodeType(nodeType1);
                    }
                }
            }
        }

        //node level
        value = node.get("nodeLevel");
        if (value != null) {
            JSONNumber number = value.isNumber();
            if (number != null) {
                Double v = number.doubleValue();
                actualNode.setLevel(v.intValue());
            }
        }

        //node sort type
        value = node.get("nodeSortType");
        actualNode.setSortType(NodeSortType.stX);
        if (value != null) {
            JSONString string = value.isString();
            if (string != null) {
                String nodeType = string.stringValue();
                if (nodeType != null) {
                    NodeSortType nodeType1 = NodeSortType.GetNodeTypeByString(nodeType);
                    if (nodeType1 != null) actualNode.setSortType(nodeType1);
                }
            }
        }

        //child count
        value = node.get("childCount");
        if (value != null) {
            JSONNumber number = value.isNumber();
            if (number != null) {
                Double v = number.doubleValue();
                actualNode.setChildCount(v.intValue());
            }
        }

        //bound
        value = node.get("bound");
        if (value != null) {
            JSONNumber number = value.isNumber();
            if (number != null) {
                Double v = number.doubleValue();
                actualNode.setBound(v.intValue());
            }
        }

        //uid
        value = node.get("uid");
        if (value != null) {
            JSONString number = value.isString();
            if (number != null) {
                actualNode.setUid(number.toString());
            }
        }

        //rotation type
        actualNode.setRotationType(RotationType.rtNoRotation);
        value = node.get("rotationType");
        if (value != null) {
            JSONString string = value.isString();
            if (string != null) {
                String nodeType = string.stringValue();
                if (nodeType != null) {
                    RotationType rotationType = RotationType.toEvent(nodeType);
                    if (rotationType != null) actualNode.setRotationType(rotationType);
                }
            }
        }

        //bound
        value = node.get("rotationIndex");
        if (value != null) {
            JSONNumber number = value.isNumber();
            if (number != null) {
                Double v = number.doubleValue();
                actualNode.setRotationIndex(v.intValue());
            }
        }

        if (nodeList != null) nodeList.add(actualNode);
        return actualNode;
    }

    private enum HashIndex {hiClass, hiWidth, hiHeight, hiEventType, hiTree, hiEvent, hiNodes}

}
