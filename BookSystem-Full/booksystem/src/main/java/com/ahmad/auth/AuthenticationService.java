package com.ahmad.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ahmad.config.JWTService;
import com.ahmad.token.Token;
import com.ahmad.token.TokenRepository;
import com.ahmad.token.TokenType;
import com.ahmad.users.Users;
import com.ahmad.users.Role;
import com.ahmad.users.UsersRepository;

import lombok.RequiredArgsConstructor;
import lombok.var;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    
    @Autowired
    private final UsersRepository userRepo;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final TokenRepository tokenRepository;

    @Autowired
    private final JWTService jwtService;

    @Autowired
    private final AuthenticationManager authenticationManager;


    
    // Register a new user
    public AuthenticationResponse register(RegisterRequest request) {
        // init new user object 
        var user = Users.builder().firstName(request.getFirstname())
        .lastName(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
        // save user to database
        var savedUser = userRepo.save(user);
        // generate token
        var token = jwtService.generateToken(user);
        // save token to database
        savetoUserToken(savedUser, token);
        return AuthenticationResponse.builder().token(token).build();
        
    }


    private void savetoUserToken(Users savedUser, String JWTtoken) {

        // init token object
        var token = Token.builder()
        .user(savedUser)
        .token(JWTtoken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
        tokenRepository.save(token);
    }


// Admin registration
    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        var user = Users.builder().firstName(request.getFirstname())
        .lastName(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.ADMIN)
        .build();
        var savedUser = userRepo.save(user);
        var token = jwtService.generateToken(user);
        savetoUserToken(savedUser, token);
        return AuthenticationResponse.builder().token(token).build();
        
    }

    // Authenticate user
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtService.generateToken(user);
        removeUserTokens(user);
        savetoUserToken(user, token);
        return AuthenticationResponse.builder().token(token).build();


        
    }

    

    // Remove user tokens
    private void removeUserTokens(Users user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
          return;
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
          token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
      }
}
