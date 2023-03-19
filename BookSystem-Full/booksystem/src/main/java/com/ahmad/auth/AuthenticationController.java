package com.ahmad.auth;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmad.config.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private final AuthenticationService service;

    @Autowired
    private final LogoutService logoutService;


    
    /**
     * Controller which calls authentication function in Authentication Service
     * @param request
     * @return ResponseEntity
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));

    }

    
    /**
     * Controller which calls register function in Authentication Service
     * @param request
     * @return ResponseEntity
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

       /**
     * Controller which calls logout function in Authentication Service
     * @param request
     * @return ResponseEntity
     */
    @PostMapping("/logout")
    public String logout( @RequestHeader("Authorization") String authHeader ){
        logoutService.CustomLogout(authHeader);
        return "Logged Out";
    }


       /**
     * Controller which calls admin register function in Authentication Service
     * @param request
     * @return ResponseEntity
     */
    @PostMapping("/register/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.registerAdmin(request));
    }
    
}
