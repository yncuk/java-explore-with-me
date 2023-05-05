package ru.practicum.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.RequestStatus;

import javax.persistence.*;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {

    String created;

    Integer event;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Integer requester;

    @Enumerated(EnumType.STRING)
    RequestStatus status;
}
