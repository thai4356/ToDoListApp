package todo.todo.repository.teamMember;

import java.util.List;
import java.util.Set;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import todo.todo.dto.response.teamMember.TeamMemberRes;
import todo.todo.entity.team_member.QTeamMember;
import todo.todo.entity.team_member.TeamMember;
import todo.todo.entity.team_member.TeamMember.Role;
import todo.todo.repository.BaseRepository;

public class TeamMemberRepositoryImpl extends BaseRepository implements TeamMemberRepositoryCustom {

        private final EntityManager em;
        private JPAQueryFactory queryFactory;

        private final QTeamMember qTeamMember = QTeamMember.teamMember;

        public TeamMemberRepositoryImpl(EntityManager em) {
                this.em = em;
        }

        @PostConstruct
        void init() {
                this.queryFactory = new JPAQueryFactory(em);
        }

        @Override
        public List<TeamMember> findByTeam_IdAndDeletedIsNull(int teamId) {
                return queryFactory
                                .selectFrom(qTeamMember)
                                .where(qTeamMember.team.id.eq(teamId)
                                                .and(qTeamMember.deleted.isFalse()))
                                .fetch();
        }

        @Override
        public TeamMember findByUserId(int userId) {
                return queryFactory
                                .selectFrom(qTeamMember)
                                .where(qTeamMember.user.id.eq(userId)
                                                .and(qTeamMember.deleted.isFalse()))
                                .fetchOne();
        }

        @Override
        public TeamMember findByUserIdAndTeamId(int userId, int teamId) {
                return queryFactory
                                .selectFrom(qTeamMember)
                                .where(qTeamMember.user.id.eq(userId)
                                                .and(qTeamMember.team.id.eq(teamId))
                                                .and(qTeamMember.deleted.isFalse()))
                                .fetchOne();
        }

        @Override
        public List<TeamMember> findActiveByTeam(int teamId) {
                return queryFactory
                                .selectFrom(qTeamMember)
                                .where(qTeamMember.team.id.eq(teamId)
                                                .and(qTeamMember.deleted.isFalse()))
                                .fetch();
        }

        @Override
        public boolean existsOwnerAdminInTeam(int userId, int teamId, Set<Role> roles) {
                Integer found = queryFactory
                                .selectOne()
                                .from(qTeamMember)
                                .where(
                                                qTeamMember.user.id.eq(userId)
                                                                .and(qTeamMember.team.id.eq(teamId))
                                                                .and(qTeamMember.role.in(roles))
                                                                .and(qTeamMember.deleted.isFalse()
                                                                                .or(qTeamMember.deleted.isFalse())))
                                .fetchFirst();
                return found != null;
        }

        @Override
        public List<TeamMemberRes> findActiveMemberDtosByTeamId(int teamId) {
                return query()
                                .select(Projections.constructor(
                                                TeamMemberRes.class,
                                                qTeamMember.user.fullName,
                                                qTeamMember.user.email,
                                                qTeamMember.user.avatarUrl,
                                                qTeamMember.role.stringValue(),
                                                qTeamMember.createdAt))
                                .from(qTeamMember)
                                .where(
                                                qTeamMember.team.id.eq(teamId),
                                                qTeamMember.deleted.isFalse())
                                .fetch();
        }

        
}
