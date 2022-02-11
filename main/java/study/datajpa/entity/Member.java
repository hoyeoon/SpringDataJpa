package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
) // 장점 : 애플리케이션 로딩시점에 쿼리를 파싱하여 JPQL을 SQL로 만드는 과정에서 문법 오류가 있으면 알려준다.
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // NoArgsConstructor 어노테이션으로 없앨 수 있다.
/*    protected Member() {
    }*/

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    // Setter 대신에 다음과 같이 쓰면 좋다.
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
