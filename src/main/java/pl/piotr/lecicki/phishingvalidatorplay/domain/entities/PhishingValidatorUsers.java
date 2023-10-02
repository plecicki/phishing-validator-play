package pl.piotr.lecicki.phishingvalidatorplay.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PhishingValidatorUsers {

    @Id
    @GeneratedValue
    @Column(unique = true, nullable = false)
    private Long Id;

    @Column(unique = true)
    private String phoneNumber;
}
