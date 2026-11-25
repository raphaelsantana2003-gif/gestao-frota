package br.com.frota.infrastructure;

import br.com.frota.application.AbrirManutencaoUseCase;
import br.com.frota.domain.DispositivoTelemetria;
import br.com.frota.domain.FrotaAlocada;
import br.com.frota.domain.LinhaOnibus;
import br.com.frota.domain.Onibus;
import br.com.frota.domain.Placa;
import br.com.frota.domain.port.LinhaRepositorio;
import br.com.frota.domain.port.OnibusRepositorio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class FrotaController {
    private final LinhaRepositorio linhaRepositorio;
    private final OnibusRepositorio onibusRepositorio;
    private final AbrirManutencaoUseCase abrirManutencaoUseCase;

    public FrotaController(LinhaRepositorio linhaRepositorio,
                           OnibusRepositorio onibusRepositorio,
                           AbrirManutencaoUseCase abrirManutencaoUseCase) {
        this.linhaRepositorio = linhaRepositorio;
        this.onibusRepositorio = onibusRepositorio;
        this.abrirManutencaoUseCase = abrirManutencaoUseCase;
    }

    public record CriarLinhaRequest(
            @NotBlank String codigo,
            @NotBlank String nome,
            @NotBlank String origem,
            @NotBlank String destino,
            @NotNull BigDecimal tarifa
    ) {}

    public record CriarOnibusRequest(
            @NotBlank String prefixo,
            @NotBlank String placa,
            @NotBlank String chassi,
            @NotBlank String marca,
            @NotBlank String modelo,
            int anoFabricacao,
            int capacidadePassageiros,
            @NotNull BigDecimal quilometragem,
            @NotBlank String tipoCombustivel,
            boolean acessibilidadeCompleta,
            boolean arCondicionado,
            @NotNull DispositivoTelemetria telemetria
    ) {}

    public record AlocarRequest(@NotBlank String linhaId) {}

    public record ManutencaoRequest(
            @NotBlank String motivo,
            Set<String> canaisExtras,
            String emailGaragem,
            String telefoneDespachante
    ) {}

    public record OnibusResponse(
            String id,
            String prefixo,
            String placa,
            String modelo,
            String statusOperacional,
            String linhaId,
            boolean superaquecendo,
            DispositivoTelemetria telemetria
    ) {
        static OnibusResponse de(Onibus o) {
            return new OnibusResponse(
                    o.id(),
                    o.prefixo(),
                    o.placa().valor(),
                    o.modelo(),
                    o.statusOperacional().name(),
                    o.linhaId(),
                    o.estaSuperaquecendo(),
                    o.telemetria()
            );
        }
    }

    public record FrotaLinhaResponse(String linhaId, long totalEmOperacao, List<OnibusResponse> onibus) {}

    @GetMapping("/onibus")
    public List<OnibusResponse> listarTodosOnibus() {
        return onibusRepositorio.listarTodos().stream().map(OnibusResponse::de).toList();
    }

    @PostMapping("/linhas")
    public ResponseEntity<LinhaOnibus> criarLinha(@Valid @RequestBody CriarLinhaRequest req) {
        LinhaOnibus nova = new LinhaOnibus(null, req.codigo(), req.nome(), req.origem(), req.destino(), req.tarifa());
        return ResponseEntity.status(HttpStatus.CREATED).body(linhaRepositorio.salvar(nova));
    }

    @GetMapping("/linhas/{linhaId}/onibus")
    public FrotaLinhaResponse listarOnibusDaLinha(@PathVariable String linhaId) {
        FrotaAlocada frota = new FrotaAlocada();
        onibusRepositorio.listarPorLinhaId(linhaId).forEach(frota::adicionar);
        List<OnibusResponse> lista = frota.listar().stream().map(OnibusResponse::de).toList();
        return new FrotaLinhaResponse(linhaId, frota.contarEmOperacao(), lista);
    }

    @PostMapping("/onibus")
    public ResponseEntity<OnibusResponse> criarOnibus(@Valid @RequestBody CriarOnibusRequest req) {
        Onibus novo = new Onibus(
                "bus-" + req.prefixo(),
                req.prefixo(),
                new Placa(req.placa()),
                req.chassi(),
                req.marca(),
                req.modelo(),
                req.anoFabricacao(),
                req.capacidadePassageiros(),
                req.quilometragem(),
                req.tipoCombustivel(),
                req.acessibilidadeCompleta(),
                req.arCondicionado(),
                Onibus.Status.DISPONIVEL,
                LocalDate.now(),
                null,
                req.telemetria()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OnibusResponse.de(onibusRepositorio.salvar(novo)));
    }

    @PatchMapping("/onibus/{onibusId}/alocacao")
    public OnibusResponse alocarNaLinha(@PathVariable String onibusId, @Valid @RequestBody AlocarRequest req) {
        linhaRepositorio.buscarPorId(req.linhaId())
                .orElseThrow(() -> new NoSuchElementException("Linha não encontrada: " + req.linhaId()));
        Onibus onibus = onibusRepositorio.buscarPorId(onibusId)
                .orElseThrow(() -> new NoSuchElementException("Ônibus não encontrado: " + onibusId));
        onibus.alocarNaLinha(req.linhaId());
        return OnibusResponse.de(onibusRepositorio.salvar(onibus));
    }

    @PostMapping("/onibus/{onibusId}/manutencao")
    public Map<String, Object> abrirManutencao(@PathVariable String onibusId,
                                               @Valid @RequestBody ManutencaoRequest req) {
        List<String> notificacoes = abrirManutencaoUseCase.executar(
                onibusId, req.motivo(), req.canaisExtras(), req.emailGaragem(), req.telefoneDespachante()
        );
        return Map.of(
                "onibusId", onibusId,
                "statusAtual", "EM_MANUTENCAO",
                "notificacoesDisparadas", notificacoes
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> erro400(IllegalArgumentException e) {
        return Map.of("erro", e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> erro404(NoSuchElementException e) {
        return Map.of("erro", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> erro409(IllegalStateException e) {
        return Map.of("erro", e.getMessage());
    }
}