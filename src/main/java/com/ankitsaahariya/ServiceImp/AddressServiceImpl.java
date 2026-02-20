package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.AddressService;
import com.ankitsaahariya.dao.AddressRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.dto.request.AddressRequest;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.entities.Address;
import com.ankitsaahariya.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    @Override
    public AddressResponse addAddress(AddressRequest request) {
        UserEntity user = getCurrentUser();
        Address address = Address.builder()
                .name(request.getName())
                .address(request.getAddress())
                .mobile(request.getMobile())
                .locality(request.getLocality())
                .city(request.getCity())
                .state(request.getState())
                .pinCode(request.getPinCode())
                .addressType(request.getAddressType())
                .user(user)
                .build();

            long addressCount = addressRepository.countByUserId(user.getId());
            if (addressCount == 0){
                address.setIsDefault(true);
            }else {
                address.setIsDefault(false);
            }

            Address savedAddress = addressRepository.save(address);

            return mapToResponse(savedAddress);
    }

    private AddressResponse mapToResponse(Address address) {
        AddressResponse response =
                AddressResponse.builder()
                        .id(address.getId())
                        .name(address.getName())
                        .mobile(address.getMobile())
                        .address(address.getAddress())
                        .addressType(address.getAddressType())
                        .city(address.getCity())
                        .state(address.getState())
                        .pinCode(address.getPinCode())
                        .isDefault(address.getIsDefault())
                        .locality(address.getLocality())
                        .build();

        if (address.getUser() != null) {
            response.setUserId(address.getUser().getId());
            response.setUserEmail(address.getUser().getEmail());
        }

        return response;
    }

    private UserEntity getCurrentUser() {
        String email  = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User Not Found With this Email : "+ email));
    }
}
