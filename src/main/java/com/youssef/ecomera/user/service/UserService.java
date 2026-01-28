package com.youssef.ecomera.user.service;

import com.youssef.ecomera.user.dto.ChangePasswordRequest;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Change the password of the connected user
     *
     * @param req           data carrier object containing the old and new password
     * @param connectedUser the principal is the currently logged in user. However, you retrieve it through the security context which is bound to the current thread and as such, it's also bound to the current request and its session.
     */
    public void changePassword(ChangePasswordRequest req, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // Check if the new password is not the same as the old password
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong Password");
        }
        // Check the new password is not the same as the old password
        if (!req.newPassword().equals(req.confirmPassword())) {
            throw new IllegalStateException("Confirmation Password and New Password do not match");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(req.newPassword()));

        // Save changes
        userRepository.save(user);

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getConnectedUser(Principal connectedUser) {
        return (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
    }

    public List<String> getConnectedUserRoles(Principal connectedUser) {
        return getConnectedUser(connectedUser).getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
