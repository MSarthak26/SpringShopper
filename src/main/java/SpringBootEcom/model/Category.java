package SpringBootEcom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    private Long categoryId;

    @NotBlank(message = "Category name cannot be blank.")
    @Size(min = 5,message = "Category name must contain atleast 5 characters")
    private String categoryName;

}
