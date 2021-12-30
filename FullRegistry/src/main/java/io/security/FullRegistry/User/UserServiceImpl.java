package io.security.FullRegistry.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j @RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public String signUpUser(User user) {
        return null;
    }

    @Override
    public Integer enableAppUser(String email) {
        return null;
    }
}
