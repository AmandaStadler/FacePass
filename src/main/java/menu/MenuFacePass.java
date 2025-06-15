/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package menu;

import cadastro.CadastroRosto;
import reconhecimento.Reconhecimento;
import org.opencv.core.Core;
import org.bytedeco.opencv.global.opencv_core;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Amand
 */
public class MenuFacePass {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Continental FacePass - Controle de Acesso por Reconhecimento Facial");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        frame.setLayout(new BorderLayout());

        //LOGO
        JPanel painelLogo = new JPanel();
        painelLogo.setOpaque(false);
        painelLogo.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));

        try {
            ImageIcon logoIcon = new ImageIcon(MenuFacePass.class.getResource("/images/logo.png"));
            Image logo = logoIcon.getImage().getScaledInstance(220, 160, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logo));
            painelLogo.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Erro ao carregar a logo.");
        }

        //PAINEL CENTRAL
        JPanel painelCaixa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        painelCaixa.setPreferredSize(new Dimension(450, 280));
        painelCaixa.setOpaque(false);
        painelCaixa.setLayout(new BoxLayout(painelCaixa, BoxLayout.Y_AXIS));
        painelCaixa.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        //TÍTULO
        JLabel titulo = new JLabel("FACEPASS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0xF7A000));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCaixa.add(titulo);
        painelCaixa.add(Box.createVerticalStrut(20));

        //BOTÕES
        JButton btnCadastrar = criarBotao("Cadastrar", true, 200);
        JButton btnReconhecer = criarBotao("Reconhecer", true, 200);
        JButton btnSair = criarBotao("Sair", false, 120);

        btnCadastrar.addActionListener(e -> new CadastroRosto().abrir());
        btnReconhecer.addActionListener(e -> new Reconhecimento().abrir());
        btnSair.addActionListener(e -> System.exit(0));

        painelCaixa.add(btnCadastrar);
        painelCaixa.add(Box.createVerticalStrut(10));
        painelCaixa.add(btnReconhecer);
        painelCaixa.add(Box.createVerticalStrut(10));
        painelCaixa.add(btnSair);

        //CENTRALIZADOR
        JPanel painelCentralizado = new JPanel(new GridBagLayout());
        painelCentralizado.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(-150, 0, 0, 0); // ← Sobe o painel na tela

        painelCentralizado.add(painelCaixa, gbc);

        frame.add(painelLogo, BorderLayout.NORTH);
        frame.add(painelCentralizado, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    //MÉTODO PARA CRIAR BOTÕES
    private static JButton criarBotao(String texto, boolean laranja, int largura) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isEnabled()) {
                    g2.setColor(new Color(220, 220, 220));
                } else if (getModel().isPressed()) {
                    g2.setColor(laranja ? new Color(255, 180, 0) : new Color(200, 200, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(laranja ? new Color(255, 200, 60) : new Color(230, 230, 230));
                } else {
                    g2.setColor(laranja ? new Color(0xF7A000) : new Color(220, 220, 220));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                if (laranja) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0xF7A000));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    g2.dispose();
                }
            }
        };

        botao.setFocusPainted(false);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        botao.setForeground(laranja ? Color.WHITE : Color.DARK_GRAY);
        botao.setContentAreaFilled(false);
        botao.setOpaque(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setMaximumSize(new Dimension(largura, 40)); // ← Largura flexível
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return botao;
    }
}
