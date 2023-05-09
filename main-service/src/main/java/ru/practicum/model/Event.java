package ru.practicum.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    Category category;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    @Column(name = "created_on")
    String createdOn;

    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;

    @Column(name = "paid", nullable = false, columnDefinition = "false")
    Boolean paid;

    @Column(name = "participant_limit", nullable = false, columnDefinition = "0")
    Integer participantLimit;

    @Column(name = "published_on")
    String publishedOn;

    @Column(name = "request_moderation", nullable = false, columnDefinition = "true")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    State state;

    String title;

    Integer views;

    Integer comments;
}
