Gabriel Bazan Pereira - Programação Orientada a Objetos - ADS Fatec Mogi Mirim.

Foi utilizada a IA do Google Gemini na criação e edição da interface GUI
Aqui estão os prompts (detalhe: o prompt foi criado com base nas próprias instruções do material. além da indexação de imagens para auxiliar):

Interface Gráfica com Usuário: As telas devem ser desenvolvidas utilizando a biblioteca javax.swing.* (como
JFrame , JTextField , JLabel , JButton , JTextArea / JScrollPane ), tratando os eventos de clique com
ActionListener e os fechamentos de janela com WindowListener . O design deve seguir a padronização e boas
práticas vistas em aula (exemplo das classes TelaMovimento e TelaCalculadora ).

Tratamento de Exceções: É obrigatória a criação de pelo menos uma Checked Exception customizada (herdando
diretamente de Exception , nos moldes do exercício SemSaldoException do material 08), além da captura de erros
comuns como NumberFormatException na conversão de campos de texto. As mensagens de alerta e de erro devem
ser devolvidas visualmente ao usuário via JOptionPane.showMessageDialog() .

O código do SemSaldoException está nas imagens. O TelaMovimento também