package ru.practicum.compilation.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.CompilationEntity;

@Repository
public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {

    @Query("SELECT ce FROM CompilationEntity AS ce " +
            "WHERE (:pinned IS NULL OR ce.pinned = :pinned)")
    Page<CompilationEntity> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);

}
