package com.optimiza.ehope.web.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

@Configuration
//@PropertySource("/WEB-INF/system-settings.properties")
//@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Value("${system.website.base}")
	private String SERVER_BASE;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String WWW_SERVER_BASE = SERVER_BASE.replace("https://", "https://www.");
		registry.addMapping("/**")
				.allowedOrigins(SERVER_BASE, WWW_SERVER_BASE)
				.allowedMethods("POST");
	}

	/*
	 * Here we register the Hibernate5Module into an ObjectMapper, then set this custom-configured ObjectMapper
	 * to the MessageConverter and return it to be added to the HttpMessageConverters of our application
	 */
	public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

		ObjectMapper mapper = new ObjectMapper();
		//Registering Hibernate5Module to support lazy objects
		mapper.registerModule(new Hibernate5Module());

		//Register the default date format, format manipulation happens in front end
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		mapper.setDateFormat(new ISO8601DateFormat());

		messageConverter.setObjectMapper(mapper);
		return messageConverter;

	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//Here we add our custom-configured HttpMessageConverter
		converters.add(jacksonMessageConverter());
		super.configureMessageConverters(converters);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/assets/");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	//	@Bean(name = "multipartResolver")
	//	public CommonsMultipartResolver getMultipartResolver() {
	//		return new CommonsMultipartResolver();
	//	}

	//    @Bean(name = "messageSource")
	//    public ReloadableResourceBundleMessageSource getMessageSource() {
	//        ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
	//        resource.setBasename("classpath:messages");
	//        resource.setDefaultEncoding("UTF-8");
	//        return resource;
	//    }

	//More configuration....

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/{[path:[^\\.]*}").setViewName("forward:index.html");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}
}
