package de.uni_due.paluno.sse.elephant.service.monitoring.db;

import de.uni_due.paluno.sse.elephant.service.monitoring.model.Measurement;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Ole Meyer
 */

public interface MeasurementRepository extends MongoRepository<Measurement,String> {
}
