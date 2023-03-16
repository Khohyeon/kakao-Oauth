package shop.mtcoding.getintherelogin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import shop.mtcoding.getintherelogin.model.User;

public class UserStore {
    public static List<User> userList = new ArrayList<>();

    static {
        userList.add(new User(1, "kakao_2705558795", UUID.randomUUID().toString(), "khh5762@naver.com", "kakao"));

    }

    public static void save(User user) {
        userList.add(user);
    }

    public static User findByUsername(String username){
        for (User user : userList){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }
}
