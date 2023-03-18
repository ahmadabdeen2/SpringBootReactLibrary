package com.ahmad.bookrental;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ahmad.books.Books;
import com.ahmad.users.Users;
import java.util.Optional;

public interface BookRentalRepository extends JpaRepository<BookRental, Long> {

    public Optional<BookRental> findByUserAndBook(Users user, Books book);
    public Optional<BookRental> findByUserAndBookAndReturned( Users user, Books book, boolean returned);
    public List<BookRental> findByUser(Users user);
    public List<BookRental> findByUserAndReturned(Users users, boolean returned);
    @Query("SELECT br FROM BookRental br JOIN FETCH br.book JOIN FETCH br.user")
    List<BookRental> findAllWithBooksAndUsers();

    @Query("SELECT br FROM BookRental br JOIN FETCH br.book JOIN FETCH br.user WHERE br.user.id = :userId AND br.returned = false")
    List<BookRental> findAllWithBooksAndUsersByUserIdAndNotReturned(@Param("userId") Long userId);
}
