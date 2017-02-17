package org.apereo.cas.ext.login.repository;

import org.apereo.cas.ext.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Created by wudongshen on 2017/2/16.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.username = :username and u.deleted = 0")
    public List<User> findByUsername(@Param("username") String username);
}
