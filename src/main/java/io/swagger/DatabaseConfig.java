package io.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Created by David on 12/9/2017.
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("DatabaseConfig");

    @Bean
    public DataSource dataSource(){
       /* URI dbUri = null;
        try {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.err.println("URI SYNTAX EXCEPTION!!! STOPPING EXECUTION");
            System.exit(0001);
        }*/

        String username = "lxrwyleutevvuy";//dbUri.getUserInfo().split(":")[0];
        String password = "9c9abb6ccd4d293ed7e7cd21f714e0534c5e6af7a6c5d4e7dffab344e1e86ad9";//dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://ec2-54-217-245-9.eu-west-1.compute.amazonaws.com:5432/daj3qafrragjt1";// + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();


        LOGGER.error("username: " + username);
        LOGGER.error("password: " + password);
        LOGGER.error("dbUrl: " + dbUrl);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setPassword(password);
        dataSource.setUsername("lxrwyleutevvuy");
        dataSource.setUrl(dbUrl);

        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPackagesToScan("io.swagger", "io.swagger.model");
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.show_sql", "true");
//        hibernateProperties.put("hinbernate.hbm2dll.auto", "update");
//        hibernateProperties.put("javax.persistence.schema-generation.database.action", "create");
        hibernateProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        hibernateProperties.put("hibernate.connection.charSet", "UTF-8");
        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        try {
            sessionFactoryBean.afterPropertiesSet();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(){
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
}
