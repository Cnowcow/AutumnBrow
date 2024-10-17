
package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.controller.UserForm;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    // 회원가입 요청
    @Transactional
    public Long register(String name, String phone, Date birthDay){
        User user = new User();
        user.setName(name);

        try {
            user.setPhone(EncryptionUtil.encrypt(phone));
        } catch (Exception e) {
            throw new IllegalStateException("전화번호 암호화 실패", e);
        }

        user.setBirthDay(birthDay);
        user.setTreatmentCount(0L);
        user.setFirstVisitDate(new Date());

        // 중복 회원 검증
        validateDuplicateMember(user);

        userRepository.save(user);

        return user.getId();
    }

    // 중복 회원 검증
    public void validateDuplicateMember(User user) {
        Optional<User> findUser = userRepository.findByNameAndPhone(user.getName(), user.getPhone());
        if (findUser.isPresent()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


    // 회원 목록
    public List<UserForm> findAll(Long RoleId) {
        List<User> users = userRepository.findByRoleId(RoleId);
        List<UserForm> userForms = new ArrayList<>();

        for(User user : users){
            UserForm userForm = new UserForm();
            userForm.setUserId(user.getId());
            userForm.setName(user.getName());

            String decryptedPhone = null;
            try {
                decryptedPhone = EncryptionUtil.decrypt(user.getPhone());
            } catch (Exception e){
                System.out.println("전화번호 복호화 실패" + e.getMessage());
            }
            userForm.setPhone(decryptedPhone); // 복호화된 전화번호 설정

            userForm.setBirthDay(user.getBirthDay());
            userForm.setFirstVisitDate(user.getFirstVisitDate());
            userForm.setTreatmentCount(user.getTreatmentCount());

            userForms.add(userForm);
        }
        return userForms;
    }


    // 사용자 id로 조회
    public User findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 전화번호 복호화
        try {
            String decryptedPhone = EncryptionUtil.decrypt(user.getPhone());
            user.setPhone(decryptedPhone);
        } catch (Exception e) {
            System.out.println("전화번호 복호화 실패: " + e.getMessage());
        }

        return user;
    }


    // 사용자 정보 수정
    @Transactional
    public void updateUser(Long id, String name, String phone, Date birthDay, Date firstVisitDate) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id));

        user.setName(name);
        user.setPhone(EncryptionUtil.encrypt(phone)); // 전화번호 암호화
        user.setBirthDay(birthDay);
        user.setFirstVisitDate(firstVisitDate);

        userRepository.save(user);
    }


    /*
    // 로그인 요청
    @Transactional
    public User login(String name, String phone) throws Exception {
        // 전화번호를 해시 처리하여 사용자 조회
        List<User> users = userRepository.findByUser(name, EncryptionUtil.encrypt(phone));
        // 사용자가 존재하면 true 반환
        return users.isEmpty() ? null : users.get(0);
    }
    */
}
