import Utility.Esito;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Register extends JFrame{
    private JTextField username;
    private JButton regButton;
    private JPanel registerForm;
    private JPasswordField passwordField;
    private final Client client;

    public Register(ClientMain client){
        this.client=client;

        setLocationRelativeTo(null);
        add(registerForm);
        setSize(400,200);

        regButton.addActionListener(e ->{
            try{
                Esito resp= client.register(username.getText(), String.valueOf(passwordField.getPassword()));
                JOptionPane.showMessageDialog(null, resp.message);
            }
        });
    }
}
