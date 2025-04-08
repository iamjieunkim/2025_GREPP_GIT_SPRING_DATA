package io.jieun.db.util;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;

@Slf4j
class ConnectionUtilTests {

    @Test
    @DisplayName("Database Connection 테스트")
    void connection_test() throws Exception {
        Connection conn = ConnectionUtil.getConnection();

        log.info("conn = {}", conn);
        conn.close();
    }

    @Test
    @DisplayName("Pool")
    void test_1() throws Exception {
        //dataSource 커넥션을 가져오는 방법 자체를 추상화 한 것
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                ConnectionUtil.MysqlDbConnectionConstant.URL,
                ConnectionUtil.MysqlDbConnectionConstant.USERNAME,
                ConnectionUtil.MysqlDbConnectionConstant.PASSWORD
        );

        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        log.info("conn1 = {}", conn1);
        log.info("conn2 = {}", conn2);
        conn1.close();
        conn2.close();
    }


    @Test
    @DisplayName("hikari")
    void hikari_test() throws Exception {
        //hikariDataSource그 dataSource를 추상화 한게 히카리 데이터 소스다
        HikariDataSource hikariDataSource = new HikariDataSource(); //이렇게 되면 일단 커넥셔션 풀이 생성이 된다.

        hikariDataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL); //커넥션 풀이 접속(?) 을 받는다
        hikariDataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        hikariDataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        hikariDataSource.setMaximumPoolSize(5);

        //자고 있던 커넥션 3개를 꺼낸거임
        Connection conn1 = hikariDataSource.getConnection();
        Connection conn2 = hikariDataSource.getConnection();
        Connection conn3 = hikariDataSource.getConnection();

        Thread.sleep(10000);

    }
}