package dev.code925.pdf2img.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LanguageConverter {
    /**
     * Convierte una cadena de abreviaciones separadas por comas a un Set de Strings.
     *
     * @param inputString La cadena de entrada, ej: "eng,spa,jpn"
     * @return Un Set<String> con las abreviaciones.
     */
    public static Set<String> convertToLanguageSet(String inputString) {
        if (inputString == null || inputString.trim().isEmpty()) {
            // Manejo de caso nulo o vacío para evitar errores
            return new HashSet<>();
        }

        // 1. Usar el método split(',') para dividir la cadena en un array de strings
        String[] languagesArray = inputString.split(",");

        // 2. Convertir el array de Strings a un HashSet
        // El constructor de HashSet acepta un array de objetos
        Set<String> languageSet = new HashSet<>(Arrays.asList(languagesArray));

        return languageSet;
    }
}
