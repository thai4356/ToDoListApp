package todo.todo.other_services.storage.s3;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class StorageS3Helper {
    public String uploadFile(S3Client s3Client, InputStream inputStream, String path, String bucket) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, getContentLength(inputStream)));
        } catch (IOException e) {
            log.error("uploadFile", e);
        }
        return path;
    }

    public InputStream readFile(S3Client s3Client, String path, String bucket) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
        try (ResponseInputStream<GetObjectResponse> s3InputStream = s3Client.getObject(getObjectRequest)) {
            return s3InputStream;
        } catch (IOException e) {
            log.error("readFile", e);
            return null;
        }
    }

    public boolean deleteFile(S3Client s3Client, String path, String bucket) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    public static long getContentLength(InputStream inputStream) throws IOException {
        long length = 0;
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            length += bytesRead;
        }
        return length;
    }
}
