package br.com.frota.application;

import br.com.frota.domain.Onibus;
import br.com.frota.domain.notification.MontadorNotificador;
import br.com.frota.domain.notification.NotificadorManutencao;
import br.com.frota.domain.port.OnibusRepositorio;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class AbrirManutencaoUseCase {
    private final OnibusRepositorio onibusRepositorio;
    private final MontadorNotificador montadorNotificador;

    public AbrirManutencaoUseCase(OnibusRepositorio onibusRepositorio, MontadorNotificador montadorNotificador) {
        this.onibusRepositorio = onibusRepositorio;
        this.montadorNotificador = montadorNotificador;
    }

    public List<String> executar(String onibusId, String motivo, Set<String> canaisExtras,
                                 String emailDestino, String telefoneDestino) {
        Onibus onibus = onibusRepositorio.buscarPorId(onibusId)
                .orElseThrow(() -> new NoSuchElementException("Ônibus não encontrado: " + onibusId));

        onibus.enviarParaManutencao();
        onibusRepositorio.salvar(onibus);

        NotificadorManutencao notificador = montadorNotificador.montar(canaisExtras, emailDestino, telefoneDestino);
        return notificador.notificar(onibus.prefixo(), onibus.placa().valor(), motivo);
    }
}