package com.ahmad.bookrental;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ahmad.books.Books;
import com.ahmad.books.BooksDTO;
import com.ahmad.books.BooksRepository;
import com.ahmad.config.JWTService;
import com.ahmad.users.Users;
import com.ahmad.users.UsersDTO;
import com.ahmad.users.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;


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

    
    @Transactional
    public String rentBook(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest) {
        Books bookFound = booksRepo.findById(request.getBook_id()).orElseThrow();
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        String userind = jwtService.getEmailFromToken(token);
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
        
        
        String userFromToken = jwtService.getEmailFromToken(token);
        if (userFromToken.equals(userFound.getEmail())) {
            BookRental newBookRental = BookRental.builder()
            .book(bookFound)
            .user(userFound)
            .rentalDate(new Date(System.currentTimeMillis()))
            .build();
            BookRental savedBookRental = bookRentalRepo.save(newBookRental);
            bookFound.setQuantity(quantity - 1);
        }
        return "Book rented";
    }

    @Transactional
    public void returnBook(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest) {
        Books bookFound = booksRepo.findById(request.getBook_id()).orElseThrow();
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        String userind = jwtService.getEmailFromToken(token);
        Users userFound = userRepo.findByEmail(userind).orElseThrow();
        String userFromToken = jwtService.getEmailFromToken(token);
        if (userFromToken.equals(userFound.getEmail())) {
            BookRental bookRentalFound = bookRentalRepo.findByUserAndBookAndReturned(  userFound, bookFound, false).orElseThrow();
            bookRentalFound.setReturnDate(new Date(System.currentTimeMillis()));
            bookRentalFound.setReturned(true);
            bookFound.setQuantity(bookFound.getQuantity() + 1);
            bookRentalRepo.save(bookRentalFound);
        }


        
    }



    public List<BookRentalDTO> getBooksByUser(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);
        String userFromToken = jwtService.getEmailFromToken(token);
        Users user = userRepo.findByEmail(userFromToken).orElseThrow();
        Long userId = Long.valueOf(user.getId());
        // var bookRentals = bookRentalRepo.findAllWithBooksAndUsers();
        // return bookRentals
        List<BookRental> bookRentals = bookRentalRepo.findAllWithBooksAndUsersByUserIdAndNotReturned(userId);
        List<BookRentalDTO> bookRentalDTOs = new ArrayList<>();
    
        for (BookRental bookRental : bookRentals) {
            bookRentalDTOs.add(convertToDTO(bookRental));
        }
    
        return bookRentalDTOs;


    }


    public List<BookRentalDTO> getAllLogs() {
        List<BookRental> bookRentals = bookRentalRepo.findAll();
        List<BookRentalDTO> bookRentalDTOs = new ArrayList<>();
        for (BookRental bookRental : bookRentals) {
            bookRentalDTOs.add(convertToDTO(bookRental));
        }
        return bookRentalDTOs;
    }

    private BookRentalDTO convertToDTO(BookRental bookRental) {
        BookRentalDTO dto = new BookRentalDTO();
        dto.setId(bookRental.getId());
        dto.setRentalDate(bookRental.getRentalDate());
        dto.setReturnDate(bookRental.getReturnDate());
        dto.setReturned(bookRental.isReturned());
    
        BooksDTO bookDTO = new BooksDTO();
        bookDTO.setId(bookRental.getBook().getId());
        bookDTO.setTitle(bookRental.getBook().getTitle());
        bookDTO.setImageURL(bookRental.getBook().getImageURL());
        dto.setBook(bookDTO);
    
        UsersDTO userDTO = new UsersDTO();
        Long userId = Long.valueOf(bookRental.getUser().getId());
        userDTO.setId(userId);
        userDTO.setUsername(bookRental.getUser().getUsername());
        // Set other fields from the user entity to the userDTO object
        dto.setUser(userDTO);
    
        return dto;
    }
    
}
