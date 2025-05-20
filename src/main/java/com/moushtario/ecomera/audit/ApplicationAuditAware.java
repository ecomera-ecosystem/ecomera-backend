package com.moushtario.ecomera.audit;

import com.moushtario.ecomera.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


public class ApplicationAuditAware implements AuditorAware<Integer> {

    /**
     * Returns the current auditor.
     * who is a logged-in user that is performing the action
     * Needed to populate the createdBy and lastModifiedBy fields in your entities
     *
     * @return the current auditor id or null if not authenticated
     */
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }

        User userPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());
    }
}
