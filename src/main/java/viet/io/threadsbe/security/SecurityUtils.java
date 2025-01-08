package viet.io.threadsbe.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class SecurityUtils {
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetails) {
            return ((AuthUserDetails) authentication.getPrincipal()).getId();
        }
        throw new RuntimeException("User not authenticated");
    }
}
