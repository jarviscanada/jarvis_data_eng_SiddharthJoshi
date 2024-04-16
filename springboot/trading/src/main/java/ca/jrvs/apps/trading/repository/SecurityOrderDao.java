package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.SecurityOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityOrderDao extends JpaRepository<SecurityOrder, Integer> {

}
