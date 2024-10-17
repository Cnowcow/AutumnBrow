package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.controller.PostForm;
import autumn.browmanagement.domain.*;
import autumn.browmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TreatmentRepository treatmentRepository;
    private final VisitRepository visitRepository;


    // 시술내역 등록
    @Transactional
    public void createPost(PostForm postForm) throws Exception {
        Post post = new Post();

        String encryptedPhone = EncryptionUtil.encrypt(postForm.getPhone());

        Optional<User> findUser = userRepository.findByNameAndPhone(postForm.getName(), encryptedPhone);

        User user;
        if (findUser.isPresent()) {
            user = findUser.get(); // 기존 사용자
            user.setTreatmentCount(user.getTreatmentCount() + 1L); // treatmentCount 1 증가
        } else {
            // 새로운 사용자 생성
            user = new User();
            user.setName(postForm.getName());
            user.setPhone(encryptedPhone);

            if (postForm.getBirthDay() != null) {
                user.setBirthDay(postForm.getBirthDay()); // 입력한 생일이 있을 경우
            } else {
                user.setBirthDay(new Date()); // 입력한 생일이 없으면 현재 날짜로 설정
            }

            Role role = roleRepository.findById(2L)
                    .orElseThrow(() -> new IllegalArgumentException("Role ID 2 not found"));
            user.setRole(role); // Role 설정
            user.setTreatmentCount(1L);
            user.setFirstVisitDate(new Date()); // 첫방문날짜

            userRepository.save(user); // 새로운 사용자 저장
        } // user 이름, 전화번호, 생년월일

        post.setVisitPath(postForm.getVisitPath()); // 방문경로
        post.setParentTreatment(postForm.getParentTreatment()); // 시술내용
        post.setChildTreatment(postForm.getChildTreatment()); // 세부내용
        if (postForm.getTreatmentDate() != null) {
            post.setTreatmentDate(postForm.getTreatmentDate());
        } else {
            post.setTreatmentDate(new Date());
        } // 시술 날짜 세팅
        post.setBeforeImageUrl(postForm.getBeforeImageUrl()); // 비포
        post.setAfterImageUrl(postForm.getAfterImageUrl()); // 애프터
        post.setInfo(postForm.getInfo()); // 비고
        post.setIsDeleted("N"); // 삭제유무

        post.setUser(user); // Post에 User 설정

        postRepository.save(post);
    }


    // 시술내역 조회
    @Transactional
    public List<PostForm> findAll(String isDeleted) {
        List<Post> posts = postRepository.findByIsDeleted(isDeleted); // 모든 게시물 조회
        List<PostForm> postForms = new ArrayList<>();

        for (Post post : posts) {
            PostForm postForm = new PostForm();
            postForm.setPostId(post.getId());
            postForm.setUserId(post.getUser().getId()); //유저아이디
            postForm.setName(post.getUser().getName()); // 이름

            // 전화번호 복호화
            String decryptedPhone = null;
            try {
                decryptedPhone = EncryptionUtil.decrypt(post.getUser().getPhone());
            } catch (Exception e) {
                System.out.println("전화번호 복호화 실패: " + e.getMessage());
            }
            postForm.setPhone(decryptedPhone); // 복호화된 전화번호 설정

            postForm.setBirthDay(post.getUser().getBirthDay()); // 생년월일
            postForm.setFirstVisitDate(post.getUser().getFirstVisitDate()); // 첫방문 날짜
            postForm.setTreatmentCount(post.getUser().getTreatmentCount()); // 방문횟수

            if (post.getParentTreatment() != null) {
                Optional<Treatment> parrentTreatment = treatmentRepository.findById(post.getParentTreatment());
                if (parrentTreatment.isPresent()) {
                    postForm.setParentTreatment(post.getParentTreatment()); // ID 설정
                    postForm.setParentName(parrentTreatment.get().getName()); // 이름 설정
                }
            } // 시술내용

            if (post.getChildTreatment() != null) {
                Optional<Treatment> parrentTreatment = treatmentRepository.findById(post.getChildTreatment());
                if (parrentTreatment.isPresent()) {
                    postForm.setChildTreatment(post.getChildTreatment()); // ID 설정
                    postForm.setChildName(parrentTreatment.get().getName()); // 이름 설정
                }
            } // 세부내용

            postForm.setTreatmentDate(post.getTreatmentDate()); // 시술날짜
            postForm.setVisitPath(post.getVisitPath()); // 방문경로

            if (post.getVisitPath() != null) {
                Optional<Visit> visitPath = visitRepository.findById(post.getVisitPath());
                if (visitPath.isPresent()) {
                    postForm.setVisitPath(post.getVisitPath()); // ID 설정
                    postForm.setVisitName(visitPath.get().getVisitPath()); // 이름 설정
                }
            } // 방문 경로

            postForm.setRetouch(Boolean.valueOf(post.getRetouch())); // 리터치 여부
            postForm.setRetouchDate(post.getRetouchDate()); // 리터치 날짜
            postForm.setBeforeImageUrl(post.getBeforeImageUrl()); // 비포
            postForm.setAfterImageUrl(post.getAfterImageUrl()); // 애프터
            postForm.setInfo(post.getInfo()); // 비고

            postForms.add(postForm);
        }
        return postForms;
    }


    // 수정할 게시물 가져오기
    public Post findById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        return post;
    }


    // 게시물 수정
    @Transactional
    public void updatePost(Long id, Long parentTreatment, Long childTreatment,
                           Date treatmentDate, Long visitPath, String retouch,
                           Date retouchDate, String info) {
        //String beforeImageUrl, String afterImageUrl,

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + id));

        // 게시물 정보 업데이트
        post.setParentTreatment(parentTreatment);
        post.setChildTreatment(childTreatment);
        post.setTreatmentDate(treatmentDate);
        post.setVisitPath(visitPath);
//        post.setBeforeImageUrl(beforeImageUrl);
//        post.setAfterImageUrl(afterImageUrl);
        post.setRetouch(Boolean.valueOf(retouch));
        post.setRetouchDate(retouchDate);
        post.setInfo(info);

        postRepository.save(post);
    }

}
