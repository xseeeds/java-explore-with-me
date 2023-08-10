package ru.practicum.user.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT ue FROM UserEntity AS ue " +
            "WHERE (:userIds IS NULL OR ue.id IN :userIds OR ue.id IS NULL)")
    Page<UserEntity> findAllByIdIn(@Param("userIds") List<Long> userIds, Pageable pageable);

}
