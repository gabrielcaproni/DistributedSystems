package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChatGUI extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;
    private JTextField localPortField;
    private JTextField remotePortField;
    private JTextField nameField;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    private Sender sender;
    private String from;

    public ChatGUI() {

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Seu Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(15);
        topPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(new JLabel("Porta Local:"), gbc);

        gbc.gridx = 1;
        localPortField = new JTextField(5);
        topPanel.add(localPortField, gbc);

        gbc.gridx = 2;
        remotePortField = new JTextField(5);
       
        JPanel remotePortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        remotePortPanel.add(new JLabel("Porta Remota:"));
        remotePortPanel.add(remotePortField);
        topPanel.add(remotePortPanel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        connectButton = new JButton("Conectar");
        topPanel.add(connectButton, gbc);
        
        messageArea = new JTextArea(20, 50);
        messageArea.setEditable(false);
        messageArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(messageArea);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        sendButton.setEnabled(false);

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(e -> connect());
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connect() {
        try {
            int localPort = Integer.parseInt(localPortField.getText());
            int remotePort = Integer.parseInt(remotePortField.getText());
            from = nameField.getText();

            if (from.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira seu nome.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sender = ChatFactory.build("localhost", remotePort, localPort, this);

            sendButton.setEnabled(true);
            connectButton.setEnabled(false);
            nameField.setEditable(false);
            localPortField.setEditable(false);
            remotePortField.setEditable(false);
            messageArea.append("Conectado com sucesso! Pode enviar mensagens.\n");
            messageField.requestFocusInWindow();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "As portas devem ser números válidos.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o chat: " + ex.getCause().getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String messageText = messageField.getText();
        if (sender != null && !messageText.trim().isEmpty()) {
            try {
                String messageToSend = String.format("%s%s%s", messageText, MessageContainer.FROM, from);
                sender.send(messageToSend); 
                newMessage(String.format("%s%sEu", messageText, MessageContainer.FROM));
                messageField.setText("");
            } catch (ChatException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem: " + ex.getMessage(), "Erro de Envio", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void newMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;

        SwingUtilities.invokeLater(() -> {
            String[] messageParts = message.split(MessageContainer.FROM); 
            String senderName = "Desconhecido";
            String msg = messageParts[0].trim();

            if (messageParts.length > 1) {
                senderName = messageParts[1].trim();
            }
            
            if (!msg.isEmpty()) {
                messageArea.append(String.format("%s> %s\n", senderName, msg));  
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ChatGUI());
    }
}