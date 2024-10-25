package autumn.browmanagement.service;

import autumn.browmanagement.domain.Likey;
import autumn.browmanagement.domain.Notice;
import autumn.browmanagement.domain.User;
import autumn.browmanagement.repository.LikeyRepository;
import autumn.browmanagement.repository.NoticeRepository;
import autumn.browmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeyService {

    private final LikeyRepository likeyRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;



    @Transactional
    // 좋아요 추가
    public void likeNotice(Long noticeId, Long userId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new IllegalArgumentException("공지사항이 없습니다."));
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
        likeyRepository.findByNotice_NoticeIdAndUser_UserId(noticeId, userId).ifPresentOrElse(
                like -> {
                    throw new IllegalArgumentException("이미 좋아요를 눌렀습니다.");
                },
                () -> {
                    Likey likey = new Likey();
                    likey.setNotice(notice);
                    likey.setUser(user);
                    likeyRepository.save(likey);
                }
        );
    }

}
