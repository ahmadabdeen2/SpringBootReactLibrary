package com.ahmad.bookrental;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ahmad.config.JWTService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class BookRentalController {

    @Autowired
    private final BookRentalService bookRentalService;

    @Autowired
    private final JWTService jwtService;

    @Autowired
    UserDetailsService userDetailsService;

    /**
     * Retrieves a list of book rentals for the authenticated user.
     *
     * @param httpRequest the HttpServletRequest containing the user's
     *                    authentication information.
     * @return a list of BookRentalDTO objects representing the books rented by the
     *         user.
     */
    @GetMapping("/usersbooks")
    public List<BookRentalDTO> getBooksByUser(HttpServletRequest httpRequest) {
        return bookRentalService.getBooksByUser(httpRequest);
    }

    /**
     * Retrieves a list of all book rentals. This method is only accessible to Admin
     *
     * @param authHeader the Authorization header containing the admin's
     *                   authentication information.
     * @return a list of BookRentalDTO objects representing all books rented.
     */
    @GetMapping("/userslogs")
    public List<BookRentalDTO> getAllRentals(@RequestHeader("Authorization") String authHeader) {
        String JWT = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(JWT);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        boolean valid = userDetails.getAuthorities().toString().contains("ADMIN");
        if (jwtService.isAuthTokenValid(JWT, userDetails) && valid) {
            return bookRentalService.getAllLogs();
        }
        return null;
}

}