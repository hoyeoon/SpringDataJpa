package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 순수 JPA를 사용하여 다음과 같이 Auditing을 구현하여 사용할 수 있다.
 */
@Getter
@MappedSuperclass   // 객체의 입장에서 공통 매핑 정보가 필요할 때 사용한다. 이 부분이 없으면 테이블에 공통 속성 상속이 되지 않는다.
public class JpaBaseEntity {

    @Column(updatable = false)  // createDate는 변경되지 못하게 한 것
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // 저장(insert) 전에 호출
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;  // this.createdDate로 쓸 수도 있지만, IDE가 색을 다 칠해주기 때문에 굳이 안쓰는 편이다.(정말 중요할 경우만 this. 붙임)
        updatedDate = now;
    }

    // Update 전에 호출
    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}

