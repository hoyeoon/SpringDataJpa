package study.datajpa.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터를 활용한 부분 (권장 X)
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // @PageableDefault - 특별한 페이징 설정 (글로벌 설정보다 우선한다)
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username")  Pageable pageable) {
        // 리팩토링 v0 - 고전 방식
        /* Page<Member> page = memberRepository.findAll(pageable); // 이런 방식으로 넘기면 곤란하다. Member 엔티티를 외부에 노출하는 꼴. 스펙 변경에도 취약.
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return map;
         */
        // 리팩토링 v1 - 인라인 뷰
        /* return memberRepository.findAll(pageable).
                map(member -> new MemberDto(member.getId(), member.getUsername(), null));
         */
        // 리팩토링 v2 - MemberDto 생성자 파라미터를 Member로 받기
        /*return memberRepository.findAll(pageable).
                map(member -> new MemberDto(member));*/

        // 리팩토링 v3 - Method Reference
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    /**
     * @PostConstruct : Spring Application이 올라올 때 실행된다.
     */
    @PostConstruct
    public void init() {
//        memberRepository.save(new Member("userA")); // 데이터를 1개 넣는 부분

        for(int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
