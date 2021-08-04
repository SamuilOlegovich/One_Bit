package com.samuilolegovich.repository;


import com.samuilolegovich.domain.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessagesRepo extends CrudRepository<Player, Long> {
}
