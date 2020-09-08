package com.qyx.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot 整合 ElasticSearch （Rest Client方式）
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:27
 */
@SpringBootApplication
public class ElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }

}
