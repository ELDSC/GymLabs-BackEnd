package com.GYMLABS.proyecto.security;

import com.GYMLABS.proyecto.model.Personal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Personal personal;

    public CustomUserDetails(Personal personal) {
        this.personal = personal;
    }

    public Personal getPersonal() {
        return personal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We prepend "ROLE_" because Spring Security often expects it for hasRole()
        String roleName = personal.getRol().getNombre();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return personal.getPassword();
    }

    @Override
    public String getUsername() {
        return personal.getCorreo();
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
        return personal.getActivo();
    }
}
