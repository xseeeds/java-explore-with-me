package ru.server.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.server.model.EndpointHitEntity;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, COUNT((eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN coalesce(:start, eh.createdOn) AND coalesce(:end, eh.createdOn) AND eh.uri IN :uris " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStatistic> findStatisticByUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, COUNT((eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN coalesce(:start, eh.createdOn) AND coalesce(:end, eh.createdOn) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.id) DESC")
    List<ViewStatistic> findAllStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, COUNT(DISTINCT(eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN coalesce(:start, eh.createdOn) AND coalesce(:end, eh.createdOn) AND eh.uri IN :uris " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.id) DESC")
    List<ViewStatistic> findStatisticForUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                                 @Param("uris") List<String> uris);

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, COUNT(DISTINCT(eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN coalesce(:start, eh.createdOn) AND coalesce(:end, eh.createdOn) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStatistic> findAllStatisticsForUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}