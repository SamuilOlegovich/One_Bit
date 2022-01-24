package com.samuilolegovich.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long playerId;
    private String userName;
    private String password;
    private boolean enable;
    private LocalDateTime lastLoginTimestamp;
    private List<GrantedAuthority> grantedAuthorities;

    public UserDetailsImpl(Player player) {
        this.userId = player.getId();
        this.userName = player.getUsername();
        this.password = player.getPassword();
        this.enable = player.isEnabled();
        this.grantedAuthorities = mapToGrantedAuthority(player);
        this.lastLoginTimestamp = player.getLastLoginTimestamp();
    }

    public List<Role> getRoles() {
        return grantedAuthorities.stream().map(
                a -> Role.valueOf(a.getAuthority()))
                .collect(Collectors.toList());
    }

    private List<GrantedAuthority> mapToGrantedAuthority(UserInfo user) {
        return user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
