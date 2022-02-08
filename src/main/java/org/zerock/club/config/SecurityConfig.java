package org.zerock.club.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.club.security.handler.ClubLoginSuccessHandler;
import org.zerock.club.security.service.ClubUserDetailsService;

@Configuration
@Slf4j

//-- 2) 어노테이션 기반의 접근 제한 설정할 수 있도록 하는 설정.
// 일반적으로 SecurityConfig 와 같이 시큐리티 관련 설정 클래스에 붙임.
//      prePostEnable : @PreAuthorize 이용하기 위해 사용 (컨트롤러에서)
//                      @PreAuthorize() 의 value 로는 문자열로 된 표현식을 넣는다.
//      securedEnabled : 예전 버전의 @Secure 어노테이션 사용 가능한지 지정
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //-- Remember me 설정 1.
    @Autowired
    private ClubUserDetailsService userDetailsService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    // 특정한 리소스에 접근 제한 방식으로는
    // 1) 설정을 통해 패턴 지정
    // 2) 어노테이션 이용해 적용
    // 하는 방법이 있는데, 아래 예시는 Config 클래스에서 설정하는 예시
    protected void configure(HttpSecurity http) throws Exception{

//        //-- 1) 설정을 통한 패턴 지정
//        //http.authorizeRequests() 로 인증이 필요한 리소스를 설정할 수 있고,
//        http.authorizeRequests()
//                // antMatchers()는 **/* 와 같은 앤트 스타일 패턴으로 원하는 리소스를 선택할 수 있음
//                .antMatchers("/sample/all").permitAll()
//                .antMatchers("/sample/member").hasRole("USER");

        // 인가, 인증 문제시 로그인 화면
        // 별도의 로그인 페이지는 loginPage() 로 설정할 수 있고,
        // loginProcessUrl(), defaultSuccessUrl(), failureUrl() 등을 이용해 필요한 설정 할 수 있음
        http.formLogin();

        // <form> 태그 이용하는 방식에서는 CSRF 토큰이 보안상 권장되지만,
        // REST 방식 등에서 매번 CSRF 토큰 값 알아내는 불편함 때문에
        // 경우에 따라 CSRF 토큰 발행하지 않는 경우도 있음
        http.csrf().disable();

        http.oauth2Login().successHandler(successHandler());

        //-- Remember me 설정 2.
        // 이 설정을 하면 로그인 화면에 Remember me 체크박스 출력됨
        // rememberMe 체크하고 로그인하면 remember-me 라는 쿠키 생성됨.
        // 단, 소셜 로그인(oAuth) 에서는 적용되지 않음
        http.rememberMe().tokenValiditySeconds(60*60*24*7)
                .userDetailsService(userDetailsService); //7Days

        // 스프링 시큐리티가 제공하는 기본 로그아웃 페이지 적용
        // logoutUrl(), logoutSuccessUrl() 등으로 별도 로그아웃 관련 설정 추가 가능
        // 스프링 시큐리티는 기본적으로 HttpSession 을 이용하는데,
        // invalidatedHttpSession(), deleteCookies() 로 쿠키, 세션 무효화 가능
//        http.logout();
    }

    @Bean
    public ClubLoginSuccessHandler successHandler(){
        return new ClubLoginSuccessHandler(passwordEncoder());
    }

//    @Override
//    // authenticationManager 설정 쉽게 처리할 수 있도록 도와주는 configure() 메서드 오버라이딩
//    // 파라미터로 사용된 Auth~~ManagerBuilder 는 말 그대로 코드를 통해서 직접 인증 매니저를 설정할 때 사용
//    //--------------> ClubuserDetailsService 로 대체
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//        //사용자 계정은 user1
//        auth.inMemoryAuthentication().withUser("user2")
//                .password("$2a$10$FYQreCz4DGfV9KdcC.rsjOXINBa4NDlbSvqEyWuxjnZgmfw1ZeXnm") // 1111
//                .roles("USER");
//    }
}
