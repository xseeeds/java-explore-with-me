package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.HitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.HitDto(h.app, h.uri, COUNT((h.ip))) " +
            "FROM EndpointHit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<HitDto> findStatisticByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.HitDto(h.app, h.uri, COUNT((h.ip))) " +
            "FROM EndpointHit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    List<HitDto> findAllStatistics(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.HitDto(h.app, h.uri, COUNT(DISTINCT(h.ip))) " +
            "FROM EndpointHit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    List<HitDto> findStatisticForUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.HitDto(h.app, h.uri, COUNT(DISTINCT(h.ip))) " +
            "FROM EndpointHit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<HitDto> findAllStatisticsForUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h FROM EndpointHit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2")
    List<EndpointHit> findByDatetime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h FROM EndpointHit AS h " +
            "WHERE h.created BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3")
    List<EndpointHit> findByDatetimeAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}