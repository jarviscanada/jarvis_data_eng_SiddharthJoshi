package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionDao extends ReadOnlyRepository<Position, Integer> {

    @Query(
            value = "SELECT * FROM position WHERE account_id=?1",
            nativeQuery = true
    )
    List<Position> findAllByAccountId(Integer accountId);
}
