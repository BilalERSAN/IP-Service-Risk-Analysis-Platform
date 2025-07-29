package com.appsolute.database_writer.rest;

import com.appsolute.database_writer.model.RiskScoreResult;
import com.appsolute.database_writer.mongo.RiskScoreResultRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@RestController
public class RiskScoreController {

    private final RiskScoreResultRepository repository;

    public RiskScoreController(RiskScoreResultRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/risk-scores/latest")
    public List<RiskScoreResult> getLatestRiskScores() {
        return repository.findAllByOrderByTimestampDesc(PageRequest.of(0, 10));
    }
}

//son 10 kaydı döndürecek restApi