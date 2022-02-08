package org.zerock.club.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.club.entity.ClubMember;
import org.zerock.club.repository.ClubMemberRepository;
import org.zerock.club.security.dto.ClubAuthMemberDTO;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubUserDetailsService implements UserDetailsService {

    private final ClubMemberRepository clubMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        // 별도 처리 없이 loadUserByUsername() 에서 로그 기록
        log.info("\t ++ ClubUserDetailsService loadUserByUsername : " + username);

        Optional<ClubMember> result = clubMemberRepository.findByEmail(username, false);

        if (!result.isPresent()){
            throw new UsernameNotFoundException("Check Email or Social");
        }

        ClubMember clubMember = result.get();

        log.info("------------------");
        log.info("\t ++ clubMember : {}", clubMember);

        // ClubMember 를 UserDetails 타입으로 처리하기 위해 ClubAuthMemberDTO 타입으로 변환
        ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
                clubMember.getEmail(),
                clubMember.getPassword(),
                clubMember.isFromSocial(),
                clubMember.getRoleSet().stream()
                        // 스프링 시큐리티에서 사용하는 SimpleGrantedAuthority 로 변환.
                        // 'ROLE_' 이라는 접두어 추가해서 사용.
                        // (스프링 시큐리티에서 모든 역할에는, 역할임을 식별하기 위해 'ROLE_' 접두어가 붙어야 함)
                        // 'ROLE_USER' 는 'hasRole("USER")' 와 같음
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toSet())
        );

        clubAuthMember.setName(clubMember.getName());
        clubAuthMember.setFromSocial(clubAuthMember.isFromSocial());

        return clubAuthMember;
    }
}
