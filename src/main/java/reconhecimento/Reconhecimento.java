/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reconhecimento;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author Amand
 */
public class Reconhecimento {

    private static volatile boolean rodando = true;
    private static final String PASTA_ROSTOS = "src/main/resources/rostos/";
    private static final double LIMIAR_SEMELHANCA = 0.7;

    // Estrutura para guardar histograma e nome
    private static class HistogramaComNome {

        Mat histograma;
        String nome;

        HistogramaComNome(Mat histograma, String nome) {
            this.histograma = histograma;
            this.nome = nome;
        }
    }

    public void abrir() {
        System.load("C:/opencv/build/java/x64/opencv_java460.dll");

        List<HistogramaComNome> histNomes = carregarHistogramasComNomes();

        if (histNomes.isEmpty()) {
            System.out.println("Nenhuma imagem encontrada em " + PASTA_ROSTOS);
            return;
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Erro ao acessar a câmera.");
            return;
        }

        String classificadorPath = "src/main/resources/classificadores/haarcascade_frontalface_alt.xml";
        CascadeClassifier detectorFace = new CascadeClassifier(classificadorPath);
        if (detectorFace.empty()) {
            System.out.println("Erro ao carregar o classificador Haar.");
            return;
        }

        JFrame janela = new JFrame("Reconhecimento Facial");
        JLabel label = new JLabel();
        janela.getContentPane().setBackground(Color.WHITE);  // Fundo branco
        janela.add(label);
        janela.setSize(640, 480);
        janela.setVisible(true);
        janela.setLocationRelativeTo(null);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        janela.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    rodando = false;
                }
            }
        });

        janela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                rodando = false;
            }
        });

        rodando = true;

        Thread cameraThread = new Thread(() -> {
            Mat frame = new Mat();
            Mat imagemCinza = new Mat();
            MatOfRect faces = new MatOfRect();

            while (rodando) {
                if (!camera.read(frame)) {
                    continue;
                }

                Imgproc.cvtColor(frame, imagemCinza, Imgproc.COLOR_BGR2GRAY);
                detectorFace.detectMultiScale(imagemCinza, faces);

                if (!faces.empty()) {
                    for (Rect face : faces.toArray()) {
                        Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0), 2);

                        Mat rostoRecortado = new Mat(imagemCinza, face);
                        Mat histAtual = calcularHistograma(rostoRecortado);
                        String nomeReconhecido = reconhecer(histAtual, histNomes);

                        String texto;
                        Scalar corTexto;

                        if (nomeReconhecido != null) {
                            texto = "Porta aberta para colaborador " + nomeReconhecido;
                            corTexto = new Scalar(0, 255, 0);  // Verde
                        } else {
                            texto = "Porta fechada colaborador nao reconhecido";
                            corTexto = new Scalar(0, 0, 255);  // Vermelho
                        }

                        Imgproc.putText(frame, texto, new Point(face.x, face.y - 10),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, corTexto, 2);
                    }
                }

                BufferedImage imagemExibida = matParaBufferedImage(frame);
                if (imagemExibida != null) {
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(imagemExibida));
                        label.repaint();
                    });
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            camera.release();
            janela.dispose();
            System.out.println("Reconhecimento encerrado.");
        });

        cameraThread.start();
    }

    private static List<HistogramaComNome> carregarHistogramasComNomes() {
        List<HistogramaComNome> lista = new ArrayList<>();
        File pasta = new File(PASTA_ROSTOS);
        File[] arquivos = pasta.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (arquivos == null || arquivos.length == 0) {
            return lista;
        }

        for (File arquivo : arquivos) {
            Mat imagem = Imgcodecs.imread(arquivo.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
            if (!imagem.empty()) {
                Mat hist = calcularHistograma(imagem);
                String nomeArquivo = arquivo.getName();
                String nome = nomeArquivo.substring(0, nomeArquivo.lastIndexOf('.'));
                lista.add(new HistogramaComNome(hist, nome));
            }
        }

        return lista;
    }

    private static String reconhecer(Mat histAtual, List<HistogramaComNome> histNomes) {
        for (HistogramaComNome hn : histNomes) {
            double correlacao = Imgproc.compareHist(histAtual, hn.histograma, Imgproc.CV_COMP_CORREL);
            if (correlacao >= LIMIAR_SEMELHANCA) {
                return hn.nome;
            }
        }
        return null;
    }

    private static Mat calcularHistograma(Mat imagem) {
        List<Mat> lista = new ArrayList<>();
        lista.add(imagem);
        Mat hist = new Mat();
        Imgproc.calcHist(lista, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0, 256));
        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
        return hist;
    }

    private static BufferedImage matParaBufferedImage(Mat mat) {
        int tipo = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer);
        BufferedImage imagem = new BufferedImage(mat.cols(), mat.rows(), tipo);
        byte[] pixels = ((DataBufferByte) imagem.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, pixels, 0, buffer.length);
        return imagem;
    }

    public static void main(String[] args) {
        new Reconhecimento().abrir();
    }
}
