package autumn.browmanagement.repository;

import autumn.browmanagement.config.EncryptionUtil;
import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.Role;
import autumn.browmanagement.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByIsDeleted(String isDeleted);

    /*private final EntityManager em;

    public void save(Post post){
        if (post.getId() == null){
            em.persist(post);
        } else {
            em.merge(post);
        }
    }

    public List<Post> findAll() {
        return em.createQuery("select m from Post m", Post.class)
                .getResultList();
    }

    public Post findOne(Long postId){
        //return em.find(Post.class, postId);
        Post post = em.find(Post.class, postId);

        if (post != null) {
            try {
                String decrypt = EncryptionUtil.decrypt(post.getUser().getPhone());
                post.getUser().setPhone(decrypt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return post;
    }*/

}
