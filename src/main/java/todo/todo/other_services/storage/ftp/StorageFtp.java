package todo.todo.other_services.storage.ftp;

import todo.todo.other_services.storage.StorageConfig;
import todo.todo.other_services.storage.StorageResource;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
@NoArgsConstructor
public class StorageFtp implements StorageResource {
    private StorageFtpConfig ftpStorageConfig;

    private StorageFtpHelper ftpStorageService;

    public StorageFtp(StorageConfig ftpStorageConfig) {
        log.info("configure: {}", ftpStorageConfig);
        this.ftpStorageConfig = (StorageFtpConfig) ftpStorageConfig;
        this.ftpStorageService = new StorageFtpHelper();
    }

    private FTPClient openConnection() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(ftpStorageConfig.getServer(), ftpStorageConfig.getPort());
            ftpClient.login(ftpStorageConfig.getUsername(), ftpStorageConfig.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            log.info("replyCode: {}", reply);
            if (ftpClient.isConnected()) {
                log.info("FTP connected");
            } else {
                throw new RuntimeException("The FTP can not connect");
            }
            return ftpClient;
        } catch (IOException e) {
            log.error("openConnection error:", e);
            throw new RuntimeException(e);
        }
    }

    private void closeFTP(FTPClient ftpClient) {
        if (ftpClient == null) return;
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            log.warn("Cause:", ex);
        }
    }

    @Override
    public InputStream readResource(String path) {
        FTPClient ftpClient = openConnection();
        try {
            log.info("FTP download = {}", path);
            return ftpStorageService.downloadFile(ftpClient, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeFTP(ftpClient);
        }
    }

    @Override
    public String writeResource(InputStream inputStream, String path) {
        FTPClient ftpClient = openConnection();
        try {
            return ftpStorageService.uploadFtp(ftpClient, inputStream, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeFTP(ftpClient);
        }
    }

    @Override
    public boolean deleteFile(String file) {
        FTPClient ftpClient = openConnection();
        try {
            log.info("FTP delete file = {}", file);
            return ftpStorageService.deleteFile(ftpClient, file);
        } catch (Exception e) {
            log.error("FTP delete cause: ", e);
            throw new RuntimeException(e);
        } finally {
            closeFTP(ftpClient);
        }
    }

    @Override
    public String getUrl(String file) {
        return null;
    }
}
