package com.gabriel.SpringEcom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String orderId;
    private String customName;
    private String email;
    private String status;
    private LocalDate orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

//    O padrão do @ManyToOne é EAGER, se eu não tivesse colocado o fetch = FetchType.LAZY no Order,
//    toda vez que eu listasse 100 pedidos, o Hibernate poderia fazer 1 consulta para os pedidos + 100 consultas extras,
//    uma para cada usuário. Isso é o famoso e temido Problema das N+1 Consultas

}
