package org.learning.auction.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "real_property")
public class RealProperty extends Property{
    private String location;

    @Column(name = "area_size")
    private double areaSize;

    @Builder
    public RealProperty(Long id,
                            String name,
                            String description,
                            String ownerName,
                            Category category,
                            String location,
                            double areaSize) {
        super(id, name, description, ownerName, category);
        this.location = location;
        this.areaSize = areaSize;
    }
}
