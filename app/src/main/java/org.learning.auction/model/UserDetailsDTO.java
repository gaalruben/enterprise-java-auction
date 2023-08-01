package org.learning.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserDetailsDTO {
    String fullName;
    String role;
    String balance;
}
