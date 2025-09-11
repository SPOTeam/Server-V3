package kr.spot.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "kr.spot")
@Configuration
public class FeignConfig {

}
