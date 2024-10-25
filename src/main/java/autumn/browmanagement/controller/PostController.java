
package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.TreatmentDTO;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.domain.Visit;
import autumn.browmanagement.service.PostService;
import autumn.browmanagement.service.TreatmentService;
import autumn.browmanagement.service.VisitService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final TreatmentService treatmentService;
    private final PostService postService;
    private final VisitService visitServce;



    // 시술내역 등록 폼
    @GetMapping("/post/create")
    public String createForm(Model model){
        List<Visit> visits = visitServce.visitList(); // 모든 방문 경로 조회
        model.addAttribute("visits", visits);

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments);

        return "post/postCreateForm";
    }


    // 시술내역 등록 요청
    @PostMapping("/post/create")
    public String createPost(PostDTO postDTO, Model model) throws IOException {
        try {
            // 서비스에 파일 처리 및 업로드 요청
            postService.handleFileUpload(postDTO);

            Treatment createdTreatment = null;
            if (postDTO.getParentTreatment() != null && postDTO.getParentTreatment().equals(1L)) {
                TreatmentDTO treatmentDTO = new TreatmentDTO();
                treatmentDTO.setName(postDTO.getChildView()); // 직접 입력한 값
                treatmentDTO.setParentId(postDTO.getParentTreatment()); // 부모 ID 설정

                createdTreatment = treatmentService.createTreatment(treatmentDTO);
            }

            postService.createPost(postDTO, createdTreatment); // Post 생성

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "error";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/post/list";
    }


    // 시술내역 삭제 요청
    @PostMapping("/post/{postId}/delete")
    public String deletePost(@PathVariable Long postId, Model model){

        return postService.deletePost(postId);

    }


    // 시술내역 조회
    @GetMapping("/post/list")
    public String list(Model model){
        List<PostDTO> postDTOS = postService.findAll("N"); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postList";
    }


    // 휴지통
    @GetMapping("/post/deleted")
    public String deletedList(Model model){
        List<PostDTO> postDTOS = postService.findAll("Y"); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postDeletedList";
    }


    // 시술내역 수정 페이지
    @GetMapping("/post/{postId}/update")
    public String updatePostForm(@PathVariable Long postId, Model model) throws Exception {

        Post post = postService.findById(postId);
        model.addAttribute("post", post);

        List<Visit> visits = visitServce.visitList(); // 모든 방문 경로 조회
        model.addAttribute("visits", visits);

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments); // 상위 시술 목록 추가

        return "post/postUpdateForm";
    }


    // 시술내역 수정 요청
    @PostMapping("/post/{postId}/update")
    public String updatePost(@PathVariable Long postId, @ModelAttribute("post") PostDTO postDTO, Model model ) throws Exception {

        try {
            // 서비스에 파일 처리 및 업로드 요청
            postService.handleFileUpload(postDTO);

            Treatment createdTreatment = null;
            if (postDTO.getParentTreatment() != null && postDTO.getParentTreatment().equals(1L)) {
                TreatmentDTO treatmentDTO = new TreatmentDTO();
                treatmentDTO.setName(postDTO.getChildView()); // 직접 입력한 값
                treatmentDTO.setParentId(postDTO.getParentTreatment()); // 부모 ID 설정

                createdTreatment = treatmentService.createTreatment(treatmentDTO);
            }

            postService.updatePost(postId, postDTO, createdTreatment);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "error";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/post/list";
    }


    /*
    // 시술내역 수정 요청
    @PostMapping("/post/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") PostForm form, Model model ) throws Exception {

        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.connect(); // FTP 연결

        try {
            // beforeImageFile 처리
            MultipartFile beforeImageFile = form.getBeforeImageFile();
            if (beforeImageFile != null && !beforeImageFile.isEmpty()) {
                String originalFilename = beforeImageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String beforeFileName = UUID.randomUUID().toString() + fileExtension;
                File localBeforeFile = new File(System.getProperty("java.io.tmpdir") + "/" + beforeFileName);
                beforeImageFile.transferTo(localBeforeFile);
                ftpUtil.uploadFile("/autumnBrow/" + beforeFileName, localBeforeFile);
                form.setBeforeImageUrl(beforeFileName); // DB에 저장할 파일 이름
                // 임시 파일 삭제
                localBeforeFile.delete();
            }
            // afterImageFile 처리
            MultipartFile afterImageFile = form.getAfterImageFile();
            if (afterImageFile != null && !afterImageFile.isEmpty()) {
                String originalFilename = afterImageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String afterFileName = UUID.randomUUID().toString() + fileExtension;
                File localAfterFile = new File(System.getProperty("java.io.tmpdir") + "/" + afterFileName);
                afterImageFile.transferTo(localAfterFile);
                ftpUtil.uploadFile("/autumnBrow/" + afterFileName, localAfterFile);
                form.setAfterImageUrl(afterFileName); // DB에 저장할 파일 이름
                // 임시 파일 삭제
                localAfterFile.delete();
            }

            Treatment createdTreatment = null;
            if (form.getParentTreatment() != null && form.getParentTreatment().equals(1L)) {
                TreatmentDTO treatmentDTO = new TreatmentDTO();
                treatmentDTO.setName(form.getChildView()); // 직접 입력한 값
                treatmentDTO.setParentId(form.getParentTreatment()); // 부모 ID 설정

                createdTreatment = treatmentService.createTreatment(treatmentDTO);

            }

            postService.updatePost(id, form, createdTreatment);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ftpUtil.disconnect();// FTP 연결 종료
        }

        return "redirect:/post/list";
    }
    */


    // 내 시술내역 자세히
    @GetMapping("/post/{postId}/view")
    public String viewPost(@PathVariable Long postId, Model model) throws Exception {

        PostDTO postDTO = postService.findByIdView(postId);
        model.addAttribute("post", postDTO);

        List<Visit> visits = visitServce.visitList(); // 모든 방문 경로 조회
        model.addAttribute("visits", visits);

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments); // 상위 시술 목록 추가

        return "post/postView";
    }


    // 관리자용 사용자별 시술내역 조회
    @GetMapping("/post/{userId}/list")
    public String ownlist(@PathVariable Long userId, Model model){
        List<PostDTO> postDTOS = postService.findByUserId(userId); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postOwnList";
    }


    // 관리자용 시술내역 조회
    @GetMapping("/post/{userId}/userList")
    public String postUserlist(@PathVariable Long userId, Model model, HttpSession session){
/*
        User sessionUser = (User) session.getAttribute("user");

        if (!sessionUser.getId().equals(userId)) {
            return "권한이 없습니다."; // 다른 사용자의 시술 내역을 조회하려고 하면 홈으로 리다이렉트
        }
*/

        List<PostDTO> postDTOS = postService.findByUserId(userId); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postUserList";
    }


    // 사용자별 시술내역 조회
    @GetMapping("/post/{userId}/ownList")
    public String postOwnList(@PathVariable Long userId, Model model, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser.getRole().getRoleId() == 1){
            System.out.println("sessionUser.getRole().getId() = " + sessionUser.getRole().getRoleId());
            return "post/postOwnList";
        }
        
        if (!sessionUser.getUserId().equals(userId)) {
            return "권한이 없습니다."; // 다른 사용자의 시술 내역을 조회하려고 하면 홈으로 리다이렉트
        }

        List<PostDTO> postDTOS = postService.findByUserId(userId); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postOwnList";
    }


}
