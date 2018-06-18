package com.MDS.ThesisMDS.backend.database.repository;

import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreeNodeDBRepository extends JpaRepository<TreeNodeDB, Long> {
    TreeNodeDB findById(Integer id);

    List<TreeNodeDB> findAllByTree(TreeDB tree);

    List<TreeNodeDB> findAllByTreeAndDataNotLike(TreeDB tree, String data);

    List<TreeNodeDB> findAllByTreeAndDeletedNot(TreeDB tree, Boolean deleted);

    List<TreeNodeDB> findAllByTreeAndDeletedNotAndDataNotLike(TreeDB tree, Boolean deleted, String data);

    void deleteAllByTree(TreeDB tree);
}

