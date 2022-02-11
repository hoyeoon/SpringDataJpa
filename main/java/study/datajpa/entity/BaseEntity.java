package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 스프링 데이터 JPA를 사용하여 다음과 같이 Auditing을 구현하여 사용할 수 있다.
 */
@EntityListeners(AuditingEntityListener.class)  // 이벤트를 기반으로 동작한다는 것을 의미
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity{
    /**
     * 등록자, 수정자는 이렇게만 두면 값이 안들어가지고, 스프링을 활용해야 한다.
     * DataJpaApplication 클래스에 AuditorAware 빈을 등록한다.
     * 앞으로 등록되거나 수정될 때마다 AuditorAware 빈을 호출해서 결과물을 꺼내어
     * createdBy, lastModifiedBy 값을 채운다.
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;   // 등록자

    @LastModifiedBy
    private String lastModifiedBy;  // 수정자
}
