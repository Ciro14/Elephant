package de.uni_due.paluno.sse.elephant.service.monitoring.db;

import de.uni_due.paluno.sse.elephant.service.monitoring.model.MetricDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Ole Meyer
 */
public interface MetricRepository extends MongoRepository<MetricDefinition,String> {
}
