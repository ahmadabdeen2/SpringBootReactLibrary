package com.ahmad.auth;

import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ahmad.config.JWTService;
import com.ahmad.token.Token;
import com.ahmad.token.TokenRepository;
import com.ahmad.users.Users;
import com.ahmad.users.UsersRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsersRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JWTService jwtService;

    private AuthenticationService authService;

    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(userRepo, passwordEncoder, tokenRepository, jwtService, authenticationManager);
    }

    /**
     * Test method for {@link com.ahmad.auth.AuthenticationService#register(com.ahmad.auth.RegisterRequest)}.
     * 
     */
    @Test
    void registerUserSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("test", "last", "John@email.com", "Doe123");
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepo.save(any())).thenReturn(new Users());
        when(jwtService.generateToken(any())).thenReturn("sample_token");
        when(tokenRepository.save(any())).thenReturn(new Token());
        
        AuthenticationResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("sample_token", response.getToken());
        verify(userRepo).findByEmail("John@email.com");
        verify(passwordEncoder).encode("Doe123");
        verify(userRepo).save(any());
        verify(jwtService).generateToken(any());
        verify(tokenRepository).save(any());
    }
}