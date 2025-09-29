package com.example.appleshop.service;



import com.example.appleshop.entity.Address;
import com.example.appleshop.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAddressesByUser(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Address getAddress(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    public Address updateAddress(Long id, Address newAddress) {
        return addressRepository.findById(id).map(addr -> {
            addr.setRecipientName(newAddress.getRecipientName());
            addr.setPhone(newAddress.getPhone());
            addr.setStreet(newAddress.getStreet());
            addr.setCity(newAddress.getCity());
            addr.setState(newAddress.getState());
            addr.setPostalCode(newAddress.getPostalCode());
            addr.setCountry(newAddress.getCountry());
            addr.setIsDefault(newAddress.getIsDefault());
            return addressRepository.save(addr);
        }).orElse(null);
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
