package autumn.browmanagement.service;

import autumn.browmanagement.domain.Visit;
import autumn.browmanagement.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;

    public List<Visit> visitList(){
        return visitRepository.findAll();
    }

}
