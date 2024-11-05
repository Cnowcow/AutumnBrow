package autumn.browmanagement.service;

import autumn.browmanagement.DTO.VisitDTO;
import autumn.browmanagement.Entity.Treatment;
import autumn.browmanagement.Entity.Visit;
import autumn.browmanagement.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    public List<Visit> visitList(){
        return visitRepository.findAll();
    }

    // 방문경로 이름
    public String visitList(Long visitId){
        Optional<Visit> visit = visitRepository.findByVisitId(visitId);

        if(visit.isPresent()){
            String name = visit.get().getVisitPath();
            System.out.println("name" + name);
        }

        return visit.get().getVisitPath();
    }


    @Transactional
    public Visit visitCreate(VisitDTO visitDTO) {
        Visit visit = new Visit();
        visit.setVisitPath(visitDTO.getVisitPath());

        return visitRepository.save(visit);
    }

}
