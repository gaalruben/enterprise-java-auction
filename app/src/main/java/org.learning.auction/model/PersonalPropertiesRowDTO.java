package org.learning.auction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PersonalPropertiesRowDTO {
    String name;
    String category;
    String description;
    String ownerName;
    String weight;
    String sizeWidth;
    String sizeHeight;
    String sizeDepth;

}
