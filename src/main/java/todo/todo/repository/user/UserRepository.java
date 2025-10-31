package todo.todo.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import todo.todo.entity.user.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    boolean existsByEmail(String email);
    User findById(int id);
}