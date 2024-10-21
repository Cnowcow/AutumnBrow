package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.controller.PostForm;
import autumn.browmanagement.domain.*;
import autumn.browmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TreatmentRepository treatmentRepository;
    private final VisitRepository visitRepository;
    private final FtpUtil ftpUtil;


    /* ------------- 시술내역 등록 메소드 ------------- */
    public void handleFileUpload(PostForm postForm) throws IOException {
        ftpUtil.connect(); // FTP 연결

        try {
            uploadFile(postForm.getBeforeImageFile(), postForm, true);
            uploadFile(postForm.getAfterImageFile(), postForm, false);
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }
    }

    private void uploadFile(MultipartFile file, PostForm postForm, boolean isBefore) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);
            file.transferTo(localFile);
            ftpUtil.uploadFile("/autumnBrow/" + uniqueFileName, localFile);

            if (isBefore) {
                postForm.setBeforeImageUrl(uniqueFileName); // 비포 URL 설정
            } else {
                postForm.setAfterImageUrl(uniqueFileName); // 애프터 URL 설정
            }
            localFile.delete(); // 임시 파일 삭제
        }
    }

    @Transactional
    public void createPost(PostForm postForm, Treatment createdTreatment) throws Exception {
        Post post = new Post();

        Optional<User> findUser = userRepository.findByNameAndPhone(postForm.getName(), postForm.getPhone());

        User user;
        if (findUser.isPresent()) {
            user = findUser.get(); // 기존 사용자
            user.setTreatmentCount(user.getTreatmentCount() + 1L); // treatmentCount 1 증가
        } else {
            user = createUser(postForm); // 새로운 사용자 생성
        }

        // Post 정보 설정
        setPostDetails(post, postForm, user);

        // Treatment ID 설정
        if (createdTreatment != null) {
            post.setChildTreatment(createdTreatment.getTreatmentId()); // 생성된 Treatment의 ID를 설정
        }

        postRepository.save(post); // Post 저장
    }

    private User createUser(PostForm postForm) throws Exception {
        User user = new User();
        user.setName(postForm.getName());
        user.setPhone(postForm.getPhone());
        user.setPassword(EncryptionUtil.encrypt(postForm.getPhone()));
        user.setBirthDay(Optional.ofNullable(postForm.getBirthDay()).orElse(new Date()));
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Role ID 2 not found"));
        user.setRole(role);
        user.setTreatmentCount(1L);
        user.setFirstVisitDate(new Date());
        user.setIsDeleted("N");

        return userRepository.save(user); // 새로운 사용자 저장
    }

    private void setPostDetails(Post post, PostForm postForm, User user) {
        post.setVisitPath(postForm.getVisitPath());
        post.setParentTreatment(postForm.getParentTreatment());
        post.setChildTreatment(postForm.getChildTreatment());
        post.setTreatmentDate(Optional.ofNullable(postForm.getTreatmentDate()).orElse(LocalDateTime.now()));
        post.setBeforeImageUrl(postForm.getBeforeImageUrl());
        post.setAfterImageUrl(postForm.getAfterImageUrl());
        post.setInfo(postForm.getInfo());
        post.setIsDeleted("N");
        post.setUser(user);
    }
    /* 시술내역 등록 메소드 종료 */


    // 시술내역 삭제
    @Transactional
    public String deletePost(Long postId){
        Post post = postRepository.findById(postId).orElse(null);

        if (post != null) {
            // isDeleted 값을 "Y"로 변경
            post.setIsDeleted("Y");
            // 포스트 업데이트
            postRepository.save(post);
            return null;
        }
        return null;
    }


    // 시술내역 조회
    @Transactional
    public List<PostForm> findAll(String isDeleted) {
        List<Post> posts = postRepository.findByIsDeletedOrderByTreatmentDateDesc(isDeleted); // 모든 게시물 조회
        List<PostForm> postForms = new ArrayList<>();

        for (Post post : posts) {
            PostForm postForm = new PostForm();
            postForm.setPostId(post.getId());
            postForm.setUserId(post.getUser().getId()); //유저아이디
            postForm.setName(post.getUser().getName()); // 이름
            postForm.setPhone(post.getUser().getPhone()); // 전화번호
            postForm.setBirthDay(post.getUser().getBirthDay()); // 생년월일
            postForm.setFirstVisitDate(post.getUser().getFirstVisitDate()); // 첫방문 날짜
            postForm.setTreatmentCount(post.getUser().getTreatmentCount()); // 방문횟수
            
            if(!"N".equals(post.getUser().getIsDeleted())){
                postForm.setIsDeletedUser("탈퇴한 사용자");
            }
            
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
    public void updatePost(Long id, PostForm form, Treatment treatment) {
        //String beforeImageUrl, String afterImageUrl,

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + id));

        // 게시물 정보 업데이트
        post.setParentTreatment(form.getParentTreatment());
        post.setChildTreatment(form.getChildTreatment());
        post.setTreatmentDate(form.getTreatmentDate());
        post.setVisitPath(form.getVisitPath());

        if (form.getBeforeImageFile() != null){
            post.setBeforeImageUrl(String.valueOf(form.getBeforeImageFile()));
        }else {
            post.setBeforeImageUrl(post.getBeforeImageUrl());
        }

        if(form.getAfterImageFile() != null){
            post.setAfterImageUrl(String.valueOf(form.getAfterImageFile()));
        }else {
            post.setAfterImageUrl(post.getAfterImageUrl());
        }

        if (form.getRetouch() == null) {
            form.setRetouch(false); // 체크박스가 체크되지 않으면 false로 설정
        }
        post.setRetouch(form.getRetouch());

        post.setRetouchDate(form.getRetouchDate());
        post.setInfo(form.getInfo());

        // Treatment ID 설정
        if (treatment != null) {
            post.setChildTreatment(treatment.getTreatmentId()); // 생성된 Treatment의 ID를 설정
        }

        postRepository.save(post);
    }


    // 사용자별 시술내역 조회
    @Transactional
    public List<PostForm> findByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserIdAndIsDeletedOrderByTreatmentDateDesc(userId, "N"); // 사용자별 게시물 조회
        List<PostForm> postForms = new ArrayList<>();

        for (Post post : posts) {
            PostForm postForm = new PostForm();
            postForm.setPostId(post.getId());
            postForm.setUserId(post.getUser().getId()); //유저아이디
            postForm.setName(post.getUser().getName()); // 이름
            postForm.setPhone(post.getUser().getPhone()); // 전화번호
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



    // 시술내역 자세히 보기
    public PostForm findByIdView(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        PostForm postForm = new PostForm();
        postForm.setUserId(post.getUser().getId());
        postForm.setName(post.getUser().getName());
        postForm.setPhone(post.getUser().getPhone());

        if(post.getParentTreatment() != null){
            Optional<Treatment> parentTreatment = treatmentRepository.findById(post.getParentTreatment());
            if(parentTreatment.isPresent()){
                Treatment parentTreatmentName = parentTreatment.get();
                postForm.setParentName(parentTreatmentName.getName());
            }
        }

        if (post.getChildTreatment() != null) {
            Optional<Treatment> childTreatment = treatmentRepository.findById(post.getChildTreatment());
            if (childTreatment.isPresent()) {
                Treatment childTreatmentName = childTreatment.get();
                postForm.setChildName(childTreatmentName.getName()); // 자식 시술 이름 설정
            }
        }

        postForm.setTreatmentDate(post.getTreatmentDate());
        postForm.setBeforeImageUrl(post.getBeforeImageUrl());
        postForm.setAfterImageUrl(post.getAfterImageUrl());
        postForm.setRetouch(post.getRetouch());
        postForm.setRetouchDate(post.getRetouchDate());

        return postForm;
    }

}
