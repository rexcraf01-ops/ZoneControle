package com.lab.zonecontrol.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private final Integer id;
    private final String username;
    private final String password;
    private final String rol;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String username, String password, String rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        java.util.Set<GrantedAuthority> auths = new java.util.HashSet<>();
        if (rol != null) {
            String clean = rol.toUpperCase().replace(" ", "_").replace("/", "_");
            auths.add(new SimpleGrantedAuthority("ROLE_" + clean));
            if (clean.contains("ADMIN")) {
                auths.add(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
                auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            if (clean.contains("GESTOR")) {
                auths.add(new SimpleGrantedAuthority("ROLE_GESTOR"));
                auths.add(new SimpleGrantedAuthority("ROLE_GESTOR_DE_PERSONAL"));
            }
            if (clean.contains("SUPERVISOR") || clean.contains("AUDITOR")) {
                auths.add(new SimpleGrantedAuthority("ROLE_SUPERVISOR"));
                auths.add(new SimpleGrantedAuthority("ROLE_AUDITOR"));
                auths.add(new SimpleGrantedAuthority("ROLE_SUPERVISOR_AUDITOR"));
            }
        }
        this.authorities = auths;
    }

    public Integer getId() { return id; }
    public String getRol() { return rol; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}