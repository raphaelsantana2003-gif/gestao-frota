package br.com.frota.domain.notification;

import java.util.List;

public interface NotificadorManutencao {
    List<String> notificar(String prefixo, String placa, String motivo);
}