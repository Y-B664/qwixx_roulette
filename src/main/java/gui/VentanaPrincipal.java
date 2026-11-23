package gui;

//import gestion.GestorPartida; // Faltaria este gestor
import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    //private GestorPartida gestorPartida;
    private JTextField txtNombreJugador;
    private JComboBox<String> cbxDificultad;
    private JButton btnJugar;
    public VentanaPrincipal() {
        setTitle("QWIXX: Spin & Write - Inicio");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#121212")); // Fondo Negro Casino[cite: 3]
        setLayout(new BorderLayout());

        mostrarMenu();
    }
    public void mostrarMenu() {
        JPanel panelCentral = new JPanel(new GridLayout(4, 1, 10, 10));
        panelCentral.setBackground(Color.decode("#F5F5F0")); // Fondo Blanco Marfil[cite: 3]
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lblTitulo = new JLabel("QWIXX: SPIN & WRITE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        txtNombreJugador = new JTextField();
        txtNombreJugador.setBorder(BorderFactory.createTitledBorder("Nombre de Usuario"));
        String[] dificultades = {"Fácil", "Medio", "Difícil"};
        cbxDificultad = new JComboBox<>(dificultades);
        cbxDificultad.setBorder(BorderFactory.createTitledBorder("Dificultad del Bot"));
        btnJugar = new JButton("JUGAR");
        btnJugar.setBackground(Color.decode("#2E7D32")); // Verde Tapete[cite: 3]
        btnJugar.setForeground(Color.WHITE);
        btnJugar.setFont(new Font("Arial", Font.BOLD, 14));
        btnJugar.addActionListener(e -> {
            String nombre = txtNombreJugador.getText();
            if (!nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Iniciando partida para: " + nombre);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor ingresa un nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelCentral.add(lblTitulo);
        panelCentral.add(txtNombreJugador);
        panelCentral.add(cbxDificultad);
        panelCentral.add(btnJugar);
        add(panelCentral, BorderLayout.CENTER);
    }
}
