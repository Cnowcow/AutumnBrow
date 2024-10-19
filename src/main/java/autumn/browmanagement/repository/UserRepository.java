package autumn.browmanagement.repository;

import autumn.browmanagement.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNameAndPhone(String name, String phone);

    List<User> findByRoleIdAndIsDeletedOrderByIdDesc(Long roleId, String isDeleted);

    Optional<User> findById(Long userId); // ID로 사용자 조회

}