package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing	// 스프링 데이터 Jpa의 Auditing 기능 사용하기 위해 선언
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		/*
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString());
			}
		};*/ // 인터페이스의 메서드가 1개이므로 아래와 같이 Lambda로 바꿀 수 있다.
		// 실무에서는 Spring Security 등을 활용해 세션 정보를 가져와 ID를 꺼내야 한다.
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}