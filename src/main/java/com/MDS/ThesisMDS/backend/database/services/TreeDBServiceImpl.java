package com.MDS.ThesisMDS.backend.database.services;


import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.repository.TreeDBRepository;
import com.MDS.ThesisMDS.backend.utils.OffsetBasedPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class TreeDBServiceImpl implements TreeDBService {
    private static final long serialVersionUID = -2006622339916372647L;
    @Autowired
    private TreeDBRepository repository;

    @Override
    public List<TreeDB> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<TreeDB> findAll(int offset, int limit) {
        return repository.findAll(new OffsetBasedPageRequest(offset, limit));
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void save(TreeDB tree) {
        repository.saveAndFlush(tree);
    }

    @Override
    public void saveKeep(TreeDB tree) {
        repository.save(tree);
    }

    @Override
    public void delete(TreeDB tree) {
        repository.delete(tree);
    }

    @Override
    public TreeDB findById(Integer id) {
        return repository.findById(id);
    }
}
