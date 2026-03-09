package io.rosetta.autoconfigure;

import io.rosetta.config.RosettaProperties;
import io.rosetta.controller.RosettaController;
import io.rosetta.service.RosettaSchemaService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@AutoConfiguration
@ConditionalOnClass({ EntityManagerFactory.class, DispatcherServlet.class })
@EnableConfigurationProperties(RosettaProperties.class)
public class RosettaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RosettaSchemaService rosettaSchemaService(EntityManagerFactory emf) {
        return new RosettaSchemaService(emf);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rosetta", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RosettaController rosettaController(RosettaSchemaService svc) {
        return new RosettaController(svc);
    }
}
