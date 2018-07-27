package com.willwinder.cuscribe;

import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.GUIBackend;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.services.JogService;
import com.willwinder.universalgcodesender.uielements.jog.JogPanel;
import com.willwinder.universalgcodesender.uielements.panels.CommandPanel;
import com.willwinder.universalgcodesender.uielements.panels.MachineStatusPanel;
import com.willwinder.universalgcodesender.uielements.panels.SendStatusPanel;
import com.willwinder.universalgcodesender.utils.SettingsFactory;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App
{
  public static void main( String[] args ) throws Exception {
    BackendAPI backend = new GUIBackend();
    backend.applySettings(SettingsFactory.loadSettings());

    final JFrame connectionFrame = connectionFrame(backend);

    // Create the main frame.
    final JFrame main = mainFrameB(backend);

    backend.addUGSEventListener(evt -> {
      if (evt.isControllerStatusEvent()) {
        boolean connected = evt.getControlState() != UGSEvent.ControlState.COMM_DISCONNECTED;
        connectionFrame.setVisible(!connected);
        main.setVisible(connected);
      }
    });

    //connectionFrame.setVisible(true);
    main.setVisible(true);
  }

  private static JFrame connectionFrame(BackendAPI backend) {
    JFrame frame = new JFrame("CuScribe");
    frame.getContentPane().add(new ConnectionPanel(backend));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 200);
    frame.pack();
    return frame;
  }

  private static JFrame mainFrameB(BackendAPI backend) {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("wrap 2, fill", "[center]rel[grow]"));

    JPanel cuScribePanel = new CuScribePanel(backend);
    //cuScribePanel.setBorder(BorderFactory.createBevelBorder(1));
    panel.add(cuScribePanel, "");

    JPanel machine = new SendStatusPanel(backend);
    machine.setBorder(BorderFactory.createEtchedBorder());
    panel.add(machine, "grow");

    JPanel console = new CommandPanel(backend);
    panel.add(console, "spanx 2, grow");

    final JFrame main = new JFrame("CuScribe");
    main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    main.getContentPane().setLayout(new BorderLayout());
    main.getContentPane().add(panel, BorderLayout.CENTER);
    main.pack();

    return main;
  }

  private static JFrame mainFrameA(BackendAPI backend) {
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout("wrap 2, fill", "[center]rel[grow]"));

    JPanel connection = new CuScribePanel(backend);
    //connection.setBorder(BorderFactory.createBevelBorder(1));
    panel.add(connection, "");

    JPanel machine = new MachineStatusPanel(backend);
    machine.setBorder(BorderFactory.createEtchedBorder());
    panel.add(machine, "spany 2, grow");

    JPanel jog = new JogPanel(backend, new JogService(backend), false);
    //jog.setBorder(BorderFactory.createBevelBorder(1));
    panel.add(jog);

    JPanel console = new CommandPanel(backend);
    panel.add(console, "spanx 2, grow");

    final JFrame main = new JFrame("CuScribe");
    main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    main.getContentPane().setLayout(new BorderLayout());
    main.getContentPane().add(panel, BorderLayout.CENTER);
    main.pack();

    return main;
  }

  private static JButton dialogLauncherButton(String title, JDialog c) {
    JButton button = new JButton(title);
    button.addActionListener((ActionEvent e) -> {
      c.setVisible(true);
    });
    return button;
  }
}
