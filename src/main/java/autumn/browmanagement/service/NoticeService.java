package autumn.browmanagement.service;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.DTO.PostDTO;
import autumn.browmanagement.domain.Notice;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.repository.NoticeRepository;
import autumn.browmanagement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest request;


    // 공지사항 조회
    public List<NoticeDTO> noticeList(){
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeIdDesc();
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
            noticeDTO.setNoticeLike(notice.getNoticeLike());

            noticeDTOS.add(noticeDTO);
        }
        return noticeDTOS;
    }


    // 공지사항 중요도 순
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
            noticeDTO.setNoticeLike(notice.getNoticeLike());

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
        noticeDTO.setNoticeLike(notice.getNoticeLike());
        if (notice.getUser().getId() != null) {
            Optional<User> user = userRepository.findById(notice.getUser().getId());
            if (user.isPresent()) {
                User userName = user.get();
                noticeDTO.setUserName(userName.getName());
            }
        }

        return noticeDTO;
    }


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


/*


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
*/


    @Transactional
    public void increaseNoticeLike(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시술내용이 없습니다."));

        if (notice != null) {
            Long currentLike = notice.getNoticeLike();
            notice.setNoticeLike(currentLike + 1L);
            noticeRepository.save(notice);
        }

        noticeRepository.save(notice);
    }


 }
