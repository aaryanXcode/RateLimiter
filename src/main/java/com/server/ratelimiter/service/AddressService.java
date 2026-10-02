package com.server.ratelimiter.service;

import com.server.ratelimiter.domain.AddressDTO;
import com.server.ratelimiter.entity.AddressEntity;
import com.server.ratelimiter.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    public AddressService(AddressRepository addressRepository){
        this.addressRepository = addressRepository;
    }

    public List<AddressDTO> getAllAddress(){
        List<AddressEntity> addressEntityList = addressRepository.findAll();
        return addressEntityList.stream().map(a->toDTO(a)).toList();
    }

    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO) {
        AddressEntity addressEntity = new AddressEntity(
                addressDTO.addressType(),
                addressDTO.city(),
                addressDTO.street(),
                addressDTO.isDefault()
        );
        AddressEntity savedAddress = addressRepository.save(addressEntity);
        return toDTO(savedAddress);
    }

    private AddressDTO toDTO(AddressEntity entity) {
        return new AddressDTO(
                entity.getAddressType(),
                entity.getStreet(),
                entity.getCity(),
                entity.isDefault()
        );
    }
}
