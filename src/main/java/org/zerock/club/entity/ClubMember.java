package org.zerock.club.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Slf4j
public class ClubMember {

    @Id
    private String email;

    private String password;
    private String name;
    private boolean fromSocial;

    // ClubMember 와 ClubMemberRole 은 1:N 관계이지만,
    // 사실상 ClubMemberRole 자체가 핵심적인 역할을 하진 못하므로
    // 별도의 엔티티보다는 @ElementCollection 을 통해 별도의 PK 생성 없이 구성
    //-- @ElementCollection --//
    // JPA는 엔티티가 아닌 Basic Type 이나 Embeddable Class 로 정의된 컬렉션을 테이블로 생성하여 one-to-many 관계를 다룸
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default //null 이 아닌 빈 Set 으로 생성됨
    private Set<ClubMemberRole> roleSet = new HashSet<>();

    public void addMemberRole(ClubMemberRole clubMemberRole){
        log.info("## addMemberRole({}) invoked.", clubMemberRole);
        log.info("## roleSet : {}", roleSet);
        roleSet.add(clubMemberRole);
    }
}
