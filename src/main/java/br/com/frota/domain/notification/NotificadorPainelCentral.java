package br.com.frota.domain.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificadorPainelCentral implements NotificadorManutencao {
    @Override
    public List<String> notificar(String prefixo, String placa, String motivo) {
        List<String> avisos = new ArrayList<>();
        avisos.add("PAINEL CENTRAL: Ônibus " + prefixo + " (" + placa + ") em manutenção - " + motivo);
        return avisos;
    }
}