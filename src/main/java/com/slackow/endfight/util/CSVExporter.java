package com.slackow.endfight.util;

import com.google.gson.stream.JsonReader;
import com.slackow.endfight.EndFightMod;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

public class CSVExporter {
    public static void exportLastXAttempts(Path exportPath, int attempts){
        File[] recordsFiles = EndFightMod.endFightRecordsFile.listFiles();
        Arrays.sort(recordsFiles, Comparator.comparingLong(File::lastModified).reversed());
        recordsFiles = Arrays.copyOf(recordsFiles, attempts);
        writeRecordsFilesToCSV(exportPath, recordsFiles, "last-" + attempts + "-attempts-as-of-" + new SimpleDateFormat("MMddyyyyhhmmss").format(new Date()));
    }

    public static void exportSpecificDayAttempts(Path exportPath, String formattedDate) {
        File[] recordsFiles = EndFightMod.endFightRecordsFile.listFiles();
        Predicate<File> filter = (file) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            return dateFormat.format(new Date(file.lastModified())).equals(formattedDate);
        };
        recordsFiles = Arrays.stream(recordsFiles)
                .filter(filter)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .toArray(File[]::new);
        writeRecordsFilesToCSV(exportPath, recordsFiles, "from-specific-day-" + formattedDate);
    }

    public static void exportAllAttempts(Path exportPath) {
        File[] recordsFiles = EndFightMod.endFightRecordsFile.listFiles();
        Arrays.sort(recordsFiles, Comparator.comparingLong(File::lastModified).reversed());
        writeRecordsFilesToCSV(exportPath, recordsFiles, "all-attempts-as-of-" + new SimpleDateFormat("MMddyyyyhhmmss").format(new Date()));
    }

    private static void writeRecordsFilesToCSV(Path exportPath, File[] recordsFiles, String suffix) {
        String filePath = exportPath + "\\endfights-" + suffix + ".csv";
        try (FileWriter fileWriter = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write("In Game Time,Real Time\n");
            for (File recordsFile : recordsFiles) {
                try (JsonReader jsonReader = new JsonReader(new FileReader(recordsFile))) {
                    jsonReader.beginObject();
                    String category = null;
                    boolean isCompleted = false;
                    int currentGamemode = -1;
                    while (jsonReader.hasNext()) {
                        String name = jsonReader.nextName();
                        if (name.equals("category")) {
                            category = jsonReader.nextString();
                        } else if (name.equals("is_completed")) {
                            isCompleted = jsonReader.nextBoolean();
                        } else if (name.equals("current_gamemode")) {
                            currentGamemode = jsonReader.nextInt();
                        } else if (name.equals("final_igt")) {
                            if ("end_fight".equals(category) && currentGamemode == 0) {
                                long finalIgt = jsonReader.nextLong();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(isCompleted ? finalIgt : "DNF").append("\n");
                                bufferedWriter.write(stringBuilder.toString());
                            } else {
                                jsonReader.skipValue();
                            }
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured writing .csv");
            e.printStackTrace();
        }
    }
}
