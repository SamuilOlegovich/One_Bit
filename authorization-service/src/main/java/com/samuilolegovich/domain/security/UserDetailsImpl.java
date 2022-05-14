package com.samuilolegovich.domain.security;

import com.samuilolegovich.domain.User;
import com.samuilolegovich.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long userId;
    private String userName;
    private String password;
    private boolean enable;
    private LocalDateTime lastLoginTimestamp;
    private List<GrantedAuthority> grantedAuthorities;

    public UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.enable = user.isEnabled();
        this.grantedAuthorities = mapToGrantedAuthority(user);
        this.lastLoginTimestamp = user.getLastLoginTimestamp();
    }

    public List<Role> getRoles() {
        return grantedAuthorities.stream().map(
                a -> Role.valueOf(a.getAuthority()))
                .collect(toList());
    }

    private List<GrantedAuthority> mapToGrantedAuthority(User user) {
        return user.getRoles().stream()
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
