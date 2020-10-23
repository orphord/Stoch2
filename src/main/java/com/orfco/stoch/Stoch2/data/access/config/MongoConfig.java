package com.orfco.stoch.Stoch2.data.access.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories("com.orfco.stoch.Stoch2.data.access")
public class MongoConfig extends AbstractMongoClientConfiguration {
  @Bean
  public MongoClient mongo() {
      ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/CloseData");
      MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .build();
      
      return MongoClients.create(mongoClientSettings);
  }

  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
      return new MongoTemplate(mongo(), "test");
  }
  
	@Override
	protected String getDatabaseName() {
		return "CloseData";
	}

}