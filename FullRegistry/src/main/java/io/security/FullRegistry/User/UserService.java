package io.security.FullRegistry.User;

public interface UserService {

    String signUpUser(User user);

    Integer enableAppUser(String email);

}
