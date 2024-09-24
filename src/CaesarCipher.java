import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CaesarCipher {
    // Алфавит
    private static final char[] ALPHABET = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'я', '.', ',', '«', '»', '"', '\'', ':', '!', '?', ' '};
    // Метод для шифрования текста
    public void encrypt(String inputFile, String outputFile, int key) throws IOException {
        validateInput(inputFile, outputFile, key);
        String content = readFile(inputFile);
        StringBuilder encryptedContent = new StringBuilder();
        for (char c : content.toCharArray()) {
            encryptedContent.append(shiftCharacter(c, key));
        }
        writeFile(outputFile, encryptedContent.toString());
    }

    // Метод для расшифровки текста
    public void decrypt(String inputFile, String outputFile, int key) throws IOException {
        validateInput(inputFile, outputFile, key);
        String content = readFile(inputFile);
        StringBuilder decryptedContent = new StringBuilder();
        for (char c : content.toCharArray()) {
            decryptedContent.append(shiftCharacter(c, -key));
        }
        writeFile(outputFile, decryptedContent.toString());
    }

    // Метод для brute force расшифровки
    public void bruteForce(String inputFile, String outputFile, String optionalSampleFile) throws IOException {
        String content = readFile(inputFile);
        String sampleContent = optionalSampleFile != null ? readFile(optionalSampleFile) : null;

        for (int key = 0; key < ALPHABET.length; key++) {
            StringBuilder decryptedContent = new StringBuilder();
            for (char c : content.toCharArray()) {
                decryptedContent.append(shiftCharacter(c, -key));
            }
            if (sampleContent != null && decryptedContent.toString().contains(sampleContent)) {
                writeFile(outputFile, decryptedContent.toString());
                return;
            }
        }

        writeFile(outputFile, "Brute force failed to find the correct key.");
    }

    // Метод для статистического анализа расшифровки
    public void statisticalAnalysis(String inputFile, String outputFile, String optionalSampleFile) throws IOException {
        String content = readFile(inputFile);
        String sampleContent = optionalSampleFile != null ? readFile(optionalSampleFile) : null;

        if (sampleContent == null) {
            throw new IllegalArgumentException("Sample file is required for statistical analysis.");
        }

        Map<Character, Integer> sampleFrequency = calculateFrequency(sampleContent);
        double[] sampleVector = normalizeVector(sampleFrequency);

        double minDistance = Double.MAX_VALUE;
        int bestKey = 0;

        for (int key = 0; key < ALPHABET.length; key++) {
            StringBuilder decryptedContent = new StringBuilder();

            for (char c : content.toCharArray()) {
                decryptedContent.append(shiftCharacter(c, -key));
            }

            Map<Character, Integer> decryptedFrequency = calculateFrequency(decryptedContent.toString());
            double[] decryptedVector = normalizeVector(decryptedFrequency);

            double distance = calculateDistance(sampleVector, decryptedVector);

            if (distance < minDistance) {
                minDistance = distance;
                bestKey = key;
            }
        }

        decrypt(inputFile, outputFile, bestKey);
    }

    // Вспомогательные методы
    private void validateInput(String inputFile, String outputFile, int key) {
        if (!new File(inputFile).exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + inputFile);
        }
        if (key < 0 || key >= ALPHABET.length) {
            throw new IllegalArgumentException("Key must be between 0 and " + (ALPHABET.length - 1));
        }
    }

    private char shiftCharacter(char c, int shift) {
        int index = Arrays.binarySearch(ALPHABET, c);
        if (index < 0) {
            return c; // Если символ не найден в алфавите, возвращаем его без изменений
        }
        int newIndex = (index + shift + ALPHABET.length) % ALPHABET.length;
        return ALPHABET[newIndex];
    }

    private String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private void writeFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }

    private Map<Character, Integer> calculateFrequency(String text) {
        Map<Character, Integer> frequency = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        }
        return frequency;
    }

    private double[] normalizeVector(Map<Character, Integer> frequency) {
        double[] vector = new double[ALPHABET.length];
        int total = frequency.values().stream().mapToInt(Integer::intValue).sum();
        for (int i = 0; i < ALPHABET.length; i++) {
            vector[i] = frequency.getOrDefault(ALPHABET[i], 0) / (double) total;
        }
        return vector;
    }

    private double calculateDistance(double[] vector1, double[] vector2) {
        double sum = 0;
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(sum);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CaesarCipher cipher = new CaesarCipher();

        System.out.println("Выберите режим работы:");
        System.out.println("1. Шифрование");
        System.out.println("2. Расшифровка с ключом");
        System.out.println("3. Brute force");
        System.out.println("4. Статистический анализ");
        System.out.println("0. Выход");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                System.out.print("Введите имя файла для чтения: ");
                String inputFileEncrypt = scanner.nextLine();
                System.out.print("Введите имя файла для записи: ");
                String outputFileEncrypt = scanner.nextLine();
                System.out.print("Введите сдвиг (целое число): ");
                int shiftEncrypt = scanner.nextInt();
                try {
                    cipher.encrypt(inputFileEncrypt, outputFileEncrypt, shiftEncrypt);
                    System.out.println("Текст успешно зашифрован и записан в файл: " + outputFileEncrypt);
                } catch (IOException e) {
                    System.err.println("Ошибка при работе с файлом: " + e.getMessage());
                }
                break;
            case 2:
                System.out.print("Введите имя файла для чтения: ");
                String inputFileDecrypt = scanner.nextLine();
                System.out.print("Введите имя файла для записи: ");
                String outputFileDecrypt = scanner.nextLine();
                System.out.print("Введите сдвиг (целое число): ");
                int shiftDecrypt = scanner.nextInt();
                try {
                    cipher.decrypt(inputFileDecrypt, outputFileDecrypt, shiftDecrypt);
                    System.out.println("Текст успешно расшифрован и записан в файл: " + outputFileDecrypt);
                } catch (IOException e) {
                    System.err.println("Ошибка при работе с файлом: " + e.getMessage());
                }
                break;
            case 3:
                System.out.print("Введите имя файла для чтения: ");
                String inputFileBruteForce = scanner.nextLine();
                System.out.print("Введите имя файла для записи: ");
                String outputFileBruteForce = scanner.nextLine();
                System.out.print("Введите имя файла с примером текста (опционально): ");
                String sampleFileBruteForce = scanner.nextLine();
                try {
                    cipher.bruteForce(inputFileBruteForce, outputFileBruteForce, sampleFileBruteForce);
                    System.out.println("Текст успешно расшифрован методом brute force и записан в файл: " + outputFileBruteForce);
                } catch (IOException e) {
                    System.err.println("Ошибка при работе с файлом: " + e.getMessage());
                }
                break;
            case 4:
                System.out.print("Введите имя файла для чтения: ");
                String inputFileStatistical = scanner.nextLine();
                System.out.print("Введите имя файла для записи: ");
                String outputFileStatistical = scanner.nextLine();
                System.out.print("Введите имя файла с примером текста: ");
                String sampleFileStatistical = scanner.nextLine();
                try {
                    cipher.statisticalAnalysis(inputFileStatistical, outputFileStatistical, sampleFileStatistical);
                    System.out.println("Текст успешно расшифрован методом статистического анализа и записан в файл: " + outputFileStatistical);
                } catch (IOException e) {
                    System.err.println("Ошибка при работе с файлом: " + e.getMessage());
                }
                break;
            case 0:
                System.out.println("Выход из программы.");
                break;
            default:
                System.out.println("Неверный выбор. Пожалуйста, выберите от 0 до 4.");
        }

        scanner.close();
    }
}