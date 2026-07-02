package com.examen.gamestore.config;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcMapperConfig {

	@Bean
	JdbcTemplateMapperFactory jdbcTemplateMapperFactory() {
		return JdbcTemplateMapperFactory.newInstance();
	}
}
