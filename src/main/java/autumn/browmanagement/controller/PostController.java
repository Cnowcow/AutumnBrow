
package autumn.browmanagement.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final TreatmentService treatmentService;
    private final PostService postService;
    private final VisitServce visitServce;

    @GetMapping("/test/post/create")
    public String createForm(Model model){
        List<Visit> visits = visitServce.visitList(); // 모든 방문 경로 조회
        model.addAttribute("visits", visits);

        List<Treatment> treatments = treatmentService.treatmentList(); // 모든 시술 조회
        model.addAttribute("treatments", treatments); // 상위 시술 목록 추가

        return "test/post/postCreateForm";
    }

    @PostMapping("/test/post/create")
    public String createPost(PostForm postForm, Model model) throws IOException {
        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.connect();

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

            postService.createPost(postForm); // 서비스 메서드 호출

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다."); // 사용자에게 피드백
            return "error"; // 오류 처리 (필요 시 에러 페이지로 리다이렉트)
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }

        return "redirect:/test"; // 성공적으로 저장 후 리다이렉트
    }


    @GetMapping("/test/post/list")
    public String list(Model model){
        List<PostForm> postForms = postService.findAll("N"); // 모든 게시물 조회
        model.addAttribute("posts", postForms); // 모델에 게시물 목록 추가

        return "test/post/postList";
    }










    /*@GetMapping("/post/new")
    public String createForm(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || user.getRoleId() != 1){
            model.addAttribute("errror", "입력할 수 있는 권한이 없습니다.");
            return "redirect:/?authority=true";
        }

        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        model.addAttribute("userInfo", userInfo);
        if (userInfo == null || userInfo.getName() == null){
            return "redirect:/user/login";
        }
        return "post/postCreateForm";
    }

    @PostMapping("/post/new")
    public String createPost(PostForm postForm, Model model, HttpSession session) throws IOException {

        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.connect();

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
            }
            System.out.println("99999 = " + beforeImageFile);

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
            }
            System.out.println("88888 = " + afterImageFile);

            postService.createPost(postForm); // 서비스 메서드 호출
        } catch (IOException e) {
            e.printStackTrace();
            return "error"; // 오류 처리 (필요 시 에러 페이지로 리다이렉트)
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }

        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        model.addAttribute("userInfo", userInfo);
        if (userInfo == null || userInfo.getName() == null){
            return "redirect:/user/login";
        }
        System.out.println("77777 = " + postForm);
        return "redirect:/"; // 성공적으로 저장 후 리다이렉트
    }

    // 게시물 목록
    @GetMapping("/post/list")
    public String listPosts(Model model, HttpSession session) {
        List<PostForm> postForms = postService.findAll(); // 모든 게시물 조회

        model.addAttribute("posts", postForms); // 모델에 게시물 목록 추가

        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        if (userInfo == null){
            return "redirect:/user/login";
        }
        return "post/postList"; // 게시물 목록 페이지로 이동
    }

    // 게시물 수정 페이지
    @GetMapping("/post/{postId}/edit")
    public String updatePostForm(@PathVariable("postId") Long postId, Model model, HttpSession session) {
        Post post = (Post) postService.findOne(postId);

        PostForm form = new PostForm();
        form.setId(postId);
        form.setName(post.getUser().getName());
        form.setPhone(post.getUser().getPhone());
        form.setBirthDay(post.getUser().getBirthDay());
        form.setParentTreatment(post.getParentTreatment());
        form.setChildTreatment(post.getChildTreatment());
        form.setTreatmentDate(post.getTreatmentDate());
        form.setVisitPath(post.getVisit().getVisitPath());
        form.setBeforeImageUrl(post.getBeforeImageUrl());
        form.setAfterImageUrl(post.getAfterImageUrl());
        form.setRetouch(post.getRetouch());
        form.setRetouchDate(post.getRetouchDate());
        form.setInfo(post.getInfo());


        model.addAttribute("postUpdateForm", form);

        List<Treatment> parentTreatments = treatmentService.findParentTreatments();
        model.addAttribute("parent", parentTreatments); // 모델에 부모 시술 정보 추가


        // 헤더에 보낼 userInfo
        User userInfo = (User) session.getAttribute("user");
        if (userInfo == null){
            return "redirect:/user/login";
        }
        return "post/postUpdateForm";
    }

    @PostMapping("/post/{postId}/edit")
    public String updatePost(@PathVariable Long postId, @ModelAttribute("postUpdateForm") PostForm form ){
        postService.updatePost(postId, form.getName(), form.getPhone(), form.getParentTreatment(),
                form.getChildTreatment(), form.getTreatmentDate(), String.valueOf(form.getRetouch()), form.getRetouchDate(), form.getInfo());
        // birthDay, visitPath, beforeUrl, afterUrl

        return "redirect:/post/list";
    }*/

}
