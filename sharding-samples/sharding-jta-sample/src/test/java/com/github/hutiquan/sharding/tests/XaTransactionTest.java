package com.github.hutiquan.sharding.tests;

import com.github.hutiquan.sharding.samples.XaBootstrap;
import com.github.hutiquan.sharding.samples.service.BuyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-06-09 12:53 AM
 */
@SpringBootTest(classes = XaBootstrap.class)
public class XaTransactionTest {

  @Autowired private BuyService buyService;


  @Test
//  @Commit
//  @Transactional
  public void testXaTransaction() throws InterruptedException {
    buyService.buyBook(false);

    Thread.sleep(2000);
  }
}
