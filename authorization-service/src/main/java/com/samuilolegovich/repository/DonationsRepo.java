package com.samuilolegovich.repository;

import com.samuilolegovich.domain.Donations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DonationsRepo extends CrudRepository<Donations, Long> {
    Donations findFirstByOrderByCreatedAtDesc();
}
