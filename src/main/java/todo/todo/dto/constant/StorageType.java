package todo.todo.dto.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StorageType {
    STORAGE_NFS_LOCAL,
    STORAGE_FTP,
    STORAGE_S3,
    STORAGE_AZURE,
    STORAGE_GCP,
    STORAGE_CLOUDINARY;

    public static StorageType getType(int index) {
        return StorageType.values()[index];
    }
    public static StorageType getType(String name) {
        return StorageType.valueOf(name);
    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }

}