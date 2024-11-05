package autumn.browmanagement.service;

import autumn.browmanagement.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;


    // 공지사항 사진등록
    @Transactional
    public void noticeImageDelete(String imageUrl){
        imageRepository.deleteByImageUrl(imageUrl);
    }


    // 이벤트 사진등록
    @Transactional
    public void eventImageDelete(String imageUrl){
        imageRepository.deleteByImageUrl(imageUrl);
    }
}
