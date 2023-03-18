package com.ahmad.books;

import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ahmad.config.JWTService;
import com.ahmad.users.Users;
import com.ahmad.users.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BooksService {

    @Autowired
    BooksRepository booksRepo;

    @Autowired
    UsersRepository userRepo;

    @Autowired
    JWTService jwtService;

    public Books addBook(BookAdditionRequest request) {
        Books book = Books.builder().title(request.getTitle())
        .isbn(request.getIsbn())
        .author(request.getAuthor())
        .quantity(request.getQuantity())
        .description(request.getDescription())
        .imageURL(request.getImageURL())
        .pdfURL(request.getPdfURL())
        .build();
        Books savedBook = booksRepo.save(book);
        return  savedBook;
    }

    public String addMultipleBooks(BookAdditionRequest[] requests) {
        for (BookAdditionRequest request : requests) {
            addBook(request);
        }
        return "Books added successfully";
    }


    // @Transactional
    // public void rentBook(@RequestBody BookRentingRequest request, HttpServletRequest httpRequest) {

    //     Users user = userRepo.findById(request.getUser_id()).orElseThrow();
    //     Books book = booksRepo.findById(request.getBook_id()).orElseThrow();
    //     String token = httpRequest.getHeader("Authorization");
    //     token = token.substring(7);
    //     // final String token = httpRequest.getHeader("Authorization");
    //     System.out.println(token);
    //     // var token = request.getToken();
    //     var userFromToken = jwtService.getEmailFromToken(token);
    //     if (userFromToken.equals(user.getEmail())) {
    //         user.getRentedBooks().add(book);
    //         userRepo.save(user);
    //     }

    //     user.getRentedBooks().add(book);

    //     // book.getRentedBooks().add(user);

    //     userRepo.save(user);
    //     // bookRepository.save(book);
    // }


    

    public Books getBook(String title) {
        return booksRepo.findByTitle(title);
    }

    public Books[] getAllBooks() {
        return booksRepo.findAll().toArray(new Books[0]);
    }

    
    
}
