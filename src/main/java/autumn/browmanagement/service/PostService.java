package autumn.browmanagement.service;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.Entity.*;
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
    public void createPost(PostDTO postDTO, Treatment createdTreatment) throws Exception {
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

        // Treatment ID 설정
        if (createdTreatment != null) {
            post.setChildTreatment(createdTreatment.getTreatmentId()); // 생성된 Treatment의 ID를 설정
        }

        // visitId 설정
        Long visitId = postDTO.getVisitId();
        if (visitId != null) {
            post.setVisitId(visitId); // 선택한 visitId 할당
        } else {
            if (postDTO.getVisitPath() != null && !postDTO.getVisitPath().isEmpty()) {
                Visit existingVisit = visitRepository.findByVisitPath(postDTO.getVisitPath());

                if (existingVisit != null) {
                    // 이미 존재하는 경우, 기존 visitId를 사용
                    post.setVisitId(existingVisit.getVisitId());
                } else {

                    Visit newVisit = new Visit();
                    newVisit.setVisitPath(postDTO.getVisitPath()); // 새로운 visitPath 설정
                    visitRepository.save(newVisit); // 새로운 Visit 저장

                    post.setVisitId(newVisit.getVisitId()); // 새로 생성된 visitId를 Post에 할당
                }
            }
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
        post.setParentTreatment(postDTO.getParentTreatment());
        post.setChildTreatment(postDTO.getChildTreatment());
        post.setTreatmentDate(Optional.ofNullable(postDTO.getTreatmentDate()).orElse(LocalDateTime.now()));
        post.setBeforeImageUrl(postDTO.getBeforeImageUrl());
        post.setAfterImageUrl(postDTO.getAfterImageUrl());
        post.setInfo(postDTO.getInfo());
        post.setIsDeleted("N");
        post.setUser(user);
    }
    /* 시술내역 등록 메소드 종료 */


    // 시술내역 휴지통
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
    public List<PostDTO> findAll(String isDeleted) {
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
            
            if (post.getParentTreatment() != null) {
                Optional<Treatment> parrentTreatment = treatmentRepository.findById(post.getParentTreatment());
                if (parrentTreatment.isPresent()) {
                    postDTO.setParentTreatment(post.getParentTreatment()); // ID 설정
                    postDTO.setParentName(parrentTreatment.get().getName()); // 이름 설정
                }
            } // 시술내용

            if (post.getChildTreatment() != null) {
                Optional<Treatment> parrentTreatment = treatmentRepository.findById(post.getChildTreatment());
                if (parrentTreatment.isPresent()) {
                    postDTO.setChildTreatment(post.getChildTreatment()); // ID 설정
                    postDTO.setChildName(parrentTreatment.get().getName()); // 이름 설정
                }
            } // 세부내용

            postDTO.setTreatmentDate(post.getTreatmentDate()); // 시술날짜
            postDTO.setVisitId(post.getVisitId()); // 방문경로

            if (post.getVisitId() != null) {
                Optional<Visit> visitPath = visitRepository.findById(post.getVisitId());
                if (visitPath.isPresent()) {
                    postDTO.setVisitId(post.getVisitId()); // ID 설정
                    postDTO.setVisitPath(visitPath.get().getVisitPath()); // 이름 설정
                }
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


    // 수정할 게시물 가져오기
    public Post findById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        return post;
    }



    // 시술내역 수정 메소드
    @Transactional
    public void updatePost(Long postId, PostDTO postDTO, Treatment treatment) {
        //String beforeImageUrl, String afterImageUrl,

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        // visitId
        post.setVisitId(postDTO.getVisitId());

        // visitId 설정
        Long visitId = postDTO.getVisitId();
        if (visitId != null) {
            post.setVisitId(visitId);
        } else {
            if (postDTO.getVisitPath() != null && !postDTO.getVisitPath().isEmpty()) {
                Visit existingVisit = visitRepository.findByVisitPath(postDTO.getVisitPath());

                if (existingVisit != null) {
                    // 이미 존재하는 경우, 기존 visitId를 사용
                    post.setVisitId(existingVisit.getVisitId());
                } else {

                    Visit newVisit = new Visit();
                    newVisit.setVisitPath(postDTO.getVisitPath()); // 새로운 visitPath 설정
                    visitRepository.save(newVisit); // 새로운 Visit 저장

                    post.setVisitId(newVisit.getVisitId()); // 새로 생성된 visitId를 Post에 할당
                }
            }
        }

        // 게시물 정보 업데이트
        post.setParentTreatment(postDTO.getParentTreatment());

        // Treatment ID 설정
        if (treatment != null) {
            post.setChildTreatment(treatment.getTreatmentId()); // 생성된 Treatment의 ID를 설정
        }


        if (postDTO.getBeforeImageUrl() != null) {
            post.setBeforeImageUrl(postDTO.getBeforeImageUrl());
        }
        if (postDTO.getAfterImageUrl() != null) {
            post.setAfterImageUrl(postDTO.getAfterImageUrl());
        }

        if (postDTO.getRetouch() == null) {
            postDTO.setRetouch(false); // 체크박스가 체크되지 않으면 false로 설정
        }
        post.setRetouch(postDTO.getRetouch());

        post.setRetouchDate(postDTO.getRetouchDate());
        post.setInfo(postDTO.getInfo());


        postRepository.save(post);
    }


    /*
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
*/

    // 사용자별 시술내역 조회
    @Transactional
    public List<PostDTO> findByUserId(Long userId) {
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

            if (post.getParentTreatment() != null) {
                Optional<Treatment> parrentTreatment = treatmentRepository.findById(post.getParentTreatment());
                if (parrentTreatment.isPresent()) {
                    postDTO.setParentTreatment(post.getParentTreatment()); // ID 설정
                    postDTO.setParentName(parrentTreatment.get().getName()); // 이름 설정
                }
            } // 시술내용

            if (post.getChildTreatment() != null) {
                Optional<Treatment> parrentTreatment = treatmentRepository.findById(post.getChildTreatment());
                if (parrentTreatment.isPresent()) {
                    postDTO.setChildTreatment(post.getChildTreatment()); // ID 설정
                    postDTO.setChildName(parrentTreatment.get().getName()); // 이름 설정
                }
            } // 세부내용

            postDTO.setTreatmentDate(post.getTreatmentDate()); // 시술날짜
            postDTO.setVisitId(post.getVisitId()); // 방문경로

            if (post.getVisitId() != null) {
                Optional<Visit> visitPath = visitRepository.findById(post.getVisitId());
                if (visitPath.isPresent()) {
                    postDTO.setVisitId(post.getVisitId()); // ID 설정
                    postDTO.setVisitPath(visitPath.get().getVisitPath()); // 이름 설정
                }
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
    public PostDTO findByIdView(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. :" + postId));

        PostDTO postDTO = new PostDTO();
        postDTO.setUserId(post.getUser().getUserId());
        postDTO.setName(post.getUser().getName());
        postDTO.setPhone(post.getUser().getPhone());

        if(post.getParentTreatment() != null){
            Optional<Treatment> parentTreatment = treatmentRepository.findById(post.getParentTreatment());
            if(parentTreatment.isPresent()){
                Treatment parentTreatmentName = parentTreatment.get();
                postDTO.setParentName(parentTreatmentName.getName());
            }
        }

        if (post.getChildTreatment() != null) {
            Optional<Treatment> childTreatment = treatmentRepository.findById(post.getChildTreatment());
            if (childTreatment.isPresent()) {
                Treatment childTreatmentName = childTreatment.get();
                postDTO.setChildName(childTreatmentName.getName()); // 자식 시술 이름 설정
            }
        }

        postDTO.setTreatmentDate(post.getTreatmentDate());
        postDTO.setBeforeImageUrl(post.getBeforeImageUrl());
        postDTO.setAfterImageUrl(post.getAfterImageUrl());
        postDTO.setRetouch(post.getRetouch());
        postDTO.setRetouchDate(post.getRetouchDate());

        return postDTO;
    }

}
