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

    @Transactional
    public void noticeImageDelete(String imageUrl){
        System.out.println("zzzzzzzzzzzz = " + imageUrl);
        imageRepository.deleteByImageUrl(imageUrl);
    }

    @Transactional
    public void eventImageDelete(String imageUrl){
        System.out.println("zzzzzzzzzzzz = " + imageUrl);
        imageRepository.deleteByImageUrl(imageUrl);
    }
}
