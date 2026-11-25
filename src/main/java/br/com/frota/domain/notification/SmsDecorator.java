package br.com.frota.domain.notification;

import java.util.ArrayList;
import java.util.List;

public class SmsDecorator extends NotificadorDecorator {
    private final String telefone;

    public SmsDecorator(NotificadorManutencao envolvido, String telefone) {
        super(envolvido);
        this.telefone = telefone;
    }

    @Override
    public List<String> notificar(String prefixo, String placa, String motivo) {
        List<String> avisos = new ArrayList<>(super.notificar(prefixo, placa, motivo));
        avisos.add("SMS -> " + telefone + ": Enviar reserva para cobrir ônibus " + prefixo + " (" + placa + ")");
        return avisos;
    }
}