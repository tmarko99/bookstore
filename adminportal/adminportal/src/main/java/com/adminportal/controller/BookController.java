package com.adminportal.controller;

import com.adminportal.entity.Book;
import com.adminportal.repository.BookRepository;
import com.adminportal.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@Controller
@RequestMapping("/book")
public class BookController {
    public static String uploadDir = "C:/Users/Marko/IdeaProjects/adminportal/adminportal/src/main/resources/static/image/book/";


    @Autowired
    private BookService bookService;

    @RequestMapping("/add")
    public String addBook(Model model){
        model.addAttribute("book", new Book());
        return "addBook";
    }

    @PostMapping("/add")
    public String addBookPost(@ModelAttribute("book")Book book){
        bookService.saveBook(book);

        MultipartFile bookImage = book.getBookImage();

        try {
            byte[] bytes = bookImage.getBytes();
            String name = book.getId() + ".png";
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(uploadDir + name)));
            stream.write(bytes);
            stream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:bookList";
    }

    @RequestMapping("/bookList")
    public String bookList(Model model){
        List<Book> bookList = bookService.findAllBooks();
        model.addAttribute("bookList", bookList);

        return "bookList";
    }

    @RequestMapping("/bookInfo/{id}")
    public String bookInfo(@PathVariable("id")Long id, Model model){
        Book byBookId = bookService.findByBookId(id).get();
        model.addAttribute("book", byBookId);

        return "bookInfo";
    }

    @RequestMapping("/deleteBook/{id}")
    public String deleteBook(@PathVariable("id")Long id){
        bookService.deleteBookById(id);

        return "bookList";
    }

    @RequestMapping("/updateBook/{id}")
    public String updateBook(@PathVariable("id")Long id, Model model){
        Book book = bookService.findByBookId(id).get();
        model.addAttribute("book", book);

        return "updateBook";
    }

    @PostMapping("/updateBook")
    public String updateBookPost(@ModelAttribute("book")Book book, HttpServletRequest request){
        bookService.saveBook(book);

        MultipartFile bookImage = book.getBookImage();

        if(!bookImage.isEmpty()){
            try{
                byte[] bytes = bookImage.getBytes();
                String name = book.getId() + ".png";
                Files.delete(Paths.get(uploadDir + name));
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(uploadDir + name)));
                stream.write(bytes);
                stream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/book/bookInfo/" + book.getId();
    }
}
