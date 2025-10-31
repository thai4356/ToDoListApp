package todo.todo.repository.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import todo.todo.dto.response.user.UserListRes;
import todo.todo.entity.user.User;

@Repository 
public interface UserRepositoryCustom {
    User loginByEmail(String email);
    // long countUser();
    List<UserListRes> getUsers();

    User getUserByEmail(String email);

    boolean existsByCode (String code);
}