#!/bin/bash

APP_NAME="kiwi"
INSTALL_DIR="$HOME/.local/bin"
# Caminho do executável gerado pelo task nativeCompile do Gradle
BUILD_BINARY="./build/native/nativeCompile/$APP_NAME"

echo "Instalando o $APP_NAME localmente..."

# 1. Verifica se o binário nativo realmente foi compilado
if [ ! -f "$BUILD_BINARY" ]; then
    echo "Erro: O executável nativo não foi encontrado em $BUILD_BINARY"
    echo "Rode o comando do Gradle primeiro: ./gradlew nativeCompile"
    exit 1
fi

# 2. Garante que o diretório ~/.local/bin existe
mkdir -p "$INSTALL_DIR"

# 3. Copia o binário local diretamente para a pasta de executáveis do usuário
cp "$BUILD_BINARY" "$INSTALL_DIR/$APP_NAME"

# 4. Garante permissão de execução
chmod +x "$INSTALL_DIR/$APP_NAME"

# 5. Adiciona ~/.local/bin ao PATH no .bashrc se ainda não estiver configurado
if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo "export PATH=\"\$HOME/.local/bin:\$PATH\"" >> ~/.bashrc
    echo "Configuração do PATH adicionada ao ~/.bashrc. Reinicie o terminal ou rode 'source ~/.bashrc'."
fi

echo "Sucesso! O $APP_NAME foi instalado em $INSTALL_DIR/$APP_NAME"