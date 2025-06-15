/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastro;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Amand
 */
public class CadastroRosto {

    static {
        System.load("C:/opencv/build/java/x64/opencv_java460.dll");
    }

    private volatile boolean exibindo = true;
    private Mat imagemAtual = new Mat();
    private Mat imagemCinza = new Mat();
    private MatOfRect facesDetectadas = new MatOfRect();
    private CascadeClassifier detectorFace;

    public void abrir() {
        JFrame frame = new JFrame("Cadastro de Rosto");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Color.WHITE);

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel titulo = new JLabel("Cadastrar Novo Rosto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(0xF7A000));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        painelForm.add(titulo, gbc);

        JLabel labelNome = new JLabel("Nome:");
        labelNome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy++;
        painelForm.add(labelNome, gbc);

        JTextField campoNome = new JTextField();
        campoNome.setPreferredSize(new Dimension(300, 40));
        campoNome.setBorder(new RoundedBorder(15, new Color(0xF7A000)));
        gbc.gridy++;
        painelForm.add(campoNome, gbc);

        JLabel labelMatricula = new JLabel("Matrícula:");
        labelMatricula.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy++;
        painelForm.add(labelMatricula, gbc);

        JTextField campoMatricula = new JTextField();
        campoMatricula.setPreferredSize(new Dimension(300, 40));
        campoMatricula.setBorder(new RoundedBorder(15, new Color(0xF7A000)));
        gbc.gridy++;
        painelForm.add(campoMatricula, gbc);

        JButton botaoTirarFoto = criarBotao("Tirar Foto", true, 150);
        gbc.gridy++;
        painelForm.add(botaoTirarFoto, gbc);

        JButton botaoVerCadastros = criarBotao("Ver Cadastros", true, 150);
        gbc.gridy++;
        painelForm.add(botaoVerCadastros, gbc);

        JButton botaoVoltar = criarBotao("Voltar", false, 120);
        gbc.gridy++;
        painelForm.add(botaoVoltar, gbc);

        JPanel painelCamera = new JPanel(new BorderLayout());
        painelCamera.setBackground(Color.WHITE);
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);
        painelCamera.add(imageLabel, BorderLayout.CENTER);

        painelPrincipal.add(painelForm, BorderLayout.WEST);
        painelPrincipal.add(painelCamera, BorderLayout.CENTER);
        frame.setContentPane(painelPrincipal);
        frame.setVisible(true);

        String caminhoCascade = getClass().getResource("/classificadores/haarcascade_frontalface_alt.xml")
                .getPath().replaceFirst("^/", "");
        detectorFace = new CascadeClassifier(caminhoCascade);
        if (detectorFace.empty()) {
            JOptionPane.showMessageDialog(frame, "Erro ao carregar classificador.");
            return;
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(frame, "Erro ao acessar a câmera.");
            return;
        }

        Thread cameraThread = new Thread(() -> {
            while (exibindo && camera.read(imagemAtual)) {
                if (!imagemAtual.empty()) {
                    Imgproc.cvtColor(imagemAtual, imagemCinza, Imgproc.COLOR_BGR2GRAY);
                    detectorFace.detectMultiScale(imagemCinza, facesDetectadas);
                    for (Rect face : facesDetectadas.toArray()) {
                        Imgproc.rectangle(imagemAtual, face.tl(), face.br(), new Scalar(0, 255, 0), 2);
                    }

                    BufferedImage imagemConvertida = toBufferedImage(imagemAtual);
                    if (imagemConvertida != null) {
                        ImageIcon icon = new ImageIcon(imagemConvertida);
                        SwingUtilities.invokeLater(() -> imageLabel.setIcon(icon));
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    break;
                }
            }
            camera.release();
        });
        cameraThread.start();

        botaoTirarFoto.addActionListener(e -> {
            String nome = campoNome.getText().trim();
            String matricula = campoMatricula.getText().trim();

            if (nome.isEmpty() || matricula.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Preencha nome e matrícula.");
                return;
            }

            if (facesDetectadas.toArray().length == 0) {
                JOptionPane.showMessageDialog(frame, "Nenhum rosto detectado.");
                return;
            }

            String pastaDestino = "src/main/resources/rostos/";
            new File(pastaDestino).mkdirs();

            File[] arquivos = new File(pastaDestino).listFiles();
            String nomeAtual = nome.replaceAll("\\s+", "_") + "_" + matricula;

            if (arquivos != null) {
                for (File arq : arquivos) {
                    String nomeMatriculaExistente = arq.getName().replace(".jpg", "");
                    if (nomeMatriculaExistente.equalsIgnoreCase(nomeAtual)) {
                        JOptionPane.showMessageDialog(frame, "Este nome com esta matrícula já está cadastrado.");
                        return;
                    }
                }
            }

            Rect face = facesDetectadas.toArray()[0];
            Mat faceRecortada = new Mat(imagemCinza, face);
            Imgproc.resize(faceRecortada, faceRecortada, new Size(160, 160));

            String nomeArquivo = nomeAtual + ".jpg";
            File arquivo = new File(pastaDestino + nomeArquivo);
            Imgcodecs.imwrite(arquivo.getAbsolutePath(), faceRecortada);
            JOptionPane.showMessageDialog(frame, "Rosto salvo com sucesso: " + nomeArquivo);
            campoNome.setText("");
            campoMatricula.setText("");
        });

        botaoVerCadastros.addActionListener(e -> {
            File pasta = new File("src/main/resources/rostos/");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
            try {
                Desktop.getDesktop().open(pasta);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Não foi possível abrir a pasta.");
            }
        });

        botaoVoltar.addActionListener(e -> {
            exibindo = false;
            frame.dispose();
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exibindo = false;
                super.windowClosing(e);
            }
        });
    }

    private BufferedImage toBufferedImage(Mat mat) {
        int tipo = (mat.channels() == 1) ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        byte[] dados = new byte[mat.rows() * mat.cols() * (int) mat.elemSize()];
        mat.get(0, 0, dados);
        BufferedImage imagem = new BufferedImage(mat.cols(), mat.rows(), tipo);
        byte[] pixels = ((DataBufferByte) imagem.getRaster().getDataBuffer()).getData();
        System.arraycopy(dados, 0, pixels, 0, dados.length);
        return imagem;
    }

    public static class RoundedBorder extends AbstractBorder {

        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 8;
            return insets;
        }
    }

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
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        botao.setForeground(laranja ? Color.WHITE : Color.DARK_GRAY);
        botao.setContentAreaFilled(false);
        botao.setOpaque(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setMaximumSize(new Dimension(largura, 40));
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return botao;
    }

    public static void main(String[] args) {
        new CadastroRosto().abrir();
    }
}
