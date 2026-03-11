#!/bin/zsh
# Script para compilar el proyecto usando Maven dentro de Flatpak NetBeans

echo "🔨 Compilando proyecto..."
flatpak run --command=sh org.apache.netbeans -c "export JAVA_HOME=/app/jdk && cd /home/luis/Documentos/programacion/Restaurante/proyecto_restaurante && /app/netbeans/java/maven/bin/mvn clean compile"

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
else
    echo "❌ Error en la compilación"
    exit 1
fi
