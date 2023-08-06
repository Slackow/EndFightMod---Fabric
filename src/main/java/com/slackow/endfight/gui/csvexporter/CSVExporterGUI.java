package com.slackow.endfight.gui.csvexporter;

import com.slackow.endfight.gui.core.TooltipRenderer;
import com.slackow.endfight.gui.widget.TooltipButtonWidget;
import com.slackow.endfight.util.CSVExporter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.tinyremapper.extension.mixin.common.data.Pair;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import javax.swing.*;

import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CSVExporterGUI extends Screen {
    private static final List<Pair<String, String>> modes = Arrays.asList( // (name, description) pairs
            Pair.of("Specific Day", "Export endfight stats from a specific day, today by default."),
            Pair.of("Last # Attempts", "Export endfight stats from the last # attempts."),
            Pair.of("Lifetime", "Export all endfight stats from your .minecraft/speedrunigt/endfight-records folder.\n" +
                    "May take a while.")
    );
    private final Screen from;
    private int modeIndex;
    private TextFieldWidget exportDestination;
    private TextFieldWidget specificDate;
    private TextFieldWidget attemptsCount;

    public CSVExporterGUI(Screen from) {
        this.from = from;
        this.modeIndex = 0;
    }

    public void init() {
        buttons.add(new TooltipButtonWidget(0, width/2 - 75, 20, 150, 20, modes.get(modeIndex).first(), modes.get(modeIndex).second()));
        buttons.add(new ButtonWidget(1, width / 2 - 75, height / 6 + 122, 150, 20, "Start Export"));
        buttons.add(new ButtonWidget(2, width/2 + 130, 120, 50, 20, "Browse..."));
        buttons.add(new ButtonWidget(3, width / 2 - 100, height / 6 + 174, 200, 20, I18n.translate("gui.done")));
        exportDestination = new TextFieldWidget(textRenderer, width/2 - 125, 120, 250, 20);
        exportDestination.setMaxLength(128);
        exportDestination.setText(FabricLoader.getInstance().getGameDir().toString());
        specificDate = new TextFieldWidget(textRenderer, width/2 - 75, 70, 150, 20);
        specificDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        attemptsCount = new TextFieldWidget(textRenderer, width/2 - 75, 70, 150, 20);
        attemptsCount.setText("100");
    }

    @Override
    public void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                modeIndex = (modeIndex + 1) % 3;
                button.message = modes.get(modeIndex).first();
                ((TooltipButtonWidget)button).setTooltip(modes.get(modeIndex).second());
                break;
            case 1:
                Path exportPath = new File(exportDestination.getText()).toPath();
                switch (modeIndex) {
                    case 0:
                        String date;
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        sdf.setLenient(false);
                        try {
                            sdf.parse(date = specificDate.getText());
                        } catch (ParseException e) {
                            date = sdf.format(new Date());
                            specificDate.setText(date);
                        }
                        CSVExporter.exportSpecificDayAttempts(exportPath, date);
                        break;
                    case 1:
                        int attempts;
                        try {
                            attempts = Integer.parseInt(attemptsCount.getText());
                        } catch (NumberFormatException e) {
                            attempts = 100;
                            attemptsCount.setText("" + attempts);
                        }
                        CSVExporter.exportLastXAttempts(exportPath, attempts);
                        break;
                    case 2:
                        CSVExporter.exportAllAttempts(exportPath);
                        break;
                }
                break;
            case 2:
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile();
                    exportDestination.setText(selectedDirectory.toString());
                }
                frame.dispose();
                break;
            case 3:
                client.setScreen(from);
                break;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, "Export Endfight Stats as CSV", width/2,5, 0xFFFFFF);
        drawWithShadow(textRenderer, "Export Mode", width/2 - 72, 42, -6250336);
        switch (modeIndex) {
            case 0:
                specificDate.render();
                drawWithShadow(textRenderer, "Date (mm/dd/yyyy)", width/2 - 72, 94, -6250336);
                break;
            case 1:
                attemptsCount.render();
                drawWithShadow(textRenderer, "# Attempts", width/2 - 72, 94, -6250336);
                break;
            case 2:
                break;
        }
        exportDestination.render();
        super.render(mouseX, mouseY, tickDelta);
        drawWithShadow(textRenderer, "Path to export csv", width/2 - 122, 145, -6250336);
        ((TooltipRenderer)this).renderTooltipsFromButtons();
    }

    @Override
    public void tick() {
        exportDestination.tick();
        specificDate.tick();
        attemptsCount.tick();
        super.tick();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        exportDestination.mouseClicked(mouseX, mouseY, button);
        specificDate.mouseClicked(mouseX, mouseY, button);
        attemptsCount.mouseClicked(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

    protected void keyPressed(char id, int code) {
        exportDestination.keyPressed(id, code);
        specificDate.keyPressed(id, code);
        attemptsCount.keyPressed(id, code);
        super.keyPressed(id, code);
    }
}
