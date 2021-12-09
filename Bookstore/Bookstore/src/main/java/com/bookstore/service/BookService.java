package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Book findOne(Long id){
        return bookRepository.findById(id).orElseThrow();
    }


    public List<Book> search(String keyword) {
        List<Book> bookList = bookRepository.findByTitleContaining(keyword);

        List<Book> activeBookList = new ArrayList<>();

        for(Book book : bookList){
            if(book.isActive()){
                activeBookList.add(book);
            }
        }

        return activeBookList;
    }
}
