package com.ankitsaahariya.controller;


import com.ankitsaahariya.Service.AddressService;
import com.ankitsaahariya.dto.request.AddressRequest;
import com.ankitsaahariya.dto.response.AddressResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/add")
    public ResponseEntity<AddressResponse> addAddress(@Valid  @RequestBody  AddressRequest request){
        return  ResponseEntity.ok(addressService.addAddress(request));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ){
        return ResponseEntity.ok(addressService.updateAddress(id,request));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<MessageResponse> deleteAddress(@PathVariable Long id){
        return ResponseEntity.ok(addressService.deleteAddress(id));
    }
}
