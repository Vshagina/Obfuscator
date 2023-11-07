package com.company;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Obfuscator {
    private static AtomicLong directoryNumber = new AtomicLong(0);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите путь к директории: ");
        String directoryPath = scanner.nextLine();

        try {
            Directory(directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Directory(String directoryPath) throws IOException {
        Path start = Paths.get(directoryPath);

        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    process(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                directoryNumber.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }
        });
    }
    private static void process(Path filePath) throws IOException {
        String content = readFile(filePath);
        content = deleteComments(content);
        content = replaceClassAndConstructorNames(content);
        content = replaceIdentifiers(content);
        content = compressCode(content);

        Path newFilePath = newFile(filePath);
        writeFile(newFilePath, content);
    }

    private static String readFile(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }

    private static void writeFile(Path filePath, String content) throws IOException {
        Files.write(filePath, content.getBytes());
    }

    private static String compressCode(String code) {
        // здесь происходит сжатие кода за счет удаления лишних пробелов и символов перехода на новую строку
        String compressedCode = code.replaceAll("\\s+", " ").trim().concat("\n");
        return compressedCode;
    }

    private static String deleteComments(String code) {
        // удаляю комментарии
        DeleteComment commentDeleter = new DeleteComment(code);
        return commentDeleter.deleteComment();
    }

    private static String replaceClassAndConstructorNames(String code) {
        // заменяю имя класса
        code = code.replaceAll("Main", "newClass");

        // заменяю имя конструктора
        Pattern constructorPattern = Pattern.compile("\\s*\\([^\\)]*\\)\\s*\\{");
        Matcher constructorMatcher = constructorPattern.matcher(code);
        code = constructorMatcher.replaceAll("newconstructor(String[] args) {");

        return code;
    }

    private static String replaceIdentifiers(String code) {
        // заменяю идентификаторы
        Pattern methodParamsPattern = Pattern.compile("\\(.*?\\)\\s*\\{");
        Matcher methodParamsMatcher = methodParamsPattern.matcher(code);
        StringBuffer result = new StringBuffer();
        methodParamsMatcher.appendTail(result);
        code = result.toString();

        return code;
    }

    private static Path newFile(Path originalFilePath) {
        String fileName = originalFilePath.getFileName().toString();
        String newFileName = fileName.replaceAll(".java$", ".java");
        return Paths.get(originalFilePath.getParent().toString(), newFileName);
    }
}