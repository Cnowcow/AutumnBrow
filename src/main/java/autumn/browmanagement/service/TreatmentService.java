package autumn.browmanagement.service;

import autumn.browmanagement.DTO.TreatmentDTO;
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


    // 시술내용 추가
    @Transactional
    public Treatment addTreatment(TreatmentDTO treatmentDTO) {
        Treatment treatment = new Treatment();
        Long treatmentId = treatmentDTO.getTreatmentId();
        String parentName = treatmentDTO.getParentName();
        String childName = treatmentDTO.getChildName();

        if (parentName == null || parentName.isEmpty() || childName == null || childName.isEmpty() ){
            throw new IllegalArgumentException("시술내용을 입력하세요.");
        }

        if (treatmentId == 1L) {
            System.out.println("직접입력일 때");
            if (parentName != null && !parentName.isEmpty()) {
                Treatment newTreatment = new Treatment();
                newTreatment.setName(parentName);
                Treatment saveParent = treatmentRepository.save(newTreatment);

                treatment.setParent(saveParent);
                treatment.setName(childName);
            } else {
                throw new IllegalArgumentException("시술 내용이 필요합니다.");
            }
        } else {
            Treatment parent = treatmentRepository.findById(treatmentId)
                    .orElseThrow(() -> new IllegalArgumentException("시술 내용을 찾을 수 없습니다."));

            treatment.setParent(parent);
            treatment.setName(childName);
        }
        return treatmentRepository.save(treatment);
    }


    // 상위 시술 조회
    public List<Treatment> treatmentList() {
        return treatmentRepository.findByParentIsNullOrderByTreatmentIdDesc();
    }


    // 상위 시술 조회
    public String findParentTreatments(Long parentId){
        Optional<Treatment> treatment = treatmentRepository.findById(parentId);

        if(treatment.isPresent()){
            String name = treatment.get().getName();
            System.out.println("name" + name);
        }

        return treatment.get().getName();
    }

    // 하위 시술 조회
    public List<TreatmentDTO> findChildTreatments(Long parentId) {
        List<Treatment> treatments = treatmentRepository.findByParent_TreatmentId(parentId);
        return treatments.stream()
                .map(treatment -> {
                    TreatmentDTO form = new TreatmentDTO();
                    form.setTreatmentId(treatment.getTreatmentId());
                    form.setName(treatment.getName());
                    form.setParentId(treatment.getParent() != null ? treatment.getParent().getTreatmentId() : null);
                    return form;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    // 직접입력된 시술 추가
    public Treatment createTreatment(TreatmentDTO treatmentDTO) {
        Treatment treatment = new Treatment();
        treatment.setName(treatmentDTO.getName());

        if (treatmentDTO.getParentId() != null) {
            Treatment parentTreatment = new Treatment();
            parentTreatment.setTreatmentId(treatmentDTO.getParentId());

            treatment.setParent(parentTreatment);

        }

        return treatmentRepository.save(treatment);
    }


    public List<Treatment> findAll(){

        return treatmentRepository.findAll();
    }
}
