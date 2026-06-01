package com.Esteban.cinema.Model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras_productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Users usuario;

    @Column(name = "fecha", nullable = false)
    private Timestamp fecha;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "codigo_qr", columnDefinition = "text")
    private String codigoQr;

    @OneToMany(mappedBy = "compraProducto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompraProductoItem> items = new ArrayList<>();
}
