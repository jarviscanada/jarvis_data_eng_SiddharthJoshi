package ca.jrvs.apps.trading.securityorder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityOrderDao extends JpaRepository<SecurityOrder, Integer> {

}
