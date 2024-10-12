package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Post;
import autumn.browmanagement.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

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

}
