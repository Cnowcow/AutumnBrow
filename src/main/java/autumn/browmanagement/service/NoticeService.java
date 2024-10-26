package autumn.browmanagement.service;

import autumn.browmanagement.DTO.NoticeDTO;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.Entity.*;
import autumn.browmanagement.repository.ImageRepository;
import autumn.browmanagement.repository.LikeyRepository;
import autumn.browmanagement.repository.NoticeRepository;
import autumn.browmanagement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final LikeyRepository likeyRepository;
    private final ImageRepository imageRepository;
    private final HttpServletRequest request;
    private final FtpUtil ftpUtil;


    // 이미지 처리 메소드
    private String handleImageUpload(MultipartFile file) throws IOException {

        ftpUtil.connect();
        // 원본 파일명 몇 확장자 가져오기
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 랜덤파일명 생성
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        // 임시파일 저장 경로
        File localFile = new File(System.getProperty("java.io.tmpdir") + "/" + uniqueFileName);
        // 임시파일 업로드
        file.transferTo(localFile);
        // FTP에 파일 업로드
        ftpUtil.uploadFile("/AutumnBrow/notice/" + uniqueFileName, localFile);
        localFile.delete(); // 임시 파일 삭제
        ftpUtil.disconnect();

        return uniqueFileName;
    }


    // 공지사항 등록
    @Transactional
    public void noticeCreate(NoticeDTO noticeDTO, MultipartFile[] noticeImages){
        Notice notice = new Notice();
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setNoticeDate(LocalDateTime.now());
        notice.setImportant("on".equals(noticeDTO.getImportant()) ? "Y" : "N");

        User user = userRepository.findByUserId(noticeDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + noticeDTO.getUserId()));
        notice.setUser(user);

        noticeRepository.save(notice);

        for (MultipartFile noticeImage : noticeImages) {
            if (noticeImage != null && !noticeImage.isEmpty()) {
                try {
                    String imageUrl = handleImageUpload(noticeImage); // 이미지 업로드 처리

                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setNotice(notice); // 공지사항에 이미지 연결
                    imageRepository.save(image); // 이미지 저장
                } catch (IOException e) {
                    System.err.println("업로드 중 에러발생" + e.getMessage());
                }
            }
        }
        noticeDTO.setNoticeId(notice.getNoticeId());
    }


    // 공지사항 조회 날짜순 (index용)
    public List<NoticeDTO> noticeListIndex(){
        List<Notice> notices = noticeRepository.findAllByOrderByNoticeDateDesc();

        /*
        List<Notice> limitedNotices = notices.stream()
                .limit(10)
                .collect(Collectors.toList()); // 출력할 최대갯수 지정
        */

        List<NoticeDTO> noticeDTOS = new ArrayList<>();

        for(Notice notice : notices){
//        for(Notice notice : limitedNotices){
            List<Image> images = imageRepository.findByNotice_NoticeId(notice.getNoticeId());

            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setNoticeId(notice.getNoticeId());
            noticeDTO.setTitle(notice.getTitle());
            noticeDTO.setContent(notice.getContent());
            noticeDTO.setUserId(noticeDTO.getUserId());
            noticeDTO.setImportant(notice.getImportant());
            noticeDTO.setNoticeDate(notice.getNoticeDate());
            noticeDTO.setNoticeHits(notice.getNoticeHits());

            List<String> imageUrls = images.stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());
            noticeDTO.setImageUrls(imageUrls);

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
            noticeDTO.setImportant(notice.getImportant());
            noticeDTO.setNoticeDate(notice.getNoticeDate());
            noticeDTO.setNoticeHits(notice.getNoticeHits());

            noticeDTOS.add(noticeDTO);
        }
        return noticeDTOS.stream()
                .sorted(Comparator.comparing(NoticeDTO::getImportant).reversed()) // Y가 먼저 오도록 정렬
                .collect(Collectors.toList());
    }


    // 공지사항 디테일, 수정폼
    public NoticeDTO noticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. :" + noticeId));

        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setNoticeId(notice.getNoticeId());
        noticeDTO.setTitle(notice.getTitle().replaceAll("<", "&lt").replaceAll(">", "&gt").replaceAll("\n", "<br>"));
        noticeDTO.setContent(notice.getContent().replaceAll("<", "&lt").replaceAll(">", "&gt").replaceAll("\n", "<br>"));
        noticeDTO.setNoticeDate(notice.getNoticeDate());
        noticeDTO.setNoticeHits(notice.getNoticeHits());
        noticeDTO.setImportant(notice.getImportant());

        List<Image> images = imageRepository.findByNotice_NoticeId(noticeId);
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        noticeDTO.setImageUrls(imageUrls);

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


    public NoticeDTO noticeUpdateForm(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. :" + noticeId));

        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setNoticeId(notice.getNoticeId());
        noticeDTO.setTitle(notice.getTitle());
        noticeDTO.setContent(notice.getContent());
        noticeDTO.setNoticeDate(notice.getNoticeDate());
        noticeDTO.setImportant(notice.getImportant());

        List<Image> images = imageRepository.findByNotice_NoticeId(noticeId);
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        noticeDTO.setImageUrls(imageUrls);

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


    // 공지사항 수정 요청
    @Transactional
    public void noticeUpdate(NoticeDTO noticeDTO, MultipartFile[] noticeImages) {
        Notice notice = noticeRepository.findById(noticeDTO.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. :" + noticeDTO.getNoticeId()));

        // 게시물 정보 업데이트
        notice.setNoticeId(noticeDTO.getNoticeId());
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
        notice.setImportant("on".equals(noticeDTO.getImportant()) ? "Y" : "N");

        List<Image> images = imageRepository.findByNotice_NoticeId(notice.getNoticeId());
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        for (MultipartFile noticeImage : noticeImages) {
            if (noticeImage != null && !noticeImage.isEmpty()) {
                try {
                    String imageUrl = handleImageUpload(noticeImage); // 이미지 업로드 처리

                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setNotice(notice); // 공지사항에 이미지 연결
                    imageRepository.save(image); // 이미지 저장
                } catch (IOException e) {
                    System.err.println("업로드 중 에러 발생: " + e.getMessage());
                }
            }
        }

        // 기존 이미지를 삭제할 경우
        if (noticeDTO.getImageUrls() != null) {
            // 삭제할 이미지 URL들을 처리
            List<String> toDeleteImageUrls = imageUrls.stream()
                    .filter(url -> !Arrays.asList(noticeDTO.getImageUrls()).contains(url))
                    .collect(Collectors.toList());

            for (String url : toDeleteImageUrls) {
                // 해당 URL에 해당하는 이미지를 삭제
                Image imageToDelete = images.stream()
                        .filter(image -> image.getImageUrl().equals(url))
                        .findFirst()
                        .orElse(null);
                if (imageToDelete != null) {
                    imageRepository.delete(imageToDelete);
                }
            }
        }

        noticeRepository.save(notice);
    }



    /* ip기준 조회수 시작 */
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
    /* 조회수 메소드 종료 */

 }
