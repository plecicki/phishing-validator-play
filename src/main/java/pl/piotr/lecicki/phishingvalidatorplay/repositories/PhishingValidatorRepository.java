package pl.piotr.lecicki.phishingvalidatorplay.repositories;

import org.springframework.data.repository.CrudRepository;
import pl.piotr.lecicki.phishingvalidatorplay.domain.entities.PhishingValidatorUsers;

public interface PhishingValidatorRepository extends CrudRepository<PhishingValidatorUsers, Long> {

    public Boolean existsByPhoneNumber(String phoneNumber);
    public void deleteByPhoneNumber(String phoneNumber);
}
