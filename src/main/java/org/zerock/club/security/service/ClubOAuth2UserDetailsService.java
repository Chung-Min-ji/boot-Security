package org.zerock.club.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.zerock.club.entity.ClubMember;
import org.zerock.club.entity.ClubMemberRole;
import org.zerock.club.repository.ClubMemberRepository;
import org.zerock.club.security.dto.ClubAuthMemberDTO;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.zerock.club.entity.ClubMemberRole.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubOAuth2UserDetailsService extends DefaultOAuth2UserService {

    private final ClubMemberRepository repository;
    private final PasswordEncoder encoder;

    @Override
    //--1. loadUser() 에서는 최종 결과를 가지고 있는 OAuth2User에서 getAttribute()를 사용해 이메일 정보 추출
    //--2. 추출된 이메일 주소로 현재 db에 있는 사용자가 아니라면 자동으로 회원가입 처리
    //--3. 소셜 로그인으로 처리될 경우, 일반적인 폼 방식의 로그인은 불가능하게 하기 위해 fromSocial 값을 이용
    public OAuth2User loadUser(OAuth2UserRequest userReq) throws OAuth2AuthenticationException{
        log.info("-----------------------------------");
        log.info("userRequest : {}", userReq); //OAuth2UserRequest 객체

        String clientName = userReq.getClientRegistration().getClientName();
        log.info("===================================");
        log.info("clientName : {}", clientName); //Google로 출력
        log.info("{}", userReq.getAdditionalParameters());

        OAuth2User oAuth2User = super.loadUser(userReq);

        log.info("====================================");
        oAuth2User.getAttributes().forEach((k, v) -> {
            log.info("{} : {}", k, v); //sub, picture, email, email_verified, EMAIL 등 출력
        });

        String email = null;

        if (clientName.equals("Google")){ //구글 이용하는 경우
            email = oAuth2User.getAttribute("email");
        }

        log.info("EMAIL : {}", email);

//        ClubMember member = saveSocialMember(email);
//
//        return oAuth2User;
        ClubMember member = saveSocialMember(email);

        ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
                member.getEmail(),
                member.getPassword(),
                true, //fromSocial
                member.getRoleSet().stream().map(
                        role -> new SimpleGrantedAuthority("ROLE_" + role.name())
                ).collect(Collectors.toList()),
                oAuth2User.getAttributes()
        );
        clubAuthMember.setName(member.getName());

        return clubAuthMember;
    }

    private ClubMember saveSocialMember(String email){

        //기존에 동일한 이메일로 가입한 회원이 있는 경우에는 그대로 조회만
        Optional<ClubMember> result = repository.findByEmail(email, true);

        if (result.isPresent()){
            return result.get();
        }

        //없다면 회원 추가 패스워드는 1111, 이름은 그냥 이메일 주소로
        ClubMember clubMember = ClubMember.builder().email(email)
                .name(email)
                .password(encoder.encode("1111"))
                .fromSocial(true)
                .build();

        clubMember.addMemberRole(ClubMemberRole.USER);

        repository.save(clubMember);

        return clubMember;
    }
}
