package todo.todo.other_services.storage.nfs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todo.todo.other_services.storage.StorageConfig;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StorageNfsConfig implements StorageConfig {
    private String directory;
    private String serverUrl;
}
