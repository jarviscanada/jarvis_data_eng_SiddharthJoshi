package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.entity.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionDao extends ReadOnlyRepository<Position, Integer> {

    @Query(
            value = "SELECT * FROM position WHERE account_id=?1",
            nativeQuery = true
    )
    List<Position> findAllByAccountId(Integer accountId);

    @Query(
            value = "SELECT * FROM position WHERE account_id=?1 AND ticker=?2",
            nativeQuery = true
    )
    Optional<Position> findByAccountIdAndTicker(Integer accountId, String ticker);
}
