package br.imob.enderecos.dtos;

public record EnderecosRequestDtos(
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado
) {
}
