package br.imob.imovel.dtos;

import br.imob.enderecos.dtos.EnderecosResponseDto;
import br.imob.imovel.enums.Status;
import br.imob.imovel.enums.TipoImovel;

import java.math.BigDecimal;
import java.util.List;

public record ImovelDetailDto(
        Long id,
        String titulo,
        String descricao,
        BigDecimal valor,
        TipoImovel tipoImovel,
        Status status,
        BigDecimal areaTotal,
        BigDecimal areaConstruida,
        Integer quartos,
        Integer banheiros,
        Integer vagasGaragem,
        EnderecosResponseDto endereco,
        List<String> urlsFotos
) {
}
