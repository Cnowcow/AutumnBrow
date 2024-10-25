package autumn.browmanagement.service;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.DTO.TreatmentDTO;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.domain.*;
import autumn.browmanagement.repository.LikeyRepository;
import autumn.browmanagement.repository.NoticeRepository;
import autumn.browmanagement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final LikeyRepository likeyRepository;
    private final HttpServletRequest request;
    private final FtpUtil ftpUtil;


    // 공지사항 조회 날짜순 (index용)
    public List<NoticeDTO> noticeList(){
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeDateDesc();
        List<NoticeDTO> noticeDTOS = new ArrayList<>();

        for(Notice notice : notices){
            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setNoticeId(notice.getNoticeId());
            noticeDTO.setTitle(notice.getTitle());
            noticeDTO.setContent(notice.getContent());
            noticeDTO.setNoticeUrl(notice.getNoticeUrl());
            noticeDTO.setUserId(noticeDTO.getUserId());
            noticeDTO.setImportant(notice.getImportant());
            noticeDTO.setNoticeDate(notice.getNoticeDate());
            noticeDTO.setNoticeHits(notice.getNoticeHits());


            noticeDTOS.add(noticeDTO);
        }
        return noticeDTOS;
    }


    // 공지사항 중요도 순 (공지사항 페이지용)
    public List<NoticeDTO> noticeListImportant() {
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeDateDesc(); // 모든 공지사항 불러오기
        List<NoticeDTO> noticeDTOS = new ArrayList<>();
        for(Notice notice : notices){
            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setNoticeId(notice.getNoticeId());
            noticeDTO.setTitle(notice.getTitle());
            noticeDTO.setContent(notice.getContent());
            noticeDTO.setNoticeUrl(notice.getNoticeUrl());
            noticeDTO.setUserId(noticeDTO.getUserId());
            noticeDTO.setImportant(notice.getImportant());
            noticeDTO.setNoticeDate(notice.getNoticeDate());
            noticeDTO.setNoticeHits(notice.getNoticeHits());

            noticeDTOS.add(noticeDTO);
        }
        return noticeDTOS.stream()
                .sorted(Comparator.comparing(NoticeDTO::getImportant).reversed()) // Y가 먼저 오도록 정렬
                .collect(Collectors.toList());
    }


    // 공지사항 디테일
    @Transactional
    public NoticeDTO findById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. :" + noticeId));

        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setNoticeId(notice.getNoticeId());
        noticeDTO.setTitle(notice.getTitle());
        noticeDTO.setContent(notice.getContent());
        noticeDTO.setNoticeUrl(notice.getNoticeUrl());
        noticeDTO.setNoticeDate(notice.getNoticeDate());
        noticeDTO.setNoticeHits(notice.getNoticeHits());

        Long likeCount = likeyRepository.countByNotice(notice);
        noticeDTO.setLikesCount(likeCount);

        if (notice.getUser().getUserId() != null) {
            Optional<User> user = userRepository.findById(notice.getUser().getUserId());
            if (user.isPresent()) {
                User userName = user.get();
                noticeDTO.setUserName(userName.getName());
            }
        }


        return noticeDTO;
    }


    // ip기준 조회수
    public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Transactional
    public void increaseNoticeHits(Long noticeId) {

        // 클라이언트의 IP 주소를 가져옴
        String clientIp = getClientIp(request);

        // 해당 IP 주소로 조회한 게시물 번호를 가져옴
        Set<Long> notice = (Set<Long>) request.getSession().getAttribute(clientIp);
        if (notice == null) {
            notice = new HashSet<>();
            request.getSession().setAttribute(clientIp, notice);
        }

        // 해당 IP 주소로 조회한 게시물 번호가 없으면 조회수 증가 처리 후 세션에 저장
        if (!notice.contains(noticeId)) {
            Notice notices = noticeRepository.findById(noticeId).orElse(null);
            if (notices != null) {
                Long currentHits = notices.getNoticeHits();
                notices.setNoticeHits(currentHits + 1L);
                noticeRepository.save(notices);
            }
            // 해당 IP 주소로 조회한 게시물 번호를 세션에 저장하여 중복 조회 방지
            notice.add(noticeId);
            request.getSession().setAttribute(clientIp, notice);
        }
    }


/*

    // 그냥 조회수 증가
    @Transactional
    public void increaseNoticeHits(Long noticeId) {

        Notice notices = noticeRepository.findById(noticeId).orElse(null);
        if (notices != null) {
            Long currentHits = notices.getNoticeHits();
            notices.setNoticeHits(currentHits + 1L);
            noticeRepository.save(notices);
        }

    }
*/

    // 수정할 공지사항 가져오기
    public Notice noticeFindById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. :" + noticeId));

        return notice;
    }


    // 공지사항 수정 요청
    @Transactional
    public void noticeUpdate(Long noticeId, NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. :" + noticeId));

        // 게시물 정보 업데이트
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());

        String important = noticeDTO.getImportant();
        notice.setImportant("on".equals(important) ? "Y" : "N");


        if (noticeDTO.getNoticeUrl() != null) {
            notice.setNoticeUrl(noticeDTO.getNoticeUrl());
        }


        noticeRepository.save(notice);
    }


    // 파일 등록 메소드
    public void handleFileUpload(NoticeDTO noticeDTO) throws IOException {
        ftpUtil.connect(); // FTP 연결

        try {
            uploadFile(noticeDTO.getNoticeImage(), noticeDTO);
        } catch (IOException e) {
            System.err.println("FTP 업로드 중 오류가 발생했습니다: " + e.getMessage());
            throw e;
        } finally {
            ftpUtil.disconnect(); // FTP 연결 종료
        }

    }

    private void uploadFile(MultipartFile file, NoticeDTO noticeDTO) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);

            // 임시 파일 저장
            file.transferTo(localFile);

            // FTP 업로드
            try {
                System.out.println("FTP 업로드 시작: " + uniqueFileName);
                ftpUtil.uploadFile("/AutumnBrow/notice/" + uniqueFileName, localFile);
                System.out.println("FTP 업로드 성공: " + uniqueFileName);
                noticeDTO.setNoticeUrl(uniqueFileName);
            } catch (IOException e) {
                System.err.println("FTP 업로드 중 오류가 발생했습니다: " + e.getMessage());
                throw e;
            } finally {
                localFile.delete(); // 임시 파일 삭제
            }
        }
    }




 }
