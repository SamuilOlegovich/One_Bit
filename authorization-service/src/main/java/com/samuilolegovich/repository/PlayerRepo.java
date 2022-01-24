package com.samuilolegovich.repository;


import com.samuilolegovich.domain.Player;
import com.samuilolegovich.dto.ChangePasswordTokenDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlayerRepo extends CrudRepository<Player, Long> {
    Player findById(long id);
    Player findByResetPasswordToken(String token);
}
