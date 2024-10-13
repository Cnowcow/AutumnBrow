
package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
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

    // 가입 요청
    @Transactional
    public Long create(String name, String phone, Date birthDay) {
        User user = new User();
        user.setName(name);
        //user.setPhone(phone);
        try {
            // 전화번호 암호화하여 저장
            user.setPhone(EncryptionUtil.encrypt(phone));
        } catch (Exception e) {
            throw new IllegalStateException("전화번호 암호화 실패", e);
        }
        user.setBirthDay(birthDay);

        // 중복 회원 검증
        validateDuplicateMember(user);

        userRepository.create(user);
        return user.getId();
    }

    // 중복 회원 검증
    @Transactional
    public void validateDuplicateMember(User user) {
        List<User> findUser = userRepository.findByUser(user.getName(), user.getPhone());
        if (!findUser.isEmpty()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    // 로그인 요청
    @Transactional
    public User login(String name, String phone) throws Exception {
        // 전화번호를 해시 처리하여 사용자 조회
        List<User> users = userRepository.findByUser(name, EncryptionUtil.encrypt(phone));
        // 사용자가 존재하면 true 반환
        return users.isEmpty() ? null : users.get(0);
    }

    // 전체 사용자 조회
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 사용자 id로 조회
    public User findOne(Long userId){
        return userRepository.findOne(userId);
    }

    // 사용자 정보 수정
    @Transactional
    public User updateUser(Long userId, String name, String phone, Date birthDay, Date firstVisitDate){
        User findUser = userRepository.findOne(userId);
        findUser.setName(name);
        try {
            // 전화번호 암호화하여 저장
            findUser.setPhone(EncryptionUtil.encrypt(phone));
        } catch (Exception e) {
            throw new IllegalStateException("전화번호 암호화 실패", e);
        }
        findUser.setBirthDay(birthDay);
        findUser.setFirstVisitDate(firstVisitDate);
        return findUser;
    }
}
