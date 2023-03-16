package com.ahmad.books;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAdditionRequest {

    private long id;
    private int quantity;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String imageURL;
    private String pdfURL;
    
    
}
