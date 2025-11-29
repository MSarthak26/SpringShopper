package SpringBootEcom.service;

import SpringBootEcom.payload.ProductDTO;
import SpringBootEcom.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(ProductDTO product, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchByKeyword(String keyword);

    ProductDTO updateProduct(ProductDTO product, Long productId);

    ProductDTO deleteProduct(Long productId);
}
