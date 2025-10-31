package todo.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import todo.todo.dto.request.team.AddTeamBaseReq;
import todo.todo.dto.request.teammember.AddMemberReq;
import todo.todo.dto.response.team.TeamDetailRes;
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

    @PostMapping("/{teamId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable int teamId,
            @RequestParam int currentUserId,
            @RequestBody @Valid AddMemberReq req) {
        return ResponseEntity.ok(
                teamService.addMemberToTeam(teamId, req.getUserId(), currentUserId));
    }
}
