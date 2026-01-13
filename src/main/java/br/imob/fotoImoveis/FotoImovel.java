package br.imob.fotoImoveis;

import br.imob.imovel.model.Imoveis;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fotos_imovel")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FotoImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Usando Identity para bater com o SERIAL do seu SQL
    private Long id;

    private String urlArquivo;

    @ManyToOne
    @JoinColumn(name = "imovel_id")
    private Imoveis imovel;
}
