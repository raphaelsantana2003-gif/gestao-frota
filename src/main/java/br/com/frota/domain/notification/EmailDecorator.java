package br.com.frota.domain.notification;

import br.com.frota.domain.port.EnviadorEmail;
import java.util.ArrayList;
import java.util.List;

public class EmailDecorator extends NotificadorDecorator {
    private final EnviadorEmail enviadorEmail;
    private final String destinatario;

    public EmailDecorator(NotificadorManutencao envolvido, EnviadorEmail enviadorEmail, String destinatario) {
        super(envolvido);
        this.enviadorEmail = enviadorEmail;
        this.destinatario = destinatario;
    }

    @Override
    public List<String> notificar(String prefixo, String placa, String motivo) {
        List<String> avisos = new ArrayList<>(super.notificar(prefixo, placa, motivo));
        String assunto = "[MANUTENÇÃO] Ônibus " + prefixo + " (" + placa + ")";
        enviadorEmail.enviar(destinatario, assunto, "Motivo: " + motivo);
        avisos.add("EMAIL -> " + destinatario + ": " + assunto);
        return avisos;
    }
}