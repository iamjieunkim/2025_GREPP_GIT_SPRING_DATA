package io.jieun.db.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

@Slf4j
class ConnectionUtilTests {
    @Test
    @DisplayName("Database Connection 테스트")
    void connection_test() throws Exception {
        ConnectionUtil.getConnection();
    }
}