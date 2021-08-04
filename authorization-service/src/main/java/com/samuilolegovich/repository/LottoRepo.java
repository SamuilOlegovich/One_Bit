package com.samuilolegovich.repository;


import com.samuilolegovich.domain.Lotto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LottoRepo extends CrudRepository<Lotto, Long> {
    Lotto findFirstByOrderByCreatedAtDesc();
}
