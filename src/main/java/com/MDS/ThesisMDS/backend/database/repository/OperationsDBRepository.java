package com.MDS.ThesisMDS.backend.database.repository;

import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationsDBRepository extends JpaRepository<OperationsDB, Long> {
    OperationsDB findById(Integer id);

    @Query(value = "select count(tree_id) as operation_count from \n" +
            "  (select operationsdb.id as operationId, tree_nodedb.id as nodeId, tree_nodedb.tree_id\n" +
            "   from operationsdb\n" +
            "   inner JOIN tree_nodedb on operationsdb.node_id = tree_nodedb.id)\n" +
            "    as joinTable\n" +
            "where tree_id=:treeIdparam" +
            "   GROUP BY tree_id", nativeQuery = true)
    Integer getTreeOperationCount(@Param("treeIdparam") Integer treeId);

    @Query(value = "SELECT * FROM operationsdb ORDER BY id ASC Limit 1", nativeQuery = true)
    OperationsDB getTopRow();
}
