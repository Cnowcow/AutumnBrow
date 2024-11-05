package autumn.browmanagement.service;

import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;


    // 시술내용 불러오기
    public List<Treatment> treatmentFindParent(){
        return treatmentRepository.findAllByParentIsNull();
    }


    // 시술내용에 대한 세부내용 불러오기
    public List<Treatment> findChildTreatment(Long parentId) {
        return treatmentRepository.findAllByParent_TreatmentId(parentId);
    }


    // 상위 시술 조회
    public List<Treatment> treatmentList() {
        return treatmentRepository.findByParentIsNullOrderByTreatmentIdDesc();
    }

/*


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
            Treatment parent = treatmentRepository.findByTreatmentId(treatmentId)
                    .orElseThrow(() -> new IllegalArgumentException("시술 내용을 찾을 수 없습니다."));

            treatment.setParent(parent);
            treatment.setName(childName);
        }
        return treatmentRepository.save(treatment);
    }




    // Entity -> DTO
    public TreatmentDTO convertToDTO(Treatment treatment) {
        TreatmentDTO dto = new TreatmentDTO();
        dto.setTreatmentId(treatment.getTreatmentId());
        dto.setName(treatment.getName());

        // 부모 시술 정보
        if (treatment.getParent() != null) {
            dto.setParentId(treatment.getParent().getTreatmentId());
            dto.setParentName(treatment.getParent().getName());
        }

*/
/*        // 자식 시술 정보 (재귀적으로 처리)
        if (treatment.getChild() != null && !treatment.getChild().isEmpty()) {
            List<TreatmentDTO> childDTOs = treatment.getChild().stream()
                    .map(this::convertToDTO)
                    .toList();
            dto.setChild(childDTOs);
        }*//*


        return dto;
    }

    // 전체 시술 목록을 계층적으로 가져오는 메서드
    public List<TreatmentDTO> getAllTreatments() {
        List<Treatment> treatments = treatmentRepository.findAll();
        return treatments.stream()
                .filter(treatment -> treatment.getParent() == null) // 최상위 시술만 필터링
                .map(this::convertToDTO)
                .toList();
    }


    // 시술내용 삭제 요청
    @Transactional
    public void deleteTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findByTreatmentId(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시술내용이 없습니다."));

*/
/*        if (treatment.getChild() != null && !treatment.getChild().isEmpty()) {
            throw new IllegalStateException("세부 시술내용이 있으면 삭제할 수 없습니다.");
        }*//*


        treatmentRepository.deleteById(treatmentId);
    }


*/

}
