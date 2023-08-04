package ru.defaultComponent.statisticServer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static ru.defaultComponent.dateTime.DefaultDateTimeFormatter.PATTERN_DATE_TIME;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticRequest {

    Long id;

    @NotBlank
    String app;

    @NotBlank
    String uri;

    @JsonIgnore
    @Builder.Default
    List<Long> eventsIds = emptyList();

    @NotBlank
    String ip;

    @NotNull
    @PastOrPresent
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATE_TIME)
    LocalDateTime createdOn;

    //TODO !PastOrPresent !"timestamp"

}
