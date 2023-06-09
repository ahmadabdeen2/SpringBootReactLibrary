package com.ahmad.books;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahmad.bookrental.BookRental;
import com.ahmad.bookrental.BookRentalDTO;
import com.ahmad.bookrental.BookRentalService;
import com.ahmad.bookrental.BookRentingRequest;
import com.ahmad.config.JWTService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import lombok.RequiredArgsConstructor;
// @RequestMapping("/api/v1/books")

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class BooksController {

    private final BooksService bookService;

    private final BookRentalService bookRentalService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JWTService jwtService;

    @Autowired
    BooksRepository booksRepo;

    @GetMapping("/api/v1/books")

    public ResponseEntity<List<BooksDTO>> getBooks() {
        List<Books> books = booksRepo.findAll();
        List<BooksDTO> booksDTOs = books.stream()
                .map(this::convertToBooksDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(booksDTOs);
        }

        private BooksDTO convertToBooksDTO(Books book) {
            return BooksDTO.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .imageURL(book.getImageURL())
                    .author(book.getAuthor())
                    .quantity(book.getQuantity())
                    .description(book.getDescription())
                    .pdfURL(book.getPdfURL())
                    .isbn(book.getIsbn())
                    
                    .build();
        }
        
    @PostMapping("/admin/add")
    public String addBook(@RequestBody BookAdditionRequest request, @RequestHeader("Authorization") String authHeader){
        String JWT = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(JWT);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        boolean valid = userDetails.getAuthorities().toString().contains("ADMIN");

        if (jwtService.isAuthTokenValid(JWT, userDetails) && valid){
                bookService.addBook(request);
                return "Book added" ;
        }
        
        return "Book not added";

    }
  
    @PostMapping("admin/addMultipleBooks")
    public String addMultipleBooks(@RequestBody BookAdditionRequest[] requests, @RequestHeader("Authorization") String authHeader) {
        String JWT = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(JWT);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        boolean valid = userDetails.getAuthorities().toString().contains("ADMIN");

        if (jwtService.isAuthTokenValid(JWT, userDetails) && valid){
            bookService.addMultipleBooks(requests);
            return "Books added successfully";
        }
        return "Books not added";
    }

    
  
  


    @PostMapping("/addrented")
    public String addRented(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest, @RequestHeader("Authorization") String authHeader) {
        String JWT = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(JWT);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        if (jwtService.isAuthTokenValid(JWT, userDetails)){
            bookRentalService.rentBook(request, httpRequest);
            return "Book rented";
        }
        return "Book not rented";
    }

    @PostMapping("/returnbook")
    public String returnBook(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest, @RequestHeader("Authorization") String authHeader) {
        String JWT = authHeader.substring(7);
        String email = jwtService.getEmailFromToken(JWT);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

if (jwtService.isAuthTokenValid(JWT, userDetails)){
    bookRentalService.returnBook(request, httpRequest);
    return "Book returned";
}
        return "Book not returned";
    }


}
