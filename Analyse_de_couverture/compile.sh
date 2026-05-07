#!/bin/bash
# Compile le cahier des charges en PDF dans le dossier build/

OUTPUT_DIR="build"
MAIN_FILE="main"

mkdir -p "$OUTPUT_DIR"

pdflatex -output-directory="$OUTPUT_DIR" "$MAIN_FILE.tex"
pdflatex -output-directory="$OUTPUT_DIR" "$MAIN_FILE.tex"

echo ""
echo "Compilation terminée. PDF disponible : $OUTPUT_DIR/$MAIN_FILE.pdf"
