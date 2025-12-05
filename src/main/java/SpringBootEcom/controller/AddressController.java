package SpringBootEcom.controller;

import SpringBootEcom.Util.AuthUtil;
import SpringBootEcom.model.Address;
import SpringBootEcom.model.User;
import SpringBootEcom.payload.AddressDTO;
import SpringBootEcom.service.AddressService;
import SpringBootEcom.service.AddressServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addNewAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        AddressDTO newAddress = addressService.createNewAddress(addressDTO,user);
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO>addressDTOs = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOs,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(){
        User user = authUtil.loggedInUser();
        List<AddressDTO>addressDTOs = addressService.getUserAddresses(user);
        return new ResponseEntity<>(addressDTOs,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,@RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressId,addressDTO);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
