package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findHelloBy();
    }

    @Test
    public void findTop3HelloBy() {
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 10);
        member.changeTeam(team);
        memberRepository.save(member);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findList = memberRepository.findListByUsername("AAA");
        System.out.println("findList = " + findList);
        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);
        Optional<Member> findOptional = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findOptional = " + findOptional);

        // List는 값이 없을 경우 null 이 아닌것이 보장된다.
        List<Member> emptyFindList = memberRepository.findListByUsername("asdfasdf");
        System.out.println("emptyFindList = " + emptyFindList);
        System.out.println("emptyFindList.size() = " + emptyFindList.size());

        // 단건 조회는 값이 없을 경우 null 값을 반환한다. (순수 JPA의 경우 NoResultException 예외 터뜨림)
        /* AAA값이 2개이상일 경우는 NonUniqueResultException 예외 터지는데 이를 springframework의
           IncorrectResultSizeDataAccessException 예외로 바꿔서 반환한다.
           memberRepository가 JPA에서 mongo db로 바꿔도 동일하게 동작하게 하기 위함 */
        Member emptyFindMember = memberRepository.findMemberByUsername("asdfasdf");
        System.out.println("emptyFindMember = " + emptyFindMember);

        Optional<Member> emptyFindOptional = memberRepository.findOptionalByUsername("asdfasdf");
        System.out.println("emptyFindOptional = " + emptyFindOptional);
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // ※ 주의: Page는 1부터 시작이 아니라 0부터 시작이다
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // Slice (count X) 추가로 limit + 1을 조회한다. 그래서 다음 페이지 여부 확인(최근 모바일 리스트 생각해보면 됨)
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        // 실무 꿀팁 - 엔티티를 DTO로 변경하기 (반환 타입은 엔티티가 아니라 무조건 DTO로 한다. 엔티티로 할 경우 변경될 때 API 스펙이 변경되므로)
        // Member -> DTO
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {

        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        /**
         * ※ 벌크성 쿼리 사용시 주의 사항
         *
         * 벌크성 쿼리란? 엔티티 하나를 대상으로 하는 UPDATE, DELETE가 아니라
         * 2개 이상 복수개의 엔티티의 값을 수정하고 싶을 때 쿼리 한번으로 여러 테이블의 로우를 변경하는 것을 말한다.
         *
         * jpa 변경 시 다음과 같은 절차로 동작한다.
         * ex.  1.재고가 10개 미만인 상품을 리스트로 조회
         *      2.상품 엔티티의 가격을 10% 증가
         *      3.트랜잭션 커밋 시점에 변경감지 동작하고 JPA가 값을 update 한다.
         * 위와 같이 변경감지를 동작하여 다수의 엔티티 값을 변경하려면
         * 모든 엔티티에 update SQL이 실행되기 때문에 변경된 데이터가 100건 이라면
         * 100번의 UPDATE SQL이 실행되어 성능에 문제가 생긴다.
         * 따라서, 2개이상의 데이터 수정 시 벌크성 쿼리를 사용해야 한다.
         *
         * 문제는 벌크성 쿼리 사용 시 영속성 컨텍스트는 변경되지 않고, DB의 값만 변경된다.
         * JPA의 경우 엔티티의 값을 조회할 때
         *      1.영속성 컨텍스트에 값이 있을 경우 해당 값을 조회
         *      2.영속성 컨텍스트가 비어있을 경우 db에서 조회하고 영속성 컨텍스트에 값 등록
         * 1, 2번의 순서로 값을 조회하기 때문에 벌크성 쿼리 이후 엔티티의 값을 조회하면
         * 영속성 컨텍스트에 존재하는 변경되지 않은 예전 값을 호출할 것이다. 이는 원하는 값이 아니다.
         *
         * 따라서, 벌크 연산 수행 후 영속성 컨텍스트를 초기화 해야한다.(혹은 영속성 컨텍스트가 비어있을 때 벌크 연산을 먼저 실행)
         */
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush(); // 혹시라도 변경되지 않은 내용을 DB에 반영하기 위함
//        em.clear(); // 영속성 컨텍스트 초기화 -> SpringDataJpa는 @Modifying에서 옵션으로 claer를 제공
        
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);

        System.out.println("member5 = " + member5); // clear 하지 않으면 40이라는 예상하지 못한 결과가 나온다.

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush(); // DB 반영
        em.clear(); // 영속성 컨텍스트 비우기

        // when
        /**
         * N + 1 문제 (Member 1 + Team N)
         */
        /*List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // Member 엔티티의 Team에 LAZY로딩이 발라져있으므로, 초기화된 프록시를 가져온다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            // 실제 호출을 할 때 비로소 DB에서 데이터를 가져와 값을 채운다.
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }*/
        /**
         * findMemberFetchJoin메서드에 선언된 Query의 Fetch Join으로 N + 1 문제 해결
         */
        /*List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // FetchJoin을 통해 진짜 Entity class를 갖고 있는 것을 확인할 수 있다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }*/

        /**
         * findAll()를 오버라이드하고, @EntityGraph (Fetch Join 기능)를 활용하여 메서드로 N + 1 문제 해결
         */
        /*List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // Member 엔티티의 Team에 LAZY로딩이 발라져있으므로, 초기화된 프록시를 가져온다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            // 실제 호출을 할 때 비로소 DB에서 데이터를 가져와 값을 채운다.
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }*/

        /**
         * findEntityGraphByUsername(@Param("username") String username)를 정의하고,
         * @EntityGraph (Fetch Join 기능)를 활용하여 메서드로 N + 1 문제 해결
         */
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            // Member 엔티티의 Team에 LAZY로딩이 발라져있으므로, 초기화된 프록시를 가져온다.
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            // 실제 호출을 할 때 비로소 DB에서 데이터를 가져와 값을 채운다.
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트(1차 캐시) 데이터를 DB에 동기화 해줌
        em.clear(); // 영속성 컨텍스트 초기화

        // when
        /*Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2");
        *//* flush를 하면 상태가 바뀌었다는 것을 인지한다. (dirty checking, 변경감지)
           위의 문장의 결과로 인해 변경감지가 동작하여 update query가 나간다.*//*
        em.flush();*/

        /**
         * Hint 사용을 하여 readOnly임을 밝혔다.(조회만 하겠다는 뜻) 내부적으로 최적화 되어 snapshot을 만들지 않는다.
         * 즉, 변경이 안된다고 가정하고 위에서 설명한 변경 감지 과정이 일어나지 않는다.
         */
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        memberRepository.findLockByUsername("member1"); // for update
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }
}