package io.jieun.db.query;

import io.jieun.db.member.Member;
import io.jieun.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class StatementTests {
    Connection conn; //sql에 접속을 하기(여러 메소드에서 쓰기 위해 밖으로 뺌)
    Statement stmt;
    ResultSet rs;
    PreparedStatement pstmt;

    @AfterEach
    void close() {


        if( rs != null ) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        if(pstmt != null) {
            try {
                pstmt.close();
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

        Member user1 = genMember("member", "member");
        Member user1_ng = genMember("member", "1234");

        //아이디랑 비번입력하는데 둘다 일치해야함
        //로그인이 되었다면 -> row가 조회가 되어야함
        String sql1 = genSelectQuery(user1);
        String sql2 = genSelectQuery(user1_ng);

        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql1); //resultSet

//        String findUsername="";
//        String findPassword="";

        Member findMember = new Member();

        if(rs.next()) { //다음줄이 있다면? 있다면 데이터가 있는거고, 없다면 값이 안들어있는 빈 테이블
            findMember.setMemberId(rs.getInt("member_id")); //Member필드이름이라동일하게 함
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

//        log.info("findUsername = {}", findMember.getUsername());
//        log.info("findPassword = {}", findMember.getPassword());

        assertThat(findMember.getMemberId()).isEqualTo(2);
        assertThat(findMember.getUsername()).isEqualTo("member");
        assertThat(findMember.getPassword()).isEqualTo("member");

        rs.close();

        rs = stmt.executeQuery(sql2);

        findMember = new Member();

        if(rs.next()) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isNull();
        assertThat(findMember.getPassword()).isNull();
    }

    @Test
    @DisplayName("Statement Test")
    void statement_test() throws Exception {

        Member admin = genMember("admin", "' or '' = '");
        String sql = genSelectQuery(admin);

        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);

        Member findMember = new Member();

        if(rs.next()) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isEqualTo("admin");
        assertThat(findMember.getPassword()).isEqualTo("admin");

    }

    @Test
    @DisplayName("test")
    void _test() throws Exception {

        //SELECT m.member_id, m.username, m.password FROM member as m
        // WHERE m.username = ? AND m.password = ?
        Member unsafeAttempt = genMember("admin", "' or '' = '");

        String sql = "SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = ? AND m.password = ?";

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, unsafeAttempt.getUsername());
        pstmt.setString(2, unsafeAttempt.getPassword());

        rs = pstmt.executeQuery();

        Member findMember = new Member();

        if(rs.next()) {
            findMember.setMemberId(rs.getInt("member_id"));
            findMember.setUsername(rs.getString("username"));
            findMember.setPassword(rs.getString("password"));
        }

        assertThat(findMember.getUsername()).isNull();
        assertThat(findMember.getPassword()).isNull();


    }

    private static String genSelectQuery(Member member) {
        return "SELECT m.member_id, m.username, m.password FROM member as m WHERE m.username = '%s' AND m.password = '%s'".formatted(member.getUsername(), member.getPassword());
    }

    private static String genInsertQuery(Member member) {
        return "INSERT INTO member (username, password) VALUES ('%s', '%s')".formatted(member.getUsername(), member.getPassword());
    }

    //멤버를 만드는 메소드가 빠졌다.
    private static Member genMember(String username, String password) {
        return new Member(0, username, password);
    }
}
