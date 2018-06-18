package com.MDS.ThesisMDS.backend.database.services;

import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OperationsDBService {
    List<OperationsDB> findAll();

    Page<OperationsDB> findAll(int offset, int limit);

    long count();

    void save(OperationsDB operation);

    void delete(OperationsDB operation);

    OperationsDB findById(Integer id);

    Integer getTreeOperationCount(Integer treeId);

    OperationsDB findTopByIdOrderByIdAsc();


}
