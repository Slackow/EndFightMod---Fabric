package com.slackow.endfight.util;

import com.google.gson.stream.JsonReader;
import com.slackow.endfight.EndFightMod;
import net.minecraft.util.math.MathHelper;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
            bufferedWriter.write("In Game Time,Real Time,Loadout,Island Type\n");
            System.out.println(recordsFiles.length);
            for (File recordsFile : recordsFiles) {
                try (JsonReader jsonReader = new JsonReader(new FileReader(recordsFile))) {
                    jsonReader.beginObject();
                    long finalIgt = 0;
                    long finalRta = 0;
                    String category = null;
                    boolean isCompleted = false;
                    int currentGamemode = -1;
                    int initialBeds = EndFightMod.initialBeds;
                    int initialArrows = EndFightMod.initialArrows;
                    int islandType = 0;
                    while (jsonReader.hasNext()) {
                        String name = jsonReader.nextName();
                        if (name.equals("category")) {
                            category = jsonReader.nextString();
                        } else if (name.equals("is_completed")) {
                            isCompleted = jsonReader.nextBoolean();
                        } else if (name.equals("current_gamemode")) {
                            currentGamemode = jsonReader.nextInt();
                        } else if (name.equals("initial_beds")) {
                            initialBeds = jsonReader.nextInt();
                        } else if (name.equals("initial_arrows")) {
                            initialArrows = jsonReader.nextInt();
                        } else if (name.equals("final_igt")) {
                            finalIgt = jsonReader.nextLong();
                        } else if (name.equals("final_rta")) {
                            finalRta = jsonReader.nextLong();
                        } else if (name.equals("island_type")) {
                            islandType = jsonReader.nextInt();
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                    if ("end_fight".equals(category) && currentGamemode == 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String formattedFinalIgt = "DNF";
                        String formattedFinalRta = "DNF";
                        if (isCompleted) {
                            formattedFinalIgt =  LocalTime.ofSecondOfDay(MathHelper.clamp((int)finalIgt, 0, 84599) / 1000).format(DateTimeFormatter.ofPattern("mm:ss"));
                            formattedFinalRta =  LocalTime.ofSecondOfDay(MathHelper.clamp((int)finalRta, 0, 84599) / 1000).format(DateTimeFormatter.ofPattern("mm:ss"));
                        }
                        stringBuilder.append(formattedFinalIgt).append(",");
                        stringBuilder.append(formattedFinalRta).append(",");
                        stringBuilder.append(initialBeds).append(" Beds ").append(initialArrows).append(" Arrows").append(",");
                        stringBuilder.append(islandType == -2 ? "Random" : "Set");
                        stringBuilder.append("\n");
                        bufferedWriter.write(stringBuilder.toString());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured writing .csv");
            e.printStackTrace();
        }
    }
}
