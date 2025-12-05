package todo.todo.other_services.storage.ftp;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.ftp.FTPClient;

import java.io.InputStream;

@Log4j2
public class StorageFtpHelper {

    public String uploadFtp(FTPClient ftpClient, InputStream inputStream, String pathFTP) throws Exception {
        log.info("begin connect to FTP server");
        long begin = System.currentTimeMillis();
        if (pathFTP.contains("/")) {
            String folder = pathFTP.substring(0, pathFTP.indexOf('/'));
            String subFolder = pathFTP.substring(0, pathFTP.lastIndexOf('/'));
            ftpClient.makeDirectory(folder);
            ftpClient.makeDirectory(subFolder);
        }
        log.info("begin upload file to FTP {}", begin);
        boolean done = ftpClient.storeFile(pathFTP, inputStream);
        long end = System.currentTimeMillis();
        log.info("end upload file to FTP {} duration = {}", end, (end - begin));
        return pathFTP;
    }

    public InputStream downloadFile(FTPClient ftpClient, String file) throws Exception {
        InputStream inputStream = ftpClient.retrieveFileStream(file);
        return inputStream;
    }

    public boolean deleteFile(FTPClient ftpClient, String file) throws Exception {
        boolean deleteFile = ftpClient.deleteFile(file);
        return deleteFile;
    }
}
