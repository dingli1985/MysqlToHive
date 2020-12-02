package com.asiainfo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 配置项
 * 
 * @Title: DruidConfig
 * @Description:
 * @author:Administrator
 * @date 2018年8月4日
 */
@Configuration
public class DruidConfig {

	@Autowired
	private Environment env;

	@Bean(name = "hiveJdbcDataSource")
	@Qualifier("hiveJdbcDataSource")
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("hive.jdbc"));
		dataSource.setDriverClassName(env.getProperty("hive.driver-class-name"));
		dataSource.setUsername(env.getProperty("hive.user"));
		dataSource.setPassword(env.getProperty("hive.password"));
		dataSource.setTestWhileIdle(Boolean.valueOf(env.getProperty("hive.testWhileIdle")));
		dataSource.setMaxActive(Integer.valueOf(env.getProperty("hive.max-active")));
		dataSource.setInitialSize(Integer.valueOf(env.getProperty("hive.initialSize")));
		dataSource.setRemoveAbandoned(Boolean.valueOf(env.getProperty("hive.removeAbandoned")));
		dataSource.setRemoveAbandonedTimeout(Integer.valueOf(env.getProperty("hive.removeAbandonedTimeout")));
		return dataSource;
	}

	@Bean(name = "hiveJdbcTemplate")
	public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveJdbcDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}


	@Bean(name = "mysqlJdbcDataSource")
	@Qualifier("mysqlJdbcDataSource")
	public DataSource mysqlDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		dataSource.setUsername(env.getProperty("spring.datasource.user"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
		return dataSource;
	}

	@Bean(name = "mysqlJdbcTemplate")
	public JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlJdbcDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}


	@Bean(name = "oracleJdbcDataSource")
	@Qualifier("oracleJdbcDataSource")
	public DataSource oracleDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("oracle.jdbc.url"));
		dataSource.setDriverClassName(env.getProperty("oracle.jdbc.driver-class-name"));
		dataSource.setUsername(env.getProperty("oracle.jdbc.username"));
		dataSource.setPassword(env.getProperty("oracle.jdbc.password"));
		return dataSource;
	}

	@Bean(name = "oracleJdbcTemplate")
	public JdbcTemplate oracleJdbcTemplate(@Qualifier("oracleJdbcDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}


}
