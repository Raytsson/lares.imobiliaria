package br.imob.imovel.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ImovelResponseDto(
        Long id,
        String titulo,
        BigDecimal valor,
        String cidade,
        String logradouro,
        String bairro,
        List<String> urlsFotos
) {
}
