package SpringBootEcom.service;

import SpringBootEcom.exceptions.APIException;
import SpringBootEcom.exceptions.ResourceNotFoundException;
import SpringBootEcom.model.Address;
import SpringBootEcom.model.User;
import SpringBootEcom.payload.AddressDTO;
import SpringBootEcom.repository.AddressRepository;
import SpringBootEcom.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createNewAddress(AddressDTO addressDTO, User user) {

        Address address = modelMapper.map(addressDTO,Address.class);
        List<Address> userAddresses = user.getAddresses();
        userAddresses.add(address);
        user.setAddresses(userAddresses);

        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        if(addresses.isEmpty()){
            throw new APIException("No address added yet!!!.");
        }

        List<AddressDTO> addressDTOS = addresses.stream().map(add -> modelMapper.map(add,AddressDTO.class)).toList();

        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addressList = user.getAddresses();
        if(addressList.isEmpty()){
            throw new APIException("User has no saved address.");
        }
        List<AddressDTO> addressDTOS = addressList.stream().map(add -> modelMapper.map(add,AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCountry(addressDTO.getCountry());
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPincode(addressDTO.getPincode());

        Address updatedAddress = addressRepository.save(address);

        User user = address.getUser();
        List<Address> userAddresses= user.getAddresses();

        userAddresses.removeIf(add->add.getAddressId().equals(addressId));
        userAddresses.add(updatedAddress);

        userRepository.save(user);

        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address","addressId",addressId));

        addressRepository.delete(address);

        User user = address.getUser();
        List<Address> addresses = user.getAddresses();
        addresses.removeIf(add->add.getAddressId().equals(addressId));
        userRepository.save(user);

        return "Address Deleted Successfully";
    }
}
