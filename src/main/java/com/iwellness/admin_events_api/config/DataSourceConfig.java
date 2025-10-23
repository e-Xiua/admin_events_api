package com.iwellness.admin_events_api.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
    
    @Autowired 
    Environment env;

    @Bean
    public DataSource dataSource() {        
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        
        // Determinar si estamos en un contexto de test
        boolean isTestContext = isTestContext();
        
        // URL de la base de datos
        String url = isTestContext 
                ? "jdbc:sqlite:iwellness_admin_events_api_test.db"
                : "jdbc:sqlite:iwellness_admin_events_api.db";
        
        dataSource.setUrl(url);        
        return dataSource;
    }
    
    /**
     * Detecta si estamos en un contexto de test
     */
    private boolean isTestContext() {
        // Método 1: Verificar si estamos en un perfil "test"
        for (String profile : env.getActiveProfiles()) {
            if ("test".equals(profile)) {
                return true;
            }
        }
        
        // Método 2: Verificar la pila de llamadas para buscar clases de test
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (className.contains("Test") || className.contains("MockitoJUnitRunner") ||
                className.contains("SpringRunner") || className.contains("JUnit")) {
                return true;
            }
        }
        
        // Método 3: Verificar si hay propiedades específicas de test en el environment
        return env.containsProperty("spring.test.context.cache.maxSize");
    }
}
