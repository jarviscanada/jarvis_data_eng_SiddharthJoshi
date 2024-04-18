package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.domain.SecurityOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityOrderDao extends JpaRepository<SecurityOrder, Integer> {

    @Query(
            value = "SELECT * FROM security_order WHERE account_id=?1",
            nativeQuery = true
    )
    List<SecurityOrder> findAllByAccountId(Integer id);

    @Query(
            value = "DELETE FROM security_order WHERE account_id=?1",
            nativeQuery = true
    )
    @Modifying
    void deleteById(Integer account_id);
}
