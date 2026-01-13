package br.imob.imovel.model;

import br.imob.enderecos.model.Enderecos;
import br.imob.fotoImoveis.FotoImovel;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "imoveis")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Imoveis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;
    @Enumerated(EnumType.STRING)
    private Status status;
    private BigDecimal valor;
    private BigDecimal areaTotal;
    private BigDecimal areaConstruida;
    private int quartos;
    private int banheiros;
    private int vagasGaragem;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Enderecos endereco;
    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL)
    private List<FotoImovel> fotos;
}
