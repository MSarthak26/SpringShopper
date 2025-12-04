package SpringBootEcom.service;

import SpringBootEcom.exceptions.APIException;
import SpringBootEcom.exceptions.ResourceNotFoundException;
import SpringBootEcom.model.Cart;
import SpringBootEcom.model.CartItem;
import SpringBootEcom.model.Category;
import SpringBootEcom.model.Product;
import SpringBootEcom.payload.CartDTO;
import SpringBootEcom.payload.ProductDTO;
import SpringBootEcom.payload.ProductResponse;
import SpringBootEcom.repository.CartRepository;
import SpringBootEcom.repository.CategoryRepository;
import SpringBootEcom.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","category_id",categoryId));

        boolean exists = false;

        Product product = modelMapper.map(productDTO,Product.class);

        Product check = productRepository.findByProductName(product.getProductName());
        if(check != null){
            throw new APIException("Product with " + check.getProductName() +" already exists");
        }
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice = product.getPrice() - (product.getDiscount()* 0.01 * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> products = productPage.getContent();
        if (products.isEmpty()){
            throw new APIException("No products added yet.");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements((long) productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }


    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category","category_id",categoryId));

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> productPageByCategory = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> productsByCategory = productPageByCategory.getContent();

        if(productsByCategory.isEmpty()){
            throw new APIException("No products matching your search exists.");
        }

        List<ProductDTO> productDTOSBYCategory = productsByCategory.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOSBYCategory);
        productResponse.setPageNumber(productPageByCategory.getNumber());
        productResponse.setPageSize(productPageByCategory.getSize());
        productResponse.setTotalElements((long) productPageByCategory.getTotalPages());
        productResponse.setLastPage(productPageByCategory.isLast());
        return productResponse;

    }

    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> productPageByKeyword = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);

        List<Product> productsByKeyword = productPageByKeyword.getContent();

        if(productsByKeyword.isEmpty()){
            throw new APIException("No products matching your search exists.");
        }
        List<ProductDTO> productDTOSBYCategory = productsByKeyword.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOSBYCategory);
        productResponse.setPageNumber(productPageByKeyword.getNumber());
        productResponse.setPageSize(productPageByKeyword.getSize());
        productResponse.setTotalElements((long) productPageByKeyword.getTotalPages());
        productResponse.setLastPage(productPageByKeyword.isLast());
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

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO>cartDTOS = carts.stream().map(cart->{
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
            List<ProductDTO> productDTOS = cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        cartDTOS.forEach(cartDTO -> cartService.updateProductsInCart(cartDTO.getCartId(),productId));

        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product deletedProduct = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product","product_id",productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(),productId));

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
