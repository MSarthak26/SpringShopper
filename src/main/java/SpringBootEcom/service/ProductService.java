package SpringBootEcom.service;

import SpringBootEcom.model.Product;
import SpringBootEcom.payload.ProductDTO;

public interface ProductService {
    ProductDTO addProduct(Product product, Long categoryId);
}
