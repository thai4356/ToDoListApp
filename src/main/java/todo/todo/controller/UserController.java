package todo.todo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import todo.todo.dto.response.BaseResponse;
import todo.todo.dto.response.user.UserListRes;
import todo.todo.entity.user.User;
import todo.todo.repository.user.UserRepository;
import todo.todo.service.User.UserService;

@RestController
@RequestMapping("/api/")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get list members")
    @GetMapping("v1/user/members")
    public ResponseEntity<BaseResponse<List<UserListRes>>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get self info")
    @GetMapping("/v1/user/me")
    public ResponseEntity<BaseResponse<User>> getSelfInfo() {

        User u = userService.getUserByEmail();
        System.out.println(u.getEmail());

        BaseResponse<User> res = new BaseResponse<>();
        res.setMessage("success");
        res.setData(u);

        return ResponseEntity.ok(res);
    }
}
