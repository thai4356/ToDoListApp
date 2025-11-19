package todo.todo.service.User;

import java.util.List;

import todo.todo.dto.request.UserLoginReq;
import todo.todo.dto.response.BaseResponse;
import todo.todo.dto.response.user.RegisterUser;
import todo.todo.dto.response.user.UserDetailRes;
import todo.todo.dto.response.user.UserListRes;
import todo.todo.entity.user.User;


public interface UserService {
    UserDetailRes login(UserLoginReq request);

    BaseResponse<List<UserListRes>> getUsers();

    UserDetailRes register(RegisterUser request);

    User getUserByEmail ();
}
