package todo.todo.repository.teamMember;

import java.util.List;

import todo.todo.entity.team_member.QTeamMember;
import todo.todo.entity.team_member.TeamMember;
import todo.todo.repository.BaseRepository;

public class TeamMemberRepositoryImpl extends BaseRepository implements TeamMemberRepositoryCustom {

        private final QTeamMember qTeamMember = QTeamMember.teamMember;

        @Override
        public List<TeamMember> findByTeam_IdAndDeletedIsNull(int teamId) {
                return query()
                                .select(qTeamMember)
                                .from(qTeamMember)
                                .where(
                                                qTeamMember.team.id.eq(teamId)
                                                                .and(qTeamMember.deleted.isFalse()))
                                .fetch();
        }

        @Override
        public TeamMember findByUserId(int userId) {
                return query()
                                .select(qTeamMember)
                                .from(qTeamMember)
                                .where(
                                                qTeamMember.user.id.eq(userId)
                                                                .and(qTeamMember.deleted.isNull()))
                                .fetchOne();
        }

        @Override
        public TeamMember findByUserIdAndTeamId(int userId, int teamId) {
                return query()
                                .select(qTeamMember)
                                .from(qTeamMember)
                                .where(
                                                qTeamMember.user.id.eq(userId)
                                                                .and(qTeamMember.team.id.eq(teamId))
                                                                .and(qTeamMember.deleted.isTrue()))
                                .fetchOne();
        }
}
