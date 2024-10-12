package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.config.HashUtil;
import autumn.browmanagement.controller.PostForm;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.repository.PostRepository;
import autumn.browmanagement.repository.UserRepository;
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


    @Transactional
    public void createPost(PostForm postForm) throws Exception {
        Long id = postForm.getId(); // #
        String name = postForm.getName(); // 이름
        String phone = EncryptionUtil.encrypt(postForm.getPhone()); // 전화번호
        Date birthDay = postForm.getBirthDay(); // 생년월일
        String parentTreatment = postForm.getParentTreatment(); // 시술내용
        String childTreatment = postForm.getChildTreatment(); // 시술내용
        Date treatmentDate = postForm.getTreatmentDate(); // 시술 날짜
        String visitPath = postForm.getVisitPath(); // 방문내역
        String beforeImageUrl = postForm.getBeforeImageUrl(); // 비포
        String afterImageUrl = postForm.getAfterImageUrl(); // 애프터
        String info = postForm.getInfo(); // 비고

        System.out.println("11111 = " + info);

        // User 처리
        Optional<User> optionalUser = userRepository.findByPhone(phone);
        User user;

        System.out.println("22222 = " + postForm);

        if (optionalUser.isPresent()) {
            user = optionalUser.get(); // User가 존재하는 경우
            user.setTreatmentCount(user.getTreatmentCount() + 1);

        } else {
            // User가 없는 경우, 새 User 객체 생성
            user = new User();
            user.setName(name);
            user.setPhone(phone);

            if (birthDay != null) {
                user.setBirthDay(birthDay); // 입력한 생일이 있을 경우
            } else {
                user.setBirthDay(new Date()); // 입력한 생일이 없으면 현재 날짜로 설정
            }

            userRepository.create(user); // User를 저장
        }

        System.out.println("333333 = " + user);

        // 시술 날짜가 입력되지 않았으면 현재 날짜로 설정
        if (treatmentDate == null) {
            treatmentDate = new Date(); // 현재 날짜로 설정
        }

        // Post 객체 생성 및 설정
        Post post = new Post();
        post.setId(id);
        post.setUser(user); // 설정된 User를 Post에 연결
        post.setParentTreatment(parentTreatment);
        post.setChildTreatment(childTreatment);
        post.setTreatmentDate(treatmentDate);
        post.setVisitPath(visitPath);
        post.setBeforeImageUrl(beforeImageUrl);
        post.setAfterImageUrl(afterImageUrl);
        post.setInfo(info);

        System.out.println("44444 = " + post);
        // Post를 저장
        postRepository.save(post); // Post를 저장
    }


    @Transactional
    public List<PostForm> findAll() {
        List<Post> posts = postRepository.findAll(); // 모든 게시물 조회
        System.out.println("조회했니? = " + posts);
        return convertToPostForms(posts); // Post 목록을 PostForm 목록으로 변환
    }

    private List<PostForm> convertToPostForms(List<Post> posts) {
        List<PostForm> postForms = new ArrayList<>();
        System.out.println("새로 목록만들었니? = " + postForms);
        for (Post post : posts) {
            PostForm postForm = new PostForm();
            postForm.setId(post.getId());
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
            postForm.setParentTreatment(post.getParentTreatment()); // 시술내용
            postForm.setChildTreatment(post.getChildTreatment()); // 시술내용
            postForm.setTreatmentDate(post.getTreatmentDate()); // 시술날짜
            postForm.setVisitPath(post.getVisitPath()); // 방문경로
            postForm.setFirstVisitDate(post.getUser().getFirstVisitDate()); // 첫방문 날짜
            postForm.setTreatmentCount(post.getUser().getTreatmentCount()); // 방문횟수
            postForm.setRetouch(Boolean.valueOf(post.getRetouch())); // 리터치 여부
            postForm.setRetouchDate(post.getRetouchDate()); // 리터치 날짜
            postForm.setBeforeImageUrl(post.getBeforeImageUrl()); // 비포
            postForm.setAfterImageUrl(post.getAfterImageUrl()); // 애프터
            postForm.setInfo(post.getInfo()); // 비고
            // 필요한 필드를 모두 설정하세요
            postForms.add(postForm);
            System.out.println("필드설정 다 됐니?");
        }
        System.out.println(" 이제 보낸다?");
        return postForms;
    }

    @Transactional
    public List<Post> findAll2(){

        List<Post> posts = postRepository.findAll();
        return posts;
    }

}
