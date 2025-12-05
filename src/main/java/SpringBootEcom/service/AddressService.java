package SpringBootEcom.service;

import SpringBootEcom.model.User;
import SpringBootEcom.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createNewAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
