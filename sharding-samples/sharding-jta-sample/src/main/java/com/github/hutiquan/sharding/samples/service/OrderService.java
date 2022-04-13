package com.github.hutiquan.sharding.samples.service;

import com.github.hutiquan.sharding.api.Sharding;
import com.github.hutiquan.sharding.samples.entity.Order;
import com.github.hutiquan.sharding.samples.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-12 10:29 AM
 */
@Service
public class OrderService {
  @Autowired private OrderMapper orderMapper;



  @Sharding("order")
  public void saveOrder(Order order) {
    if (order != null) {
      orderMapper.insert(order);
    }
  }

}
