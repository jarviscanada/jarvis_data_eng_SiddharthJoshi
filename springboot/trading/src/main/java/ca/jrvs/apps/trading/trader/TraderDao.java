package ca.jrvs.apps.trading.trader;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TraderDao extends JpaRepository<Trader, Integer> {

}
