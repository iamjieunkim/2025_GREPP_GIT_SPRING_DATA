package io.jieun.db.query;

import com.zaxxer.hikari.HikariDataSource;
import io.jieun.db.dao.SimpleCrudRepository;
import io.jieun.db.dao.SimpleJdbcCrudRepository;
import io.jieun.db.member.Member;
import io.jieun.db.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class SimpleJdbcCrudRepositoryTests {

    //구현체보다 인터페이스를 통해 테스트하는게 좋다(상위파일로 하는게 좋음)
    SimpleCrudRepository repository;

    @BeforeEach
    void init(){

        //추상화되어있는 데이터 소스를 이용하면 풀에 있는거 뽑아올 수도 있고 폴이 아닌것도 뽑아올 수도 있다.5ㄱ55ㄱㄱㄱㄱㄱㄱㄱㄱㄱ
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        dataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        dataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        repository = new SimpleJdbcCrudRepository(dataSource); //이때는 구현체를 넣는게 맞음(왜냐면 해당 조건에 대한 테스트를 해야되니까)
    }

    @Test
    @DisplayName("save Test-추가 insert")
    void save_test() throws Exception {

        //랜덤으로 user이름을 만들겠다.
        String randomUsrStr = "USER_" + ((int)(Math.random() * 1_000_000));
        log.info("randomUsrStr = {}", randomUsrStr);

        Member saveRequest = new Member(0, randomUsrStr, randomUsrStr);//맴버 인스턴스 하나 생성
        Member savedMember = repository.save(saveRequest);

        log.info("savedMember = {}", savedMember); //중급방법
        assertThat(savedMember.getMemberId()).isNotEqualTo(0); //고수방법

    }

    @Test
    @DisplayName("read test - 조회하는 테스트(성공)")
    void read_test_ok() throws Exception {

        int availableIdx = 1;


        Optional<Member> memberOptional = repository.findById(availableIdx);//where문에 1이 들어가서 테스트를 하는 것

        boolean result = memberOptional.isPresent();
        assertThat(result).isTrue();

        Member findMember = memberOptional.get();

        assertThat(findMember).isNotNull();
        assertThat(findMember.getMemberId()).isEqualTo(availableIdx);

        log.info("findMember = {}", findMember);

    }

    @Test
    @DisplayName("read test - 조회하는 테스트(실패)")
    void read_test_ng() throws Exception {

        int unavailableIdx = 9999;


        Optional<Member> memberOptional = repository.findById(unavailableIdx);

        boolean result = memberOptional.isPresent();
        assertThat(result).isFalse();

        assertThatThrownBy(
                () -> {
                    memberOptional.get();
                }
        ).isInstanceOf(NoSuchElementException.class);


    }

    @Test
    @DisplayName("update test - 수정하는 테스트")
    void update_test() throws Exception {

        //이미 저장이 되어있는 row를 꺼내올것
        //꺼내온다음, 비밀번호를 랜덤으로 바꾸고 업데이트를 하고
        //제대로 바뀌었나 또 조회를 할꺼임!
        int availableIdx = 1;

        Optional<Member> memberOptional = repository.findById(availableIdx);
        boolean result = memberOptional.isPresent();
        assertThat(result).isTrue();

        Member findMember = memberOptional.get(); //실제로 DB에 있는 계정을 하나 가져왔

        String targetPwd = UUID.randomUUID().toString();

        findMember.setPassword(targetPwd);
        repository.update(findMember);

        Optional<Member> findMemberOptional = repository.findById(availableIdx);
        boolean result2 = findMemberOptional.isPresent();
        assertThat(result2).isTrue();

        Member updatedMember = findMemberOptional.get();

        log.info("updateMember = {}", updatedMember);

        assertThat(updatedMember.getMemberId()).isEqualTo(findMember.getMemberId());
        assertThat(updatedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(updatedMember.getPassword()).isEqualTo(targetPwd);


    }


    @Test
    @DisplayName("remove test- 삭제테스트")
    void remove_test() throws Exception {

        int targetId = 2;
        repository.remove(targetId);

        Optional<Member> memberOptional = repository.findById(targetId);
        boolean result = memberOptional.isPresent();
        assertThat(result).isFalse();

        assertThatThrownBy(
                () -> {
                    memberOptional.get();
                }
        ).isInstanceOf(NoSuchElementException.class);

    }
}