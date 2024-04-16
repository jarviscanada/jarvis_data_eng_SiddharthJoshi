package ca.jrvs.apps.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

// Not to create an instance of this
@NoRepositoryBean
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {

    List<T> findAll();
    Optional<T> findById(ID id);
    List<T> findAllById(Iterable<ID> ids);
}
