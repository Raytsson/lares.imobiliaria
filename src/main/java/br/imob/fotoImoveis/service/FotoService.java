package br.imob.fotoImoveis.service;

import br.imob.fotoImoveis.FotoImovel;
import br.imob.fotoImoveis.repository.FotoImovelRepository;
import br.imob.imovel.model.Imoveis;
import br.imob.imovel.repository.ImovelRepository;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FotoService {

    @Autowired
    private FotoImovelRepository fotoRepository;

    @Autowired
    private ImovelRepository imovelRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public void salvarFotos(Long imovelId, List<MultipartFile> arquivos) {
        Imoveis imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        arquivos.forEach(arquivo -> {
            String nomeArquivo = salvarArquivoNoDisco(arquivo);

            FotoImovel foto = new FotoImovel();
            foto.setUrlArquivo(nomeArquivo);
            foto.setImovel(imovel);

            fotoRepository.save(foto);
        });
    }

    private String salvarArquivoNoDisco(MultipartFile arquivo) {
        try {
            // nome base sempre .webp
            String nomeBase = UUID.randomUUID() + ".webp";
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            // lê os bytes uma vez só (InputStream só pode ser lido uma vez)
            byte[] bytes = arquivo.getBytes();

            // ── FULL (lightbox) → 1600px, qualidade 0.80 ──
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(1600, 1600)
                    .keepAspectRatio(true)
                    .outputFormat("webp")
                    .outputQuality(0.80)
                    .toFile(dir.resolve(nomeBase).toFile());

            // ── MEDIUM (card principal) → 900px, qualidade 0.75 ──
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(900, 900)
                    .keepAspectRatio(true)
                    .outputFormat("webp")
                    .outputQuality(0.75)
                    .toFile(dir.resolve("medium_" + nomeBase).toFile());

            // ── THUMB (miniaturas) → 400px, qualidade 0.70 ──
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(400, 400)
                    .keepAspectRatio(true)
                    .outputFormat("webp")
                    .outputQuality(0.70)
                    .toFile(dir.resolve("thumb_" + nomeBase).toFile());

            return nomeBase;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem", e);
        }
    }
}
