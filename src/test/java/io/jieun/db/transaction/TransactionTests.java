package io.jieun.db.transaction;

import com.zaxxer.hikari.HikariDataSource;
import io.jieun.db.member.Member;
import io.jieun.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.*;

@Slf4j
public class TransactionTests {

    Connection conn; //sql에 접속을 하기(여러 메소드에서 쓰기 위해 밖으로 뺌)
    PreparedStatement stmt;
    ResultSet rs;

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

        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Database와 연결시 auto commit 파라미터를 false로 변경해서 auto commit끄기")
    void auto_commit_off() throws Exception {

        HikariDataSource dataSource = new HikariDataSource();

        //dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/grepp_jdbc"); //이렇게 줘도 되고, 밑에 처럼 줘도 됨
        dataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        dataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);  // ✅ 이 줄 추가
        dataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        //dataSource.setAutoCommit(false); //jdbc:mysql://localhost:3306/grepp_jdbc?autoCommit=false&charset=utf-8&timeZone=Asia/seo

        try{

            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            Member saveReq = new Member(0, "_test", "_test");

            String sql = "INSERT INTO member (username, password) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, saveReq.getUsername());
            stmt.setString(2, saveReq.getPassword());

            stmt.executeUpdate();

            //throw new RuntimeException();

            conn.commit();

        } catch (SQLException e){
            conn.rollback(); //무슨 문제가 있을때 명시적으로 롤백을 할 수 있다.
        }

    }

}
