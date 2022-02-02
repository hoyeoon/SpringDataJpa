package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional   // Test가 끝날 때 rollback을 시킨다. -> Jpa의 영속성 컨텍스트도 flush를 안한다.(DB에 아무 쿼리도 보내지 않는다.)
@Rollback(false) // 공부하는 입장에서는 데이터를 쌓아 테스트하기 위해 사용. 실무에서는 X. Jpa가 마지막에 rollback을 안하고, commit을 해버린다. -> Jpa의 영속성 컨텍스트를 flush 시킨다.(DB에 쿼리를 보낸다.)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
}