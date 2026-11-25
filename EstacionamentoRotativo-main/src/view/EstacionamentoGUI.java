package view;

import exceptions.VagaNaoDisponivelException;
import interfaces.Tarifavel;
import model.TicketEstacionamento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstacionamentoGUI extends JFrame implements ActionListener, WindowListener {

    protected Dimension dFrame, dLabel, dTextField, dButton, dTextArea;
    protected JButton cmdEntrada, cmdConsulta, cmdRetirada, cmdSair;
    protected JTextField txtPlaca, txtVaga;
    protected JLabel lblPlaca, lblVaga, lblStatusVagas;
    protected JTextArea txtMsg;
    protected JScrollPane scrollPane;

    // Lista em memória utilizando Generics e Collections
    private List<TicketEstacionamento> listaTickets;

    // Capacidade total de vagas do estacionamento
    private static final int TOTAL_VAGAS = 20;

    public EstacionamentoGUI() {
        listaTickets = new ArrayList<>();

        // Dimensões dos componentes
        dFrame = new Dimension(400, 520);
        dLabel = new Dimension(50, 20);
        dTextField = new Dimension(150, 20);
        dButton = new Dimension(105, 25);
        dTextArea = new Dimension(330, 150);

        // Propriedades da Janela Principal
        setTitle("Controle de Estacionamento Rotativo");
        setResizable(false);
        setSize(dFrame);
        setLocation(200, 200);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Componentes - Rótulos e Campos de Texto
        lblPlaca = new JLabel("Placa:");
        lblPlaca.setSize(dLabel);
        lblPlaca.setLocation(25, 20);
        add(lblPlaca);

        txtPlaca = new JTextField();
        txtPlaca.setSize(dTextField);
        txtPlaca.setLocation(80, 20);
        add(txtPlaca);

        lblVaga = new JLabel("Vaga:");
        lblVaga.setSize(dLabel);
        lblVaga.setLocation(25, 55);
        add(lblVaga);

        txtVaga = new JTextField();
        txtVaga.setSize(dTextField);
        txtVaga.setLocation(80, 55);
        add(txtVaga);

        // Label para exibir o status das vagas ocupadas / total
        lblStatusVagas = new JLabel();
        lblStatusVagas.setSize(new Dimension(330, 20));
        lblStatusVagas.setLocation(25, 90);
        lblStatusVagas.setFont(new Font("Arial", Font.BOLD, 12));
        add(lblStatusVagas);
        atualizarStatusVagas();

        // Botões
        cmdEntrada = new JButton("Entrada");
        cmdEntrada.setSize(dButton);
        cmdEntrada.setLocation(25, 125);
        add(cmdEntrada);

        cmdRetirada = new JButton("Retirada");
        cmdRetirada.setSize(dButton);
        cmdRetirada.setLocation(155, 125);
        add(cmdRetirada);

        cmdConsulta = new JButton("Consulta");
        cmdConsulta.setSize(dButton);
        cmdConsulta.setLocation(25, 160);
        add(cmdConsulta);

        cmdSair = new JButton("Sair");
        cmdSair.setSize(dButton);
        cmdSair.setLocation(155, 160);
        add(cmdSair);

        // Área de Texto com ScrollPane
        txtMsg = new JTextArea();
        scrollPane = new JScrollPane(txtMsg);
        scrollPane.setSize(dTextArea);
        scrollPane.setLocation(25, 200);
        txtMsg.setEditable(false);
        add(scrollPane);

        // Registro de Listeners
        cmdEntrada.addActionListener(this);
        cmdRetirada.addActionListener(this);
        cmdConsulta.addActionListener(this);
        cmdSair.addActionListener(this);
        this.addWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmdEntrada) {
            cadastrarEntrada();
        } else if (e.getSource() == cmdRetirada) {
            realizarRetirada();
        } else if (e.getSource() == cmdConsulta) {
            consultarTickets();
        } else if (e.getSource() == cmdSair) {
            fecharAplicacao();
        }
    }

    private void atualizarStatusVagas() {
        long vagasOcupadas = listaTickets.stream().filter(t -> t.getSaida() == null).count();
        lblStatusVagas.setText("Vagas Ocupadas: " + vagasOcupadas + " / " + TOTAL_VAGAS);
    }

    private void cadastrarEntrada() {
        try {
            if (txtPlaca.getText().trim().isEmpty() || txtVaga.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Preencha todos os campos (Placa e Vaga).");
            }

            int vaga = Integer.parseInt(txtVaga.getText().trim());

            if (vaga < 1 || vaga > TOTAL_VAGAS) {
                throw new IllegalArgumentException("Número de vaga inválido! Escolha entre 1 e " + TOTAL_VAGAS + ".");
            }

            String placa = txtPlaca.getText().trim();

            for (TicketEstacionamento t : listaTickets) {
                if (t.getVaga() == vaga && t.getSaida() == null) {
                    throw new VagaNaoDisponivelException("A vaga " + vaga + " já está ocupada!");
                }
            }

            TicketEstacionamento novoTicket = new TicketEstacionamento(placa, vaga);
            listaTickets.add(novoTicket);

            Collections.sort(listaTickets);

            atualizarStatusVagas();
            JOptionPane.showMessageDialog(null, "Entrada registrada com sucesso para a placa: " + placa);
            txtPlaca.setText("");
            txtVaga.setText("");
            consultarTickets();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Erro: O número da vaga deve ser um valor inteiro válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (VagaNaoDisponivelException e) {
            JOptionPane.showMessageDialog(null, "Erro de Negócio: " + e.getMessage(), "Vaga Indisponível", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void realizarRetirada() {
        try {
            String placaBusca = JOptionPane.showInputDialog(null, "Informe a placa do veículo para saída:");
            if (placaBusca == null || placaBusca.trim().isEmpty()) return;

            boolean encontrado = false;
            for (TicketEstacionamento t : listaTickets) {
                if (t.getPlaca().equalsIgnoreCase(placaBusca.trim()) && t.getSaida() == null) {
                    t.setSaida(java.time.LocalDateTime.now());
                    encontrado = true;

                    // Mensagem informando os detalhes da saída e que a vaga ficou livre
                    JOptionPane.showMessageDialog(null,
                            "Saída registrada com sucesso!\n" +
                                    "Vaga " + t.getVaga() + " agora está LIVRE.\n" +
                                    "Tempo de permanência: " + t.getMinutosPermanencia() + " min\n" +
                                    "Total a Pagar: " + t.formatarValor(t.calcularPrecoEstadia()),
                            "Saída de Veículo",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Nenhum ticket ativo encontrado para a placa: " + placaBusca, "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            atualizarStatusVagas();
            consultarTickets();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao processar saída: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consultarTickets() {
        txtMsg.setText("");
        if (listaTickets.isEmpty()) {
            txtMsg.setText("Nenhum veículo registrado no estacionamento.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (TicketEstacionamento t : listaTickets) {
            sb.append(t.toString()).append("\n");
        }
        txtMsg.setText(sb.toString());
    }

    private void fecharAplicacao() {
        int resposta = JOptionPane.showConfirmDialog(null, "Deseja realmente sair do sistema?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        fecharAplicacao();
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}