
package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.DTO.UserDTO;
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
        user.setPhone(phone);
        try {
            user.setPassword(EncryptionUtil.encrypt(phone));
        } catch (Exception e) {
            throw new IllegalStateException("전화번호 암호화 실패", e);
        }

        user.setBirthDay(birthDay);
        user.setTreatmentCount(0L);
        user.setFirstVisitDate(new Date());
        user.setIsDeleted("N");

        // 중복 회원 검증
        validateDuplicateMember(user);

        userRepository.save(user);

        return user.getUserId();
    }

    // 중복 회원 검증
    public void validateDuplicateMember(User user) {
        Optional<User> findUser = userRepository.findByNameAndPhone(user.getName(), user.getPhone());
        if (findUser.isPresent()) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


    // 로그인 요청
    @Transactional
    public User login(String name, String password) throws Exception {
        // 전화번호를 해시 처리하여 사용자 조회
        Optional<User> users = userRepository.findByNameAndPassword(name, EncryptionUtil.encrypt(password));
        // 사용자가 존재하면 true 반환
        return users.isEmpty() ? null : users.get();
    }


    // 회원 목록
    public List<UserDTO> findAll(Long RoleId, String isDeleted) {
        List<User> users = userRepository.findByRole_RoleIdAndIsDeletedOrderByUserIdDesc(RoleId, isDeleted);
        List<UserDTO> userDTOS = new ArrayList<>();

        for(User user : users){
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setName(user.getName());
            userDTO.setPhone(user.getPhone());
            userDTO.setBirthDay(user.getBirthDay());
            userDTO.setFirstVisitDate(user.getFirstVisitDate());
            userDTO.setTreatmentCount(user.getTreatmentCount());

            userDTOS.add(userDTO);
        }
        return userDTOS;
    }


    // 사용자 id로 조회
    public User findById(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        return user;
    }


    // 사용자 정보 수정
    @Transactional
    public Long updateUser(Long userId, String name, String phone, Date birthDay, Date firstVisitDate) throws Exception {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        user.setName(name);
        user.setPhone(phone);
        user.setPassword(EncryptionUtil.encrypt(phone)); // 전화번호 암호화
        user.setBirthDay(birthDay);
        user.setFirstVisitDate(firstVisitDate);

        userRepository.save(user);

        return user.getUserId();
    }


    // 회원 삭제
    @Transactional
    public String deletePost(Long postId){
        User user = userRepository.findByUserId(postId).orElse(null);

        if (user != null) {
            // isDeleted 값을 "Y"로 변경
            user.setIsDeleted("Y");
            // 포스트 업데이트
            userRepository.save(user);
            return null;
        }
        return null;
    }

}
