package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame implements MessageContainer {

    private static final long serialVersionUID = 1L;
    // Campos para configuração da conexão
    private JTextField nameField;
    private JTextField localPortField;
    private JTextField remoteIpField; // NOVO
    private JTextField remotePortField;
    private JCheckBox isTcpCheckBox; // NOVO

    // Componentes da interface
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    
    private Sender sender;
    private String from;

    public ChatGUI() {
        setTitle("Chat P2P - UDP/TCP");
        
        // PAINEL DE CONFIGURAÇÃO (TOPO)
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Linha 0: Nome
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Seu Nome:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(20);
        topPanel.add(nameField, gbc);

        // Linha 1: Portas
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Porta Local:"), gbc);
        gbc.gridx = 1;
        localPortField = new JTextField(5);
        topPanel.add(localPortField, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Porta Remota:"), gbc);
        gbc.gridx = 3;
        remotePortField = new JTextField(5);
        topPanel.add(remotePortField, gbc);

        // Linha 2: IP Remoto e Flag TCP
        gbc.gridy = 2;
        gbc.gridx = 0;
        topPanel.add(new JLabel("IP Remoto:"), gbc);
        gbc.gridx = 1;
        remoteIpField = new JTextField("localhost", 10); // NOVO
        topPanel.add(remoteIpField, gbc);
        gbc.gridx = 2; gbc.gridwidth = 2;
        isTcpCheckBox = new JCheckBox("Usar TCP (orientado à conexão)", true); // NOVO
        topPanel.add(isTcpCheckBox, gbc);
        
        // Linha 3: Botão de Conectar
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        connectButton = new JButton("Conectar");
        topPanel.add(connectButton, gbc);

        // ÁREA DE MENSAGENS (CENTRO)
        messageArea = new JTextArea(20, 50);
        messageArea.setEditable(false);
        messageArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(messageArea);

        // PAINEL DE ENVIO (ABAIXO)
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        sendButton.setEnabled(false);
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // ORGANIZAÇÃO DO FRAME PRINCIPAL
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // LISTENERS DE EVENTOS
        connectButton.addActionListener(e -> connect());
        ActionListener sendMessageListener = e -> sendMessage();
        sendButton.addActionListener(sendMessageListener);
        messageField.addActionListener(sendMessageListener);

        // CONFIGURAÇÕES DA JANELA
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Ajusta o tamanho da janela aos componentes
        setResizable(false);
        setLocationRelativeTo(null); // Centraliza na tela
        setVisible(true);
    }

    private void connect() {
        try {
            // Coleta os dados da interface
            from = nameField.getText();
            int localPort = Integer.parseInt(localPortField.getText());
            String remoteIp = remoteIpField.getText(); // NOVO
            int remotePort = Integer.parseInt(remotePortField.getText());
            boolean isTcp = isTcpCheckBox.isSelected(); // NOVO

            if (from.trim().isEmpty() || remoteIp.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e IP Remoto são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Usa a fábrica para criar o ambiente de chat
            sender = ChatFactory.build(isTcp, remoteIp, remotePort, localPort, this);

            // Habilita/Desabilita campos após a conexão
            setFieldsEnabled(false);
            messageArea.append("Conectado! Pode começar a conversar.\n");
            messageField.requestFocusInWindow();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "As portas devem ser números válidos.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o chat: " + ex.getMessage() + "\nCausa: " + ex.getCause().getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String messageText = messageField.getText();
        if (sender != null && !messageText.trim().isEmpty()) {
            try {
                // Formata a mensagem com o separador e o remetente
                String messageToSend = String.format("%s%s%s", messageText, MessageContainer.FROM, from);
                sender.send(messageToSend);
                // Exibe a mensagem enviada localmente
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

        // Garante que a atualização da GUI ocorra na Thread de Eventos do Swing
        SwingUtilities.invokeLater(() -> {
            String[] messageParts = message.split(MessageContainer.FROM);
            String senderName = "Desconhecido";
            String msg = messageParts[0].trim();

            if (messageParts.length > 1) {
                senderName = messageParts[1].trim();
            }

            if (!msg.isEmpty()) {
                messageArea.append(String.format("%s> %s\n", senderName, msg));
                // Move o scroll para o final
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            }
        });
    }
    
    private void setFieldsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        localPortField.setEnabled(enabled);
        remoteIpField.setEnabled(enabled);
        remotePortField.setEnabled(enabled);
        isTcpCheckBox.setEnabled(enabled);
        connectButton.setEnabled(enabled);
        sendButton.setEnabled(!enabled);
    }

    public static void main(String[] args) {
        try {
            // Usa a aparência do sistema operacional
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}