package com.ahmad.bookrental;

import java.util.Date;

import com.ahmad.books.BooksDTO;
import com.ahmad.users.UsersDTO;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRentalDTO {
    private Long id;
    private BooksDTO book;
    private UsersDTO user;
    private Date rentalDate;
    private Date returnDate;
    private boolean returned;




    
    
}
