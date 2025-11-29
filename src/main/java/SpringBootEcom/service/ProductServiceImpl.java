package SpringBootEcom.service;

import SpringBootEcom.exceptions.APIException;
import SpringBootEcom.exceptions.ResourceNotFoundException;
import SpringBootEcom.model.Category;
import SpringBootEcom.model.Product;
import SpringBootEcom.payload.ProductDTO;
import SpringBootEcom.payload.ProductResponse;
import SpringBootEcom.repository.CategoryRepository;
import SpringBootEcom.repository.ProductRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","category_id",categoryId));

        Product product = modelMapper.map(productDTO,Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice = product.getPrice() - (product.getDiscount()* 0.01 * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()){
            throw new APIException("No products added yet.");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }


    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","category_id",categoryId));
        List<Product> productsByCategory = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOSBYCategory = productsByCategory.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOSBYCategory);
        return productResponse;

    }

    @Override
    public ProductResponse searchByKeyword(String keyword) {
        List<Product> productsByKeyword = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
        List<ProductDTO> productDTOSBYCategory = productsByKeyword.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOSBYCategory);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product existingProduct = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product","product_id",productId));

        Product newProduct = modelMapper.map(productDTO,Product.class);

        existingProduct.setProductName(newProduct.getProductName());
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setQuantity(newProduct.getQuantity());
        existingProduct.setDiscount(newProduct.getDiscount());
        existingProduct.setDescription(newProduct.getDescription());

        double specialPrice = existingProduct.getPrice() - (existingProduct.getDiscount()* 0.01 * existingProduct.getPrice());
        existingProduct.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(existingProduct);

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product deletedProduct = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product","product_id",productId));
        productRepository.delete(deletedProduct);
        return modelMapper.map(deletedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product existingProduct = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product","product_id",productId));

        String fileName = fileService.uploadImage(path,image);

        existingProduct.setImage(fileName);

        Product savedProduct = productRepository.save(existingProduct);

        return modelMapper.map(savedProduct,ProductDTO.class);


    }


}
