package com.moushtario.ecomera.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

/**
 * @author Youssef
 * @version 1.0
 * @created 13/04/2025
 * @lastModified 20/05/2025
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Change the password of the connected user
     * @param req data carrier object containing the old and new password
     * @param connectedUser the principal is the currently logged in user. However, you retrieve it through the security context which is bound to the current thread and as such, it's also bound to the current request and its session.
     */
    public void changePassword(ChangePasswordRequest req, Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // Check if the new password is not the same as the old password
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
           throw new BadCredentialsException("Wrong Password");
        }
        // Check the new password is not the same as the old password
        if(!req.getNewPassword().equals(req.getConfirmPassword())){
            throw new IllegalStateException("Confirmation Password and New Password do not match");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));

        // Save changes
        userRepository.save(user);

    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public User getConnectedUser(Principal connectedUser) {
        return (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
    }

    public List<String> getConnectedUserRoles(Principal connectedUser) {
        return getConnectedUser(connectedUser).getAuthorities()
                .stream()
                .map(role -> role.getAuthority())
                .toList();
    }
}
