package com.mycompany.saidera_project.ui;

import com.mycompany.saidera_project.data.DataRepository;
import com.mycompany.saidera_project.models.Product;
import com.mycompany.saidera_project.models.StockItem;
import com.mycompany.saidera_project.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Diálogo de exportação de dados do sistema.
 * Permite exportar Produtos, Estoque ou Usuários nos formatos CSV ou PDF,
 * sem dependências externas — CSV via java.io, PDF via java.awt.print.
 */
public class ExportDialog extends JDialog {

    // ── Tipos de dados exportáveis ──────────────────────────────────────────
    private static final String[] DATASETS = {"Cardápio (Produtos)", "Controle de Estoque", "Usuários"};

    // ── Referências de UI ───────────────────────────────────────────────────
    private JComboBox<String> datasetCombo;
    private JToggleButton csvBtn;
    private JToggleButton pdfBtn;
    private JLabel previewLabel;
    private JButton exportBtn;
    private JLabel statusLabel;

    // ── Estado ──────────────────────────────────────────────────────────────
    private String selectedFormat = "CSV";

    public ExportDialog(Frame owner) {
        super(owner, "Exportar Dados", true);
        setSize(520, 470);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        buildUI();
    }

    // ───────────────────────────────────────────────────────────────────────
    // UI Construction
    // ───────────────────────────────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIPalette.SURFACE);
        root.setBorder(BorderFactory.createLineBorder(UIPalette.SLATE, 1));
        setContentPane(root);

        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIPalette.SLATE);
        header.setBorder(new EmptyBorder(22, 28, 22, 28));

        // Draggable window support
        final Point[] initialClick = {null};
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick[0] != null) {
                    Point windowLoc = getLocation();
                    int xMoved = e.getX() - initialClick[0].x;
                    int yMoved = e.getY() - initialClick[0].y;
                    setLocation(windowLoc.x + xMoved, windowLoc.y + yMoved);
                }
            }
        });

        JLabel title = new JLabel("Exportar Dados");
        title.setFont(UIPalette.FONT_DISPLAY.deriveFont(Font.BOLD, 20f));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Escolha o conjunto de dados e o formato do arquivo");
        subtitle.setFont(UIPalette.FONT_BODY.deriveFont(13f));
        subtitle.setForeground(new Color(255, 255, 255, 180));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 3));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);
        header.add(titles, BorderLayout.CENTER);

        // Ícone de fechar no header
        JButton closeX = new JButton("✕");
        closeX.setFont(UIPalette.FONT_BODY.deriveFont(14f));
        closeX.setForeground(new Color(255, 255, 255, 200));
        closeX.setContentAreaFilled(false);
        closeX.setBorderPainted(false);
        closeX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeX.addActionListener(e -> dispose());
        header.add(closeX, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // ── Body ───────────────────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UIPalette.SURFACE);
        body.setBorder(new EmptyBorder(24, 28, 10, 28));

        // -- Seleção de dados --
        JLabel sec1 = sectionLabel("1. Selecione os dados a exportar");
        sec1.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sec1);
        body.add(Box.createVerticalStrut(10));

        datasetCombo = new JComboBox<>(DATASETS);
        datasetCombo.setFont(UIPalette.FONT_BODY);
        datasetCombo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        datasetCombo.setMinimumSize(new Dimension(50, 38));
        datasetCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        datasetCombo.putClientProperty("FlatLaf.style", "arc: 8");
        datasetCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        datasetCombo.addActionListener(e -> updatePreview());
        body.add(datasetCombo);

        body.add(Box.createVerticalStrut(20));

        // -- Seleção de formato --
        JLabel sec2 = sectionLabel("2. Formato do arquivo");
        sec2.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sec2);
        body.add(Box.createVerticalStrut(10));

        JPanel formatRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        formatRow.setOpaque(false);
        formatRow.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        formatRow.setMinimumSize(new Dimension(310, 40));
        formatRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formatRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        csvBtn = formatToggle("CSV", "📊", true);
        pdfBtn = formatToggle("PDF", "📄", false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(csvBtn);
        bg.add(pdfBtn);

        csvBtn.addActionListener(e -> { selectedFormat = "CSV"; updatePreview(); });
        pdfBtn.addActionListener(e -> { selectedFormat = "PDF"; updatePreview(); });

        formatRow.add(csvBtn);
        formatRow.add(pdfBtn);
        body.add(formatRow);

        body.add(Box.createVerticalStrut(20));

        // -- Pré-visualização --
        JLabel sec3 = sectionLabel("3. Resumo da exportação");
        sec3.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sec3);
        body.add(Box.createVerticalStrut(10));

        // Painel com cantos arredondados e borda sutil para a pré-visualização
        JPanel previewPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x1E293B) : new Color(0xF0F4FF));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(com.formdev.flatlaf.FlatLaf.isLafDark() ? new Color(0x334155) : new Color(0xD0E0FF));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        previewPanel.setOpaque(false);
        previewPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        previewPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 64));
        previewPanel.setMinimumSize(new Dimension(100, 64));
        previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        previewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        previewLabel = new JLabel();
        previewLabel.setFont(UIPalette.FONT_BODY.deriveFont(13f));
        previewLabel.setForeground(UIPalette.ON_BACKGROUND);
        previewPanel.add(previewLabel, BorderLayout.CENTER);

        body.add(previewPanel);

        root.add(body, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(UIPalette.SURFACE);
        footer.setBorder(new EmptyBorder(10, 28, 22, 28));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIPalette.FONT_LABEL.deriveFont(12f));
        statusLabel.setForeground(UIPalette.SUCCESS);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.setFont(UIPalette.FONT_LABEL);
        cancelBtn.setPreferredSize(new Dimension(100, 36));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());
        cancelBtn.putClientProperty("FlatLaf.style", 
            "arc: 8; " +
            "borderWidth: 1; " +
            "borderColor: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#334155" : "#D2D6DC") + "; " +
            "background: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#1E293B" : "#FFFFFF") + "; " +
            "foreground: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#94A3B8" : "#4B5563") + "; " +
            "hoverBackground: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#334155" : "#F3F4F6")
        );

        exportBtn = new JButton("Exportar  →");
        exportBtn.setFont(UIPalette.FONT_LABEL.deriveFont(Font.BOLD, 13f));
        exportBtn.setPreferredSize(new Dimension(120, 36));
        exportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e -> performExport());
        exportBtn.putClientProperty("FlatLaf.style", 
            "arc: 8; " +
            "background: #FFBF00; " + // UIPalette.AMBER
            "foreground: #FFFFFF; " +
            "hoverBackground: #E0A800; " +
            "borderWidth: 0"
        );

        btnRow.add(cancelBtn);
        btnRow.add(exportBtn);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(btnRow, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        // Carrega preview inicial
        updatePreview();
    }

    // ───────────────────────────────────────────────────────────────────────
    // Helper builders
    // ───────────────────────────────────────────────────────────────────────

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIPalette.FONT_LABEL.deriveFont(Font.BOLD, 12f));
        lbl.setForeground(UIPalette.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JToggleButton formatToggle(String format, String icon, boolean selected) {
        JToggleButton btn = new JToggleButton(icon + "  " + format);
        btn.setFont(UIPalette.FONT_BODY.deriveFont(13f));
        btn.setSelected(selected);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setMinimumSize(new Dimension(140, 40));
        btn.setMaximumSize(new Dimension(140, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        String style = "arc: 8; " +
            "borderWidth: 1; " +
            "borderColor: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#334155" : "#E2E8F0") + "; " +
            "background: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#1E293B" : "#F8FAFC") + "; " +
            "foreground: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#94A3B8" : "#64748B") + "; " +
            "selectedBackground: #FFBF00; " + // UIPalette.AMBER
            "selectedForeground: #FFFFFF; " +
            "hoverBackground: " + (com.formdev.flatlaf.FlatLaf.isLafDark() ? "#334155" : "#E2E8F0");
        btn.putClientProperty("FlatLaf.style", style);
        
        return btn;
    }

    // ───────────────────────────────────────────────────────────────────────
    // Preview update
    // ───────────────────────────────────────────────────────────────────────

    private void updatePreview() {
        int idx = datasetCombo.getSelectedIndex();
        String dataset = DATASETS[idx];
        int count = getRowCount(idx);
        String cols = getColumnNames(idx);
        previewLabel.setText("<html><b>" + count + " registro(s)</b> de <i>" + dataset +
                "</i> serão exportados como <b>." + selectedFormat.toLowerCase() +
                "</b><br><small style='color:gray'>Colunas: " + cols + "</small></html>");
        statusLabel.setText(" ");
    }

    private int getRowCount(int datasetIdx) {
        DataRepository repo = DataRepository.getInstance();
        switch (datasetIdx) {
            case 0: return repo.getProducts().size();
            case 1: return repo.getInventory().size();
            case 2: return repo.getUsers().size();
            default: return 0;
        }
    }

    private String getColumnNames(int datasetIdx) {
        switch (datasetIdx) {
            case 0: return "ID, Nome, Categoria, Preço, Status";
            case 1: return "ID, Produto, Nível Atual, Mínimo, Unidade, Alerta";
            case 2: return "ID, Nome, Usuário/Email, Cargo, Data de Cadastro";
            default: return "";
        }
    }

    // ───────────────────────────────────────────────────────────────────────
    // Export dispatcher
    // ───────────────────────────────────────────────────────────────────────

    private void performExport() {
        int datasetIdx = datasetCombo.getSelectedIndex();
        String defaultName = buildDefaultFileName(datasetIdx);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar arquivo de exportação");
        chooser.setSelectedFile(new File(defaultName));

        if ("CSV".equals(selectedFormat)) {
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivo CSV (*.csv)", "csv"));
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivo PDF (*.pdf)", "pdf"));
        }

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        // Garante extensão correta
        String ext = "." + selectedFormat.toLowerCase();
        if (!file.getName().toLowerCase().endsWith(ext)) {
            file = new File(file.getAbsolutePath() + ext);
        }

        exportBtn.setEnabled(false);
        exportBtn.setText("Exportando…");

        final File finalFile = file;
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if ("CSV".equals(selectedFormat)) {
                    exportCsv(finalFile, datasetIdx);
                } else {
                    exportPdf(finalFile, datasetIdx);
                }
                return true;
            }

            @Override
            protected void done() {
                exportBtn.setEnabled(true);
                exportBtn.setText("Exportar  →");
                try {
                    get(); // propaga exceção se houver
                    statusLabel.setForeground(UIPalette.SUCCESS);
                    statusLabel.setText("✔ Arquivo salvo: " + finalFile.getName());
                    // Oferece abrir a pasta
                    offerOpenFolder(finalFile);
                } catch (Exception ex) {
                    statusLabel.setForeground(UIPalette.ERROR);
                    statusLabel.setText("✘ Erro: " + ex.getCause().getMessage());
                }
            }
        };
        worker.execute();
    }

    private String buildDefaultFileName(int idx) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String[] names = {"produtos", "estoque", "usuarios"};
        return "saidera_" + names[idx] + "_" + date;
    }

    private void offerOpenFolder(File file) {
        int opt = JOptionPane.showConfirmDialog(this,
                "Arquivo exportado com sucesso!\nDeseja abrir a pasta de destino?",
                "Exportação concluída", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(file.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ───────────────────────────────────────────────────────────────────────
    // CSV Export
    // ───────────────────────────────────────────────────────────────────────

    private void exportCsv(File file, int datasetIdx) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // BOM UTF-8 para abrir corretamente no Excel
            bw.write('\uFEFF');

            switch (datasetIdx) {
                case 0 -> exportProductsCsv(bw);
                case 1 -> exportInventoryCsv(bw);
                case 2 -> exportUsersCsv(bw);
            }
        }
    }

    private void exportProductsCsv(BufferedWriter bw) throws IOException {
        bw.write("ID,Nome,Categoria,Preço (R$),Status");
        bw.newLine();
        for (Product p : DataRepository.getInstance().getProducts()) {
            bw.write(escapeCsv(p.getId()) + "," +
                     escapeCsv(p.getName()) + "," +
                     escapeCsv(p.getCategory()) + "," +
                     String.format("%.2f", p.getPrice()).replace('.', ',') + "," +
                     (p.isActive() ? "Ativo" : "Inativo"));
            bw.newLine();
        }
    }

    private void exportInventoryCsv(BufferedWriter bw) throws IOException {
        bw.write("ID,Produto,Nível Atual,Mínimo,Unidade,Situação");
        bw.newLine();
        for (StockItem s : DataRepository.getInstance().getInventory()) {
            String situacao = s.isLowStock() ? "Estoque Baixo" :
                              (s.getCurrentLevel() < s.getMinimumLevel() * 1.5 ? "Atenção" : "Normal");
            bw.write(escapeCsv(s.getId()) + "," +
                     escapeCsv(s.getName()) + "," +
                     s.getCurrentLevel() + "," +
                     s.getMinimumLevel() + "," +
                     escapeCsv(s.getUnit()) + "," +
                     situacao);
            bw.newLine();
        }
    }

    private void exportUsersCsv(BufferedWriter bw) throws IOException {
        bw.write("ID,Nome,Usuário/Email,Cargo,Data de Cadastro");
        bw.newLine();
        for (User u : DataRepository.getInstance().getUsers()) {
            bw.write(escapeCsv(u.getId()) + "," +
                     escapeCsv(u.getName()) + "," +
                     escapeCsv(u.getEmail()) + "," +
                     escapeCsv(u.getRole()) + "," +
                     escapeCsv(u.getRegistrationDate()));
            bw.newLine();
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // ───────────────────────────────────────────────────────────────────────
    // PDF Export  (via java.awt.print — sem bibliotecas externas)
    // ───────────────────────────────────────────────────────────────────────

    private void exportPdf(File file, int datasetIdx) throws Exception {
        // Usa o PrinterJob em modo de captura para arquivo PostScript/PDF
        // Na prática no Windows e macOS o sistema imprime para PDF quando
        // o destino é um arquivo via PrintStream ou via javax.print.
        // A solução portável e sem dependências é usar java.awt.print + PrinterJob
        // com o destino gravado em PDF pelo driver virtual — aqui usamos
        // PrinterJob.getPrinterJob() com um Printable custom e salvamos via stream.

        // Coleta os dados antes de entrar no contexto de impressão
        String[][] data;
        String[] headers;
        String reportTitle;

        switch (datasetIdx) {
            case 0 -> {
                headers = new String[]{"Nome", "Categoria", "Preço (R$)", "Status"};
                reportTitle = "Relatório de Cardápio — Saidera";
                List<Product> products = DataRepository.getInstance().getProducts();
                data = new String[products.size()][4];
                for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    data[i][0] = p.getName();
                    data[i][1] = p.getCategory();
                    data[i][2] = String.format("R$ %.2f", p.getPrice());
                    data[i][3] = p.isActive() ? "Ativo" : "Inativo";
                }
            }
            case 1 -> {
                headers = new String[]{"Produto", "Nível Atual", "Mínimo", "Unidade", "Situação"};
                reportTitle = "Relatório de Estoque — Saidera";
                List<StockItem> inv = DataRepository.getInstance().getInventory();
                data = new String[inv.size()][5];
                for (int i = 0; i < inv.size(); i++) {
                    StockItem s = inv.get(i);
                    data[i][0] = s.getName();
                    data[i][1] = String.valueOf(s.getCurrentLevel());
                    data[i][2] = String.valueOf(s.getMinimumLevel());
                    data[i][3] = s.getUnit();
                    data[i][4] = s.isLowStock() ? "⚠ Baixo" :
                                 (s.getCurrentLevel() < s.getMinimumLevel() * 1.5 ? "Atenção" : "✓ Normal");
                }
            }
            default -> {
                headers = new String[]{"Nome", "Usuário/Email", "Cargo", "Cadastro"};
                reportTitle = "Relatório de Usuários — Saidera";
                List<User> users = DataRepository.getInstance().getUsers();
                data = new String[users.size()][4];
                for (int i = 0; i < users.size(); i++) {
                    User u = users.get(i);
                    data[i][0] = u.getName();
                    data[i][1] = u.getEmail();
                    data[i][2] = u.getRole();
                    data[i][3] = u.getRegistrationDate();
                }
            }
        }

        // Monta o Printable
        final String[][] fData = data;
        final String[] fHeaders = headers;
        final String fTitle = reportTitle;
        final String fDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        Printable printable = (graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            float x = (float) pageFormat.getImageableX();
            float y = (float) pageFormat.getImageableY();
            float pw = (float) pageFormat.getImageableWidth();

            // Título
            g2.setColor(new Color(0x1E40AF));
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString(fTitle, (int) x, (int) (y + 20));

            // Data
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.drawString("Gerado em: " + fDate, (int) x, (int) (y + 36));

            // Linha separadora
            g2.setColor(new Color(0x1E40AF));
            g2.fillRect((int) x, (int) (y + 44), (int) pw, 2);

            // Cabeçalhos da tabela
            float colW = pw / fHeaders.length;
            float ty = y + 60;

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(0x1E293B));
            for (int i = 0; i < fHeaders.length; i++) {
                g2.drawString(fHeaders[i], x + i * colW + 4, ty);
            }

            // Dados
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            float rowH = 18;
            for (int r = 0; r < fData.length; r++) {
                float ry = ty + (r + 1) * rowH;
                if (r % 2 == 0) {
                    g2.setColor(new Color(0xF1F5F9));
                    g2.fillRect((int) x, (int) (ry - 13), (int) pw, (int) rowH);
                }
                g2.setColor(new Color(0x334155));
                for (int c = 0; c < fData[r].length; c++) {
                    String cell = fData[r][c] != null ? fData[r][c] : "";
                    // Trunca se muito longo
                    if (cell.length() > 30) cell = cell.substring(0, 27) + "...";
                    g2.drawString(cell, x + c * colW + 4, ry);
                }
            }

            // Rodapé
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect((int) x, (int) (pageFormat.getImageableHeight() + y - 20), (int) pw, 1);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 9));
            g2.setColor(Color.GRAY);
            g2.drawString("Saidera — Sistema de Gestão de Choperia", (int) x, (int) (pageFormat.getImageableHeight() + y - 6));

            return Printable.PAGE_EXISTS;
        };

        // Configura PrinterJob para gravar em arquivo PDF via javax.print
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(printable);

        // Configura saída para arquivo
        javax.print.PrintService[] services = javax.print.PrintServiceLookup.lookupPrintServices(
            javax.print.DocFlavor.SERVICE_FORMATTED.PRINTABLE,
            new javax.print.attribute.HashPrintRequestAttributeSet()
        );

        // Tenta usar o destino de arquivo via atributos de impressão
        javax.print.attribute.HashPrintRequestAttributeSet attrs = new javax.print.attribute.HashPrintRequestAttributeSet();
        attrs.add(new javax.print.attribute.standard.Destination(file.toURI()));
        attrs.add(javax.print.attribute.standard.OrientationRequested.LANDSCAPE);
        attrs.add(javax.print.attribute.standard.MediaSizeName.ISO_A4);

        // Tenta imprimir diretamente para arquivo usando o PrinterJob padrão
        // Isso funciona em muitos ambientes como PDF virtual no Windows 10+
        try {
            job.print(attrs);
        } catch (PrinterException pe) {
            // Fallback: se não há impressora PDF disponível, exporta como HTML
            // que pode ser aberto no browser e impresso como PDF pelo usuário
            exportHtmlFallback(file, fTitle, fDate, fHeaders, fData);
        }
    }

    /**
     * Fallback quando não há impressora PDF disponível.
     * Salva um HTML bem formatado que o usuário pode abrir e imprimir como PDF.
     */
    private void exportHtmlFallback(File pdfFile, String title, String date,
                                     String[] headers, String[][] data) throws IOException {
        // Substitui extensão .pdf por .html
        File htmlFile = new File(pdfFile.getParent(),
                pdfFile.getName().replace(".pdf", ".html"));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(htmlFile), StandardCharsets.UTF_8))) {
            bw.write("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            bw.write("<title>" + escapeHtml(title) + "</title>");
            bw.write("<style>body{font-family:Arial,sans-serif;margin:40px;color:#1e293b}");
            bw.write("h1{color:#1e40af;font-size:20px;margin-bottom:4px}");
            bw.write(".meta{color:#64748b;font-size:12px;margin-bottom:20px}");
            bw.write("table{width:100%;border-collapse:collapse}");
            bw.write("th{background:#1e40af;color:white;padding:10px 12px;text-align:left;font-size:13px}");
            bw.write("td{padding:8px 12px;font-size:12px;border-bottom:1px solid #e2e8f0}");
            bw.write("tr:nth-child(even){background:#f8fafc}");
            bw.write("footer{margin-top:30px;font-size:11px;color:#94a3b8;font-style:italic}");
            bw.write("@media print{button{display:none}}");
            bw.write("</style></head><body>");
            bw.write("<h1>" + escapeHtml(title) + "</h1>");
            bw.write("<div class='meta'>Gerado em: " + date + " &nbsp;|&nbsp; " + data.length + " registro(s)</div>");
            bw.write("<table><thead><tr>");
            for (String h : headers) bw.write("<th>" + escapeHtml(h) + "</th>");
            bw.write("</tr></thead><tbody>");
            for (String[] row : data) {
                bw.write("<tr>");
                for (String cell : row) bw.write("<td>" + escapeHtml(cell != null ? cell : "") + "</td>");
                bw.write("</tr>");
            }
            bw.write("</tbody></table>");
            bw.write("<footer>Saidera — Sistema de Gestão de Choperia</footer>");
            bw.write("</body></html>");
        }

        // Abre no browser para o usuário imprimir como PDF
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(htmlFile.toURI());
        }

        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this,
                "<html>Arquivo aberto no navegador.<br>" +
                "Use <b>Ctrl+P → Salvar como PDF</b> para gerar o PDF.</html>",
                "Exportação HTML", JOptionPane.INFORMATION_MESSAGE));
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
