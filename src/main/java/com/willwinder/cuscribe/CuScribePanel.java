package com.willwinder.cuscribe;

import static com.willwinder.universalgcodesender.utils.GUIHelpers.displayErrorDialog;

import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.uielements.UGSSettingsDialog;
import com.willwinder.universalgcodesender.uielements.panels.ConnectionSettingsPanel;

import net.miginfocom.swing.MigLayout;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

/**
 * @author wwinder
 * Created on: 7/26/18
 */
public class CuScribePanel extends JPanel {
  private static final String help1 = "1. Manually set height stop on your CuScribe before continuing.";
  private static final String help2 = "2. Move cutter head to home location.";
  private static final String help3 = "3. Turn on spindle and vacuum";
  private static final String help4 = "4. Load gcode file.";
  private static final String help5 = "5. Begin cutting, click button again to pause.";

  private final BackendAPI backend;

  private final String send = Localization.getString("mainWindow.swing.sendButton");

  private final JButton cutHeightButton = new JButton("Set Cut Height");
  private final JButton homeButton = new JButton("Home");
  private final JButton loadGcodeFileButton = new JButton("Load Gcode File");
  private final JButton startCuttingButton;
  private final JButton helpButton = new JButton("Help");
  private final JButton settingsButton;
  private final JFileChooser fileChooser = new JFileChooser();


  public CuScribePanel(BackendAPI backend) {
    this.backend = backend;

    homeButton.addActionListener((al) -> {
      try {
        backend.performHomingCycle();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    loadGcodeFileButton.addActionListener((al) -> openFileChooser());

    startCuttingButton = new JButton(backend.getPauseResumeText());
    startCuttingButton.addActionListener((al) -> {
      try {
        backend.pauseResume();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    settingsButton = dialogLauncherButton("Settings",
        new UGSSettingsDialog(
            "Settings",
            new ConnectionSettingsPanel(backend.getSettings()),
            null,
            true));

    helpButton.addActionListener((al) -> {
      StringBuilder message = new StringBuilder()
          .append(help1).append("\n")
          .append(help2).append("\n")
          .append(help3).append("\n")
          .append(help4).append("\n")
          .append(help5).append("\n");

      JOptionPane.showMessageDialog(new JFrame(),
          message,
          Localization.getString("mainWindow.helpDialog"),
          JOptionPane.INFORMATION_MESSAGE);
    });

    setLayout(new MigLayout("wrap 2"));
    add(new JLabel("1. "));
    add(cutHeightButton, "grow");

    add(new JLabel("2. "));
    add(homeButton, "grow");

    add(new JLabel("3. "));
    add(new JLabel("Turn on spindle / vacuum."));

    add(new JLabel("4. "));
    add(loadGcodeFileButton, "grow");

    add(new JLabel("5. "));
    add(startCuttingButton, "grow");

    add(helpButton, "span 2, grow");
    add(settingsButton, "span 2, grow");
  }

  private static JButton dialogLauncherButton(String title, JDialog c) {
    JButton button = new JButton(title);
    button.addActionListener((ActionEvent e) -> {
      c.setVisible(true);
    });
    return button;
  }

  private void openFileChooser() {
    int returnVal = fileChooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      try {
        File gcodeFile = fileChooser.getSelectedFile();
        backend.setGcodeFile(gcodeFile);
      } catch (Exception ex) {
        displayErrorDialog(ex.getMessage());
      }
    }
  }
}
