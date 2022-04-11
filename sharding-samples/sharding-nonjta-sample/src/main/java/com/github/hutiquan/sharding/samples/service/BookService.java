package com.github.hutiquan.sharding.samples.service;

import com.github.hutiquan.sharding.api.Sharding;
import com.github.hutiquan.sharding.samples.entity.Book;
import com.github.hutiquan.sharding.samples.mapper.BookMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



  // =============== 以下是saveBook ===============
  @Transactional
  @Sharding("book")
  public void saveBook(Book book) {
    bookMapper.insert(book);
  }

  @Sharding("book1")
  public void saveBook1(Book book) {
    bookMapper.insert(book);
  }

  @Sharding("book2")
  public void saveBook2(Book book) {
    bookMapper.insert(book);
  }

  @Sharding("book3")
  public void saveBook3(Book book) {
    bookMapper.insert(book);
  }
}
