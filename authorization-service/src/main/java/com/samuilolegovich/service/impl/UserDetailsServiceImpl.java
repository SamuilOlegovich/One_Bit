package com.samuilolegovich.service.impl;

import com.samuilolegovich.domain.security.UserDetailsImpl;
import com.samuilolegovich.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepository;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) {
        return userRepository.findByUserName(userName)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Invalid username"));
    }
}
