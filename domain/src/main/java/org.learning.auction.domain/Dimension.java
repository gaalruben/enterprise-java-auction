package org.learning.auction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Embeddable
public class Dimension {

    private double width;
    private double height;
    private double depth;

}
