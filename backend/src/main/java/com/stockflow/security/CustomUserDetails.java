package com.stockflow.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Long userId;
    private final Long organizationId;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                             Long userId, Long organizationId) {
        super(username, password, authorities);
        this.userId = userId;
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}
