package io.jieun.db.query;

import io.jieun.db.member.Member;
import io.jieun.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class StatementTests {
    Connection conn; //sql에 접속을 하기(여러 메소드에서 쓰기 위해 밖으로 뺌)
    Statement stmt;

    @AfterEach
    void close() {

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    @BeforeEach
    void init() {
        conn  = ConnectionUtil.getConnection(); //sql에 접속을 하기
    }


    @Test
    @DisplayName("JDBC, Statement Query-일종의 회원가입")
    void insert_test() throws Exception {

        Member admin = genMember("admin", "admin");
        Member member = genMember("member", "member");

        String sql1 = genInsertQuery(admin);
        String sql2 = genInsertQuery(member);

        stmt = conn.createStatement(); //명령문을 입력하는 창 하나 띄우기

        int rows = stmt.executeUpdate(sql1);//실행시키기 위한
        log.info("적용된 rows: {}", rows);

        rows = stmt.executeUpdate(sql2);//실행시키기 위한
        log.info("적용된 rows: {}", rows);

    }

    @Test
    @DisplayName("JDBC, Select-일종의 로그인")
    void selectTest() throws Exception {

        //아이디랑 비번입력하는데 둘다 일치해야함

    }

    private static String genInsertQuery(Member member) {
        return "INSERT INTO member (username, password) VALUES ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    //멤버를 만드는 메소드가 빠졌다.
    private static Member genMember(String username, String password) {
        return new Member(0, username, password);
    }
}
