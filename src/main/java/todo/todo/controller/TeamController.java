package todo.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import todo.todo.dto.request.team.AddTeamBaseReq;
import todo.todo.dto.request.teammember.AddMemberReq;
import todo.todo.dto.response.team.TeamDetailRes;
import todo.todo.dto.response.teamMember.TeamMemberRes;
import todo.todo.service.team.TeamService;

@RestController
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    public ResponseEntity<TeamDetailRes> createTeam(
            @RequestBody AddTeamBaseReq request,
            @RequestParam int currentUserId) {
        TeamDetailRes result = teamService.CreateTeam(request, currentUserId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTeam(
            @PathVariable int id,
            @RequestParam int userId) {
        try {
            TeamDetailRes result = teamService.DeleteTeam(id, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TeamDetailRes> updateTeam(
            @PathVariable int id,
            @RequestParam int currentUserId,
            @Valid @RequestBody AddTeamBaseReq request) {
        TeamDetailRes response = teamService.UpdateTeam(request, id, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable int teamId,
            @RequestParam int currentUserId,
            @RequestBody @Valid AddMemberReq req) {
        return ResponseEntity.ok(
                teamService.addMemberToTeam(teamId, req.getUserId(), currentUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDetailRes> getTeamDetail(@PathVariable int id) {
        return ResponseEntity.ok(teamService.getTeamDetail(id));
    }

    @PatchMapping("/{teamId}/members/{userId}/role")
    public ResponseEntity<TeamMemberRes> updateMemberRole(
            @PathVariable int teamId,
            @PathVariable int userId,
            @Valid @RequestBody UpdateRoleReq body) {
        return ResponseEntity.ok(teamService.updateMemberRole(teamId, userId, body.role()));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<TeamMemberRes> deleteMember(
            @PathVariable int teamId,
            @PathVariable int userId,
            @RequestParam int currentUserId) {
        return ResponseEntity.ok(teamService.deleteMemberFromTeam(teamId, userId, currentUserId));
    }

    public record UpdateRoleReq(@NotBlank String role) {
    }

    @GetMapping("/by-user")
    public ResponseEntity<TeamDetailRes> getTeamByUser(
            @RequestParam int userId) {
        return ResponseEntity.ok(teamService.getTeamByUser(userId));
    }
}
