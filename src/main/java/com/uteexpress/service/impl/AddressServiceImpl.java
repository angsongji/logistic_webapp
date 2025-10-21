package com.uteexpress.service.impl;

import com.uteexpress.dto.customer.AddressDto;
import com.uteexpress.entity.Address;
import com.uteexpress.entity.User;
import com.uteexpress.repository.AddressRepository;
import com.uteexpress.service.customer.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDto> getAddressesByUser(User user) {
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDto createAddress(User user, AddressDto addressDto) {
        // If this is set as default, unset other default addresses
        if (addressDto.getIsDefault() != null && addressDto.getIsDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .name(addressDto.getName())
                .phone(addressDto.getPhone())
                .address(addressDto.getAddress())
                .city(addressDto.getCity())
                .district(addressDto.getDistrict())
                .ward(addressDto.getWard())
                .postalCode(addressDto.getPostalCode())
                .isDefault(addressDto.getIsDefault() != null ? addressDto.getIsDefault() : false)
                .user(user)
                .build();

        Address saved = addressRepository.save(address);
        return toDto(saved);
    }

    @Override
    @Transactional
    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // If this is set as default, unset other default addresses
        if (addressDto.getIsDefault() != null && addressDto.getIsDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(address.getUser())
                    .ifPresent(addr -> {
                        if (!addr.getId().equals(addressId)) {
                            addr.setIsDefault(false);
                            addressRepository.save(addr);
                        }
                    });
        }

        address.setName(addressDto.getName());
        address.setPhone(addressDto.getPhone());
        address.setAddress(addressDto.getAddress());
        address.setCity(addressDto.getCity());
        address.setDistrict(addressDto.getDistrict());
        address.setWard(addressDto.getWard());
        address.setPostalCode(addressDto.getPostalCode());
        if (addressDto.getIsDefault() != null) {
            address.setIsDefault(addressDto.getIsDefault());
        }

        Address saved = addressRepository.save(address);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    @Transactional
    public AddressDto setDefaultAddress(User user, Long addressId) {
        // Unset current default
        addressRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(addr -> {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                });

        // Set new default
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setIsDefault(true);
        Address saved = addressRepository.save(address);
        return toDto(saved);
    }

    @Override
    public AddressDto getDefaultAddress(User user) {
        return addressRepository.findByUserAndIsDefaultTrue(user)
                .map(this::toDto)
                .orElse(null);
    }

    private AddressDto toDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .name(address.getName())
                .phone(address.getPhone())
                .address(address.getAddress())
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .postalCode(address.getPostalCode())
                .isDefault(address.getIsDefault())
                .build();
    }
}
