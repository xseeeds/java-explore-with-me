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

    @Query("select ue from UserEntity as ue " +
            "where (:ids is null or ue.id in :ids or ue.id is null)")
    Page<UserEntity> findAllByAdmin(@Param("ids") List<Long> ids, Pageable pageable);

}
