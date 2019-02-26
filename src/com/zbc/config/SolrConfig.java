package com.zbc.config;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;

@Configuration
@PropertySource("classpath:my.properties")
public class SolrConfig {

	@Resource
    private Environment environment;
	
	@Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(environment.getRequiredProperty("solr.server.url"))
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client){
        return new SolrTemplate(client);
    }

}
