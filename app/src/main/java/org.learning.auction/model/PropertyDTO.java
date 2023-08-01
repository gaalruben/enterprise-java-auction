package org.learning.auction.model;

import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class PropertyDTO {
    boolean isPersonal;

    @NotEmpty
    String name;
    String category;
    @NotEmpty
    String description;
    @NotEmpty
    String ownerName;

    String location;

    int areaSize;
    int weight;
    int sizeWidth;
    int sizeHeight;
    int sizeDepth;

    public boolean isPersonal() {
        return ((getLocation() == null || getLocation().isEmpty() || getLocation().isBlank()) && getAreaSize() == 0);
    }

    @AssertTrue(message = "Weight and Size values must be greater than 0")
    public boolean isPersonalPropertyValid(){
        if(!isPersonal())
            return true;
        return weight > 0 &&
                sizeWidth > 0 &&
                sizeHeight >0 &&
                sizeDepth > 0;
    }

    @AssertTrue(message = "Location must not be empty and Area Size must be greater than 0")
    public boolean isRealPropertyValid(){
        if(isPersonal())
            return true;
        return !location.isEmpty() &&
                areaSize > 0;
    }
}
