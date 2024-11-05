
package autumn.browmanagement.controller;

import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.Entity.*;
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
    private final VisitService visitService;


    // 시술내역 등록 폼
    @GetMapping("/post/create")
    public String postCreateForm(Model model){

        List<Treatment> treatments = treatmentService.treatmentFindParent(); // 모든 시술 조회
        model.addAttribute("treatments", treatments);

        List<Visit> visits = visitService.visitList(); // 모든 방문 경로 조회
        model.addAttribute("visits", visits);

        return "post/postCreateForm";
    }


    // 시술내역 등록 요청
    @PostMapping("/post/create")
    public String postCreate(@ModelAttribute PostDTO postDTO, Model model) {

        try {
            // 서비스에 파일 처리 및 업로드 요청
            postService.handleFileUpload(postDTO);
            postService.postCreate(postDTO); // Post 생성

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "error";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/post/list";
    }


    // 시술내역 조회
    @GetMapping("/post/list")
    public String postList(Model model){
        List<PostDTO> postDTOS = postService.postList("N"); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postList";
    }


    // 관리자용 사용자별 시술내역 조회
    @GetMapping("/post/{userId}/userList")
    public String postListByUser(@PathVariable Long userId, Model model, HttpSession session){

        List<PostDTO> postDTOS = postService.postListByUser(userId);
        model.addAttribute("posts", postDTOS);

        return "post/postOwnList";
    }


    // 사용자별 시술내역 자세히
    @GetMapping("/post/{postId}/detail")
    public String postDetailByUser(@PathVariable Long postId, Model model) throws Exception {

        PostDTO postDTO = postService.postDetailByUser(postId);
        model.addAttribute("post", postDTO);

        return "post/postView";
    }


    // 사용자별 시술내역 조회
    @GetMapping("/post/{userId}/ownList")
    public String postOwnList(@PathVariable Long userId, Model model, HttpSession session){
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser.getRole().getRoleId() == 1 || sessionUser.getUserId().equals(userId)){
            return "post/postOwnList";
        }

        if (!sessionUser.getUserId().equals(userId)) {
            return "권한이 없습니다."; // 다른 사용자의 시술 내역을 조회하려고 하면 홈으로 리다이렉트
        }

        List<PostDTO> postDTOS = postService.postListByUser(userId);
        model.addAttribute("posts", postDTOS);

        return "post/postOwnList";
    }


    // 시술내역 수정 페이지
    @GetMapping("/post/{postId}/update")
    public String postUpdateForm(@PathVariable Long postId, Model model) throws Exception {

        Post post = postService.postListByPostId(postId);
        model.addAttribute("post", post);

        List<Visit> visits = visitService.visitList();
        model.addAttribute("visits", visits);

        List<Treatment> treatments = treatmentService.treatmentFindParent();
        model.addAttribute("treatments", treatments);

        return "post/postUpdateForm";
    }


    // 시술내역 수정 요청
    @PostMapping("/post/{postId}/update")
    public String postUpdate(@PathVariable Long postId, @ModelAttribute("post") PostDTO postDTO, Model model ) throws Exception {

        try {
            // 서비스에 파일 처리 및 업로드 요청
            postService.handleFileUpload(postDTO);
            postService.postUpdate(postId, postDTO);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "error";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/post/list";
    }


    // 휴지통
    @GetMapping("/post/deleted")
    public String postDeleteList(Model model){
        List<PostDTO> postDTOS = postService.postList("Y"); // 모든 게시물 조회
        model.addAttribute("posts", postDTOS); // 모델에 게시물 목록 추가

        return "post/postDeletedList";
    }


    // 휴지통 이동 요청
    @PostMapping("/post/{postId}/delete")
    public String postDelete(@PathVariable Long postId){

        return postService.postDelete(postId);

    }


    // 휴지통에서 복원 요청
    @PostMapping("/post/{postId}/restore")
    public String postRestore(@PathVariable Long postId){

        return postService.postRestore(postId);

    }


}
