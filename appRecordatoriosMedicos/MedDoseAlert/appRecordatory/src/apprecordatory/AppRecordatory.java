package apprecordatory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AppRecordatory {

    private static Clip clip;
    private static JComboBox<String> comboBoxRecordatorios = new JComboBox<>();

    /**
     * @param args the command line arguments
     */
    private static List<Recordatorio> recordatorios = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
            iniciarTemporizador();
        });
    }

    private static void createAndShowGUI() {

        JFrame frame = new JFrame("MedDoseAlert");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Cargar el icono desde el archivo de imagen
        ImageIcon icono = new ImageIcon("C:\\Users\\romer\\OneDrive\\Desktop\\appRecordatory\\src\\apprecordatory\\resources\\icono.jpg");
        frame.setIconImage(icono.getImage());

        JPanel panel = new JPanel();

        // Establecer el color de fondo del panel
        panel.setBackground(new Color(200, 220, 240)); // Puedes ajustar los valores RGB según tus preferencias

        frame.getContentPane().add(panel);
        panel.setLayout(new GridLayout(6, 2, 10, 10));// Ajusta los valores y cambiaras las posiciones de los jLabel

        placeComponents(panel);

        frame.setSize(600,300 );
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        JLabel labelHora = new JLabel("Hora de la alarma (HH:mm):");
        panel.add(labelHora);

        JTextField horaTextField = new JTextField(10);
        panel.add(horaTextField);

        JLabel labelNombre = new JLabel("Descripcion medicamento:");
        panel.add(labelNombre);

        JLabel labelMes = new JLabel("Mes del recordatorio (MM):");
        panel.add(labelMes);

        JTextField mesTextField = new JTextField(10);
        panel.add(mesTextField);

        JTextField nombreTextField = new JTextField(10);
        panel.add(nombreTextField);

        JButton botonRecordatorio = new JButton("Agregar Recordatorio");
        panel.add(botonRecordatorio);

        JButton botonVerRecordatorios = new JButton("Ver Recordatorios");
        panel.add(botonVerRecordatorios);

        JButton botonEliminarRecordatorio = new JButton("Eliminar Recordatorio");
        panel.add(botonEliminarRecordatorio);

        botonRecordatorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String horaIngresada = horaTextField.getText();
                String mesIngresado = mesTextField.getText();
                String nombreIngresado = nombreTextField.getText();

                if (validarFormatoHora(horaIngresada) && validarFormatoMes(mesIngresado)) {
                    agregarRecordatorio(horaIngresada, mesIngresado, nombreIngresado);
                } else {
                    mostrarMensaje("Formato de hora o mes incorrecto. Use HH:mm para la hora y MM para el mes.");
                }
            }
        });

        botonVerRecordatorios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarRecordatorios();
            }
        });

// Agrega un JComboBox para seleccionar el recordatorio a eliminar
        panel.add(comboBoxRecordatorios);

        botonEliminarRecordatorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarRecordatorio();
            }
        });

        JButton botonDetenerSonido = new JButton("Detener Sonido");
        panel.add(botonDetenerSonido);

        botonDetenerSonido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detenerSonido();
            }
        });

    }

    private static boolean validarFormatoHora(String hora) {
        return hora.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");
    }

    private static boolean validarFormatoMes(String mes) {
        return mes.matches("^(0?[1-9]|1[0-2])$");
    }

    private static void reproducirSonido() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\romer\\OneDrive\\Desktop\\appRecordatory\\src\\apprecordatory\\resources\\sound.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            // Agrega un listener para cerrar el clip cuando termine de reproducirse
            clip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void detenerSonido() {
        // Detener el sonido
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private static void comprobarRecordatorios() {
        Date ahora = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (Recordatorio recordatorio : recordatorios) {
            if (sdf.format(ahora).equals(sdf.format(recordatorio.getHora())) && !recordatorio.isMostrado()) {
                recordatorio.setMostrado(true);
                reproducirSonido(); // Agrega esta línea para reproducir el sonido
                mostrarMensaje("¡Recordatorio! " + recordatorio.getNombre());

            }
        }
    }

    private static void agregarRecordatorio(String hora, String mes, String nombre) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM");
            Date horaRecordatorio = sdf.parse(hora + " " + mes);

            Recordatorio nuevoRecordatorio = new Recordatorio(horaRecordatorio, nombre);
            recordatorios.add(nuevoRecordatorio);

            mostrarMensaje("Recordatorio agregado: " + nombre + " a las " + hora + " del mes " + mes);
        } catch (ParseException e) {
            mostrarMensaje("Error al agregar el recordatorio.");
        }
    }

    private static void mostrarRecordatorios() {
        comboBoxRecordatorios.removeAllItems();

        if (recordatorios.isEmpty()) {
            comboBoxRecordatorios.addItem("No hay recordatorios.");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM");
            for (Recordatorio recordatorio : recordatorios) {
                comboBoxRecordatorios.addItem(sdf.format(recordatorio.getHora()) + " - " + recordatorio.getNombre());
            }
        }

    }

    private static void eliminarRecordatorio() {
        if (recordatorios.isEmpty()) {
            mostrarMensaje("No hay recordatorios para eliminar.");
            return;
        }

        int selectedIndex = comboBoxRecordatorios.getSelectedIndex();

        if (selectedIndex != -1) {
            Recordatorio recordatorio = recordatorios.get(selectedIndex);
            recordatorios.remove(selectedIndex);
            mostrarMensaje("Recordatorio eliminado: " + recordatorio.getNombre());
            mostrarRecordatorios(); // Actualiza la lista después de eliminar
        } else {
            mostrarMensaje("Selecciona un recordatorio para eliminar.");
        }
    }

    private static void mostrarMensaje(String mensaje) {
        JFrame mensajeFrame = new JFrame("Mensaje");
        JOptionPane.showMessageDialog(mensajeFrame, mensaje);
    }

    private static void iniciarTemporizador() {
        Timer temporizador = new Timer();
        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                comprobarRecordatorios();
            }
        }, 0, 1000);  // Comprobar cada segundo
    }

    static class Recordatorio {

        private Date hora;
        private String nombre;
        private boolean mostrado;

        public Recordatorio(Date hora, String nombre) {
            this.hora = hora;
            this.nombre = nombre;
            this.mostrado = false;
        }

        public Date getHora() {
            return hora;
        }

        public String getNombre() {
            return nombre;
        }

        public boolean isMostrado() {
            return mostrado;
        }

        public void setMostrado(boolean mostrado) {
            this.mostrado = mostrado;
        }
    }
}
