package com.github.hutiquan.sharding.samples.service;

import com.github.hutiquan.sharding.samples.entity.Book;
import com.github.hutiquan.sharding.samples.entity.Order;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-12 10:28 AM
 */
@Service
public class BuyService {
  @Autowired private BookService bookService;
  @Autowired private OrderService orderService;


  @Transactional
  public void buyBook(Boolean error) {
    List<Book> books = bookService.queryBook();
    if (books.isEmpty()) {
      System.err.println("没有查到书,买不了");
    }

    System.out.println("查出来了好多书，分别有 --------------->");
    books.forEach( book ->
        System.out.println(book.getName() + "有" + book.getSum() + "本,每本" + book.getPrice() + "元")
    );
    System.out.println("<---------------");

    Book book = books.get(0);
    if (book.getSum() == 0) {
      System.err.println(book.getName() + "没有库存拉，买不了了");
    }

    System.out.println("开始购买" + book.getName());
    book.setSum(book.getSum() - 1);

    Order order = new Order();
    order.setBookId(book.getId());
    order.setQuantity(1);
    order.setPrice(book.getPrice());


    bookService.updateBook(book);

    if (Boolean.TRUE.equals(error)) {
      throw new RuntimeException("系统中断");
    }
    orderService.saveOrder(order);
  }

}
