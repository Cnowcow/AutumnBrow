package autumn.browmanagement.service;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.domain.Notice;
import autumn.browmanagement.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;



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

            noticeDTOS.add(noticeDTO);
        }
        return noticeDTOS;
    }
}
