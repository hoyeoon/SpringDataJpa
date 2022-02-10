package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * ※ 클래스 명명 규칙 - 인터페이스명(여기서는 MemberRepository) + Impl
 * MemberRepositoryCustom를 상속받을 인터페이스인 MemberRepository 이름에 Impl을 더해서 만들어야한다.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    /* @RequiredArgsConstructor이 있으면 생략 가능
    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }*/

    // 순수한 JPA를 쓰고 싶어서 만든 custom 메서드
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
