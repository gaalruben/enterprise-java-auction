package org.learning.auction.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
@Entity

public abstract class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    protected String name;
    protected String description;
    @Column(name="owner_name")
    protected String ownerName;

    @Enumerated(EnumType.STRING)
    protected Category category;

    @OneToOne(mappedBy = "property")
    protected AuctionItem auction_item;

    public Property(Long id, String name, String description, String ownerName, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerName = ownerName;
        this.category = category;
    }
}
