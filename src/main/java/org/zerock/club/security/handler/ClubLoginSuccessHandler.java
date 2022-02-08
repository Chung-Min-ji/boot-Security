package org.zerock.club.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.club.security.dto.ClubAuthMemberDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ClubLoginSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private PasswordEncoder encoder;

    public ClubLoginSuccessHandler(PasswordEncoder encoder){
        this.encoder = encoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth)
        throws IOException, ServletException{

        log.info("---------------------------------");
        log.info("onAuthenticationSuccess(req, res, auth) invoked.");

        ClubAuthMemberDTO authMember = (ClubAuthMemberDTO)auth.getPrincipal();

        boolean fromSocial = authMember.isFromSocial();

        log.info("Need Modify Member?" + fromSocial);

        boolean passwordResult = encoder.matches("1111", authMember.getPassword());

        // 이를 이용해서 소셜 로그인한 사용자의 패스워드를 변경하게 하거나, 사용자 이름을 지정하게 하는 등 작업 할 수 있다.
        if (fromSocial && passwordResult){
            redirectStrategy.sendRedirect(req, res, "/member/modify?from=social");
        }

    }
}
