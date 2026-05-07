package util;

import javafx.scene.control.TextFormatter;

public class FormatterUtil {
    public static TextFormatter<String> createPasswordFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if(text.isEmpty()) return change;
            if (text.length() > 16) return null;
            if (text.matches(".*\\s.*")) return null;

            return change;
        });
    }
    public static TextFormatter<String> createUsernameFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if(text.isEmpty()) return change;
            if (text.length() > 16) return null;
            if (text.matches(".*\\s.*")) return null;

            return change;
        });
    }
    public static TextFormatter<String> createNumberFormatter(){
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (!text.matches("\\d*")){
                return null;
            }
            if(text.length() > 10) {
                return null;
            }
            if(Long.parseLong(text) > Integer.MAX_VALUE){
                return null;
            }
            return change;
        });
    }
    public static TextFormatter<String> createLetterFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (!text.matches("[\\p{L}\\s]*")){
                return null;
            }
            return change;
        });
    }
    public static TextFormatter<String> createBookNameFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (text.length() > 50){
                return null;
            }
            return change;
        });
    }
    public static TextFormatter<String> createNameLetterFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (!text.matches("^[\\p{L} ]{0,50}$")){
                return null;
            }
            return change;
        });
    }
    public static TextFormatter<String> createAgeFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (!text.matches("\\d*")){
                return null;
            }
            int age = Integer.parseInt(text);

            if (age < 1 || age > 120) return null;

            return change;
        });
    }
    public static TextFormatter<String> createPhoneNumberFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (!text.matches("^0\\d*")){
                return null;
            }
            if(text.length() > 10) {
                return null;
            }
            return change;
        });
    }
    public static TextFormatter<String> createPublicationYearFormatter() {
        return new TextFormatter<>(change -> {
            String text = change.getControlNewText();

            if (text.isEmpty()) return change;

            if (!text.matches("\\d*")){
                return null;
            }
            if(text.matches("^0")){
                return null;
            }
            if(text.length() > 4) {
                return null;
            }
            return change;
        });
    }
}
