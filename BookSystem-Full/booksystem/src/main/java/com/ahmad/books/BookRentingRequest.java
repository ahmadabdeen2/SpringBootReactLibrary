package com.ahmad.books;

import java.net.http.HttpRequest;

import com.ahmad.users.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRentingRequest  {

    Integer book_id;
    

    
}
