package com.ginfon.scada.config;

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

/**
 * 	SCADA客户端要用的数据库的配置。
 * @author Mark
 *
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.ginfon.scada.event.mapper","com.ginfon.sfclient.mapper"}, sqlSessionTemplateRef = "scadaSqlSessionTemplate")
public class ScadaEventConfiguration {

    @Bean(name = "scada-event")
    @ConfigurationProperties(prefix = "spring.datasource.scada-event")
    public DataSource scadaDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean("scada")
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        //添加配置，也可以指定文件路径
        Properties p = new Properties();
        p.setProperty( "offsetAsPageNum", "true" );
        p.setProperty( "rowBoundsWithCount", "true" );
        p.setProperty( "reasonable", "true" );
        p.setProperty( "pageSizeZero","true" );
        pageHelper.setProperties( p );
        return pageHelper;
    }
    
	@Bean(name = "scadaSqlSessionFactory")
	public SqlSessionFactory userSqlSessionFactory(@Qualifier("scada-event") DataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources("classpath:mappers/scada/*.xml"));
		return bean.getObject();
	}
	
	@Bean(name = "scadaTransactionManager")
	public DataSourceTransactionManager userTransactionManager(@Qualifier("scada-event") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
    
	@Bean(name = "scadaSqlSessionTemplate")
	public SqlSessionTemplate userSqlSessionTemplate(
			@Qualifier("scadaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
}
