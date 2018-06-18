package com.MDS.ThesisMDS.backend.database.services;


import com.MDS.ThesisMDS.backend.database.model.OperationsDB;
import com.MDS.ThesisMDS.backend.database.repository.OperationsDBRepository;
import com.MDS.ThesisMDS.backend.utils.OffsetBasedPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class OperationsDBServiceImpl implements OperationsDBService {
    private static final long serialVersionUID = -2006622339916372647L;
    @Autowired
    private OperationsDBRepository repository;

    @Override
    public List<OperationsDB> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<OperationsDB> findAll(int offset, int limit) {
        return repository.findAll(new OffsetBasedPageRequest(offset, limit));
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void save(OperationsDB operation) {
        repository.saveAndFlush(operation);
    }

    @Override
    public void delete(OperationsDB operation) {
        repository.delete(operation);
    }

    @Override
    public OperationsDB findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Integer getTreeOperationCount(Integer treeId) {
        return repository.getTreeOperationCount(treeId);
    }

    @Override
    public OperationsDB findTopByIdOrderByIdAsc() {
        return repository.getTopRow();
    }
}
