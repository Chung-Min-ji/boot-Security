package org.zerock.club.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.club.security.dto.ClubAuthMemberDTO;

@Controller
@Slf4j
@RequestMapping("/sample/")
public class SampleController {

    @GetMapping("/all")
    public void exAll(){
        log.info("exAll..........");
    }

    @GetMapping("member")
    public void exMember(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMember){
        log.info("exMember..........");

        log.info("------------------------------------");
        log.info("clubAuthMember: {}", clubAuthMember);
    }

    @GetMapping("/admin")
    public void exAdmin(){
        log.info("exAdmin............");
    }
}
