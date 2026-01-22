package br.imob.imovel.dtos;

public record EnderecoDto(
        String logradouro,
        String numero,
        String bairro,
        String cep
) {}