package br.com.frota.domain.notification;

import java.util.List;

public abstract class NotificadorDecorator implements NotificadorManutencao {
    protected final NotificadorManutencao envolvido;

    protected NotificadorDecorator(NotificadorManutencao envolvido) {
        this.envolvido = envolvido;
    }

    @Override
    public List<String> notificar(String prefixo, String placa, String motivo) {
        return envolvido.notificar(prefixo, placa, motivo);
    }
}