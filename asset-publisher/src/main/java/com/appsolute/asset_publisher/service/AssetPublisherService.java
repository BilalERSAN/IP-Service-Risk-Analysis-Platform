package com.appsolute.asset_publisher.service;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.appsolute.asset_publisher.model.AssetData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;


@Service
public class AssetPublisherService {    //Kafkaya mesaj gönderir
    

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic-name}")
    private String topicName;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AssetPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Uygulama başladığında otomatik çalışır
    @PostConstruct
    public void publishAssets() throws Exception {
        // JSON dosyasını oku
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("discovered_assets.json");
        List<AssetData> assets = objectMapper.readValue(inputStream, new TypeReference<List<AssetData>>() {});

        // Her veriyi Kafka'ya yolla
        for (AssetData asset : assets) {
            String json = objectMapper.writeValueAsString(asset);
            kafkaTemplate.send(topicName, json);
            System.out.println("Kafka'ya gönderildi: " + json);
        }
    }
    
}
