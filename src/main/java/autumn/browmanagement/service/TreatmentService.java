package autumn.browmanagement.service;

import autumn.browmanagement.controller.TreatmentForm;
import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    // 모든 시술 조회 메소드
    public List<Treatment> treatmentList() {
        return treatmentRepository.findByParentIsNull(); // 상위 시술만 반환
    }


    /*

    // 부모 시술 목록을 가져오는 서비스 메서드
    public List<Treatment> findParentTreatments() {
        return treatmentRepository.findParentTreatments();
    }

    public List<TreatmentForm> findChildTreatments(Long parentId) {
        List<Treatment> treatments = treatmentRepository.findChildTreatment(parentId);
        return treatments.stream()
                .map(treatment -> {
                    TreatmentForm form = new TreatmentForm();
                    form.setId(treatment.getId());
                    form.setName(treatment.getName());
                    form.setParentId(treatment.getParent() != null ? treatment.getParent().getId() : null);
                    return form;
                })
                .collect(Collectors.toList());
    }*/

}
