package org.learning.auction.domain;

import lombok.*;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@ToString(callSuper = true)
@Entity

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "personal_property")
public class PersonalProperty extends Property {

    private double weight;

    @Embedded
    private Dimension size;

    @Builder
    public PersonalProperty(Long id,
                            String name,
                            String description,
                            String ownerName,
                            Category category,
                            double weight,
                            Dimension size) {
        super(id, name, description, ownerName, category);
        this.weight = weight;
        this.size = size;
    }
}
