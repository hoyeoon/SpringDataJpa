package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.*;

/**
 * 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
 *
 * - 조회: find…By ,read…By ,query…By get…By,
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 * 예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
 *
 * - COUNT: count…By 반환타입 long
 *
 * - EXISTS: exists…By 반환타입 boolean
 *
 * - 삭제: delete…By, remove…By 반환타입 long
 *
 * - DISTINCT: findDistinct, findMemberDistinctBy
 *
 * - LIMIT: findFirst3, findFirst, findTop, findTop3
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    /**
     * - 간단한 쿼리일 때 사용 권장
     * - parameter가 3개이상 되면 메서드명이 너무 길어지는 단점이 있으므로 parameter가 많아지거나
     *   쿼리가 복잡해진다 싶으면 @Query, 리포지토리 메소드에 쿼리 정의 방식 사용할 것 (findUser 메서드 참고)
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy(); // By 뒤에 상태를 안넣으면 전체조회

    List<Member> findTop3HelloBy(); // 위에서부터 3개 조회 (limit 3)

//    @Query(name = "Member.findByUsername")
//    위 어노테이션 없어도 잘 동작한다. 기본적으로 JpaRepository 제네릭 타입으로 적어준 "Member.메서드명"을 찾아주기 때문
//    실무에서 NamedQuery를 직접 등록하여 사용하는 일은 드물다.
//    대신, @Query를 사용해서 리포지토리 메소드에 쿼리를 직접 정의한다.(findUser 메서드 참고)
    List<Member> findByUsername(@Param("username") String username);

    /**
     * @Query, 리포지토리 메소드에 쿼리 정의 방식 (정적 쿼리를 다룰 때 권장)
     *
     * 장점
     * - 메서드 이름을 간략하게 쓸 수 있다.
     * - 복잡한 JPQL을 넣어서 문제를 해결할 수 있다.
     * - 정적 쿼리이이므로 애플리케이션 로딩시점에 쿼리를 파싱하는데,
     *   이 때 JPQL을 SQL로 만드는 과정에서 문법 오류가 있으면 알려준다.
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // @Query, 값 조회하기
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // @Query, DTO 조회하기
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // in절
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // SpringDataJpa가 반환타입을 유연하게 사용하는 것을 지원한다.
    List<Member> findListByUsername(String username);   // 컬렉션
    Member findMemberByUsername(String username);   // 단건
    Optional<Member> findOptionalByUsername(String username);   // 단건 Optional

    // 실무에서 매우 중요!! 카운트 쿼리를 분리할 수 있다.(SQL로 페이징할 때 가장 큰 문제는 count이다. 무거운 작업)
    // 쿼리가 좀 복잡해지면 countQuery 분리를 해야 한다.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    // Slice<Member> findByAge(int age, Pageable pageable);    // Slice는 totalCount 쿼리가 날라가지 않는다.

    @Modifying(clearAutomatically = true)  // executeUpdate 역할
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * @EntityGraph - Fetch join 역할
     *
     * 간단할 때 사용하고, 복잡해지면 jpql로 직접 fetch join 하면 된다.
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 이런 방식으로 할 수도 있다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 이런 방식으로 할 수도 있다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username); // find ... By <- ...에 아무거나 적어도 된다.

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
