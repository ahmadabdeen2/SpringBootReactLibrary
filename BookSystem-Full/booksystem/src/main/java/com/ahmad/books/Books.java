package com.ahmad.books;

import java.util.ArrayList;
import java.util.List;

import com.ahmad.bookrental.BookRental;
import com.ahmad.users.Users;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Books{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int quantity;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String imageURL;
    private String pdfURL;



    @OneToMany(mappedBy = "book")
    @JsonManagedReference
    private List<BookRental> rentals = new ArrayList<>();




   
}