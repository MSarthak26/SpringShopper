package SpringBootEcom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Order order;

    @Size(min = 4 , message = "Payment method must contain atleast 4 characters.")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgPaymentStatus;
    private String pgMessageResponse;
    private String pgName;

    public Payment(String paymentMethod, String pgPaymentId, String pgPaymentStatus, String pgMessageResponse, String pgName) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgPaymentStatus = pgPaymentStatus;
        this.pgMessageResponse = pgMessageResponse;
        this.pgName = pgName;
    }
}
