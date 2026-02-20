package com.ankitsaahariya.ServiceImp;

import com.ankitsaahariya.Exception.BadRequestException;
import com.ankitsaahariya.Exception.ResourceNotFoundException;
import com.ankitsaahariya.Exception.UserNotFoundException;
import com.ankitsaahariya.Service.AddressService;
import com.ankitsaahariya.dao.AddressRepository;
import com.ankitsaahariya.dao.UserRepository;
import com.ankitsaahariya.dto.request.AddressRequest;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.Address;
import com.ankitsaahariya.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    @Transactional
    @Override
    public AddressResponse updateAddress(Long addressId, AddressRequest request) {
        UserEntity user =  getCurrentUser();

        Address address = addressRepository.findByIdAndUserId(addressId,user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Address not found or you don't have permission"));

        address.setName(request.getName());
        address.setMobile(request.getMobile());
        address.setAddress(request.getAddress());
        address.setLocality(request.getLocality());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPinCode(request.getPinCode());
        address.setAddressType(request.getAddressType());

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    @Transactional
    @Override
    public MessageResponse deleteAddress(Long addressId) {
        UserEntity user = getCurrentUser();

        Address address = addressRepository.findByIdAndUserId(addressId,user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Address not found or you don't have permission"));

        long addressCount = addressRepository.countByUserId(user.getId());
        if (addressCount<= 1){
            throw new BadRequestException("Cannot delete the only address. Please add another address first.");
        }

        boolean wasDefault = address.getIsDefault();
        addressRepository.delete(address);

        if (wasDefault){
            List<Address> remainingAddresses = addressRepository.findByUserId(user.getId());
            Address newDefault = remainingAddresses.get(0);
            newDefault.setIsDefault(true);
            addressRepository.save(newDefault);
        }

        return new MessageResponse("Address deleted Successfully !");
    }

    @Override
    public List<AddressResponse> getUserAddresses() {
        UserEntity user = getCurrentUser();
        List<Address> addresses = addressRepository.findByUserId(user.getId());

        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AddressResponse setDefaultAddress(Long addressId) {
        UserEntity user = getCurrentUser();

        Address address  =  addressRepository.findByIdAndUserId(addressId,user.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Address not found or you don't have permission "));

        addressRepository.removeDefaultForUser(user.getId());

        // Set this address as default
        address.setIsDefault(true);
        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    @Override
    public AddressResponse getDefaultAddress() {
        UserEntity user = getCurrentUser();
        Optional<Address> defaultAddress = addressRepository.findByUserIdAndIsDefaultTrue(user.getId());

        if (defaultAddress.isEmpty()) {
            // If no default, return first address
            List<Address> addresses = addressRepository.findByUserId(user.getId());
            if (addresses.isEmpty()) {
                throw new RuntimeException("No addresses found. Please add an address first.");
            }
            return mapToResponse(addresses.get(0));
        }

        return mapToResponse(defaultAddress.get());
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
