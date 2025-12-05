package todo.todo.other_services.storage.cloudinary;

import java.io.InputStream;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.extern.log4j.Log4j2;
import todo.todo.other_services.storage.StorageResource;

@Log4j2
public class StorageCloudinary implements StorageResource {

    private final Cloudinary cloudinary;

    public StorageCloudinary(StorageCloudinaryConfig config) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", config.getCloudName(),
                "api_key", config.getApiKey(),
                "api_secret", config.getApiSecret()
        ));
    }

    @Override
    public InputStream readResource(String path) {
        throw new UnsupportedOperationException("Cloudinary does not support reading files as InputStream directly");
    }

    @Override
    public String writeResource(InputStream inputStream, String path) {
        try {
            // Convert InputStream to byte[]
            byte[] bytes = inputStream.readAllBytes(); // Java 9+
            Map uploadResult = cloudinary.uploader().upload(
                    bytes,
                    ObjectUtils.asMap(
                            "public_id", path,
                            "resource_type", "image",
                            "overwrite", true
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            log.error("Failed to upload to Cloudinary", e);
            throw new RuntimeException("Upload failed");
        }
    }




    @Override
    public boolean deleteFile(String file) {
        try {
            Map result = cloudinary.uploader().destroy(file, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary", e);
            return false;
        }
    }

    @Override
    public String getUrl(String file) {
        return cloudinary.url().secure(true).generate(file);
    }

    public String getPublicUrl(String path) {
        return cloudinary.url().generate(path); // hoặc tự format URL nếu bạn dùng public_id
    }
}