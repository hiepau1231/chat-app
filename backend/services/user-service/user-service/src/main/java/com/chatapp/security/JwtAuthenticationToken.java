package com.chatapp.security;

import com.chatapp.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final User user;

    public JwtAuthenticationToken(User user) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        this.user = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
} 