package com.ctrip.framework.apollo.demo.spring.springBootDemo;

import com.ctrip.framework.apollo.demo.spring.springBootDemo.config.SampleRedisConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import com.ctrip.framework.apollo.demo.spring.common.bean.AnnotatedBean;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@SpringBootApplication(scanBasePackages = {"com.ctrip.framework.apollo.demo.spring.common",
    "com.ctrip.framework.apollo.demo.spring.springBootDemo"
})
public class SpringBootSampleApplication {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new SpringApplicationBuilder(SpringBootSampleApplication.class).run(args);
    AnnotatedBean annotatedBean = context.getBean(AnnotatedBean.class);
    SampleRedisConfig redisConfig = context.getBean(SampleRedisConfig.class);

    System.out.println("SpringBootSampleApplication Demo. Input any key except quit to print the values. Input quit to exit.");
    while (true) {
      System.out.print("> ");
      String input = new BufferedReader(new InputStreamReader(System.in, Charsets.UTF_8)).readLine();
      if (!Strings.isNullOrEmpty(input) && input.trim().equalsIgnoreCase("quit")) {
        System.exit(0);
      }

      System.out.println(annotatedBean.toString());
      System.out.println(redisConfig.toString());
    }
  }
}
