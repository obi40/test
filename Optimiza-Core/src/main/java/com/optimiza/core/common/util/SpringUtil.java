package com.optimiza.core.common.util;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	private static Connection connection;

	private static DataSource dataSource;

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public void setApplicationContext(ApplicationContext c) throws BeansException {
		context = c;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public static <T> T getBean(Class<T> beanClass) {
		return context.getBean(beanClass);
	}

	@PostConstruct
	private void setConnection() {
		EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) entityManager.getEntityManagerFactory();
		dataSource = info.getDataSource();
		connection = DataSourceUtils.getConnection(dataSource);

	}

	public static void releaseConnection() {
		DataSourceUtils.releaseConnection(connection, dataSource);
	}

	public static Connection getConnection() {

		return connection;
	}

}
