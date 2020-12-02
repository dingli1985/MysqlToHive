package com.asiainfo.config;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfig {

    private String solrUrl;

//    @Bean
//    public HttpSolrClient httpSolrClient(){
//        return new HttpSolrClient.Builder(solrUrl)
//                .build();
//
//    }

}
