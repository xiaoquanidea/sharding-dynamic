package com.github.hutiquan.sharding.samples.service;

import com.github.hutiquan.sharding.api.Sharding;
import com.github.hutiquan.sharding.samples.entity.Book;
import com.github.hutiquan.sharding.samples.mapper.BookMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-10 4:30 PM
 */
@Service
public class BookService {

//  @Autowired private BookService thisBean;

  @Autowired private BookMapper bookMapper;


  @Sharding("book.book1")
  public List<Book> queryBook1() {
    return bookMapper.selectList(null);
  }

  @Sharding("book.book2")
  public List<Book> queryBook2() {
    return bookMapper.selectList(null);
  }


  @Sharding("book.book3")
  public List<Book> queryBook3() {
    return bookMapper.selectList(null);
  }


  @Sharding("book")
  public List<Book> queryBook() {
    return bookMapper.selectList(null);
  }


  @Sharding("book")
  public void updateBook(Book book) {
    if (book != null) {
      bookMapper.updateById(book);
    }
  }

}
