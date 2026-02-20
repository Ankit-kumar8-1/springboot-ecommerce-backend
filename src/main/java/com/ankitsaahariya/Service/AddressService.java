package com.ankitsaahariya.Service;

import com.ankitsaahariya.dto.request.AddressRequest;
import com.ankitsaahariya.dto.response.AddressResponse;

public interface AddressService {

    AddressResponse addAddress(AddressRequest request);

}
