package com.appsolute.risk_reporter.service;

import com.appsolute.risk_reporter.model.VersionCheckResult;
import com.appsolute.risk_reporter.model.RiskScoreResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RiskReporterService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Map<String, Integer> SERVICE_RISK = Map.of(
        "ssh", 30,
        "rdp", 30,
        "telnet", 30,
        "mysql", 20,
        "redis", 20,
        "postgres", 20,
        "nginx", 10,
        "apache", 10,
        "httpd", 10
    );

    private static final Map<Integer, Integer> PORT_RISK = Map.of(
        22, 20,
        3389, 20,
        23, 20,
        3306, 15,
        6379, 15,
        80, 10,
        443, 10
    );

    // Version status'a göre risk puanı
    private static final Map<String, Integer> VERSION_RISK = Map.of(
        "outdated-major", 40,
        "outdated-minor", 20,
        "up-to-date", 0
    );

    @Value("${app.kafka.output-topic}")
    private String outputTopic;

    public RiskReporterService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${app.kafka.input-topic}", groupId = "risk-reporter")
    public void listen(String message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        VersionCheckResult vcr = mapper.readValue(message, VersionCheckResult.class);

        RiskScoreResult result = new RiskScoreResult();
        result.ip = vcr.ip;
        result.port = vcr.port;
        result.service = vcr.service;
        result.version = vcr.version;
        result.recommended = vcr.recommended;

        
        int versionRisk = VERSION_RISK.getOrDefault(vcr.status, 0);

        
        int serviceRisk = SERVICE_RISK.getOrDefault(vcr.service, 5);
        int portRisk = PORT_RISK.getOrDefault(vcr.port, 5);

        
        result.riskScore = versionRisk + serviceRisk + portRisk;

        
        if (result.riskScore >= 70) {
            result.riskLevel = "HIGH";
            result.highRisk = true;
        } else if (result.riskScore > 30) {
            result.riskLevel = "MEDIUM";
            result.highRisk = false;
        } else {
            result.riskLevel = "LOW";
            result.highRisk = false;
        }

        kafkaTemplate.send(outputTopic, mapper.writeValueAsString(result));
        System.out.println("Risk raporu üretildi: " + result.ip + " - " + result.riskLevel + " (" + result.riskScore + ")");
    }
}
