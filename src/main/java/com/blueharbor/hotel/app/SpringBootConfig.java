package com.blueharbor.hotel.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = "com.blueharbor.hotel",
    exclude = {WebMvcAutoConfiguration.class, SecurityAutoConfiguration.class}
)
@ConfigurationPropertiesScan(basePackages = "com.blueharbor.hotel.config")
@EnableJpaRepositories(basePackages = "com.blueharbor.hotel.repository")
@EntityScan(basePackages = "com.blueharbor.hotel.model")
public class SpringBootConfig {
}
