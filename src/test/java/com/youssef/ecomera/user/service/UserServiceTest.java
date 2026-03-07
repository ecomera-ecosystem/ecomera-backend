package com.youssef.ecomera.user.service;

import com.youssef.ecomera.user.dto.ChangePasswordRequest;
import com.youssef.ecomera.user.dto.UserDto;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.mapper.UserMapper;
import com.youssef.ecomera.user.repository.UserRepository;
import com.youssef.ecomera.utils.TestSuiteUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserMapper userMapper;

    @InjectMocks
    UserService userService;

    private User user;
    private UsernamePasswordAuthenticationToken principal;

    @BeforeEach
    void setUp() {
        user = TestSuiteUtils.createUser();
        user.setPassword("encodedOldPassword");
        principal = TestSuiteUtils.createPrincipal(user);
    }

    // ─── changePassword ───────────────────────────────────────────────────────────

    @Test
    void shouldChangePasswordSuccessfully() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword123", "newPassword123");

        given(passwordEncoder.matches("oldPassword", "encodedOldPassword")).willReturn(true);
        given(passwordEncoder.encode("newPassword123")).willReturn("encodedNewPassword");

        userService.changePassword(request, principal);

        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowWhenOldPasswordIsWrong() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "wrongPassword", "newPassword123", "newPassword123");

        given(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).willReturn(false);

        assertThatThrownBy(() -> userService.changePassword(request, principal))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Wrong Password");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowWhenConfirmPasswordDoesNotMatch() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword123", "differentPassword");

        given(passwordEncoder.matches("oldPassword", "encodedOldPassword")).willReturn(true);

        assertThatThrownBy(() -> userService.changePassword(request, principal))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("do not match");

        verify(userRepository, never()).save(any(User.class));
    }

    // ─── getAllUsers ──────────────────────────────────────────────────────────────

    @Test
    void shouldGetAllUsersSuccessfully() {
        List<User> users = List.of(user);
        List<UserDto> expectedDtos = List.of(TestSuiteUtils.createUserDto(user));

        given(userRepository.findAll()).willReturn(users);
        given(userMapper.toDtoList(users)).willReturn(expectedDtos);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findAll();
    }

    // ─── getConnectedUser ─────────────────────────────────────────────────────────

    @Test
    void shouldGetConnectedUserSuccessfully() {
        UserDto expectedDto = TestSuiteUtils.createUserDto(user);

        given(userMapper.toDto(user)).willReturn(expectedDto);

        UserDto result = userService.getConnectedUser(principal);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.getEmail());
        verify(userMapper, times(1)).toDto(user);
    }

    // ─── getConnectedUserRoles ────────────────────────────────────────────────────

    @Test
    void shouldGetConnectedUserRolesSuccessfully() {
        List<String> roles = userService.getConnectedUserRoles(principal);

        assertThat(roles).isNotEmpty();
        assertThat(roles).contains("ROLE_USER");
    }
}