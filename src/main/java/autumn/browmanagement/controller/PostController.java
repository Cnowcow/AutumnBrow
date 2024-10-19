
package autumn.browmanagement.controller;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.domain.Visit;
import autumn.browmanagement.service.PostService;
import autumn.browmanagement.service.TreatmentService;
import autumn.browmanagement.service.VisitServce;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final TreatmentService treatmentService;
    private final PostService postService;
    private final VisitServce visitServce;



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
    public String createPost(PostForm postForm, Model model) throws IOException {
        try {
            // 서비스에 파일 처리 및 업로드 요청
            postService.handleFileUpload(postForm);

            Treatment createdTreatment = null;
            if (postForm.getParentTreatment() != null && postForm.getParentTreatment().equals(3L)) {
                System.out.println("아아아아ㅏ아아아");
                System.out.println("직접입력한 값 :"+postForm.getChildView());
                TreatmentForm treatmentForm = new TreatmentForm();
                treatmentForm.setName(postForm.getChildView()); // 직접 입력한 값
                treatmentForm.setParentId(postForm.getParentTreatment()); // 부모 ID 설정

                createdTreatment = treatmentService.createTreatment(treatmentForm);
            }

            postService.createPost(postForm, createdTreatment); // Post 생성

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


/*
    @PostMapping("/post/create")
    public String createPost(PostForm postForm, Model model) throws IOException {
        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.connect(); // FTP 연결

        try {
            // beforeImageFile 처리
            MultipartFile beforeImageFile = postForm.getBeforeImageFile();
            if (beforeImageFile != null && !beforeImageFile.isEmpty()) {
                String originalFilename = beforeImageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String beforeFileName = UUID.randomUUID().toString() + fileExtension;
                File localBeforeFile = new File(System.getProperty("java.io.tmpdir") + "/" + beforeFileName);
                beforeImageFile.transferTo(localBeforeFile);
                ftpUtil.uploadFile("/autumnBrow/" + beforeFileName, localBeforeFile);
                postForm.setBeforeImageUrl(beforeFileName); // DB에 저장할 파일 이름
                // 임시 파일 삭제
                localBeforeFile.delete();
            }
            // afterImageFile 처리
            MultipartFile afterImageFile = postForm.getAfterImageFile();
            if (afterImageFile != null && !afterImageFile.isEmpty()) {
                String originalFilename = afterImageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String afterFileName = UUID.randomUUID().toString() + fileExtension;
                File localAfterFile = new File(System.getProperty("java.io.tmpdir") + "/" + afterFileName);
                afterImageFile.transferTo(localAfterFile);
                ftpUtil.uploadFile("/autumnBrow/" + afterFileName, localAfterFile);
                postForm.setAfterImageUrl(afterFileName); // DB에 저장할 파일 이름
                // 임시 파일 삭제
                localAfterFile.delete();
            }

            postService.createPost(postForm);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "error";
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }

        return "redirect:/";
    }*/


    // 시술내역 조회
    @GetMapping("/post/list")
    public String list(Model model){
        List<PostForm> postForms = postService.findAll("N"); // 모든 게시물 조회
        model.addAttribute("posts", postForms); // 모델에 게시물 목록 추가

        return "post/postList";
    }


    // 시술내역 수정 페이지
    @GetMapping("/post/{postId}/edit")
    public String updatePostForm(@PathVariable Long postId, Model model) throws Exception {
        Post post = postService.findById(postId);

        // 전화번호 복호화
        String decryptedPhone = EncryptionUtil.decrypt(post.getUser().getPhone());
        post.getUser().setPhone(decryptedPhone); // 복호화된 전화번호 설정

        model.addAttribute("post", post);

        List<Visit> visits = visitServce.visitList(); // 모든 방문 경로 조회
        model.addAttribute("visits", visits);

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments); // 상위 시술 목록 추가

        return "post/postUpdateForm";
    }


    // 시술내역 수정 요청
    @PostMapping("/post/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") PostForm form ) throws Exception {

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

            postService.updatePost(id, form.getParentTreatment(), form.getChildTreatment(),
                    form.getTreatmentDate(), form.getVisitPath(), String.valueOf(form.getRetouch()),
                    form.getRetouchDate(), form.getInfo(), form.getBeforeImageUrl(), form.getAfterImageUrl());
            //form.getBeforeImageUrl(), form.getAfterImageUrl(),

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ftpUtil.disconnect();// FTP 연결 종료
        }

        return "redirect:/post/list";
    }


    // 관리자용 사용자별 시술내역 조회
    @GetMapping("/post/{userId}/list")
    public String ownlist(@PathVariable Long userId,Model model){
        List<PostForm> postForms = postService.findByUserId(userId); // 모든 게시물 조회
        model.addAttribute("posts", postForms); // 모델에 게시물 목록 추가

        return "post/postOwnList";
    }


    // 사용자별 시술내역 조회
    @GetMapping("/post/{userId}/ownList")
    public String selflist(@PathVariable Long userId, Model model, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");
        System.out.println("sessionUserId = " + sessionUser.getId());

        if (!sessionUser.getId().equals(userId)) {
            return "redirect:/"; // 다른 사용자의 시술 내역을 조회하려고 하면 홈으로 리다이렉트
        }

        List<PostForm> postForms = postService.findByUserId(userId); // 모든 게시물 조회
        model.addAttribute("posts", postForms); // 모델에 게시물 목록 추가

        return "post/postOwnList";
    }
}
