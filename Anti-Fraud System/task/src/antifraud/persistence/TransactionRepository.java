package antifraud.persistence;

import antifraud.business.model.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE (t.dateTime BETWEEN :startTime AND :endTime) AND t.number = :number")
    List<Transaction> findAllTransactionByNumberBetweenDatetime(@Param("startTime") LocalDateTime startTime,
                                                                @Param("endTime") LocalDateTime endTime,
                                                                @Param("number") String number);

    @Query("SELECT t.region as region, t.ip as ip from Transaction t WHERE (t.dateTime BETWEEN :startTime AND :endTime) AND t.number = :number " +
            "GROUP BY t.region, t.ip")
    List<Transaction.RegionAndIP> findAllDistinctRegionAndIPTransactionBetweenDateTime(@Param("startTime") LocalDateTime startTime,
                                                                                       @Param("endTime") LocalDateTime endTime,
                                                                                       @Param("number") String number);

    <S extends Transaction> S save(S entity);
}
