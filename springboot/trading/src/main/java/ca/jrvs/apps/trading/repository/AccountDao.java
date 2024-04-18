package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountDao extends JpaRepository<Account, Integer> {

    @Query(
            value = "SELECT * FROM account WHERE trader_id=?1",
            nativeQuery = true
    )
    Optional<Account> findById(Integer id);

    @Query(
            value = "DELETE FROM account WHERE trader_id=?1",
            nativeQuery = true
    )
    @Modifying
    void deleteById(Integer trader_id);
}
