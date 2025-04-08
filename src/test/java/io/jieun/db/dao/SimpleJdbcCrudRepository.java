package io.jieun.db.dao;

import io.jieun.db.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@RequiredArgsConstructor
public class SimpleJdbcCrudRepository implements SimpleCrudRepository {

    private final DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        JdbcUtils.closeConnection(connection);
        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeResultSet(resultSet);
    }

    @Override
    public Member save(Member member) throws SQLException{

        String sql = "INSERT INTO member (username, password) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

       try{
           conn = getConnection();
           pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

           pstmt.setString(1, member.getUsername());
           pstmt.setString(2, member.getPassword());

           pstmt.executeUpdate();

           rs = pstmt.getGeneratedKeys();

           if( rs.next() ){ //가져왔으면 이제 꺼내야됨
               int idx = rs.getInt(1);//왜 int로 가져오냐면 member에 있는 member의 타입이 int이니까
               member.setMemberId(idx);
           }


           return member;

       } catch (SQLException e) {
           throw e;
       } finally {
           closeConnection(conn, pstmt, rs);
       }
    }

    @Override
    public Optional<Member> findById(Integer id) throws SQLException {

        String sql = "SELECT * FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{

            connection = getConnection(); //커넥션 꺼내오기
            preparedStatement = connection.prepareStatement(sql); //여기서는 두번째 인자 안줘도 됨 -> 어차피 한줄 다 가져올꺼라

            preparedStatement.setInt(1, id); //1번째 바인딩 해야하는 곳에다가 id를 넣어서 만들어줘

            resultSet = preparedStatement.executeQuery(); //결과를 받아서 넣어오자

            if(resultSet.next()){ //결과를 받아 왔을 수 있도 있고 안 받아왔을 수도 있다
                //받아왔으면 여기가 실행이 됨
                Member findMember = new Member(
                        resultSet.getInt("member_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
                return Optional.of(findMember);
            } else{
                return Optional.empty(); //비어있는 옵셔널을 반환을 해줘야 함
            }


        } catch (SQLException e) {
            throw e;
        } finally {
            closeConnection(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public void update(Member member) {

    }

    @Override
    public void remove(Integer id) {

    }
}
