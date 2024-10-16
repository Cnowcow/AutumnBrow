package autumn.browmanagement.repository;

import autumn.browmanagement.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNameAndPhone(String name, String phone);

    List<User> findByRoleId(Long roleId);

    Optional<User> findById(Long userId); // ID로 사용자 조회


    /*private final EntityManager em;

    // 가입 요청
    public void create(User user) {
        em.persist(user);
    }


    // id로 사용자 조회
    public User findOne(Long userId){
        //return em.find(User.class, id);

        User user = em.find(User.class, userId);

        if (user != null) {
            try {
                String decrypt = EncryptionUtil.decrypt(user.getPhone());
                user.setPhone(decrypt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return user;
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

    // 전체 사용자 조회
    public List<User> findAll(){
        List<User> users = em.createQuery("select m from User m where m.roleId = 2", User.class)
                .getResultList();

        return users.stream().map(user -> {
            try {
                String decrypt = EncryptionUtil.decrypt(user.getPhone());
                user.setPhone(decrypt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return user;
        }).collect(Collectors.toList());
    }*/


}