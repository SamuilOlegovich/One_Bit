package com.samuilolegovich.repository;


import com.samuilolegovich.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends CrudRepository<User, Long> {
    User findById(long id);
}
