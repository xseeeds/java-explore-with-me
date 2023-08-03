package ru.server.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.defaultComponent.statisticServer.dto.ViewStatistic;
import ru.server.model.EndpointHitEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<EndpointHitEntity, Long> {

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, eh.eventId, COUNT((eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN ?1 AND ?2 AND eh.uri IN ?3 " +
            "GROUP BY eh.app, eh.uri, eh.eventId " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStatistic> findStatisticByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, eh.eventId, COUNT((eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.app, eh.uri, eh.eventId " +
            "ORDER BY COUNT(eh.id) DESC")
    List<ViewStatistic> findAllStatistics(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, eh.eventId, COUNT(DISTINCT(eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN ?1 AND ?2 AND eh.uri IN ?3 " +
            "GROUP BY eh.app, eh.uri, eh.eventId " +
            "ORDER BY COUNT(eh.id) DESC")
    List<ViewStatistic> findStatisticForUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.defaultComponent.statisticServer.dto.ViewStatistic(eh.app, eh.uri, eh.eventId, COUNT(DISTINCT(eh.ip))) " +
            "FROM EndpointHitEntity AS eh " +
            "WHERE eh.createdOn BETWEEN ?1 AND ?2 " +
            "GROUP BY eh.app, eh.uri, eh.eventId " +
            "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStatistic> findAllStatisticsForUniqueIp(LocalDateTime start, LocalDateTime end);

}