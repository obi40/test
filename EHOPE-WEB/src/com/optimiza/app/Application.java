package com.optimiza.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.optimiza.core.base.repo.BaseRepositoryImpl;

import freemarker.template.TemplateModel;
import kr.pe.kwonnam.freemarker.inheritance.BlockDirective;
import kr.pe.kwonnam.freemarker.inheritance.ExtendsDirective;
import kr.pe.kwonnam.freemarker.inheritance.PutDirective;

@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
@ComponentScan(basePackages = { "com.optimiza" })
@EnableAutoConfiguration
@EntityScan("com.optimiza")
@EnableJpaRepositories(basePackages = "com.optimiza", repositoryBaseClass = BaseRepositoryImpl.class)
@EnableCaching
@EnableScheduling
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);

	}

	@Bean
	public Map<String, TemplateModel> freemarkerLayoutDirectives() {
		Map<String, TemplateModel> freemarkerLayoutDirectives = new HashMap<String, TemplateModel>();
		freemarkerLayoutDirectives.put("extends", new ExtendsDirective());
		freemarkerLayoutDirectives.put("block", new BlockDirective());
		freemarkerLayoutDirectives.put("put", new PutDirective());

		return freemarkerLayoutDirectives;
	}

	@Bean
	public FreeMarkerConfigurer freemarkerConfig() {
		FreeMarkerConfigurer freemarkerConfig = new FreeMarkerConfigurer();
		freemarkerConfig.setTemplateLoaderPath("/templates/");
		freemarkerConfig.setDefaultEncoding("UTF-8");
		Map<String, Object> freemarkerVariables = new HashMap<String, Object>();
		freemarkerVariables.put("layout", freemarkerLayoutDirectives());

		freemarkerConfig.setFreemarkerVariables(freemarkerVariables);
		return freemarkerConfig;
	}

	//	@Bean
	//	public DispatcherServlet dispatcherServlet() {
	//		return new DispatcherServlet();
	//	}

	//	@Bean
	//	public ErrorPageFilter errorPageFilter() {
	//		return new ErrorPageFilter();
	//	}
	//
	//	@Bean
	//	public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
	//		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
	//		filterRegistrationBean.setFilter(filter);
	//		filterRegistrationBean.setEnabled(false);
	//		return filterRegistrationBean;
	//	}

}