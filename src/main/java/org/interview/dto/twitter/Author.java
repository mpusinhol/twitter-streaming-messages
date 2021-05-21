package org.interview.dto.twitter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.TreeSet;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Author implements Serializable, Comparable<Author> {

    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String screenName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC")
    private Instant createdAt;

    private TreeSet<Message> messages = new TreeSet<>();

    public Author(Long id, String name, String screenName, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.screenName = screenName;
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(Author author) {
        return createdAt.compareTo(author.getCreatedAt());
    }
}
