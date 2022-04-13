package com.github.hutiquan.sharding.samples.controller;

import com.github.hutiquan.sharding.samples.entity.Book;
import com.github.hutiquan.sharding.samples.service.BookService;
import com.github.hutiquan.sharding.samples.service.BuyService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-10 4:40 PM
 */
@RestController
@RequestMapping("/book")
public class BookController {

  @Autowired private BookService bookService;

//  @Sharding("sharding")
  @Transactional
  @GetMapping
  public List<Book> queryBookList(@RequestParam(required = false, defaultValue = "100") Integer databaseNo) {
    switch (databaseNo) {
      case 1: return bookService.queryBook1();
      case 2: return bookService.queryBook2();
      case 3: return bookService.queryBook3();
      default: return bookService.queryBook();
    }
  }


  @Autowired private BuyService buyService;


  @GetMapping("/buy")
  public String buyBook(Boolean error) {
    buyService.buyBook(error);
    return "ok";
  }

}
