#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
build_dir=$(mktemp -d)
trap 'rm -rf "$build_dir"' EXIT
if command -v javac >/dev/null 2>&1; then
    compiler=(javac)
else
    compiler=(java -m jdk.compiler/com.sun.tools.javac.Main)
fi
shopt -s globstar
"${compiler[@]}" -encoding UTF-8 -d "$build_dir" \
    src/main/java/br/com/faturamento/domain/**/*.java \
    src/main/java/br/com/faturamento/application/*.java \
    src/main/java/br/com/faturamento/infra/adapters/out/messaging/BoletoSimulado.java \
    src/main/java/br/com/faturamento/infra/adapters/out/soap/NotaSimulada.java \
    src/main/java/br/com/faturamento/infra/adapters/out/smtp/NotificacaoSimulada.java \
    verificacao/VerificacaoFaturamento.java
java -cp "$build_dir" VerificacaoFaturamento
