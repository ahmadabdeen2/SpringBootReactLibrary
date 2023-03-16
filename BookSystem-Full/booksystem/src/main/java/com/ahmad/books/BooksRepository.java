package com.ahmad.books;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BooksRepository extends JpaRepository<Books, Integer> {


    public Books findByTitle(String title);
    public Books findByAuthor(String author);
    public Books findById(Long id);
    


    
    
}
