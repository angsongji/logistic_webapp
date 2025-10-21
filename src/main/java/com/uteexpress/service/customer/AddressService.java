package com.uteexpress.service.customer;

import com.uteexpress.dto.customer.AddressDto;
import com.uteexpress.entity.Address;
import com.uteexpress.entity.User;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddressesByUser(User user);
    AddressDto createAddress(User user, AddressDto addressDto);
    AddressDto updateAddress(Long addressId, AddressDto addressDto);
    void deleteAddress(Long addressId);
    AddressDto setDefaultAddress(User user, Long addressId);
    AddressDto getDefaultAddress(User user);
}
