package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.AddressRequest;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.dto.response.MessageResponse;

import java.util.List;

public interface AddressService {

    AddressResponse addAddress(AddressRequest request);

    AddressResponse updateAddress(Long addressId,AddressRequest request);

    MessageResponse deleteAddress(Long addressId);

    List<AddressResponse> getUserAddresses();

    AddressResponse setDefaultAddress(Long addressId);

}
