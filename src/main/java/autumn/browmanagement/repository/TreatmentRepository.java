package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findByParentIsNull(); // parentId가 null인 시술을 조회하는 메서드

    Optional<Treatment> findById(Long id); // id로 시술 내용을 찾는 메서드

/*
    @PersistenceContext
    private EntityManager em;

    // 모든 Treatment 조회
    public List<Treatment> findAll() {
        return em.createQuery("SELECT t FROM Treatment t", Treatment.class)
                .getResultList();
    }

    public List<Treatment> findParentTreatments() {
        return em.createQuery("SELECT t FROM Treatment t WHERE t.parent IS NULL", Treatment.class)
                .getResultList();
    }

    // 특정 부모 시술의 하위 시술 조회
    public List<Treatment> findChildTreatment(Long parentId) {
        TypedQuery<Treatment> query = em.createQuery("SELECT t FROM Treatment t WHERE t.parent.id = :parentId", Treatment.class);
        query.setParameter("parentId", parentId);
        return query.getResultList();
    }*/
}
