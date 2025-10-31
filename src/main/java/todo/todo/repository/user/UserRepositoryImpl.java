package todo.todo.repository.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;

import todo.todo.dto.response.user.UserListRes;
import todo.todo.entity.user.QUser;
import todo.todo.entity.user.User;
import todo.todo.repository.BaseRepository;
import static todo.todo.util.Constants.PAGE_SIZE;

@Repository 
public class UserRepositoryImpl extends BaseRepository implements UserRepositoryCustom {
    private final QUser qUser = QUser.user;

    @Override
    public User loginByEmail(String email) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.email.eq(email));

        return query().from(qUser)
                .where(builder)
                .select(qUser)
                .fetchOne();
    }

    @Override
    public List<UserListRes> getUsers() {
        return query()
                .select(Projections.fields(UserListRes.class,
                        qUser.id,
                        qUser.email,
                        qUser.passwordHash,
                        qUser.fullName,
                        qUser.avatarUrl,
                        qUser.createdAt,
                        qUser.updatedAt,
                        qUser.deleted))
                .from(qUser)
                .offset(0)
                .limit(PAGE_SIZE)
                .orderBy(qUser.id.desc())
                .fetch();
    }

    @Override
    public User getUserByEmail(String email) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qUser.email.eq(email));
        builder.and(qUser.deleted.eq(false));

        return query().from(qUser)
                .where(builder)
                .select(qUser)
                .fetchOne();
    }

    @Override
    public boolean existsByCode(String code) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qUser.code.eq(code));
        builder.and(qUser.deleted.eq(false));

        return query().from(qUser)
                .where(builder)
                .select(qUser.id)
                .fetchFirst() != null;
    }
}
