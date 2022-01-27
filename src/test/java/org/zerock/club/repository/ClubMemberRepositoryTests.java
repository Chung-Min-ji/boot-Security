package org.zerock.club.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.zerock.club.entity.ClubMember;
import org.zerock.club.entity.ClubMemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Rollback(false)
class ClubMemberRepositoryTests {

    @Autowired
    private ClubMemberRepository repository;

    @Autowired
    private PasswordEncoder pwEncoder;

    @Test
    public void insertDummies(){

        // 1-80 : USER
        // 81-90 : USER, MANAGER
        // 91-100 : USER, MANAGER, ADMIN
        IntStream.rangeClosed(1,100).forEach(i -> {
            ClubMember clubMember = ClubMember.builder()
                    .email("user"+i+"@zerock.org")
                    .name("사용자"+i)
                    .fromSocial(false)
                    .password(pwEncoder.encode("1111"))
                    .build();

            // default role
            clubMember.addMemberRole(ClubMemberRole.USER);

            if (i>80){
                clubMember.addMemberRole(ClubMemberRole.MANAGER);
            }
            if (i>90){
                clubMember.addMemberRole(ClubMemberRole.ADMIN);
            }

            repository.save(clubMember);
        }); //forEach
    } //insertDummies

    @Test
    public void testRead(){
        Optional<ClubMember> result = repository.findByEmail("user95@zerock.org", false);

        ClubMember clubMember = result.get();
        System.out.println("\t++ clubMember: " + clubMember);
    }
}