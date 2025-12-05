package todo.todo.service.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import todo.todo.entity.upload_file.UploadFile;
import todo.todo.entity.upload_file.constant.UploadFileType;
import todo.todo.exceptions.BusinessException;
import todo.todo.other_services.storage.StorageResource;
import todo.todo.repository.media.MediaRepository;
import todo.todo.service.BaseService;
import todo.todo.util.Constants;
import todo.todo.util.Util;

@Service
class FileStorageServiceImpl extends BaseService implements FileStorageService {

    @Autowired
    private StorageResource storageResource;

    @Autowired
    private MediaRepository mediaRepository;

    public UploadFile storeImage(final MultipartFile file) {
        String timeStamp = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_mm_SSS).format(new Date());
        String randomString = RandomStringUtils.random(6, Constants.ALPHA_NUM);
        String fileName = Util.removeCharacterVn(StringUtils.cleanPath(file.getOriginalFilename().toLowerCase()));
        String originalName = timeStamp + "_" + randomString + "_" + fileName;
        String thumbName = timeStamp + "_" + randomString + "_thumb_" + fileName;
        if (originalName.contains("..")) {
            throw new BusinessException("Sorry! Filename contains invalid path sequence " + originalName);
        }
        String type = file.getContentType();
        if ((type == null || !type.toLowerCase().startsWith("image")) && !originalName.endsWith("jpg")
                && !originalName.endsWith("jpeg") && !originalName.endsWith("png")) {
            throw new BusinessException("File format error");
        }
        try {
            BufferedImage bimg = ImageIO.read(file.getInputStream());
            UploadFile image = new UploadFile();
            if (bimg != null) {
                image.setWidth(bimg.getWidth());
                image.setHeight(bimg.getHeight());
            }
            image.setType(UploadFileType.IMAGE);
            image.setSize(file.getSize());
            image.setOriginFilePath(String.format("image/%s", originalName));
            ByteArrayOutputStream thumbOutputStream = createThumbnail(file, type, fileName);
            image.setOriginUrl(storageResource.writeResource(file.getInputStream(), "image/" + originalName));
            if (thumbOutputStream != null) {
                try (InputStream inputStream = new ByteArrayInputStream(thumbOutputStream.toByteArray())) {
                    image.setThumbUrl(storageResource.writeResource(inputStream, "image/" + thumbName));
                    image.setThumbFilePath(String.format("image/%s", thumbName));
                }
            } else {
                image.setThumbUrl(image.getOriginUrl());
            }
            image = mediaRepository.save(image);
            return image;
        } catch (IOException exception) {
            throw new BusinessException(exception.getMessage());
        }

    }

    @Override
    public void deleteFile(int fileId) {
        UploadFile uploadFile = mediaRepository.findUploadFileById(fileId);
        if (uploadFile != null) {
            String fileName = uploadFile.getOriginFilePath();
            String thumbName = uploadFile.getThumbFilePath();
            if (StringUtils.hasText(fileName)) {
                storageResource.deleteFile(fileName);
            }
            if (StringUtils.hasText(thumbName)) {
                storageResource.deleteFile(thumbName);
            }
            mediaRepository.delete(uploadFile);
        }
    }

    public InputStream getInputStream(final String fileName) {
        return storageResource.readResource(fileName);
    }

    private ByteArrayOutputStream createThumbnail(final MultipartFile originalFile, String contentType,
            String fileName) {
        try {
            String formatType;
            if ((contentType != null && contentType.contains("png")) || fileName.contains("png")) {
                formatType = "png";
            } else {
                formatType = "jpeg";
            }
            ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
            BufferedImage img = ImageIO.read(originalFile.getInputStream());
            BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC,
                    Math.min(img.getWidth(), 1000), Scalr.OP_ANTIALIAS);
            ImageIO.write(thumbImg, formatType, thumbOutput);
            return thumbOutput;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}