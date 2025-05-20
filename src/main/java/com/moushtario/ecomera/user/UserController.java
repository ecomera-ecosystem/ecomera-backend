package com.moushtario.ecomera.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req, Principal connectedUser){
        userService.changePassword(req, connectedUser);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getConnectedUser(Principal connectedUser){
        return ResponseEntity.ok(userService.getConnectedUser(connectedUser));
    }

    @GetMapping("/me/roles")
    public ResponseEntity<List<String>> getConnectedUserRoles(Principal connectedUser){
        return ResponseEntity.ok(userService.getConnectedUserRoles(connectedUser));
    }


}
