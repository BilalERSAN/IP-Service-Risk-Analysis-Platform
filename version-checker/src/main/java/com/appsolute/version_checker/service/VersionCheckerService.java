package com.appsolute.version_checker.service;

import com.appsolute.version_checker.model.AssetData;
import com.appsolute.version_checker.model.VersionCheckResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.util.Map;

@Service
public class VersionCheckerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, String> latestVersions;

    @Value("${app.kafka.output-topic}")
    private String outputTopic;

    @Value("${app.latest-versions-path}")
    private String versionsFile;

    public VersionCheckerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void init() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(versionsFile);
        latestVersions = mapper.readValue(inputStream, new TypeReference<Map<String, String>>() {});
    }

    @KafkaListener(topics = "${app.kafka.input-topic}", groupId = "version-checker")
    public void listen(String message) throws Exception {
        AssetData asset = mapper.readValue(message, AssetData.class);
        VersionCheckResult result = new VersionCheckResult();
        result.ip = asset.ip;
        result.port = asset.port;
        result.service = asset.service;
        result.version = asset.version;

        String latest = latestVersions.getOrDefault(asset.service, asset.version);
        result.recommended = latest;

        String[] currentParts = asset.version.split("\\.");
        String[] latestParts = latest.split("\\.");

        int majorDiff = Integer.parseInt(latestParts[0]) - Integer.parseInt(currentParts[0]);
        int minorDiff = Integer.parseInt(latestParts[1]) - Integer.parseInt(currentParts[1]);

        if (majorDiff >= 1) {
            result.status = "outdated-major";
        } 
        else if(minorDiff >= 1){
            result.status = "outdated-minor";
        }
        else {
            result.status = "up-to-date";
        }

        kafkaTemplate.send(outputTopic, mapper.writeValueAsString(result));
        System.out.println("Versiyon kontrolü yapıldı: " + result.ip + " - " + result.status);
    }
}
