package com.MDS.ThesisMDS.backend.database;

import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.database.services.OperationsDBService;
import com.MDS.ThesisMDS.backend.database.services.TreeDBService;
import com.MDS.ThesisMDS.backend.database.services.TreeNodeDBService;
import com.MDS.ThesisMDS.backend.implementation.objects.TreeNode;
import com.MDS.ThesisMDS.backend.types.TreeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Database {
    @Autowired
    private TreeDBService treeDBService;
    @Autowired
    private TreeNodeDBService treeNodeDBService;
    @Autowired
    private OperationsDBService operationsDBService;

    private CheckDatabaseThread checkDatabaseThread;

    public Database() {
    }

    public Database(TreeDBService treeDBService, TreeNodeDBService treeNodeDBService, OperationsDBService operationsDBService) {
        this.treeDBService = treeDBService;
        this.treeNodeDBService = treeNodeDBService;
        this.operationsDBService = operationsDBService;
    }

    protected static void deleteTree(TreeNodeDBService service, TreeDBService service1, TreeDB tree) {
        service.deleteAllByTree(tree);
        service1.delete(tree);
    }

    private void checkDatabase() {
        if (checkDatabaseThread == null) {
            checkDatabaseThread = new CheckDatabaseThread(treeDBService, treeNodeDBService, operationsDBService);
        }

        if (!checkDatabaseThread.isAlive()) {
            checkDatabaseThread.run();
        }
    }

    public void addNodeToTree(TreeNodeDB node, TreeDB tree) {
        node.setTree(tree);
        treeNodeDBService.save(node);
    }

    public TreeDB createTree(Integer size, TreeType type, Integer maxLevel) {
        TreeDB tree = new TreeDB(size, type, maxLevel);
        treeDBService.save(tree);
        return tree;
    }

    public void addNodesToTree(TreeDB tree, List<TreeNode> nodes) {
        if (tree.getId() == null || treeDBService.findById(tree.getId()) == null) {
            treeDBService.saveKeep(tree);
        }

        for (TreeNode node : nodes) {
            node.setTree(tree);
            TreeNodeDB nodeDB = TreeNodeDB.Copy(node);
            treeNodeDBService.save(nodeDB);
            node.setId(nodeDB.getId());
        }
    }

    public void addNodesDBToTree(TreeDB tree, List<TreeNodeDB> nodes) {
        if (tree.getId() != null && treeDBService.findById(tree.getId()) == null) {
            treeDBService.save(tree);
        }


        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setTree(tree);
            treeNodeDBService.save(nodes.get(i));
        }
    }

    public void addOperationDB(OperationsDB operation, TreeDB tree, List<TreeNodeDB> items) {
        addNodesDBToTree(tree, items);

        if (operation.getNode() != null) {
            for (TreeNodeDB item : items) {
                if (item.getLoc().equals(operation.getNode().getLoc()) && item.getTree().equals(operation.getNode().getTree())) {
                    operation.setNode(item);
                    break;
                }
            }
        }

        operationsDBService.save(operation);
        checkDatabase();
    }

    public void addOperation(OperationsDB operation, TreeDB tree, List<TreeNode> items) {
        addNodesToTree(tree, items);
        boolean found = false;
        //  operation.setNode(null);

        if (operation.getNode() != null) {
            for (TreeNode item : items) {
                if (item.getLoc().getX() == operation.getNode().getLoc().getX() && item.getLoc().getX() == operation.getNode().getLoc().getX() && (item.getTree().equals(operation.getNode().getTree()) || item.getTree() == null)) {
                    TreeNodeDB nodeDB = TreeNodeDB.Copy(item);
                    nodeDB.setTree(tree);
                    operation.setNode(nodeDB);
                    found = true;
                    break;
                }
            }
        }


        operationsDBService.save(operation);
        checkDatabase();
    }

    public void addTree(TreeDB tree) {
        treeDBService.save(tree);
    }

    public List<TreeNodeDB> findAllByTree(TreeDB tree) {
        return treeNodeDBService.findAllByTree(tree);
    }

    public List<TreeNodeDB> findAllByTreeWithoutDeleted(TreeDB tree) {
        return treeNodeDBService.findAllByTreeWithoutDeleted(tree);
    }

    public List<OperationsDB> findAllOperations() {
        return operationsDBService.findAll();
    }

    public void deleteOperation(OperationsDB operation) {
        Integer treeId = operation.getNode().getTree().getId();
        Boolean deleteTree = operationsDBService.getTreeOperationCount(treeId).equals(1);
        operationsDBService.delete(operation);
        if (deleteTree) deleteTree(treeNodeDBService, treeDBService, operation.getNode().getTree());
    }

    private class CheckDatabaseThread extends Thread {
        private static final int MAXOPERATIONS = 10;
        private TreeDBService treeDBService;
        private TreeNodeDBService treeNodeDBService;
        private OperationsDBService operationsDBService;

        public CheckDatabaseThread(TreeDBService treeDBService, TreeNodeDBService treeNodeDBService, OperationsDBService operationsDBService) {
            this.treeDBService = treeDBService;
            this.treeNodeDBService = treeNodeDBService;
            this.operationsDBService = operationsDBService;
        }

        @Override
        public void run() {
            while (operationsDBService.count() > MAXOPERATIONS) {
                OperationsDB firstById = operationsDBService.findTopByIdOrderByIdAsc();
                TreeDB tree = firstById.getNode().getTree();
                Boolean deleteTree = operationsDBService.getTreeOperationCount(tree.getId()).equals(1);
                operationsDBService.delete(firstById);
                if (deleteTree) deleteTree(treeNodeDBService, treeDBService, tree);
            }
        }

    }

}
