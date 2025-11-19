package todo.todo.repository.otp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;

import todo.todo.dto.constant.VerifyStatus;
import todo.todo.dto.request.otp.SendOtpReq;
import todo.todo.entity.otp.QOtp;
import todo.todo.repository.BaseRepository;

@Repository
public class OtpRepositoryImpl extends BaseRepository implements OtpRepositoryCustom {
    private final QOtp qOtp = QOtp.otp1;

    @Override
    @Transactional
    public void updateStatusVerify(SendOtpReq request) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qOtp.status.eq(VerifyStatus.VERIFY_PENDING));
        if (request.getEmail() != null) {
            builder.and(qOtp.email.eq(request.getEmail()));
        }

        query().update(qOtp)
                .where(builder)
                .set(qOtp.status, VerifyStatus.OTHER)
                .execute();

    }

    @Override
    public int findByEmail(String email) {
        Integer result = query()
                .select(qOtp.attemptCount)
                .from(qOtp)
                .where(
                        qOtp.email.eq(email)
                                .and(qOtp.deleted.isFalse()))
                .orderBy(qOtp.createdAt.desc()) 
                .fetchFirst();

        return result == null ? 0 : result;
    }

}
