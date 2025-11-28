/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.jogo.telas;

import java.util.Random;
import javax.swing.JOptionPane;
import br.com.jogo.dal.ModeloConexao;
import java.sql.*;

/**
 *
 * @author Julimar
 */
public class TelaJogo extends javax.swing.JFrame {

    /**
     * Creates new form TelaJogo
     */
    public TelaJogo() {
        initComponents();
        exibirNomeJogador();
    }

    Random rand = new Random();
    TelaInicial inicio = new TelaInicial();

    int aleatorioJogador = 0, aleatorioCpu = 0, pontoJogador = 0, pontoCpu = 0;
    String textoJogador, textoCpu;

    public void exibirNomeJogador() {
        int idDoJogador = br.com.jogo.telas.TelaCadJogador.idJogadorAtual;
        String sql = "SELECT nomejogador FROM tbjogador WHERE id = ?";
        if (idDoJogador == 0) {
            lblNomeJogador.setText("Jogador não identificado");
            return;
        }

        try (Connection conexao = ModeloConexao.conector();
                java.sql.PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setInt(1, idDoJogador);
            try (java.sql.ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    String nome = rs.getString("nomejogador");
                    lblNomeJogador.setText(nome);
                } else {
                    lblNomeJogador.setText("ID de Jogador não encontrado!");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar nome do jogador: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            lblNomeJogador.setText("Erro ao carregar nome.");
        }
    }

    public void exibirCpu() {

        // 1. GERA UM NÚMERO ALEATÓRIO (1=Pedra, 2=Papel, 3=Tesoura)
        // Supondo que você use 'rand' (Random) para gerar o número.
        aleatorioCpu = rand.nextInt(3) + 1;

        String caminhoDaImagem;

        // 2. DEFINE O CAMINHO DA IMAGEM BASEADO NO SORTEIO
        if (aleatorioCpu == 1) {
            caminhoDaImagem = "/br/com/jogo/img/pedra_grande.png";
        } else if (aleatorioCpu == 2) {
            caminhoDaImagem = "/br/com/jogo/img/papel_grande.png";
        } else if (aleatorioCpu == 3) {
            caminhoDaImagem = "/br/com/jogo/img/tesoura_grande.png";
        } else {
            // Se algo der errado no sorteio, define null para evitar N.P.E.
            caminhoDaImagem = null;
        }

        // 3. CARREGA E EXIBE A IMAGEM DE FORMA SEGURA
        if (caminhoDaImagem != null) {
            try {
                // Cria o ícone usando o caminho definido
                javax.swing.ImageIcon icone = new javax.swing.ImageIcon(
                        getClass().getResource(caminhoDaImagem)
                );

                // Aplica o ícone ao JLabel do CPU
                lblCampoCpu.setIcon(icone);

            } catch (Exception e) {
                // Se o arquivo não existir (NullPointerException aqui), avisa no console
                System.err.println("Erro Crítico: Imagem não encontrada em: " + caminhoDaImagem);
                e.printStackTrace();
            }
        }
    }

    public void exibirJogador() {

        String caminhoDaImagem;

        // 2. DEFINE O CAMINHO DA IMAGEM BASEADO NO SORTEIO
        if (aleatorioJogador == 1) {
            caminhoDaImagem = "/br/com/jogo/img/pedra_grande.png";
        } else if (aleatorioJogador == 2) {
            caminhoDaImagem = "/br/com/jogo/img/papel_grande.png";
        } else if (aleatorioJogador == 3) {
            caminhoDaImagem = "/br/com/jogo/img/tesoura_grande.png";
        } else {
            // Se algo der errado no sorteio, define null para evitar N.P.E.
            caminhoDaImagem = null;
        }

        // 3. CARREGA E EXIBE A IMAGEM DE FORMA SEGURA
        if (caminhoDaImagem != null) {
            try {
                // Cria o ícone usando o caminho definido
                javax.swing.ImageIcon icone = new javax.swing.ImageIcon(
                        getClass().getResource(caminhoDaImagem)
                );

                // Aplica o ícone ao JLabel do CPU
                lblCampoJogador.setIcon(icone);

            } catch (Exception e) {
                // Se o arquivo não existir (NullPointerException aqui), avisa no console
                System.err.println("Erro Crítico: Imagem não encontrada em: " + caminhoDaImagem);
                e.printStackTrace();
            }
        }
    }

    public void placaJogo() {

        exibirCpu();
        exibirJogador();

        if ((aleatorioJogador == 1) && (aleatorioCpu == 3)
                || (aleatorioJogador == 2) && (aleatorioCpu == 1)
                || (aleatorioJogador == 3) && (aleatorioCpu == 2)) {
            pontoJogador++;
        } else if ((aleatorioCpu == 1) && (aleatorioJogador == 3)
                || (aleatorioCpu == 2) && (aleatorioJogador == 1)
                || (aleatorioCpu == 3) && (aleatorioJogador == 2)) {
            pontoCpu++;
        }

        lblPontosJogador.setText(String.valueOf(pontoJogador));
        lblPontosCpu.setText(String.valueOf(pontoCpu));
    }

    public void salvarJogador() {
        String sql = "UPDATE tbjogador SET pontuacao = ?, resultado_final = ? WHERE id = ?";

        // Verifica se o ID do jogador foi capturado
        int idDoJogador = br.com.jogo.telas.TelaCadJogador.idJogadorAtual;

        if (idDoJogador == 0) {
            System.err.println("ERRO: ID do jogador não foi encontrado para salvar a pontuação.");
            return;
        }

        try (Connection conexao = ModeloConexao.conector();
                java.sql.PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setInt(1, pontoJogador);
            pst.setString(2, textoJogador);
            pst.setInt(3, idDoJogador);
            int linhasAfetadas = pst.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Pontuação e resultado final salvos com sucesso! ID: " + idDoJogador);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pontuação: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void salvarCpu() {
        String sql = "INSERT INTO tbcpu (pontuacao, resultado_final) VALUES (?, ?)";

        try (Connection conexao = ModeloConexao.conector();
                java.sql.PreparedStatement pst = conexao.prepareStatement(sql)) {
            pst.setInt(1, pontoCpu);
            pst.setString(2, textoCpu);

            int linhasAfetadas = pst.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Placar do CPU registrado com sucesso (NOVO REGISTRO)!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar pontuação do CPU: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void EncerrarJogo() {
        if ((pontoJogador == 3) || (pontoCpu == 3)) {

            if (pontoJogador == 3) {
                textoJogador = "VITÓRIA";
                textoCpu = "DERROTA";

                JOptionPane.showMessageDialog(null, "FIM DE JOGO! Você VENCEU!");

            } else if (pontoCpu == 3) {
                textoJogador = "DERROTA";
                textoCpu = "VITÓRIA";

                JOptionPane.showMessageDialog(null, "FIM DE JOGO! Você PERDEU!");

            }

            salvarJogador();
            salvarCpu();
            TelaInicial inicio = new TelaInicial();
            inicio.setVisible(true);
            this.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblPontosCpu = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblNomeJogador = new javax.swing.JLabel();
        lblPontosJogador = new javax.swing.JLabel();
        btnPedra = new javax.swing.JButton();
        btnPapel = new javax.swing.JButton();
        btnTesoura = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblCampoCpu = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblCampoJogador = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tela de jogo");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/jogo/img/icone_jogador.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(494, 557, -1, -1));

        jPanel1.setBackground(new java.awt.Color(179, 168, 242));

        lblPontosCpu.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel2.setText("CPU");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPontosCpu, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(180, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPontosCpu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(27, 27, 27))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(146, 49, -1, 90));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/jogo/img/rival.png"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 14, -1, -1));

        jPanel2.setBackground(new java.awt.Color(179, 224, 230));

        lblNomeJogador.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N

        lblPontosJogador.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(301, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPontosJogador)
                    .addComponent(lblNomeJogador))
                .addGap(19, 19, 19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNomeJogador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(lblPontosJogador)
                .addGap(24, 24, 24))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 580, 320, 100));

        btnPedra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/jogo/img/pedra.png"))); // NOI18N
        btnPedra.setToolTipText("");
        btnPedra.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPedra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedraActionPerformed(evt);
            }
        });
        getContentPane().add(btnPedra, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 465, -1, -1));

        btnPapel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/jogo/img/papel.png"))); // NOI18N
        btnPapel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPapel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPapelActionPerformed(evt);
            }
        });
        getContentPane().add(btnPapel, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 465, -1, -1));

        btnTesoura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/jogo/img/tesoura.png"))); // NOI18N
        btnTesoura.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTesoura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTesouraActionPerformed(evt);
            }
        });
        getContentPane().add(btnTesoura, new org.netbeans.lib.awtextra.AbsoluteConstraints(381, 465, -1, -1));

        jPanel3.setBackground(new java.awt.Color(179, 168, 242));

        lblCampoCpu.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(lblCampoCpu, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblCampoCpu, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(121, 205, -1, -1));

        jPanel4.setBackground(new java.awt.Color(179, 224, 230));

        lblCampoJogador.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblCampoJogador, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(lblCampoJogador, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(371, 205, -1, 190));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/jogo/img/background.jpg"))); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(-120, 0, 780, 710));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPedraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedraActionPerformed
        aleatorioJogador = 1;
        placaJogo();
        EncerrarJogo();
    }//GEN-LAST:event_btnPedraActionPerformed

    private void btnPapelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPapelActionPerformed
        aleatorioJogador = 2;
        placaJogo();
        EncerrarJogo();
    }//GEN-LAST:event_btnPapelActionPerformed

    private void btnTesouraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTesouraActionPerformed
        aleatorioJogador = 3;
        placaJogo();
        EncerrarJogo();
    }//GEN-LAST:event_btnTesouraActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaJogo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaJogo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPapel;
    private javax.swing.JButton btnPedra;
    private javax.swing.JButton btnTesoura;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblCampoCpu;
    private javax.swing.JLabel lblCampoJogador;
    private javax.swing.JLabel lblNomeJogador;
    private javax.swing.JLabel lblPontosCpu;
    private javax.swing.JLabel lblPontosJogador;
    // End of variables declaration//GEN-END:variables
}
