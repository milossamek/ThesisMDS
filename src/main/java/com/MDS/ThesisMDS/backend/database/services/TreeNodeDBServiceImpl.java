package com.MDS.ThesisMDS.backend.database.services;


import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import com.MDS.ThesisMDS.backend.database.model.TreeNodeDB;
import com.MDS.ThesisMDS.backend.database.repository.TreeNodeDBRepository;
import com.MDS.ThesisMDS.backend.utils.OffsetBasedPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class TreeNodeDBServiceImpl implements TreeNodeDBService {
    private static final long serialVersionUID = -2006622339916372647L;
    @Autowired
    private TreeNodeDBRepository repository;

    @Override
    public List<TreeNodeDB> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<TreeNodeDB> findAll(int offset, int limit) {
        return repository.findAll(new OffsetBasedPageRequest(offset, limit));
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void save(TreeNodeDB node) {
        repository.saveAndFlush(node);
    }

    @Override
    public void delete(TreeNodeDB node) {
        repository.delete(node);
    }

    @Override
    public TreeNodeDB findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<TreeNodeDB> findAllByTree(TreeDB tree) {
        return repository.findAllByTree(tree);
    }

    @Override
    public List<TreeNodeDB> findAllByTreeWithoutNull(TreeDB tree) {
        return repository.findAllByTreeAndDataNotLike(tree, "null");
    }

    @Override
    public List<TreeNodeDB> findAllByTreeWithoutDeleted(TreeDB tree) {
        return repository.findAllByTreeAndDeletedNot(tree, true);
    }

    @Override
    public List<TreeNodeDB> findAllByTreeWithoutDeletedAndNull(TreeDB tree) {
        return repository.findAllByTreeAndDeletedNotAndDataNotLike(tree, true, "null");
    }

    @Override
    public void deleteAllByTree(TreeDB tree) {
        repository.deleteAllByTree(tree);
    }
}
