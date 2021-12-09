package com.adminportal.service;

import com.adminportal.entity.Book;
import com.adminportal.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Book saveBook(Book book){
        return bookRepository.save(book);
    }

    public List<Book> findAllBooks(){
        return bookRepository.findAll();
    }

    public void deleteBookById(Long id){
        bookRepository.deleteById(id);
    }

    public Optional<Book> findByBookId(Long id){
        return bookRepository.findById(id);
    }
}
