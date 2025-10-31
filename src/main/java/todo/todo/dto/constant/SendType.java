package todo.todo.dto.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hình thức gửi tin: 0 - ZNS, 1 - EMAIL, 2 - SMS")
public enum SendType implements BaseEnum<Integer> {
    ZNS(0),
    EMAIL(1),
    SMS(2);

    final int value;

    SendType(int value) {
        this.value = value;
    }

    @JsonCreator
    public static SendType fromValue(int value) {
        for (SendType column : SendType.values()) {
            if (column.value == value) {
                return column;
            }
        }
        return null;
    }

    @JsonValue
    public Integer toValue() {
        return value;
    }
}