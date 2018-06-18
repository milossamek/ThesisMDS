package com.MDS.ThesisMDS.backend.database.repository;

import com.MDS.ThesisMDS.backend.database.model.TreeDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeDBRepository extends JpaRepository<TreeDB, Long> {
    TreeDB findById(Integer id);
}
