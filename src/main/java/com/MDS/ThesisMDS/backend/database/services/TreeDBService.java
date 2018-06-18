package com.MDS.ThesisMDS.backend.database.services;

import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TreeDBService {
    List<TreeDB> findAll();

    Page<TreeDB> findAll(int offset, int limit);

    long count();

    void save(TreeDB tree);

    void saveKeep(TreeDB tree);

    void delete(TreeDB tree);

    TreeDB findById(Integer id);
}
