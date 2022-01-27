package org.zerock.club.entity;

import lombok.*;

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
public class ClubMember {

    @Id
    private String email;

    private String password;
    private String name;
    private boolean fromSocial;

    // ClubMember 와 ClubMemberRole 은 1:N 관계이지만,
    // 사실상 ClubMemberRole 자체가 핵심적인 역할을 하진 못하므로
    // 별도의 엔티티보다는 @ElementCollection 을 통해 별도의 PK 생성 없이 구성
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<ClubMemberRole> roleSet = new HashSet<>();

    public void addMemberRole(ClubMemberRole clubMemberRole){
        roleSet.add(clubMemberRole);
    }
}
