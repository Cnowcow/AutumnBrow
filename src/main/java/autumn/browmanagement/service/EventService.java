package autumn.browmanagement.service;

import autumn.browmanagement.DTO.EventDTO;
import autumn.browmanagement.Entity.*;
import autumn.browmanagement.config.FtpUtil;
import autumn.browmanagement.repository.*;
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
public class EventService {

    private final EventRepository eventRepository;
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
        ftpUtil.uploadFile("/Project/AutumnBrow/event/" + uniqueFileName, localFile);
        localFile.delete(); // 임시 파일 삭제
        ftpUtil.disconnect();

        return uniqueFileName;
    }


    // 이벤트 등록 요청
    @Transactional
    public void eventCreate(EventDTO eventDTO, MultipartFile[] eventImages){
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setContent(eventDTO.getContent());
        event.setEventDate(LocalDateTime.now());
        event.setImportant("on".equals(eventDTO.getImportant()) ? "Y" : "N");

        User user = userRepository.findByUserId(eventDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + eventDTO.getUserId()));
        event.setUser(user);

        eventRepository.save(event);

        for (MultipartFile eventImage : eventImages) {
            if (eventImage != null && !eventImage.isEmpty()) {
                try {
                    String imageUrl = handleImageUpload(eventImage); // 이미지 업로드 처리

                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setEvent(event); // 이벤트에 이미지 연결
                    imageRepository.save(image); // 이미지 저장
                } catch (IOException e) {
                    System.err.println("업로드 중 에러발생" + e.getMessage());
                }
            }
        }
        eventDTO.setEventId(event.getEventId());
    }


    // 이벤트 조회 날짜순 (index용)
    public List<EventDTO> eventListIndex(){
        List<Event> events = eventRepository.findAllByOrderByEventDateDesc();

        /*
        List<Event> limitedEvents = events.stream()
                .limit(10)
                .collect(Collectors.toList()); // 출력할 최대갯수 지정
        */

        List<EventDTO> eventDTOS = new ArrayList<>();

        for(Event event : events){
//        for(Event event : limitedEvents){ 최대갯수 지정시 for문
            List<Image> images = imageRepository.findByEvent_EventId(event.getEventId());

            EventDTO eventDTO = new EventDTO();
            eventDTO.setEventId(event.getEventId());
            eventDTO.setTitle(event.getTitle());
            eventDTO.setContent(event.getContent());
            eventDTO.setUserId(eventDTO.getUserId());
            eventDTO.setImportant(event.getImportant());
            eventDTO.setEventDate(event.getEventDate());
            eventDTO.setEventHits(event.getEventHits());

            List<String> imageUrls = images.stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());
            eventDTO.setImageUrls(imageUrls);

            eventDTOS.add(eventDTO);
        }
        return eventDTOS;
    }


    // 이벤트 중요도 순 (이벤트 페이지용)
    public List<EventDTO> eventListImportant() {
        List<Event> events = eventRepository.findAllByOrderByEventDateDesc(); // 모든 이벤트 불러오기
        List<EventDTO> eventDTOS = new ArrayList<>();
        for(Event event : events){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setEventId(event.getEventId());
            eventDTO.setTitle(event.getTitle());
            eventDTO.setImportant(event.getImportant());
            eventDTO.setEventDate(event.getEventDate());
            eventDTO.setEventHits(event.getEventHits());

            eventDTOS.add(eventDTO);
        }
        return eventDTOS.stream()
                .sorted(Comparator.comparing(EventDTO::getImportant).reversed()) // Y가 먼저 오도록 정렬
                .collect(Collectors.toList());
    }


    // 이벤트 디테일, 수정폼
    public EventDTO eventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. :" + eventId));

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventId(event.getEventId());
        eventDTO.setTitle(event.getTitle().replaceAll("<", "&lt").replaceAll(">", "&gt").replaceAll("\n", "<br>"));
        eventDTO.setContent(event.getContent().replaceAll("<", "&lt").replaceAll(">", "&gt").replaceAll("\n", "<br>"));
        eventDTO.setEventDate(event.getEventDate());
        eventDTO.setEventHits(event.getEventHits());
        eventDTO.setImportant(event.getImportant());

        List<Image> images = imageRepository.findByEvent_EventId(eventId);
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        eventDTO.setImageUrls(imageUrls);

        Long likeCount = likeyRepository.countByEvent(event);
        eventDTO.setLikesCount(likeCount);

        if (event.getUser().getUserId() != null) {
            Optional<User> user = userRepository.findById(event.getUser().getUserId());
            if (user.isPresent()) {
                User userName = user.get();
                eventDTO.setUserName(userName.getName());
            }
        }
        return eventDTO;
    }


    // 이벤트 수정 폼
    public EventDTO eventUpdateForm(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. :" + eventId));

        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventId(event.getEventId());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setContent(event.getContent());
        eventDTO.setEventDate(event.getEventDate());
        eventDTO.setImportant(event.getImportant());

        List<Image> images = imageRepository.findByEvent_EventId(eventId);
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        eventDTO.setImageUrls(imageUrls);

        Long likeCount = likeyRepository.countByEvent(event);
        eventDTO.setLikesCount(likeCount);

        if (event.getUser().getUserId() != null) {
            Optional<User> user = userRepository.findById(event.getUser().getUserId());
            if (user.isPresent()) {
                User userName = user.get();
                eventDTO.setUserName(userName.getName());
            }
        }
        return eventDTO;
    }


    // 이벤트 수정 요청
    @Transactional
    public void eventUpdate(EventDTO eventDTO, MultipartFile[] eventImages) {
        Event event = eventRepository.findById(eventDTO.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. :" + eventDTO.getEventId()));

        // 게시물 정보 업데이트
        event.setEventId(eventDTO.getEventId());
        event.setTitle(eventDTO.getTitle());
        event.setContent(eventDTO.getContent());
        event.setImportant("on".equals(eventDTO.getImportant()) ? "Y" : "N");

        List<Image> images = imageRepository.findByEvent_EventId(event.getEventId());
        List<String> imageUrls = images.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        for (MultipartFile eventImage : eventImages) {
            if (eventImage != null && !eventImage.isEmpty()) {
                try {
                    String imageUrl = handleImageUpload(eventImage); // 이미지 업로드 처리

                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setEvent(event); // 이벤트에 이미지 연결
                    imageRepository.save(image); // 이미지 저장
                } catch (IOException e) {
                    System.err.println("업로드 중 에러 발생: " + e.getMessage());
                }
            }
        }

        // 기존 이미지를 삭제할 경우
        if (eventDTO.getImageUrls() != null) {
            // 삭제할 이미지 URL들을 처리
            List<String> toDeleteImageUrls = imageUrls.stream()
                    .filter(url -> !Arrays.asList(eventDTO.getImageUrls()).contains(url))
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

        eventRepository.save(event);
    }


    // 이벤트 삭제
    @Transactional
    public void eventDelete(Long eventId){
        eventRepository.deleteById(eventId);
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
    public void increaseEventHits(Long eventId) {

        // 클라이언트의 IP 주소를 가져옴
        String clientIp = getClientIp(request);

        // 해당 IP 주소로 조회한 게시물 번호를 가져옴
        Set<Long> event = (Set<Long>) request.getSession().getAttribute(clientIp);
        if (event == null) {
            event = new HashSet<>();
            request.getSession().setAttribute(clientIp, event);
        }

        // 해당 IP 주소로 조회한 게시물 번호가 없으면 조회수 증가 처리 후 세션에 저장
        if (!event.contains(eventId)) {
            Event events = eventRepository.findById(eventId).orElse(null);
            if (events != null) {
                Long currentHits = events.getEventHits();
                events.setEventHits(currentHits + 1L);
                eventRepository.save(events);
            }
            // 해당 IP 주소로 조회한 게시물 번호를 세션에 저장하여 중복 조회 방지
            event.add(eventId);
            request.getSession().setAttribute(clientIp, event);
        }
    }
    /* 조회수 메소드 종료 */

}
