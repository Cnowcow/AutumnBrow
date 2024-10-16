
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
            user.setPhone(decryptedPhone); // 복호화된 전화번호로 설정
        } catch (Exception e) {
            System.out.println("전화번호 복호화 실패: " + e.getMessage());
            // 필요에 따라 처리 로직 추가
        }

        return user;
    }

    // 사용자 정보 수정
    @Transactional
    public void updateUser(Long id, String name, String phone, Date birthDay, Date firstVisitDate) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + id));

        user.setName(name);
        user.setPhone(EncryptionUtil.encrypt(phone)); // 전화번호를 암호화해서 저장
        user.setBirthDay(birthDay);
        user.setFirstVisitDate(firstVisitDate);

        userRepository.save(user);
    }


    /*


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
    }*/
}
