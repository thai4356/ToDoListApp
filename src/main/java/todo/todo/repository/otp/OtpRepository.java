package todo.todo.repository.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import todo.todo.entity.otp.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer>, OtpRepositoryCustom{
    
}
