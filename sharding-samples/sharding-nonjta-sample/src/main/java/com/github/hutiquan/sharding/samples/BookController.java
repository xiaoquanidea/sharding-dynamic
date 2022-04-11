package com.github.hutiquan.sharding.samples;

import com.github.hutiquan.sharding.samples.entity.Book;
import com.github.hutiquan.sharding.samples.service.BookService;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import kotlin.random.RandomKt;
import kotlin.ranges.IntRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

//  @Transactional
  @GetMapping
  public List<Book> queryBookList(@RequestParam(required = false, defaultValue = "100") Integer databaseNo) {
    switch (databaseNo) {
      case 1: return bookService.queryBook1();
      case 2: return bookService.queryBook2();
      case 3: return bookService.queryBook3();
      default: return bookService.queryBook();
    }
  }

  @PostMapping
  public String saveBook(@RequestParam(required = false, defaultValue = "100") Integer databaseNo) {
    Book book = new Book();
    book.setName(UUID.randomUUID().toString());
    book.setSum(RandomKt.nextInt(RandomKt.Random(1), new IntRange(1, 50)));
    book.setCreateTime(new Date());

    bookService.saveBook(book);
    return "ok";
  }

}
