package todo.todo.repository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import todo.todo.entity.upload_file.UploadFile;

@Repository
public interface MediaRepository extends JpaRepository<UploadFile, Integer> {
    UploadFile findUploadFileById(int id);
}