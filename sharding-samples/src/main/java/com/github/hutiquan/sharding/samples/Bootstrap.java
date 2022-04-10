package com.github.hutiquan.sharding.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:xiaoquanidea@163.com">aiden.hu</a>
 * @since 2022-04-08 5:30 PM
 */
@SpringBootApplication(scanBasePackageClasses = Bootstrap.class)
public class Bootstrap {

  public static void main(String[] args) {
    SpringApplication.run(Bootstrap.class, args);
  }

}
