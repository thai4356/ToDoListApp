package todo.todo.entity.upload_file.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UploadFileTypeConverter implements AttributeConverter<UploadFileType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UploadFileType attribute) {
        return attribute != null ? attribute.toValue() : null;
    }

    @Override
    public UploadFileType convertToEntityAttribute(Integer dbData) {
        return dbData != null ? UploadFileType.fromValue(dbData) : null;
    }
}
