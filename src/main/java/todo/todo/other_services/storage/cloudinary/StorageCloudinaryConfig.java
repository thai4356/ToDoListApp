package todo.todo.other_services.storage.cloudinary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import todo.todo.other_services.storage.StorageConfig;

// @Getter
// @AllArgsConstructor
// @Configuration
// public class StorageCloudinaryConfig implements StorageConfig {
//     private String cloudName;
//     private String apiKey;
//     private String apiSecret;

//     @Override
//     public String toString() {
//         return "StorageCloudinaryConfig{" +
//                 "cloudName='" + cloudName + '\'' +
//                 ", apiKey='" + apiKey + '\'' +
//                 ", apiSecret='" + apiSecret + '\'' +
//                 '}';
//     }
// }

@AllArgsConstructor
@Getter
public class StorageCloudinaryConfig implements StorageConfig {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
}
