package antifraud.persistence;

import antifraud.business.model.entity.IP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPRepository extends CrudRepository<IP, Long> {
    <S extends IP> S save(S entity);

    Optional<IP> findByIp(String ipv4);

    void delete(IP entity);

    Iterable<IP> findAll();
}
