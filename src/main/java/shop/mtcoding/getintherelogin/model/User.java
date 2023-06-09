package shop.mtcoding.getintherelogin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String username;  // ssar@nate.com 이런식으로 나두면 겹쳐서 위험 그래서 kakao_ssar@nate.com 이런식으로 구현
    private String password;
    private String email;
    private String provider;  // getinthere, kakao, naver
}
