package shop.mtcoding.getintherelogin.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.getintherelogin.dto.KakaoToken;
import shop.mtcoding.getintherelogin.dto.OAuthProfile;
import shop.mtcoding.getintherelogin.model.User;
import shop.mtcoding.getintherelogin.util.Fetch;
import shop.mtcoding.getintherelogin.util.UserStore;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final HttpSession session;

    @GetMapping("/")
    public String main(){
        return "main";
    }

    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/callback")
    public String callback(String code) throws JsonProcessingException {
        // 1. code 값 존재 유무 확인
        if(code == null || code.isEmpty()){
            return "redirect:/loginForm";
        }

        // 2. code 값 카카오 전달 -> access token 받기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "b0ff201221818f354554f5c04d827a26");
        body.add("redirect_uri", "http://localhost:8080/callback"); // 2차 검증
        body.add("code", code); // 핵심

        ResponseEntity<String> codeEntity = Fetch.kakao("https://kauth.kakao.com/oauth/token", HttpMethod.POST, body);

        // 3. access token으로 카카오의 홍길동 resource 접근 가능해짐 -> access token을 파싱하고
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        KakaoToken kakaoToken = om.readValue(codeEntity.getBody(), KakaoToken.class);

        // 4. access token으로 email 정보 받기 (ssar@gmail.com)
        ResponseEntity<String> tokenEntity = Fetch.kakao("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoToken.getAccessToken());
        OAuthProfile oAuthProfile = om.readValue(tokenEntity.getBody(), OAuthProfile.class);

        // 5. 해당 email로 회원가입되어 있는 user 정보가 있는지 DB 조회 (X)
        User user = UserStore.findByUsername("kakao_" + oAuthProfile.getId());
    
        // 6. 있으면 그 user 정보로 session 만들어주고, (자동로그인) (X)
        if(user != null) {
            System.out.println("디버그 : 회원정보가 있어서 로그인을 바로 진행합니다.");
            session.setAttribute("principal", user);
        }
        
        // 7. 없으면 강제 회원가입 시키고, 그 정보로 session 만들어주고, (자동로그인)
        if(user == null) {
            System.out.println("디버그 : 회원정보가 없어서 회원가입을 바로 진행합니다.");
            User newUser = new User(2, "kakao_" + oAuthProfile.getId(), UUID.randomUUID().toString(), oAuthProfile.getKakaoAccount().getEmail(), "kakao") ;
            UserStore.save(newUser);
            session.setAttribute("principal", newUser);
        }
        return "redirect:/";
    }
}