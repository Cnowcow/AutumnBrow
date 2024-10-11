package autumn.browmanagement.service;

import autumn.browmanagement.config.HashUtil;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(String name, String phone, Date birthDay) {
        User user = new User();
        user.setName(name);
        //user.setPhone(phone);
        user.setPhone(HashUtil.hashPhone(phone));
        user.setBirthDay(birthDay);

        // 중복 회원 검증
        validateDuplicateMember(user);

        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateMember(User user) {
        List<User> findUser = userRepository.findByUser(user.getName(), user.getPhone());
        if (!findUser.isEmpty()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    public boolean login(String name, String phone) {
        // 전화번호를 해시 처리하여 사용자 조회
        List<User> users = userRepository.findByUser(name, HashUtil.hashPhone(phone));

        // 사용자가 존재하면 true 반환
        return !users.isEmpty();
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
