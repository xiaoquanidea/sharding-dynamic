package com.github.hutiquan.sharding.samples;

import com.github.hutiquan.sharding.api.Source;
import com.github.hutiquan.sharding.core.ShardingProperties;
import com.github.hutiquan.sharding.core.annotation.ShardingAnnotationInjector;
import com.github.hutiquan.sharding.samples.mapper.BookMapper;
import java.util.Collections;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 5:30 PM
 */
@SpringBootApplication(scanBasePackageClasses = XaBootstrap.class)
@MapperScan(basePackageClasses = BookMapper.class)
public class XaBootstrap {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(XaBootstrap.class, args);
    ShardingProperties shardingProperties = context.getBean(ShardingProperties.class);
    System.out.println("shardingProperties = " + shardingProperties);
  }


  @Bean
  public ShardingAnnotationInjector injector() {
    return () -> Collections.singletonList(Source.class.getName());
  }

}
