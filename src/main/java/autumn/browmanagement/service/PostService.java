package autumn.browmanagement.service;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.Entity.*;
import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.config.FtpUtil;
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
    public void handleFileUpload(PostDTO postDTO) throws IOException {
        ftpUtil.connect(); // FTP 연결

        try {
            uploadFile(postDTO.getBeforeImageFile(), postDTO, true);
            uploadFile(postDTO.getAfterImageFile(), postDTO, false);
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }
    }

    private void uploadFile(MultipartFile file, PostDTO postDTO, boolean isBefore) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);
            file.transferTo(localFile);
            ftpUtil.uploadFile("/AutumnBrow/BeforeAndAfter/" + uniqueFileName, localFile);

            if (isBefore) {
                postDTO.setBeforeImageUrl(uniqueFileName); // 비포 URL 설정
            } else {
                postDTO.setAfterImageUrl(uniqueFileName); // 애프터 URL 설정
            }
            localFile.delete(); // 임시 파일 삭제
        }
    }

    @Transactional
    public void postCreate(PostDTO postDTO) throws Exception {
        Post post = new Post();

        Optional<User> findUser = userRepository.findByNameAndPhone(postDTO.getName(), postDTO.getPhone());

        User user;
        if (findUser.isPresent()) {
            user = findUser.get(); // 기존 사용자
            user.setTreatmentCount(user.getTreatmentCount() + 1L); // treatmentCount 1 증가
        } else {
            user = createUser(postDTO); // 새로운 사용자 생성
        }

        // Post 정보 설정
        setPostDetails(post, postDTO, user);

        // Visit 정보 설정
        if (postDTO.getVisitId() != null) {
            Visit visit = visitRepository.findById(postDTO.getVisitId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방문 경로입니다."));
            post.setVisit(visit);
        }

        Treatment parentTreatment = null;
        Treatment childTreatment = null;

        // 시술내용이 null 일 때
        if (postDTO.getParentTreatment() == null){
            String directParentTreatment = postDTO.getDirectParentTreatment();
            String directChildTreatment = postDTO.getDirectChildTreatment();

            if (directParentTreatment != null && !directParentTreatment.isEmpty() && directChildTreatment != null && !directChildTreatment.isEmpty()) {
                // 새 대분류 생성
                parentTreatment = new Treatment();
                parentTreatment.setName(directParentTreatment);
                treatmentRepository.save(parentTreatment); // 대분류 저장

                // 새 소분류 생성
                childTreatment = new Treatment();
                childTreatment.setName(directChildTreatment);
                childTreatment.setParent(parentTreatment); // 소분류의 부모를 대분류로 설정
                treatmentRepository.save(childTreatment); // 소분류 저장
            }
        }
        // 시술내용은 기존 값, 세부내용은 null 일 때
        else {
            parentTreatment = treatmentRepository.findById(postDTO.getParentTreatment())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시술내용입니다."));

            if (postDTO.getChildTreatment() == null) {
                String directChildTreatment = postDTO.getDirectChildTreatment();
                if (directChildTreatment != null && !directChildTreatment.isEmpty()) {
                    // 새 소분류 생성
                    childTreatment = new Treatment();
                    childTreatment.setName(directChildTreatment);
                    childTreatment.setParent(parentTreatment); // 소분류의 부모를 대분류로 설정
                    treatmentRepository.save(childTreatment); // 소분류 저장
                }
            }
        }
        // 둘 다 기존값일 때
        if (postDTO.getParentTreatment() != null && postDTO.getChildTreatment() != null) {
            Treatment existTreatment = treatmentRepository.findById(postDTO.getChildTreatment())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 소분류 ID"));
            childTreatment = existTreatment; // 기존 소분류 사용
        }


        if (parentTreatment != null) {
            post.setParent(parentTreatment); // 대분류 설정
        }
        if (childTreatment != null) {
            post.setChild(childTreatment); // 소분류 설정
        }

        postRepository.save(post); // Post 저장
    }

    private User createUser(PostDTO postDTO) throws Exception {
        User user = new User();
        user.setName(postDTO.getName());
        user.setPhone(postDTO.getPhone());
        user.setPassword(EncryptionUtil.encrypt(postDTO.getPhone()));
        user.setBirthDay(Optional.ofNullable(postDTO.getBirthDay()).orElse(new Date()));
        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Role ID 2 not found"));
        user.setRole(role);
        user.setTreatmentCount(1L);
        user.setFirstVisitDate(new Date());
        user.setIsDeleted("N");

        return userRepository.save(user); // 새로운 사용자 저장
    }

    private void setPostDetails(Post post, PostDTO postDTO, User user) {
        post.setPostId(postDTO.getPostId());
        post.setTreatmentDate(Optional.ofNullable(postDTO.getTreatmentDate()).orElse(LocalDateTime.now()));
        post.setBeforeImageUrl(postDTO.getBeforeImageUrl());
        post.setAfterImageUrl(postDTO.getAfterImageUrl());
        post.setInfo(postDTO.getInfo());
        post.setIsDeleted("N");
        post.setUser(user);
    }
    /* 시술내역 등록 메소드 종료 */


    // 시술내역 조회
    @Transactional
    public List<PostDTO> postList(String isDeleted) {
        List<Post> posts = postRepository.findByIsDeletedOrderByTreatmentDateDesc(isDeleted); // 모든 게시물 조회
        List<PostDTO> postDTOS = new ArrayList<>();

        for (Post post : posts) {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getPostId());
            postDTO.setUserId(post.getUser().getUserId()); //유저아이디
            postDTO.setName(post.getUser().getName()); // 이름
            postDTO.setPhone(post.getUser().getPhone()); // 전화번호
            postDTO.setBirthDay(post.getUser().getBirthDay()); // 생년월일
            postDTO.setFirstVisitDate(post.getUser().getFirstVisitDate()); // 첫방문 날짜
            postDTO.setTreatmentCount(post.getUser().getTreatmentCount()); // 방문횟수

            if(!"N".equals(post.getUser().getIsDeleted())){
                postDTO.setIsDeletedUser("탈퇴한 사용자");
            }

            if (post.getParent() != null && post.getParent().getName() != null) {
                Optional<Treatment> parentTreatment = treatmentRepository.findById(post.getParent().getTreatmentId());
                if (parentTreatment.isPresent()) {
                    postDTO.setParentTreatment(post.getParent().getTreatmentId()); // ID 설정
                    postDTO.setDirectParentTreatment(parentTreatment.get().getName()); // 이름 설정
                }
            } else {
                postDTO.setDirectParentTreatment(""); // null이면 빈 문자열로 설정
            }// 시술내용

            if (post.getChild() != null && post.getChild().getName() != null) {
                Optional<Treatment> childTreatment = treatmentRepository.findById(post.getChild().getTreatmentId());
                if (childTreatment.isPresent()) {
                    postDTO.setChildTreatment(post.getChild().getTreatmentId()); // ID 설정
                    postDTO.setDirectChildTreatment(childTreatment.get().getName()); // 이름 설정
                }
            } else {
                postDTO.setDirectChildTreatment(""); // null이면 빈 문자열로 설정
            }// 세부내용

            postDTO.setTreatmentDate(post.getTreatmentDate()); // 시술날짜

            if (post.getVisit() != null) {
                postDTO.setVisitId(post.getVisit().getVisitId());
                Optional<Visit> visitPath = visitRepository.findById(post.getVisit().getVisitId());
                if (visitPath.isPresent()) {
                    postDTO.setVisitPath(visitPath.get().getVisitPath()); // 이름 설정
                }
            } else {
                postDTO.setVisitPath(""); // 방문이 null일 경우 빈 문자열 설정
            } // 방문 경로

            postDTO.setRetouch(Boolean.valueOf(post.getRetouch())); // 리터치 여부
            postDTO.setRetouchDate(post.getRetouchDate()); // 리터치 날짜
            postDTO.setBeforeImageUrl(post.getBeforeImageUrl()); // 비포
            postDTO.setAfterImageUrl(post.getAfterImageUrl()); // 애프터
            postDTO.setInfo(post.getInfo()); // 비고

            postDTOS.add(postDTO);
        }
        return postDTOS;
    }


    // 사용자별 시술내역 조회
    @Transactional
    public List<PostDTO> postListByUser(Long userId) {
        List<Post> posts = postRepository.findByUser_UserIdAndIsDeletedOrderByTreatmentDateDesc(userId, "N"); // 사용자별 게시물 조회
        List<PostDTO> postDTOS = new ArrayList<>();

        for (Post post : posts) {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getPostId());
            postDTO.setUserId(post.getUser().getUserId()); //유저아이디
            postDTO.setName(post.getUser().getName()); // 이름
            postDTO.setPhone(post.getUser().getPhone()); // 전화번호
            postDTO.setBirthDay(post.getUser().getBirthDay()); // 생년월일
            postDTO.setFirstVisitDate(post.getUser().getFirstVisitDate()); // 첫방문 날짜
            postDTO.setTreatmentCount(post.getUser().getTreatmentCount()); // 방문횟수

            if (post.getParent() != null && post.getParent().getName() != null) {
                Optional<Treatment> parentTreatment = treatmentRepository.findById(post.getParent().getTreatmentId());
                if (parentTreatment.isPresent()) {
                    postDTO.setParentTreatment(post.getParent().getTreatmentId()); // ID 설정
                    postDTO.setDirectParentTreatment(parentTreatment.get().getName()); // 이름 설정
                }
            } else {
                postDTO.setDirectParentTreatment(""); // null이면 빈 문자열로 설정
            }// 시술내용

            if (post.getChild() != null && post.getChild().getName() != null) {
                Optional<Treatment> childTreatment = treatmentRepository.findById(post.getChild().getTreatmentId());
                if (childTreatment.isPresent()) {
                    postDTO.setChildTreatment(post.getChild().getTreatmentId()); // ID 설정
                    postDTO.setDirectChildTreatment(childTreatment.get().getName()); // 이름 설정
                }
            } else {
                postDTO.setDirectChildTreatment(""); // null이면 빈 문자열로 설정
            }// 세부내용

            postDTO.setTreatmentDate(post.getTreatmentDate()); // 시술날짜

            if (post.getVisit() != null) {
                postDTO.setVisitId(post.getVisit().getVisitId());
                Optional<Visit> visitPath = visitRepository.findById(post.getVisit().getVisitId());
                if (visitPath.isPresent()) {
                    postDTO.setVisitPath(visitPath.get().getVisitPath()); // 이름 설정
                }
            } else {
                postDTO.setVisitPath(""); // 방문이 null일 경우 빈 문자열 설정
            } // 방문 경로

            postDTO.setRetouch(Boolean.valueOf(post.getRetouch())); // 리터치 여부
            postDTO.setRetouchDate(post.getRetouchDate()); // 리터치 날짜
            postDTO.setBeforeImageUrl(post.getBeforeImageUrl()); // 비포
            postDTO.setAfterImageUrl(post.getAfterImageUrl()); // 애프터
            postDTO.setInfo(post.getInfo()); // 비고

            postDTOS.add(postDTO);
        }
        return postDTOS;
    }


    // 시술내역 자세히 보기
    public PostDTO postDetailByUser(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        PostDTO postDTO = new PostDTO();
        postDTO.setUserId(post.getUser().getUserId()); //유저아이디
        postDTO.setName(post.getUser().getName()); // 이름
        postDTO.setPhone(post.getUser().getPhone()); // 전화번호
        postDTO.setDirectParentTreatment(post.getParent().getName()); // 시술내용
        postDTO.setDirectChildTreatment(post.getChild().getName()); // 시술내용
        postDTO.setTreatmentDate(post.getTreatmentDate()); // 시술날짜
        postDTO.setBeforeImageUrl(post.getBeforeImageUrl()); // 비포
        postDTO.setAfterImageUrl(post.getAfterImageUrl()); // 애프터
        postDTO.setRetouch(Boolean.valueOf(post.getRetouch())); // 리터치 여부
        postDTO.setRetouchDate(post.getRetouchDate()); // 리터치 날짜

        return postDTO;
    }


    // 수정할 게시물 가져오기
    public Post postListByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        return post;
    }


    // 시술내역 수정 메소드
    @Transactional
    public void postUpdate(Long postId, PostDTO postDTO) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        // Visit 정보 설정
        Visit visitPath = null;
        // visitId가 직접입력일 때
        if (postDTO.getVisitId() == null){
            String directVisitPath = postDTO.getVisitPath();
            visitPath = new Visit();
            visitPath.setVisitPath(directVisitPath);
            visitRepository.save(visitPath);
        }
        // visitId가 기존값일 때
        if (postDTO.getVisitId() != null) {
            Visit visit = visitRepository.findById(postDTO.getVisitId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방문 경로입니다."));
            visitPath = visit;
        }

        // Treatment 정보 설정
        Treatment parentTreatment = null;
        Treatment childTreatment = null;

        // 시술내용이 null 일 때
        if (postDTO.getParentTreatment() == null){
            String directParentTreatment = postDTO.getDirectParentTreatment();
            String directChildTreatment = postDTO.getDirectChildTreatment();

            if (directParentTreatment != null && !directParentTreatment.isEmpty() && directChildTreatment != null && !directChildTreatment.isEmpty()) {
                // 새 시술내용 생성
                parentTreatment = new Treatment();
                parentTreatment.setName(directParentTreatment);
                treatmentRepository.save(parentTreatment); // 대분류 저장

                // 새 세부내용 생성
                childTreatment = new Treatment();
                childTreatment.setName(directChildTreatment);
                childTreatment.setParent(parentTreatment); // 소분류의 부모를 대분류로 설정
                treatmentRepository.save(childTreatment); // 소분류 저장
            }
        }
        // 시술내용은 기존 값, 세부내용은 null 일 때
        else {
            parentTreatment = treatmentRepository.findById(postDTO.getParentTreatment())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 시술내용입니다."));

            if (postDTO.getChildTreatment() == null) {
                String directChildTreatment = postDTO.getDirectChildTreatment();
                if (directChildTreatment != null && !directChildTreatment.isEmpty()) {
                    // 새 소분류 생성
                    childTreatment = new Treatment();
                    childTreatment.setName(directChildTreatment);
                    childTreatment.setParent(parentTreatment); // 소분류의 부모를 대분류로 설정
                    treatmentRepository.save(childTreatment); // 소분류 저장
                }
            }
        }
        // 둘 다 기존값일 때
        if (postDTO.getParentTreatment() != null && postDTO.getChildTreatment() != null) {
            Treatment existTreatment = treatmentRepository.findById(postDTO.getChildTreatment())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 소분류 ID"));
            childTreatment = existTreatment; // 기존 소분류 사용
        }


        if (visitPath != null) {
            post.setVisit(visitPath); // 방문경로
        }

        if (parentTreatment != null) {
            post.setParent(parentTreatment); // 시술내용
        }

        if (childTreatment != null) {
            post.setChild(childTreatment); // 세부내용
        }

        post.setTreatmentDate(postDTO.getTreatmentDate()); // 시술 날짜

        if (postDTO.getBeforeImageUrl() != null) {
            post.setBeforeImageUrl(postDTO.getBeforeImageUrl()); // 시술 전 사진
        }

        if (postDTO.getAfterImageUrl() != null) {
            post.setAfterImageUrl(postDTO.getAfterImageUrl()); // 시술 후 사진
        }

        if (postDTO.getRetouch() == null) {
            postDTO.setRetouch(false); //체크박스가 체크되지 않으면 false로 설정
        }
        post.setRetouch(postDTO.getRetouch()); //리터치 여부

        post.setRetouchDate(postDTO.getRetouchDate()); // 리터치 날짜

        post.setInfo(postDTO.getInfo()); // 비고

        postRepository.save(post);
    }


    // 시술내역 휴지통
    @Transactional
    public String postDelete(Long postId){
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


    // 시술내역 휴지통에서 복원
    @Transactional
    public String postRestore(Long postId){
        Post post = postRepository.findById(postId).orElse(null);

        if (post != null) {
            // isDeleted 값을 "N"로 변경
            post.setIsDeleted("N");
            // 포스트 업데이트
            postRepository.save(post);
            return null;
        }
        return null;
    }


}