#!/bin/zsh
# Script para ejecutar el proyecto usando Maven dentro de Flatpak NetBeans

echo "🚀 Ejecutando aplicación..."
flatpak run --command=sh org.apache.netbeans -c "export JAVA_HOME=/app/jdk && cd /home/luis/Documentos/programacion/Restaurante/proyecto_restaurante && /app/netbeans/java/maven/bin/mvn javafx:run"
