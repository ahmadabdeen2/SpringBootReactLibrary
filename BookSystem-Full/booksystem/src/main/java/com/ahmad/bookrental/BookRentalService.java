package com.ahmad.bookrental;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ahmad.books.BookRentingRequest;
import com.ahmad.books.Books;
import com.ahmad.books.BooksRepository;
import com.ahmad.config.JWTService;
import com.ahmad.users.Users;
import com.ahmad.users.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.var;

@Service
public class BookRentalService {
    
    @Autowired
    UsersRepository userRepo;
    
    @Autowired
    BooksRepository booksRepo;

    @Autowired
    JWTService jwtService;

    @Autowired
    BookRentalRepository bookRentalRepo;

    

    // rent a new book
    @Transactional
    public String rentBook(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest) {

        Books bookFound = booksRepo.findById(request.getBook_id()).orElseThrow();
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        System.out.println(token);
        var userind = jwtService.getEmailFromToken(token);
        Users userFound = userRepo.findByEmail(userind).orElseThrow();
        int quantity = bookFound.getQuantity();
        if (quantity == 0){
            return "Book not available";
        }
        try{

        BookRental isBookAlreadyRented = bookRentalRepo.findByUserAndBook(userFound, bookFound).orElseThrow();
        if (isBookAlreadyRented.isReturned() == false){
            
            return "Book already rented";
        }
        }   catch (Exception e) {
            System.out.println("Book Rental not found");
        }
        
        
        var userFromToken = jwtService.getEmailFromToken(token);
        if (userFromToken.equals(userFound.getEmail())) {
            var newBookRental = BookRental.builder()
            .book(bookFound)
            .user(userFound)
            .rentalDate(new Date(System.currentTimeMillis()))
            .build();
            var savedBookRental = bookRentalRepo.save(newBookRental);
            bookFound.setQuantity(quantity - 1);
        }
        return "Book rented";
    }

    @Transactional
    public void returnBook(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest) {
        var bookFound = booksRepo.findById(request.getBook_id()).orElseThrow();
        // var userFound = userRepo.findById(request.getUser_id()).orElseThrow();
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        var userind = jwtService.getEmailFromToken(token);
        var userFound = userRepo.findByEmail(userind).orElseThrow();
        var userFromToken = jwtService.getEmailFromToken(token);
        if (userFromToken.equals(userFound.getEmail())) {
            var bookRentalFound = bookRentalRepo.findByUserAndBookAndReturned(  userFound, bookFound, false).orElseThrow();
            bookRentalFound.setReturnDate(new Date(System.currentTimeMillis()));
            bookRentalFound.setReturned(true);
            bookFound.setQuantity(bookFound.getQuantity() + 1);
            bookRentalRepo.save(bookRentalFound);
        }


        
    }


    public List<BookRental> getBooksByUser(HttpServletRequest httpRequest) {
        var token = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        var userFromToken = jwtService.getEmailFromToken(token);
        var user = userRepo.findByEmail(userFromToken).orElseThrow();
        var bookRentals = bookRentalRepo.findByUserAndReturned(user, false);
        return bookRentals;
    }


    public List<BookRental> getAllLogs() {
        var bookRentals = bookRentalRepo.findAll();
        return bookRentals;
    }
    
}
