package br.com.frota.domain.port;

public interface EnviadorEmail {
    void enviar(String destinatario, String assunto, String corpo);
}