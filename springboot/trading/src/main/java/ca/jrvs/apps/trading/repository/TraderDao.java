package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.entity.Trader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderDao extends JpaRepository<Trader, Integer> {

}
