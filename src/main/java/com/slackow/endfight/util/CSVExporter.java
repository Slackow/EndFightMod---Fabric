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
    public static void exportLastXAttempts(Path exportPath, int attempts, boolean includeMs) {
        File[] recordsFiles = EndFightMod.endFightRecordsFile.listFiles();
        PriorityQueue<File> recentFiles = new PriorityQueue<>(Comparator.comparingLong(File::lastModified));
        for (File file : recordsFiles) {
            if (recentFiles.size() < attempts) {
                recentFiles.offer(file);
            } else {
                File earliestFile = recentFiles.peek();
                if (file.lastModified() > earliestFile.lastModified()) {
                    recentFiles.poll();
                    recentFiles.offer(file);
                }
            }
        }
        File[] lastXAttempts = recentFiles.toArray(new File[0]);
        Arrays.sort(lastXAttempts, Comparator.comparing(File::lastModified).reversed());
        writeRecordsFilesToCSV(exportPath, lastXAttempts, "last-" + attempts + "-attempts-as-of-" + new SimpleDateFormat("MMddyyyyhhmmss").format(new Date()), includeMs);
    }

    public static void exportSpecificDayAttempts(Path exportPath, String formattedDate, boolean includeMs) {
        File[] recordsFiles = EndFightMod.endFightRecordsFile.listFiles();
        Predicate<File> filter = (file) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.format(new Date(file.lastModified())).equals(formattedDate);
        };
        recordsFiles = Arrays.stream(recordsFiles)
                .filter(filter)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .toArray(File[]::new);
        writeRecordsFilesToCSV(exportPath, recordsFiles, "from-specific-day-" + formattedDate.replace("/", ""), includeMs);
    }

    public static void exportAllAttempts(Path exportPath, boolean includeMs) {
        File[] recordsFiles = EndFightMod.endFightRecordsFile.listFiles();
        Arrays.sort(recordsFiles, Comparator.comparingLong(File::lastModified).reversed());
        writeRecordsFilesToCSV(exportPath, recordsFiles, "all-attempts-as-of-" + new SimpleDateFormat("MMddyyyyhhmmss").format(new Date()), includeMs);
    }

    private static void writeRecordsFilesToCSV(Path exportPath, File[] recordsFiles, String suffix, boolean includeMs) {
        String filePath = exportPath + "\\endfights-" + suffix + ".csv";
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("mm:ss" + (includeMs ? ".SSS" : ""));
        try (FileWriter fileWriter = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write("In Game Time,Real Time,Loadout,Island Type,Arrows Hit,Arrows Fired,Arrow Accuracy,");
            bufferedWriter.write("Beds Used,Bed Damage,Crystal Damage,Arrow Damage,Melee Damage,Total Damage,");
            bufferedWriter.write("Average (IGT), Best (IGT), Completion%\n");
            System.out.println(recordsFiles.length);
            long bestIgt = Long.MAX_VALUE;
            long runningIgt = 0;
            int runCount = 0;
            int completionCount = 0;
            int firstRunWasDNF = 0;
            StringBuilder stringBuilder = new StringBuilder();
            for (File recordsFile : recordsFiles) {
                ++runCount;
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
                    int bedsUsed = 0;
                    int arrowsUsed = 0;
                    int arrowsHit = 0;
                    double totalDamage = 0.0f;
                    double totalCrystalDamage = 0.0f;
                    double totalBedDamage = 0.0f;
                    double totalArrowDamage = 0.0f;
                    double totalMeleeDamage = 0.0f;
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
                        } else if (name.equals("beds_used")) {
                            bedsUsed = jsonReader.nextInt();
                        } else if (name.equals("arrows_used")) {
                            arrowsUsed = jsonReader.nextInt();
                        } else if (name.equals("arrows_hit")) {
                            arrowsHit = jsonReader.nextInt();
                        } else if (name.equals("total_damage")) {
                            totalDamage = jsonReader.nextDouble();
                        } else if (name.equals("total_crystal_damage")) {
                            totalCrystalDamage = jsonReader.nextDouble();
                        } else if (name.equals("total_bed_damage")) {
                            totalBedDamage = jsonReader.nextDouble();
                        } else if (name.equals("total_arrow_damage")) {
                            totalArrowDamage = jsonReader.nextDouble();
                        } else if (name.equals("total_melee_damage")) {
                            totalMeleeDamage = jsonReader.nextDouble();
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                    if ("end_fight".equals(category) && currentGamemode == 0) {
                        String formattedFinalIgt = "DNF";
                        String formattedFinalRta = "DNF";
                        if (isCompleted) {
                            if (firstRunWasDNF == 0) {
                                firstRunWasDNF = -1;
                            }
                            ++completionCount;
                            runningIgt += finalIgt;
                            bestIgt = Math.min(finalIgt, bestIgt);
                            formattedFinalIgt = LocalTime.ofNanoOfDay(Math.min(finalIgt * 1000000, 86400 * 1000000000L - 1)).format(timeFormatter);
                            formattedFinalRta = LocalTime.ofNanoOfDay(Math.min(finalRta * 1000000, 86400 * 1000000000L - 1)).format(timeFormatter);
                        } else if (firstRunWasDNF == 0) {
                            firstRunWasDNF = 1;
                        }
                        stringBuilder.append(formattedFinalIgt).append(",");
                        stringBuilder.append(formattedFinalRta).append(",");
                        if (!formattedFinalIgt.equals("DNF") && !formattedFinalRta.equals("DNF")) {
                            stringBuilder.append(initialBeds).append(" Beds ").append(initialArrows).append(" Arrows").append(",");
                            stringBuilder.append(islandType == -2 ? "Random" : "Set").append(",");
                            stringBuilder.append(arrowsHit).append(",").append(arrowsUsed).append(",");
                            stringBuilder.append((int) (100 * (arrowsUsed == 0 ? 1 : (float) arrowsHit / arrowsUsed))).append("%,");
                            stringBuilder.append(bedsUsed).append(",");
                            stringBuilder.append(totalBedDamage).append(",");
                            stringBuilder.append(totalCrystalDamage).append(",");
                            stringBuilder.append(totalArrowDamage).append(",");
                            stringBuilder.append(totalMeleeDamage).append(",");
                            stringBuilder.append(totalDamage);
                        }
                        stringBuilder.append("\n");
                    }
                }
            }
            int endOfFirstLine = stringBuilder.indexOf("\n");
            StringBuilder stringBuilder2 = new StringBuilder();
            String averageIgtString = LocalTime.ofNanoOfDay(Math.min((runningIgt / completionCount) * 1000000, 86400 * 1000000000L - 1)).format(timeFormatter);
            String bestIgtString = LocalTime.ofNanoOfDay(Math.min(bestIgt * 1000000, 86400 * 1000000000L - 1)).format(timeFormatter);
            if (firstRunWasDNF == 1) {
                stringBuilder2.append(",,,,,,,,,,");
            }
            stringBuilder2.append(",").append(averageIgtString).append(",");
            stringBuilder2.append(bestIgtString).append(",");
            stringBuilder2.append((int) (100 * ((float) completionCount / runCount))).append("%");
            stringBuilder.insert(endOfFirstLine, stringBuilder2);
            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            System.out.println("Error occured writing .csv");
            e.printStackTrace();
        }
    }
}
