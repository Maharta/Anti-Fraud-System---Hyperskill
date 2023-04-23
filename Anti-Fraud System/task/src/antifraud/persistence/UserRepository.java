package antifraud.persistence;

import antifraud.business.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Iterable<User> findAll();

    Optional<User> findByUsername(String username);

    long count();

    <S extends User> S save(S entity);
}
