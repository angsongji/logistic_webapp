package com.uteexpress.dto.customer;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String postalCode;
    private Boolean isDefault;
}
