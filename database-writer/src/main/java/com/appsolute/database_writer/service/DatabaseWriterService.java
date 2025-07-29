package com.appsolute.database_writer.service;

import com.appsolute.database_writer.model.RiskScoreResult;
import com.appsolute.database_writer.mongo.RiskScoreResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DatabaseWriterService {

    private final RiskScoreResultRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public DatabaseWriterService(RiskScoreResultRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${app.kafka.input-topic}", groupId = "database-writer")
    public void listen(String message) throws Exception {
        RiskScoreResult result = mapper.readValue(message, RiskScoreResult.class);
        result.timestamp = System.currentTimeMillis();
        repository.save(result);
        System.out.println("Veri Mongoya aktarıldı");
    }
}

