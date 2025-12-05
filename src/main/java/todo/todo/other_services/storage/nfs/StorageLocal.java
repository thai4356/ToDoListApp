package todo.todo.other_services.storage.nfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;

import lombok.extern.log4j.Log4j2;
import todo.todo.exceptions.BusinessException;
import todo.todo.other_services.storage.StorageConfig;
import todo.todo.other_services.storage.StorageResource;

@Log4j2
public class StorageLocal implements StorageResource {

    private final StorageNfsConfig config;

    public StorageLocal(StorageConfig config) {
        this.config = (StorageNfsConfig)config;
    }

    @Override
    public InputStream readResource(String path) {
        path = fixPath(path);
        String src = String.format("%s/%s", config.getDirectory(), path);
        InputStream in;
        try {
            in = new FileInputStream(src);
            return in;
        } catch (FileNotFoundException e) {
            log.error("File not found : {}", path);
            throw new BusinessException("File not found!", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public String writeResource(InputStream inputStream, String path) {
        path = fixPath(path);
        String src = String.format("%s/%s", config.getDirectory(), path);
        File file = new File(src);
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            log.error("Write file error : {}", src);
            throw new BusinessException("Write file error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return String.format("%s%s", config.getServerUrl(), path);
    }

    @Override
    public boolean deleteFile(String file) {
        file = fixPath(file);
        String src = String.format("%s/%s", config.getDirectory(), file);
        try {
            Path path;
            path = Paths.get(src);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.info("Can not remove temporary files");
            return false;
        }
        return true;
    }

    @Override
    public String getUrl(String file) {
        return config.getServerUrl() + file;
    }

    private String fixPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

}
