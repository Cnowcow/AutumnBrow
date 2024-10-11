package autumn.browmanagement.repository;

import autumn.browmanagement.domain.Post;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}
