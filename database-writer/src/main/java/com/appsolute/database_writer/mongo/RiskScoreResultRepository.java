package com.appsolute.database_writer.mongo;

import com.appsolute.database_writer.model.RiskScoreResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RiskScoreResultRepository extends MongoRepository<RiskScoreResult, String> {
    List<RiskScoreResult> findAllByOrderByTimestampDesc(Pageable pageable); 
}
