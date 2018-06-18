package com.MDS.ThesisMDS.backend.database.services;

import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TreeNodeDBService {
    List<TreeNodeDB> findAll();

    Page<TreeNodeDB> findAll(int offset, int limit);

    long count();

    void save(TreeNodeDB node);

    void delete(TreeNodeDB node);

    TreeNodeDB findById(Integer id);

    List<TreeNodeDB> findAllByTree(TreeDB tree);

    List<TreeNodeDB> findAllByTreeWithoutNull(TreeDB tree);

    List<TreeNodeDB> findAllByTreeWithoutDeleted(TreeDB tree);

    List<TreeNodeDB> findAllByTreeWithoutDeletedAndNull(TreeDB tree);

    void deleteAllByTree(TreeDB tree);

}
