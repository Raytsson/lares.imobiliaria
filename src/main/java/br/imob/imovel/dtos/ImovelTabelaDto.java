package br.imob.imovel.dtos;

import br.imob.imovel.enums.Cidades;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;
import br.imob.imovel.model.Imoveis;
import br.imob.fotoImoveis.FotoImovel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record ImovelTabelaDto(
        Long id,
        String titulo,
        TipoImovel tipoImovel,
        Status status,
        BigDecimal valor,
        Cidades cidade,
        EnderecoDto endereco,
        BigDecimal areaTotal,
        List<String> urlsFotos   // ⬅ adicionamos este campo
) {
    public ImovelTabelaDto(Imoveis imovel) {
        this(
                imovel.getId(),
                imovel.getTitulo(),
                imovel.getTipoImovel(),
                imovel.getStatus(),
                imovel.getValor(),
                imovel.getCidade(),
                new EnderecoDto(
                        imovel.getEndereco().getLogradouro(),
                        imovel.getEndereco().getNumero(),
                        imovel.getEndereco().getBairro(),
                        imovel.getEndereco().getCep()
                ),
                imovel.getAreaTotal(),
                // mapeia cada FotoImovel para o nome do arquivo
                imovel.getFotos() != null
                        ? imovel.getFotos().stream()
                        .map(FotoImovel::getUrlArquivo)  // ou getUrl, dependendo do que você salvou
                        .collect(Collectors.toList())
                        : List.of()
        );
    }
}
