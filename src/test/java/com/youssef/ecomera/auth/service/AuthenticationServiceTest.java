package com.youssef.ecomera.auth.service;

import com.youssef.ecomera.auth.dto.AuthenticationRequest;
import com.youssef.ecomera.auth.dto.AuthenticationResponse;
import com.youssef.ecomera.auth.dto.CurrentUserDto;
import com.youssef.ecomera.auth.dto.RegisterRequest;
import com.youssef.ecomera.auth.entity.Token;
import com.youssef.ecomera.auth.repository.TokenRepository;
import com.youssef.ecomera.auth.security.JwtService;
import com.youssef.ecomera.common.exception.BusinessException;
import com.youssef.ecomera.common.exception.ResourceNotFoundException;
import com.youssef.ecomera.common.exception.UnauthorizedException;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.user.repository.UserRepository;
import com.youssef.ecomera.utils.TestSuiteUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    TokenRepository tokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthenticationService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = TestSuiteUtils.createUser();
    }

    // ─── Register ────────────────────────────────────────────────────────────────

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = TestSuiteUtils.createRegisterRequest();

        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtService.generateToken(user)).willReturn("access-token");
        given(jwtService.generateRefreshToken(user)).willReturn("refresh-token");

        AuthenticationResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenRepository, times(2)).save(any(Token.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyRegistered() {
        RegisterRequest request = TestSuiteUtils.createRegisterRequest();

        given(userRepository.existsByEmail(request.email())).willReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    // ─── Authenticate ─────────────────────────────────────────────────────────────

    @Test
    void shouldAuthenticateUserSuccessfully() {
        AuthenticationRequest request = TestSuiteUtils.createAuthRequest(user.getEmail());

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(jwtService.generateToken(user)).willReturn("access-token");
        given(jwtService.generateRefreshToken(user)).willReturn("refresh-token");
        given(tokenRepository.findAllValidTokenByUser(user.getId())).willReturn(List.of());

        AuthenticationResponse response = authService.authenticate(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token");
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowWhenUserNotFoundOnAuthenticate() {
        AuthenticationRequest request = TestSuiteUtils.createAuthRequest("unknown@email.com");

        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Refresh Token ────────────────────────────────────────────────────────────

    @Test
    void shouldRefreshTokenSuccessfully() {
        Token refreshToken = TestSuiteUtils.createRefreshToken(user, "valid-refresh-token");

        given(tokenRepository.findByValue("valid-refresh-token")).willReturn(Optional.of(refreshToken));
        given(jwtService.generateToken(user)).willReturn("new-access-token");
        given(tokenRepository.findAllValidTokenByUser(user.getId())).willReturn(List.of());

        AuthenticationResponse response = authService.refreshToken("Bearer valid-refresh-token");

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("valid-refresh-token");
    }

    @Test
    void shouldThrowWhenRefreshTokenIsRevoked() {
        Token revokedToken = TestSuiteUtils.createRefreshToken(user, "revoked-token");
        revokedToken.setRevoked(true);

        given(tokenRepository.findByValue("revoked-token")).willReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> authService.refreshToken("Bearer revoked-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void shouldThrowWhenRefreshTokenIsExpired() {
        Token expiredToken = TestSuiteUtils.createRefreshToken(user, "expired-token");
        expiredToken.setExpired(true);

        given(tokenRepository.findByValue("expired-token")).willReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.refreshToken("Bearer expired-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        given(tokenRepository.findByValue(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken("Bearer unknown-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    // ─── updateLastLogin ──────────────────────────────────────────────────────────

    @Test
    void shouldUpdateLastLoginSuccessfully() {
        String ip = "192.168.1.1";

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        authService.updateLastLogin(user.getEmail(), ip);

        assertThat(user.getIpAddress()).isEqualTo(ip);
        assertThat(user.getLastLogin()).isNotNull();
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdateLastLogin() {
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateLastLogin("unknown@email.com", "192.168.1.1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

// ─── whoami ───────────────────────────────────────────────────────────────────

    @Test
    void shouldReturnCurrentUserSuccessfully() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);

        given(request.getUserPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn(user.getEmail());
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        CurrentUserDto result = authService.whoami(request);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldThrowWhenPrincipalIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getUserPrincipal()).willReturn(null);

        assertThatThrownBy(() -> authService.whoami(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("No authenticated principal");
    }

    @Test
    void shouldThrowWhenUserNotFoundOnWhoami() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);

        given(request.getUserPrincipal()).willReturn(principal);
        given(principal.getName()).willReturn("unknown@email.com");
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.whoami(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}