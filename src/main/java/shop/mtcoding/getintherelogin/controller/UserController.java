package shop.mtcoding.getintherelogin.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import shop.mtcoding.getintherelogin.dto.KakaoToken;
import shop.mtcoding.getintherelogin.util.Fetch;

@Controller
public class UserController {

    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/callback")
    public @ResponseBody String callback(String code) throws JsonProcessingException {
        // 1. code 값 존재 유무 확인
        if(code == null || code.isEmpty()){
            return "bad Request";
        }

        // 2. code 값 카카오 전달 -> access token 받기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "1f7061f93b3e8f6cbb2a143f64f71f0c");
        body.add("redirect_uri", "http://localhost:8080/callback"); // 2차 검증
        body.add("code", code); // 핵심

        ResponseEntity<String> codeEntity = Fetch.kakao("https://kauth.kakao.com/oauth/token", HttpMethod.POST, body);

        // 3. access token으로 카카오의 홍길동 resource 접근 가능해짐 -> access token을 파싱하고
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        KakaoToken kakaoToken = om.readValue(codeEntity.getBody(), KakaoToken.class);

        // 4. access token으로 email 정보 받기 (ssar@gmail.com)
        ResponseEntity<String> tokenEntity = Fetch.kakao("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoToken.getAccessToken());

        // 5. 해당 email로 회원가입되어 있는 user 정보가 있는지 DB 조회 (X)

        // 6. 있으면 그 user 정보로 session 만들어주고, (자동로그인) (X)

        // 7. 없으면 강제 회원가입 시키고, 그 정보로 session 만들어주고, (자동로그인)

        return tokenEntity.getBody();
    }
}
// package shop.mtcoding.getintherelogin.controller;

// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.client.RestTemplate;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.PropertyNamingStrategy;

// import shop.mtcoding.getintherelogin.dto.TokenProperties;
// import shop.mtcoding.getintherelogin.util.Fetch;

// @Controller
// public class UserController {

//     @GetMapping("/loginForm")
//     public String loginForm(){
//         return "loginForm";
//     }

//     @GetMapping("/callback")
//     public @ResponseBody String callback(String code){
//         // 1. code 값 존재 유무 확인
//         if(code == null || code.isEmpty()){
//             return "bad Request";
//         }

//         // 2. code 값 카카오 전달 -> access token 받기
//         String kakaoUrl = "https://kauth.kakao.com/oauth/token";
//         RestTemplate rt = new RestTemplate(); // http 요청 템플릿

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//         MultiValueMap<String, String> xForm = new LinkedMultiValueMap<>();
//         xForm.add("grant_type", "authorization_code");
//         xForm.add("client_id", "b0ff201221818f354554f5c04d827a26");
//         xForm.add("redirect_uri", "http://localhost:8080/callback"); // 2차 검증
//         xForm.add("code", code); // 핵심

//         ResponseEntity<String> codeEntity = Fetch.kakao("https://kauth.kakao.com/oauth/token", HttpMethod.POST, xForm);

//         // 3. access token으로 카카오의 홍길동 resource 접근 가능해짐
        
//         // 4. access token으로 파싱하고
//         ObjectMapper om = new ObjectMapper();
//         om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//         TokenProperties kakaoToken = om.readValue(codeEntity.getBody(), TokenProperties.class);
        
//         // 5. access token으로 email 정보 받기(password는 uuid로 랜덤으로 넣기)
        
//         ResponseEntity<String> tokenEntity = Fetch.kakao("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoToken.getAccessToken());

//         // RestTemplate rt1 = new RestTemplate();
//         // HttpHeaders headers1 = new HttpHeaders();
//         // headers1.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//         // // headers1.setBearerAuth(accessToken);
//         // headers1.add("Authorization", "Bearer " + accessToken);

//         // HttpEntity<?> entity = new HttpEntity<>(headers);

        
//         // ResponseEntity<String> response = rt1.exchange(kakaoUrl, HttpMethod.POST, entity, String.class);

//         // System.out.println("테스트 : "+response.getBody());

//         // 6. 해당 email로 회원가입 되어 있는 user 정보가 있는지 DB조회 (x)

//         // 7. 있으면 그 user 정보로 session 만들어주고 (자동 로그인) (x)

//         // 8. 없으면 강제 회원가입 시키고, 그 정보로 session 만들어 주고 (자동 로그인)
//         return tokenEntity.getBody();
//     }
// }