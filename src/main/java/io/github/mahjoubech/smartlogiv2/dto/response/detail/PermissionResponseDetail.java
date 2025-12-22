package io.github.mahjoubech.smartlogiv2.dto.response.detail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PermissionResponseDetail {
    private String id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , locale = "fr")
    private LocalDateTime dateCreation;
    private String createdBy;
}
