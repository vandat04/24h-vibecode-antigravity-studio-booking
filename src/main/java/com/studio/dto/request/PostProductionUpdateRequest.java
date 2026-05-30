package com.studio.dto.request;

import com.studio.constant.ProductionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostProductionUpdateRequest {

    @NotNull(message = "Trạng thái hậu kỳ không được trống")
    private ProductionStatus productionStatus;

    private String rawPhotoLink;
    
    private String editedPhotoLink;

    private String note;
}
