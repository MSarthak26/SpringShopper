package SpringBootEcom.repository;

import SpringBootEcom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
      //<Which table/class , data type of primary Key> this provides CRUD implementation automatically at runtime

    Category findByCategoryName(String categoryName);

}
