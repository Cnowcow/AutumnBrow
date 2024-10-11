package autumn.browmanagement.service;

import autumn.browmanagement.config.HashUtil;
import autumn.browmanagement.controller.PostForm;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.repository.PostRepository;
import autumn.browmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Transactional
    public void savePost(PostForm postForm) {
        Long id = postForm.getId();
        String name = postForm.getName();
        String phone = HashUtil.hashPhone(postForm.getPhone());
        Date treatmentDate = postForm.getTreatmentDate();
        String visitPath = postForm.getVisitPath();
        String beforeImageUrl = postForm.getBeforeImageUrl();
        String afterImageUrl = postForm.getAfterImageUrl();
        String info = postForm.getInfo();

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
            // 추가적인 필드도 필요하다면 설정

            userRepository.save(user); // User를 저장
        }

        System.out.println("333333 = " + user);

        // Post 객체 생성 및 설정
        Post post = new Post();
        post.setId(id);
        post.setVisitPath(visitPath);
        post.setTreatmentDate(treatmentDate);
        post.setBeforeImageUrl(beforeImageUrl);
        post.setAfterImageUrl(afterImageUrl);
        post.setInfo(info);
        post.setUser(user); // 설정된 User를 Post에 연결

        System.out.println("44444 = " + post);
        // Post를 저장
        postRepository.save(post); // Post를 저장
    }


}
