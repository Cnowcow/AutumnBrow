package autumn.browmanagement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TreatmentService {

    public Map<String, List<String>> getTreatments() {

        Map<String, List<String>> treatments = new HashMap<>();
        treatments.put("반영구", Arrays.asList("자연눈썹", "섀도우눈썹", "콤보눈썹", "아이라인", "헤어라인", "입술타투", "미인점"));
        treatments.put("속눈썹", Arrays.asList("속눈썹펌", "블랙틴팅펌", "탱글영양 + 블랙틴팅펌"));
        treatments.put("직접입력", Arrays.asList());
        System.out.println("서비스 treatments값 = " + treatments);
        return treatments;
    }
}
