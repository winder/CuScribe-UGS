package com.willwinder.cuscribe;

import static com.willwinder.universalgcodesender.utils.GUIHelpers.displayErrorDialog;

import com.willwinder.universalgcodesender.connection.ConnectionFactory;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.uielements.panels.ConnectionPanelGroup;
import com.willwinder.universalgcodesender.utils.FirmwareUtils;

import net.miginfocom.swing.MigLayout;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/**
 * Mostly copy/pasted from ConnectionPanelGroup.
 * @author wwinder
 * Created on: 7/26/18
 */
public class ConnectionPanel extends JPanel {
  private static final int BAUD_RATE = 115200;
  private static final String FIRMWARE = "GRBL";

  private static final Logger logger = Logger.getLogger(ConnectionPanelGroup.class.getName());

  private final JLabel portLabel = new JLabel(Localization.getString("mainWindow.swing.portLabel"));

  private final JComboBox portCombo = new JComboBox();

  private final JButton refreshButton = new JButton(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/refresh.gif")));
  private final String open = Localization.getString("mainWindow.ui.connect");
  private final JButton openCloseButton = new JButton(open);

  private final BackendAPI backend;

  public ConnectionPanel(BackendAPI backend) {
    this.backend = backend;

    loadFirmwareSelector();

    portCombo.setEditable(true);

    openCloseButton.addActionListener(evt -> opencloseButtonActionPerformed(evt));
    refreshButton.addActionListener(evt -> loadPortSelector());

    // MigLayout... 3rd party layout library.
    setLayout(new MigLayout("fill, wrap 1"));


    JLabel thumb = new JLabel();
    thumb.setIcon(new ImageIcon(getClass().getResource("/cuscribe.png")));
    add(thumb, "span 5");


    add(openCloseButton, "al center");
    add(portLabel, "split 3, al right");
    add(portCombo, "grow");
    add(refreshButton);
  }

  private void opencloseButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if (this.openCloseButton.getText().equalsIgnoreCase(open)) {
      String port = String.valueOf(portCombo.getSelectedItem());

      try {
        this.backend.connect(FIRMWARE, port, BAUD_RATE);
      } catch (Exception e) {
        logger.log(Level.WARNING, "Problem during backend.connect.", e);
        displayErrorDialog(e.getMessage());
      }
    } else {
      try {
        this.backend.disconnect();
      } catch (Exception e) {
        displayErrorDialog(e.getMessage());
      }
    }
  }


  private void loadPortSelector() {
    portCombo.removeAllItems();
    List<String> portList = ConnectionFactory.getPortNames(backend.getSettings().getConnectionDriver());

    if (portList.size() < 1) {
      if (backend.getSettings().isShowSerialPortWarning()) {
        displayErrorDialog(Localization.getString("mainWindow.error.noSerialPort"));
      }
    } else {
      for (String port : portList) {
        portCombo.addItem(port);
      }

      portCombo.setSelectedIndex(0);
    }
  }

  private void loadFirmwareSelector() {
    List<String> firmwareList = FirmwareUtils.getFirmwareList();

    if (firmwareList.size() < 1 || !firmwareList.contains("GRBL")) {
      displayErrorDialog(Localization.getString("mainWindow.error.noFirmware"));
    }
  }
}
