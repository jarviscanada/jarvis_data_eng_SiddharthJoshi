package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.Trader;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderDao extends JpaRepository<Trader, Integer> {

}
