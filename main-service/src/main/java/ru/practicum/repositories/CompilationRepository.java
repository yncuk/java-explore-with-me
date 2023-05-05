package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    @Query(" select c from Compilation as c " +
            "left join fetch c.events " +
            "where c.id = ?1")
    Optional<Compilation> findByIdFetch(Integer id);

    @Query(" select c from Compilation as c " +
            "left join fetch c.events " +
            "where c.pinned = ?1")
    List<Compilation> findByPinnedFetch(Boolean pinned);
}
