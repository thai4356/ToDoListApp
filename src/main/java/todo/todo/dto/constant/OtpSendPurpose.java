package todo.todo.dto.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mục đích gửi OTP: 0 - PASSWORD_RESET, 1 - REGISTRATION", type = "integer")
public enum OtpSendPurpose implements BaseEnum<Integer> {
    PASSWORD_RESET(0),
    REGISTRATION(1);

    final int value;

    OtpSendPurpose(int value) {
        this.value = value;
    }

    @JsonCreator
    public static OtpSendPurpose fromValue(int value) {
        for (OtpSendPurpose column : values()) {
            if (column.toValue() == value)
                return column;
        }
        return null;
    }

    @JsonValue
    public Integer toValue() {
        return value;
    }
}