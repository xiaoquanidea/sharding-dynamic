package com.github.hutiquan.sharding.samples;

import com.github.hutiquan.sharding.samples.mapper.BookMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-11 10:51 AM
 */
@SpringBootApplication
@MapperScan(basePackageClasses = BookMapper.class)
public class NonXaBootstrap {

  public static void main(String[] args) {
    SpringApplication.run(NonXaBootstrap.class, args);
  }

}
