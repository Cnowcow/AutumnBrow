package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Treatment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TreatmentRepository {

    @PersistenceContext
    private EntityManager em;

    // 모든 Treatment 조회
    public List<Treatment> findAll() {
        return em.createQuery("SELECT t FROM Treatment t", Treatment.class)
                .getResultList();
    }

    // 이름으로 Treatment 조회
    public Optional<Treatment> findByName(String name) {
        TypedQuery<Treatment> query = em.createQuery("SELECT t FROM Treatment t WHERE t.name = :name", Treatment.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }

    // Treatment 저장
    public void save(Treatment treatment) {
        if (treatment.getId() == null) {
            em.persist(treatment);
        } else {
            em.merge(treatment);
        }
    }

    // Treatment 삭제 (필요시)
    public void delete(Treatment treatment) {
        if (em.contains(treatment)) {
            em.remove(treatment);
        } else {
            em.remove(em.merge(treatment));
        }
    }
}
