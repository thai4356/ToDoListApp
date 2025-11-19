package todo.todo.service;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import todo.todo.entity.team_member.TeamMember.Role;
import todo.todo.exceptions.BusinessException;
import todo.todo.repository.task.TaskRepository;
import todo.todo.repository.teamMember.TeamMemberRepository;

@Component
@RequiredArgsConstructor
public class PermissionGuard {
    private final TeamMemberRepository teamMemberRepo;
    private final TaskRepository taskRepository;

    private static final Set<Role> OWNER_ADMIN = EnumSet.of(Role.owner, Role.admin);

    public void requireOwnerAdminForTask(int uid, int taskId) {
        int teamId = taskRepository.findTeamIdByTaskId(taskId);
        System.out.println("[GUARD] uid=" + uid + ", taskId=" + taskId + ", teamId=" + teamId);

        Set<Role> roles = Set.of(Role.owner, Role.admin);
        System.out.println("[GUARD] roles=" + roles + " (enum class=" + Role.class.getName() + ")");

        boolean ok = teamMemberRepo.existsOwnerAdminInTeam(uid, teamId, roles);
        System.out.println("[GUARD] repo.existsOwnerAdminInTeam -> " + ok);

        if (!ok) {
            System.out.println("[GUARD] Permission denied: user " + uid + " is not Owner/Admin of team " + teamId);
            throw new BusinessException(
                    "Bạn không có quyền thực hiện hành động này. Chỉ Owner hoặc Admin của team mới được phép.",
                    HttpStatus.FORBIDDEN);
        }
    }

    public void requireOwnerAdminForSubTask(int uid, int taskId) {
        int teamId = taskRepository.findTeamIdByTaskId(taskId);
        System.out.println("[GUARD] uid=" + uid + ", taskId=" + taskId + ", teamId=" + teamId);

        Set<Role> roles = Set.of(Role.owner, Role.admin);
        System.out.println("[GUARD] roles=" + roles + " (enum class=" + Role.class.getName() + ")");

        boolean ok = teamMemberRepo.existsOwnerAdminInTeam(uid, teamId, roles);
        System.out.println("[GUARD] repo.existsOwnerAdminInTeam -> " + ok);

        if (!ok) {
            System.out.println("[GUARD] Permission denied: user " + uid + " is not Owner/Admin of team " + teamId);
            throw new BusinessException(
                    "Bạn không có quyền thực hiện hành động này. Chỉ Owner hoặc Admin của team mới được phép.",
                    HttpStatus.FORBIDDEN);
        }
    }
}
