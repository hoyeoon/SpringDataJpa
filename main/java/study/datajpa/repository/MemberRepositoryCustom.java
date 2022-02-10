package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.*;

/**
 * 순수한 JPA, Mybatis, Jdbc template, Querydsl 등을 쓰고 싶을 때 다음과 같은 custom interface를 만든다.
 *
 * ※ 주의해야 할 것은 Custom으로 빼는 것 자체만으로
 *    화면에 맞춰 DTO를 뽑아 지지고 볶은 복잡한 쿼리 (ex. 통계성 쿼리)를 담은 Respository와
 *    핵심 비즈니스 로직이 있는 Repository 가 분리된 것은 아니다.
 *    유지보수를 위해 이 둘은 따로 분리해야만 한다!!
 *
 *    만약, 복잡한 화면용 쿼리를 따로 빼고 싶다면 MemberQueryRepository를
 *    인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서 직접 사용해도 된다.
 *    물론 이 경우 스프링 데이터 JPA와는 아무런 관계없이 별도로 동작한다.
 */
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
