package com.appsolute.database_writer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "risk_score_alerts")
public class RiskScoreResult {
    @Id
    public String id;
    public String ip;
    public int port;
    public String service;
    public String version;
    public String recommended;
    public int riskScore;
    public String riskLevel;
    public boolean highRisk;
    public long timestamp;
}