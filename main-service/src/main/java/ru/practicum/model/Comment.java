package ru.practicum.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.CommentStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String text;

    @Column(name = "event_id")
    Integer eventId;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    User author;

    LocalDateTime created;

    @Enumerated(EnumType.STRING)
    CommentStatus status;
}
