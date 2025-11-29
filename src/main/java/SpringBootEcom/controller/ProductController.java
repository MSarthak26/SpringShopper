package SpringBootEcom.controller;

import SpringBootEcom.model.Product;
import SpringBootEcom.payload.ProductDTO;
import SpringBootEcom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;
    @PostMapping("/api/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody Product product, @PathVariable Long categoryId){
        ProductDTO productDTO = productService.addProduct(product,categoryId);
        return new ResponseEntity<ProductDTO>(productDTO,HttpStatus.CREATED);
    }
}
