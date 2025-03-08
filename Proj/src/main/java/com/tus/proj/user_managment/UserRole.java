package com.tus.proj.user_managment;

import org.springframework.security.core.GrantedAuthority;


public enum UserRole implements GrantedAuthority {
    GUEST("Guest"),
    USER("User"),
    ADMIN("Admin");
	
    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String getAuthority() {
    	return name(); 
    }
}
