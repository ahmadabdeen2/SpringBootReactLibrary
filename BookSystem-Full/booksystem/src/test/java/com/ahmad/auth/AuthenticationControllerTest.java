package com.ahmad.auth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes= {AuthenticationController.class})
public class AuthenticationControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authService;

    @Test
    void testRegister() throws Exception {
        // Prepare the request and expected response
        RegisterRequest registerRequest = new RegisterRequest("John", "Doe", "john.doe@example.com", "password");
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder().token("sample_token").build();
    
        // Configure the mock service behavior
        when(authService.register(registerRequest)).thenReturn(expectedResponse);
    
        // Perform the request and check the result
        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.postForEntity("/register", registerRequest, AuthenticationResponse.class);
    
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("sample_token", responseEntity.getBody().getToken());
    
}
}
