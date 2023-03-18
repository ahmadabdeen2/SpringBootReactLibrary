package com.ahmad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.ahmad.token.Token;
import com.ahmad.token.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {


    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String header = request.getHeader("Authorization");
        final String JWT;
        if ((header != null || !header.startsWith("Bearer "))){
            return;
        }
        JWT = header.substring(7);
        System.out.println("Called here");
        Token Storedtoken = tokenRepository.findByToken(JWT).orElse(null);
        if (Storedtoken != null){
            System.out.println("found");
            Storedtoken.setExpired(true);
            Storedtoken.setRevoked(true);
            tokenRepository.save(Storedtoken);
            SecurityContextHolder.clearContext();
        }
        
    }

    
    public void CustomLogout(String authHeader) {
        final String header = authHeader;
        final String JWT = header.substring(7);   
        
        Token Storedtoken = tokenRepository.findByToken(JWT).orElse(null);
        if (Storedtoken != null){
            System.out.println("found");
            Storedtoken.setExpired(true);
            Storedtoken.setRevoked(true);
            tokenRepository.save(Storedtoken);
            SecurityContextHolder.clearContext();
        }
}
    
    }
