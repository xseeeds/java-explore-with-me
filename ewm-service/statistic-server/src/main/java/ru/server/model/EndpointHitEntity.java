package ru.server.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistics")
public class EndpointHitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 128, nullable = false)
    String app;

    @Column(length = 128, nullable = false)
    String uri;

    @Column(name = "event_id")
    Long eventId;

    @Column(length = 64, nullable = false)
    String ip;

    @Column(name = "created_date", nullable = false)
    LocalDateTime createdOn;

}
