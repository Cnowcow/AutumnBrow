package autumn.browmanagement.repository;

import autumn.browmanagement.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user) {
        em.persist(user);
    }


    // 전화번호와 이름으로 사용자 조회
    public List<User> findByUser(String name, String phone) {
        return em.createQuery("select m from User m where m.phone = :phone and m.name = :name", User.class)
                .setParameter("phone", phone)
                .setParameter("name", name)
                .getResultList();
    }

    // 전화번호와 이름으로 사용자 조회
    public Optional<User> findByPhone(String phone) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.phone = :phone", User.class);
        query.setParameter("phone", phone);
        return query.getResultList().stream().findFirst(); // Optional로 감싸서 반환
    }


}
