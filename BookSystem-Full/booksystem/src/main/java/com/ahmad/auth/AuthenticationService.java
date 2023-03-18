package com.ahmad.auth;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ahmad.config.JWTService;
import com.ahmad.exception.UserAlreadyExistsException;
import com.ahmad.exception.UserDoesNotExistException;
import com.ahmad.token.Token;
import com.ahmad.token.TokenRepository;
import com.ahmad.token.TokenType;
import com.ahmad.users.Users;
import com.ahmad.users.Role;
import com.ahmad.users.UsersRepository;

import lombok.RequiredArgsConstructor;


@Service

public class AuthenticationService {

    
    private  UsersRepository userRepo;
    private  PasswordEncoder passwordEncoder;
    private  TokenRepository tokenRepository;
    private  JWTService jwtService;
    private  AuthenticationManager authenticationManager;


    
    public AuthenticationService(UsersRepository userRepo, PasswordEncoder passwordEncoder,
            TokenRepository tokenRepository, JWTService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    // Register a new user
    /**
     * @param request
     * @return
     */
    public AuthenticationResponse register(RegisterRequest request) {
        Optional<Users> existingUser = userRepo.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("A user with the email " + request.getEmail() + " already exists.");
        }
        Users user = Users.builder().firstName(request.getFirstname())
        .lastName(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
        // save user to database
        Users savedUser = userRepo.save(user);
        // generate token
        Token token = jwtService.generateToken(user);
        // save token to database
        savetoUserToken(savedUser, token);
        return AuthenticationResponse.builder().token(token).build();
        
    }


    /**
     * Saves token with assosciated user to database
     * 
     * @param savedUser
     * @param JWTtoken
     * @return void
     * 
     * 
     */
    private void savetoUserToken(Users savedUser, String JWTtoken) {
        Token token = Token.builder()
        .user(savedUser)
        .token(JWTtoken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
        tokenRepository.save(token);
    }



    /**
     * Registers admins only. 
     * @param request
     * @return AuthenticationResponse
     * 
     */
    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        Optional<Users> existingUser = userRepo.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("A user with the email " + request.getEmail() + " already exists.");
        }
        Users user = Users.builder().firstName(request.getFirstname())
        .lastName(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.ADMIN)
        .build();
        Users savedUser = userRepo.save(user);
        Token token = jwtService.generateToken(user);
        savetoUserToken(savedUser, token);
        return AuthenticationResponse.builder().token(token).build();
        
    }

    
    /**
     * Service to authenticate which checks if user does not exist then if exists authenticates
     * @param request
     * @return AuthenticationResponse Object 
     * 
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Optional<Users> existingUser = userRepo.findByEmail(request.getEmail());
        if (!(existingUser.isPresent())) {
            throw new UserDoesNotExistException(request.getEmail() + " does not exist.");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Users user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        removeUserTokens(user);
        savetoUserToken(user, token);
        return AuthenticationResponse.builder().token(token).build();
    
    }

    


    /**
     * Sets User's tokens to revoked and expired when called
     * @param user
     */
    private void removeUserTokens(Users user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
          return;
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
          token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
      }
}
