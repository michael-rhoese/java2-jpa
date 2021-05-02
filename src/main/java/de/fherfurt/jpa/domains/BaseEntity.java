package de.fherfurt.jpa.domains;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 4732815183773272743L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
