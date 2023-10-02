package pl.piotr.lecicki.phishingvalidatorplay.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.piotr.lecicki.phishingvalidatorplay.domain.entities.SMSes;

@Repository
public interface SMSRepository extends CrudRepository<SMSes, Long> {
}
