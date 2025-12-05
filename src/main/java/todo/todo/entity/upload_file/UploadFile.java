package todo.todo.entity.upload_file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import todo.todo.entity.BaseEntity;
import todo.todo.entity.upload_file.constant.UploadFileType;
import todo.todo.entity.upload_file.constant.UploadFileTypeConverter;
import todo.todo.entity.user.User;

@Getter
@Setter
@Entity
@Table(name = "upload_files")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadFile extends BaseEntity {

    private String originFilePath;
    private String thumbFilePath;
    private String originUrl;
    private String thumbUrl;
    @Convert(converter = UploadFileTypeConverter.class)
    private UploadFileType type;
    private Integer width;
    private Integer height;
    private Integer duration;
    private Long size;

    @ManyToOne
    @JoinColumn(name = "user_id") // FK
    @JsonIgnore
    private User user;
}