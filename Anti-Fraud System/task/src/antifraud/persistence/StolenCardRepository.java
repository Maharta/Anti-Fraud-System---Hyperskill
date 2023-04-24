package antifraud.persistence;

import antifraud.business.model.entity.StolenCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    <S extends StolenCard> S save(S entity);

    Optional<StolenCard> findByNumber(String number);
}
