package com.samuilolegovich.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LogbackDBAppenderConfig {

    @Bean
    public DBAppender dbAppender(DataSource dataSource) {
        DBAppender dbAppender = new DBAppender();
        DataSourceConnectionSource connectionSource = new DataSourceConnectionSource();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        connectionSource.setDataSource(dataSource);
        connectionSource.setContext(loggerContext);
        connectionSource.start();

        dbAppender.setConnectionSource(connectionSource);
        dbAppender.start();

        Logger logger = loggerContext.getLogger("dbLogger");
        logger.addAppender(dbAppender);

        return dbAppender;
    }
}
