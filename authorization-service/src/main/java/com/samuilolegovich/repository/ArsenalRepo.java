package com.samuilolegovich.repository;


import com.samuilolegovich.domain.Arsenal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ArsenalRepo extends CrudRepository<Arsenal, Long> {
    Arsenal findFirstByOrderByCreatedAtDesc();

}
