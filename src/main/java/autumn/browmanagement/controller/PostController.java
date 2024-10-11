package autumn.browmanagement.controller;

import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.service.PostService;
import autumn.browmanagement.service.TreatmentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final TreatmentService treatmentService;
    private final PostService postService;

    @GetMapping("/post/new")
    public String createForm(Model model) {
        Map<String, List<String>> treatments = treatmentService.getTreatments();
        model.addAttribute("treatments", treatments);
        System.out.println("컨트롤러 treatments 값 = " + treatments);

        model.addAttribute("what", new PostForm());
        return "/post/createPostForm";
    }

    @PostMapping("/post/new")
    public String creatPost(@ModelAttribute PostForm postForm, HttpSession session) throws IOException {

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
        postService.savePost(postForm); // 서비스 메서드 호출
        } catch (IOException e) {
            e.printStackTrace();
            return "error"; // 오류 처리 (필요 시 에러 페이지로 리다이렉트)
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }
        System.out.println("77777 = " + postForm);
        return "redirect:/"; // 성공적으로 저장 후 리다이렉트
    }


}
