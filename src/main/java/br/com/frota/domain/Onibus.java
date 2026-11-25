package br.com.frota.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Onibus {
    public enum Status { DISPONIVEL, EM_OPERACAO, EM_MANUTENCAO }

    private final String id;
    private final String prefixo;
    private final Placa placa;
    private final String chassi;
    private final String marca;
    private final String modelo;
    private final int anoFabricacao;
    private final int capacidadePassageiros;
    private final BigDecimal quilometragem;
    private final String tipoCombustivel;
    private final boolean acessibilidadeCompleta;
    private final boolean arCondicionado;
    private Status statusOperacional;
    private final LocalDate dataUltimaRevisao;
    private String linhaId;
    private final DispositivoTelemetria telemetria;

    public Onibus(String id, String prefixo, Placa placa, String chassi, String marca, String modelo,
                  int anoFabricacao, int capacidadePassageiros, BigDecimal quilometragem,
                  String tipoCombustivel, boolean acessibilidadeCompleta, boolean arCondicionado,
                  Status statusOperacional, LocalDate dataUltimaRevisao, String linhaId,
                  DispositivoTelemetria telemetria) {
        this.id = id;
        this.prefixo = prefixo;
        this.placa = placa;
        this.chassi = chassi;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.capacidadePassageiros = capacidadePassageiros;
        this.quilometragem = quilometragem;
        this.tipoCombustivel = tipoCombustivel;
        this.acessibilidadeCompleta = acessibilidadeCompleta;
        this.arCondicionado = arCondicionado;
        this.statusOperacional = statusOperacional;
        this.dataUltimaRevisao = dataUltimaRevisao;
        this.linhaId = linhaId;
        this.telemetria = telemetria;
    }

    public void alocarNaLinha(String novaLinhaId) {
        if (this.statusOperacional == Status.EM_MANUTENCAO) {
            throw new IllegalStateException("Ônibus em manutenção não pode ser alocado em linha.");
        }
        if (novaLinhaId == null || novaLinhaId.isBlank()) {
            throw new IllegalArgumentException("O identificador da linha é obrigatório.");
        }
        this.linhaId = novaLinhaId;
        this.statusOperacional = Status.EM_OPERACAO;
    }

    public void enviarParaManutencao() {
        if (this.statusOperacional == Status.EM_MANUTENCAO) {
            throw new IllegalStateException("Ônibus já está em manutenção.");
        }
        this.statusOperacional = Status.EM_MANUTENCAO;
        this.linhaId = null;
    }

    public boolean estaSuperaquecendo() {
        return this.telemetria != null && this.telemetria.temperaturaAcimaDoLimite();
    }

    public boolean estaEmOperacao() {
        return this.statusOperacional == Status.EM_OPERACAO;
    }

    public String id() { return id; }
    public String prefixo() { return prefixo; }
    public Placa placa() { return placa; }
    public String chassi() { return chassi; }
    public String marca() { return marca; }
    public String modelo() { return modelo; }
    public int anoFabricacao() { return anoFabricacao; }
    public int capacidadePassageiros() { return capacidadePassageiros; }
    public BigDecimal quilometragem() { return quilometragem; }
    public String tipoCombustivel() { return tipoCombustivel; }
    public boolean acessibilidadeCompleta() { return acessibilidadeCompleta; }
    public boolean arCondicionado() { return arCondicionado; }
    public Status statusOperacional() { return statusOperacional; }
    public LocalDate dataUltimaRevisao() { return dataUltimaRevisao; }
    public String linhaId() { return linhaId; }
    public DispositivoTelemetria telemetria() { return telemetria; }
}