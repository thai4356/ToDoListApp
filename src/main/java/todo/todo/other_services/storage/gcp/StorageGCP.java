package todo.todo.other_services.storage.gcp;

import java.io.InputStream;

import lombok.extern.log4j.Log4j2;
import todo.todo.other_services.storage.StorageResource;

@Log4j2
public class StorageGCP implements StorageResource {
    @Override
    public InputStream readResource(String path) {
        return null;
    }

    @Override
    public String writeResource(InputStream inputStream, String path) {
        return null;
    }

    @Override
    public boolean deleteFile(String file) {
        return false;
    }

    @Override
    public String getUrl(String file) {
        return null;
    }
}
