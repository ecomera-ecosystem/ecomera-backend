package com.youssef.ecomera.auth.service;

import com.youssef.ecomera.auth.entity.Token;
import com.youssef.ecomera.auth.repository.TokenRepository;
import com.youssef.ecomera.user.entity.User;
import com.youssef.ecomera.utils.TestSuiteUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    TokenRepository tokenRepository;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    Authentication authentication;

    @InjectMocks
    LogoutService logoutService;

    private User user;

    @BeforeEach
    void setUp() {
        user = TestSuiteUtils.createUser();
    }

    @Test
    void shouldLogoutSuccessfully() {
        Token token = TestSuiteUtils.createBearerToken(user, "valid-token");

        given(request.getHeader("Authorization")).willReturn("Bearer valid-token");
        given(tokenRepository.findByValue("valid-token")).willReturn(Optional.of(token));

        logoutService.logout(request, response, authentication);

        assertThat(token.isExpired()).isTrue();
        assertThat(token.isRevoked()).isTrue();
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void shouldDoNothingWhenAuthHeaderIsNull() {
        given(request.getHeader("Authorization")).willReturn(null);

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, never()).findByValue(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void shouldDoNothingWhenAuthHeaderDoesNotStartWithBearer() {
        given(request.getHeader("Authorization")).willReturn("Basic sometoken");

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, never()).findByValue(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void shouldDoNothingWhenTokenNotFoundInDatabase() {
        given(request.getHeader("Authorization")).willReturn("Bearer unknown-token");
        given(tokenRepository.findByValue("unknown-token")).willReturn(Optional.empty());

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, never()).save(any());
    }
}