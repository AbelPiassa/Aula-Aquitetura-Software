package br.com.mvc.model.service;

final class Validacao {
    private Validacao() { }

    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.strip().length() > 120) {
            throw new IllegalArgumentException(campo + ": informe de 1 a 120 caracteres.");
        }
        return valor.strip();
    }

    static void quantidade(int valor) {
        if (valor < 1 || valor > 1000000) {
            throw new IllegalArgumentException("Quantidade deve estar entre 1 e 1.000.000.");
        }
    }
}
