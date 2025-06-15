#FacePass 🔐👤 - Abertura segura de portas com reconhecimento facial

O **FacePass** é uma aplicação desenvolvida para substituir crachás físicos no controle de acesso, utilizando reconhecimento facial como forma segura, eficiente e moderna de identificação. O projeto foi criado com base em conceitos de processamento de imagens em tempo real, utilizando Java e OpenCV, e visa resolver um problema real relacionado à segurança e praticidade nos acessos internos de uma empresa. Além de eliminar vulnerabilidades associadas ao uso de crachás, a solução promove mais agilidade e controle no ambiente corporativo.

---

## 1. Problema e Oportunidade ❗️💡

### Problema 🚫  
- Uso de crachás físicos apresenta fragilidades:  
  - Podem ser perdidos, esquecidos ou danificados;  
  - Podem ser emprestados, comprometendo a segurança interna;  
  - Dificultam a identificação real da pessoa que está entrando.

### Oportunidade 🌟  
Com a crescente necessidade de segurança nos ambientes empresariais, o reconhecimento facial se apresenta como solução promissora para o controle de acesso, garantindo que só pessoas previamente cadastradas terão acesso, evitando acessos não autorizados.

---

## 2. Solução proposta 🛠️

Sistema desenvolvido em Java utilizando OpenCV que realiza:

- **Cadastro facial:** o usuário insere nome, matrícula e uma foto capturada automaticamente pela webcam 📸;  
- **Reconhecimento:** ao detectar um rosto conhecido, o sistema reconhece a identidade e simula a abertura da porta 🚪, substituindo o uso de crachás físicos;  
- **Interface gráfica:** desenvolvida com Java Swing, oferecendo opções para cadastro, visualização dos cadastros e retorno à tela principal 🖥️.

---

## 3. Funcionalidades principais 🎯

- Detecção de rosto em tempo real via webcam;  
- Armazenamento de imagens para simular banco de dados 📂;  
- Interface gráfica intuitiva;  
- Simulação de resposta ao reconhecimento (ex: "Porta Aberta" ✅);  
- Verificação de duplicidade no cadastro (mesmo nome e matrícula) ⚠️.

---

## 4. Base teórica e técnicas de processamento de imagens 🧠📷

- **Detecção de rostos:** uso do classificador Haar Cascade (`haarcascade_frontalface_alt.xml`) para identificar rostos na imagem capturada;  
- **Conversão de imagem:** transformação da imagem colorida para escala de cinza, necessária para o classificador;  
- **Recorte e redimensionamento:** o rosto detectado é recortado da imagem geral e redimensionado para um tamanho padrão;  
- **Interface GUI com Swing:** entrada de dados, visualização da câmera e controle da aplicação 🎛️.

---

## 5. Plano de negócio 💼📈

Substituir crachás físicos por um sistema de reconhecimento facial de baixo custo, instalação simples e alta segurança, ampliando o controle e praticidade no acesso da empresa.

---

## 6. Referências 📚🔗

- Tutorial em vídeo: *Como configurar OpenCV no NetBeans com Maven*.  
  Disponível em: [https://www.youtube.com/watch?v=7DKbtM-BVLg](https://www.youtube.com/watch?v=7DKbtM-BVLg) ▶️  
- OpenCV 4.6 - Download oficial da biblioteca.  
  Disponível em: [https://opencv.org/releases/](https://opencv.org/releases/) ⬇️  
- Arquivo Haar Cascade para detecção facial `haarcascade_frontalface_alt.xml`.  
  Fonte oficial: Repositório OpenCV GitHub.  
  Disponível em: [https://github.com/opencv/opencv/blob/4.x/data/haarcascades/haarcascade_frontalface_alt.xml](https://github.com/opencv/opencv/blob/4.x/data/haarcascades/haarcascade_frontalface_alt.xml) 📂

---

## Instruções para executar ▶️🖥️

1. Clone o repositório.  
2. Abra o projeto no NetBeans (com Maven configurado).  
3. Certifique-se de ter o OpenCV 4.6 instalado e configurado.  
4. Execute o projeto, que abrirá a interface gráfica para cadastro e reconhecimento facial.  
5. Utilize a webcam para capturar imagens e testar o sistema.