package io.jieun.db.dao;

import io.jieun.db.member.Member;

import java.sql.SQLException;
import java.util.Optional;

public interface SimpleCrudRepository {

    Member save(Member member) throws SQLException;
    Optional<Member> findById(Integer id) throws SQLException;

    void update(Member member) throws SQLException;
    void remove(Integer id) throws SQLException;

}
