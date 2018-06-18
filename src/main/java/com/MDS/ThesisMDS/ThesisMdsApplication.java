package com.MDS.ThesisMDS;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.annotation.EnableVaadinEventBus;

@SpringBootApplication
@EnableVaadinEventBus
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class ThesisMdsApplication extends SpringBootServletInitializer {

/*	@Bean
	static VaadinSessionScope vaadinSessionScope() {
		return new VaadinSessionScope();
	}*/

/*	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ThesisMdsApplication.class);
	}*/

    public static void main(String[] args) {
        SpringApplication.run(ThesisMdsApplication.class, args);
    }
}
