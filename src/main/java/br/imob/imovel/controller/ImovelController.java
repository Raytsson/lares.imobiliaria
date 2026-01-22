package br.imob.imovel.controller;

import br.imob.fotoImoveis.service.FotoService;
import br.imob.imovel.dtos.ImovelDetailDto;
import br.imob.imovel.dtos.ImovelRequestDto;
import br.imob.imovel.dtos.ImovelResponseDto;
import br.imob.imovel.enums.Cidades;
import br.imob.imovel.service.ImovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/imoveis")
public class ImovelController {

    @Autowired
    private FotoService fotoService;
    @Autowired
    private ImovelService imovelService;

    @PostMapping("/criarImovel")
    public ResponseEntity<ImovelResponseDto> criarImovel(@RequestBody ImovelRequestDto imovelRequestDto){
        ImovelResponseDto response = imovelService.createImovel(imovelRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/fotos")
    public ResponseEntity<Void> uploadFotos(
            @PathVariable Long id,
            @RequestParam("arquivos") List<MultipartFile> arquivos) {
        fotoService.salvarFotos(id, arquivos);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ImovelResponseDto>> buscar(
            @RequestParam(required = false) Cidades cidades,
            @RequestParam(required = false) Integer quartos,
            @RequestParam(required = false) Integer vagas,
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(imovelService.buscar(cidades, quartos, vagas, bairro, valorMin, valorMax, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelDetailDto> buscarPorId(@PathVariable Long id) {
        ImovelDetailDto detalhe = imovelService.buscarPorId(id);
        return ResponseEntity.ok(detalhe);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestParam boolean ativo) {
        imovelService.alterarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        imovelService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
