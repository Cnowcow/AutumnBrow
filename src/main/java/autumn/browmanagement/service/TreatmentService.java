package autumn.browmanagement.service;

import autumn.browmanagement.controller.TreatmentForm;
import autumn.browmanagement.domain.Treatment;
import autumn.browmanagement.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;


    // 상위 시술 조회
    public List<Treatment> treatmentList() {
        return treatmentRepository.findByParentIsNull();
    }


    // 하위 시술 조회
    public List<TreatmentForm> findChildTreatments(Long parentId) {
        List<Treatment> treatments = treatmentRepository.findByParent_TreatmentId(parentId);
        return treatments.stream()
                .map(treatment -> {
                    TreatmentForm form = new TreatmentForm();
                    form.setTreatmentId(treatment.getTreatmentId());
                    form.setName(treatment.getName());
                    form.setParentId(treatment.getParent() != null ? treatment.getParent().getTreatmentId() : null);
                    return form;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    // 직접입력된 시술 추가
    public Treatment createTreatment(TreatmentForm treatmentForm) {
        Treatment treatment = new Treatment();
        treatment.setName(treatmentForm.getName());

        if (treatmentForm.getParentId() != null) {
            Treatment parentTreatment = new Treatment();
            parentTreatment.setTreatmentId(treatmentForm.getParentId());
            treatment.setParent(parentTreatment);
        }

        return treatmentRepository.save(treatment);
    }
}
