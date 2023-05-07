package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    @Query(" select c from Compilation as c " +
            "left join fetch c.events " +
            "where c.id = :id")
    Optional<Compilation> findByIdFetch(@Param("id") Integer id);

    @Query(" select c from Compilation as c " +
            "left join fetch c.events " +
            "where c.pinned = :pinned")
    List<Compilation> findByPinnedFetch(@Param("pinned") Boolean pinned);
}
