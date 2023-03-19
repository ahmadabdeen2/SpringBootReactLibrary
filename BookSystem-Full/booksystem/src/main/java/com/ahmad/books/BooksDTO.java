package com.ahmad.books;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooksDTO {
    private Long id;
    private String title;
    private String imageURL;
    private String author;
    private String isbn;
    private String description;
    private int quantity;
    private String pdfURL;
}
