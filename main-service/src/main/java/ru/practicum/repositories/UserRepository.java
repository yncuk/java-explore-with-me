package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(" select u from User u " +
            "where u.id in :ids")
    List<User> findAllByIds(@Param("ids") Integer[] ids);

}
