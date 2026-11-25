package br.com.frota.domain.notification;

import br.com.frota.domain.port.EnviadorEmail;
import java.util.Set;

public class MontadorNotificador {
    private final EnviadorEmail enviadorEmail;

    public MontadorNotificador(EnviadorEmail enviadorEmail) {
        this.enviadorEmail = enviadorEmail;
    }

    public NotificadorManutencao montar(Set<String> canaisExtras, String emailDestino, String telefoneDestino) {
        NotificadorManutencao pilha = new NotificadorPainelCentral();
        if (canaisExtras == null) {
            return pilha;
        }
        if (canaisExtras.contains("EMAIL")) {
            pilha = new EmailDecorator(pilha, enviadorEmail, emailDestino);
        }
        if (canaisExtras.contains("SMS")) {
            pilha = new SmsDecorator(pilha, telefoneDestino);
        }
        return pilha;
    }
}