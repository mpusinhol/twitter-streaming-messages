package org.interview.dto.twitter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Message implements Serializable, Comparable<Message> {

    @EqualsAndHashCode.Include
    private Long id;
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC")
    private Instant createdAt;

    @Override
    public int compareTo(Message message) {
        return createdAt.compareTo(message.getCreatedAt());
    }
}
