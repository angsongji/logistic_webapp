package com.uteexpress.controller.api;

import com.uteexpress.dto.customer.AddressDto;
import com.uteexpress.entity.User;
import com.uteexpress.service.customer.AddressService;
import com.uteexpress.service.customer.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
public class AddressRestController {

    private final AddressService addressService;
    private final CustomerService customerService;

    public AddressRestController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<AddressDto>> getAddresses(Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        List<AddressDto> addresses = addressService.getAddressesByUser(user);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<AddressDto> createAddress(@RequestBody AddressDto addressDto, Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        AddressDto created = addressService.createAddress(user, addressDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long id, @RequestBody AddressDto addressDto) {
        AddressDto updated = addressService.updateAddress(id, addressDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/set-default")
    public ResponseEntity<AddressDto> setDefaultAddress(@PathVariable Long id, Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        AddressDto updated = addressService.setDefaultAddress(user, id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/default")
    public ResponseEntity<AddressDto> getDefaultAddress(Principal principal) {
        User user = customerService.getByUsername(principal.getName());
        AddressDto defaultAddress = addressService.getDefaultAddress(user);
        return ResponseEntity.ok(defaultAddress);
    }
}
