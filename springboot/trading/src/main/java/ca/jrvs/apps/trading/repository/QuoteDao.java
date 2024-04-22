package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteDao extends JpaRepository<Quote, String> {

}
