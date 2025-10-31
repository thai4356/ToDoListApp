package todo.todo.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class BaseRepository {
     @PersistenceContext
    private EntityManager entityManager;

    protected void flush() {
        entityManager.flush();
    }

    protected void detach(Object obj) {
        entityManager.detach(obj);
    }

    protected JPAQueryFactory query() {
        return new JPAQueryFactory(entityManager);
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
