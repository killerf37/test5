package com.ginfon.core.web.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.pagehelper.PageHelper;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = { "com.ginfon.core.mapper", "com.ginfon.core.web.mapper" }, sqlSessionTemplateRef = "sysSqlSessionTemplate")
public class SystemConfiguration {

	@Bean(name = "ginfon-sys")
	@ConfigurationProperties(prefix = "spring.datasource.ginfon-sys")
	public DataSource matrixDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean("default-sys-database-pageHelper")
	public PageHelper pageHelper() {
		PageHelper pageHelper = new PageHelper();
		// 添加配置，也可以指定文件路径
		Properties p = new Properties();
		p.setProperty("offsetAsPageNum", "true");
		p.setProperty("rowBoundsWithCount", "true");
		p.setProperty("reasonable", "true");
		p.setProperty("pageSizeZero", "true");
		pageHelper.setProperties(p);
		return pageHelper;
	}
	
	@Bean(name = "sysSqlSessionFactory")
	public SqlSessionFactory matrixSqlSessionFactory(@Qualifier("ginfon-sys") DataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/system/*.xml"));
		bean.setTypeAliasesPackage("com.ginfon.core.web.entity");
		return bean.getObject();
	}
	
	@Bean(name = "sysTransactionManager")
	public DataSourceTransactionManager matrixTransactionManager(@Qualifier("ginfon-sys") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "sysSqlSessionTemplate")
	public SqlSessionTemplate matrixSqlSessionTemplate(
			@Qualifier("sysSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
