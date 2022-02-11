package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 테이블에 createdDate, lastModifiedDate 컬럼은 다 넣는 것이 원칙이다.
 * 등록자, 수정자 컬럼이 필요 없다면 아래 BaseTimeEntity를 상속받으면 되고,
 * 필요하다면 BaseEntity를 상속받으면 된다.
 */
@EntityListeners(AuditingEntityListener.class)  // 이벤트를 기반으로 동작한다는 것을 의미
@MappedSuperclass
@Getter

public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
