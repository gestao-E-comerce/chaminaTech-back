package chaminaTech.Controller;

import chaminaTech.Service.AppImpressaoTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/installer")
public class InstallerController {
    @Autowired
    private AppImpressaoTokenService appImpressaoTokenService;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadInstaller(@RequestParam("matrizId") Long matrizId) throws IOException {
        Path baseDir = Paths.get("").toAbsolutePath().resolve("installer");
        Path scriptTemplatePath = baseDir.resolve("installer-template.bat");
        Path scriptPath = baseDir.resolve("installer.bat");
        Path jarPath = baseDir.resolve("app.jar");
        Path jdk64Path = baseDir.resolve("jdk64.msi");
        Path zipPath = baseDir.resolve("installer.zip");

        // Verifica se os arquivos existem
        if (!Files.exists(scriptTemplatePath) || !Files.exists(jarPath) || !Files.exists(jdk64Path)) {
            throw new IOException("Arquivos necessários não encontrados.");
        }

        // Gera token em memória (não salva ainda)
        String token = appImpressaoTokenService.gerarTokenMemoria(matrizId);

        // Substitui os placeholders no .bat
        String scriptContent = Files.readString(scriptTemplatePath);
        scriptContent = scriptContent
                .replace("${MATRIZ_ID}", matrizId.toString())
                .replace("${TOKEN}", token);
        Files.writeString(scriptPath, scriptContent);

        // Compacta os arquivos
        List<Path> filesToZip = Arrays.asList(scriptPath, jarPath, jdk64Path);
        zipFiles(filesToZip, zipPath);

        // Verifica se o ZIP foi criado
        if (!Files.exists(zipPath)) {
            throw new IOException("Falha ao criar o arquivo ZIP.");
        }

        // Tudo certo → agora salva o token no banco
        appImpressaoTokenService.criarRegistroToken(token, matrizId);

        Resource resource = new UrlResource(zipPath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"installer.zip\"")
                .body(resource);
    }

    private void zipFiles(List<Path> files, Path outputZip) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(outputZip))) {
            for (Path file : files) {
                if (Files.exists(file)) {
                    zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                } else {
                    throw new IOException("Arquivo não encontrado: " + file);
                }
            }
        }
    }
}