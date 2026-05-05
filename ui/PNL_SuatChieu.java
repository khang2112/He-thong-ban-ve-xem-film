package ui;

import dao.PhimDAO;
import dao.SuatChieuDAO;
import entity.Phim;
import entity.SuatChieu;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import com.toedter.calendar.JDateChooser;

class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {

    private JTextField txtMaSuat;
    private JDateChooser txtNgay;
    private JSpinner spnGio;
    private JComboBox<String> cboPhim, cboPhong;

    private JButton btnThem, btnSua, btnXoaRong, btnXoa;

    private DefaultTableModel model;
    private JTable table;

    private SuatChieuDAO suatChieuDAO;
    private PhimDAO phimDAO;

    private final Color bg = new Color(15, 15, 15);
    private final Color card = new Color(28, 28, 28);
    private final Color text = new Color(220, 220, 220);

    public PNL_SuatChieu() {

        suatChieuDAO = new SuatChieuDAO();
        phimDAO = new PhimDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(bg);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= TOP =================
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 18, 18));
        pnlInput.setBackground(card);
        pnlInput.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(140, 0, 0)),
                "THÔNG TIN SUẤT CHIẾU",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(255, 60, 80)
        ));

        txtMaSuat = styleInput();

        cboPhim = new JComboBox<>();
        cboPhong = new JComboBox<>(new String[]{
                "Phòng 1 (2D)", "Phòng 2 (3D)",
                "Phòng 3 (2D)", "Phòng 4 (3D)", "VIP"
        });

        txtNgay = new JDateChooser();
        txtNgay.setDateFormatString("yyyy-MM-dd");

        spnGio = new JSpinner(new SpinnerDateModel());
        spnGio.setEditor(new JSpinner.DateEditor(spnGio, "HH:mm"));

        loadPhimToComboBox();

        pnlInput.add(lbl("MÃ SUẤT"));
        pnlInput.add(txtMaSuat);

        pnlInput.add(lbl("PHIM"));
        pnlInput.add(cboPhim);

        pnlInput.add(lbl("PHÒNG"));
        pnlInput.add(cboPhong);

        pnlInput.add(lbl("NGÀY"));
        pnlInput.add(txtNgay);

        pnlInput.add(lbl("GIỜ"));
        pnlInput.add(spnGio);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // ================= BUTTON =================
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pnlBtn.setOpaque(false);

        btnThem = btn("THÊM", new Color(220, 20, 60));
        btnSua = btn("SỬA", new Color(255, 140, 0));
        btnXoa = btn("XÓA", new Color(200, 0, 40));
        btnXoaRong = btn("RESET", new Color(80, 80, 80));

        pnlBtn.add(btnThem);
        pnlBtn.add(btnSua);
        pnlBtn.add(btnXoa);
        pnlBtn.add(btnXoaRong);

        pnlTop.add(pnlBtn, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // ================= TABLE =================
        String[] cols = {"Mã", "Phim", "Phòng", "Ngày", "Giờ"};
        model = new DefaultTableModel(cols, 0);

        table = new JTable(model);
        table.setRowHeight(30);

        table.setBackground(new Color(20, 20, 20));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));

        table.setFillsViewportHeight(true);

        // ================= HEADER FIX (QUAN TRỌNG) =================
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(new Color(140, 0, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // ================= CELL PADDING =================
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
                lbl.setForeground(Color.WHITE);
                lbl.setOpaque(true);

                if (isSelected) {
                    lbl.setBackground(new Color(90, 0, 0));
                } else {
                    lbl.setBackground(new Color(20, 20, 20));
                }

                return lbl;
            }
        });

        // ================= SCROLL FIX =================
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 0, 0)));
        sp.getViewport().setBackground(new Color(20, 20, 20));

        add(sp, BorderLayout.CENTER);

        // events
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);

        loadDataToTable();
    }

    // ================= STYLE =================
    private JTextField styleInput() {
        JTextField t = new JTextField();
        t.setBackground(new Color(45, 45, 45));
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.RED);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return t;
    }

    private JLabel lbl(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ================= DATA =================
    private void loadPhimToComboBox() {
        ArrayList<Phim> list = phimDAO.docTuBang();
        for (Phim p : list) {
            cboPhim.addItem(p.getMaPhim() + " - " + p.getTenPhim());
        }
    }

    private void loadDataToTable() {
        model.setRowCount(0);

        ArrayList<SuatChieu> ds = suatChieuDAO.docTuBang();

        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("HH:mm");

        for (SuatChieu s : ds) {
            model.addRow(new Object[]{
                    s.getMaSuat(),
                    s.getMaPhim(),
                    s.getPhongChieu(),
                    s.getNgayChieu() != null ? s.getNgayChieu().format(f1) : "",
                    s.getGioChieu() != null ? s.getGioChieu().format(f2) : ""
            });
        }
    }

    // ================= EVENTS =================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnThem) {
            suatChieuDAO.themSuatChieu(toEntity());
            loadDataToTable();
        }

        if (e.getSource() == btnSua) {
            suatChieuDAO.suaSuatChieu(toEntity());
            loadDataToTable();
        }

        if (e.getSource() == btnXoa) {
            int r = table.getSelectedRow();
            if (r != -1) {
                String ma = model.getValueAt(r, 0).toString();
                suatChieuDAO.xoaSuatChieu(ma);
                loadDataToTable();
            }
        }

        if (e.getSource() == btnXoaRong) {
            txtMaSuat.setText("");
            txtMaSuat.setEditable(true);
            txtNgay.setDate(null);
            spnGio.setValue(java.sql.Time.valueOf("00:00:00"));
        }
    }

    private SuatChieu toEntity() {
        String ma = txtMaSuat.getText();
        String phim = cboPhim.getSelectedItem().toString().split(" - ")[0];
        String phong = cboPhong.getSelectedItem().toString();

        LocalDate ngay = txtNgay.getDate()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalTime gio = ((java.util.Date) spnGio.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        return new SuatChieu(ma, phim, phong, ngay, gio);
    }

    @Override public void mouseClicked(MouseEvent e) {
        int r = table.getSelectedRow();
        if (r != -1) {
            txtMaSuat.setText(model.getValueAt(r, 0).toString());
            txtMaSuat.setEditable(false);
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}