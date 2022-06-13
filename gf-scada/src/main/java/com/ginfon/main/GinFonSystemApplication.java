package com.ginfon.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author James
 */
@ComponentScan(basePackages = { "com.ginfon", "com.goldpeak" })
@SpringBootApplication
//@MapperScan(value = "com.ginfon.sfclient.mapper")
public class GinFonSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(GinFonSystemApplication.class, args);
	}
}