package com.samuilolegovich.dto;

import com.samuilolegovich.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long playerId;
    private String userName;
    private List<Role> roles;
}
