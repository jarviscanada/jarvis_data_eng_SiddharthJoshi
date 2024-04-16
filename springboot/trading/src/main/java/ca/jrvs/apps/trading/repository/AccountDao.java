package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDao extends JpaRepository<Account, Integer> {

}
