package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.Trader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraderDao extends JpaRepository<Trader, Integer> {

}
