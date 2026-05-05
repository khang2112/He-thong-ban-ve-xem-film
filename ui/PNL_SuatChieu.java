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

import com.toedter.calendar.JDateChooser;

public class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {

    private JTextField txtMaSuat;
    private JDateChooser txtNgay;
    private JSpinner spnGio;
    private JComboBox<String> cboPhim, cboPhong;

    private JButton btnThem, btnSua, btnXoaRong, btnXoa;

    private DefaultTableModel model;
    private JTable table;

    private SuatChieuDAO suatChieuDAO;
    private PhimDAO phimDAO;

    public PNL_SuatChieu() {

        suatChieuDAO = new SuatChieuDAO();
        phimDAO = new PhimDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ================= FORM INPUT =================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 20, 20));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "THÔNG TIN SUẤT CHIẾU",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(200, 200, 200)
        );
        pnlInput.setBorder(border);

        pnlInput.add(createLabel("Mã Suất:"));
        txtMaSuat = createTextField();
        pnlInput.add(txtMaSuat);

        pnlInput.add(createLabel("Phim:"));
        cboPhim = new JComboBox<>();
        loadPhimToComboBox();
        pnlInput.add(cboPhim);

        pnlInput.add(createLabel("Phòng:"));
        cboPhong = new JComboBox<>(new String[]{
                "Phòng 1 (2D)", "Phòng 2 (3D)",
                "Phòng 3 (2D)", "Phòng 4 (3D)", "VIP"
        });
        pnlInput.add(cboPhong);

        pnlInput.add(createLabel("Ngày chiếu:"));
        txtNgay = new JDateChooser();
        txtNgay.setDateFormatString("yyyy-MM-dd");
        pnlInput.add(txtNgay);

        pnlInput.add(createLabel("Giờ chiếu:"));
        spnGio = new JSpinner(new SpinnerDateModel());
        spnGio.setEditor(new JSpinner.DateEditor(spnGio, "HH:mm"));
        pnlInput.add(spnGio);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // ================= BUTTON =================
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setOpaque(false);

        btnThem = createButton("Thêm", new Color(46, 204, 113));
        btnSua = createButton("Sửa", new Color(52, 152, 219));
        btnXoa = createButton("Xóa", new Color(231, 76, 60));
        btnXoaRong = createButton("Xóa Rỗng", new Color(127, 140, 141));

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnXoaRong);

        pnlTop.add(pnlBtns, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);

        // ================= TABLE =================
        String[] cols = {"Mã Suất", "Mã Phim", "Phòng", "Ngày", "Giờ"};
        model = new DefaultTableModel(cols, 0);

        table = new JTable(model);

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(new Color(30, 30, 30));
        sp.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

        add(sp, BorderLayout.CENTER);

        // ================= EVENTS =================
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);

        loadDataToTable();
    }

    // ================= UI HELPERS =================
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(220, 220, 220));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBackground(new Color(50, 50, 50));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 38));
        return btn;
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
            suatChieuDAO.themSuatChieu(getForm());
            loadDataToTable();
        }

        if (e.getSource() == btnSua) {
            suatChieuDAO.suaSuatChieu(getForm());
            loadDataToTable();
        }

        if (e.getSource() == btnXoa) {
            int row = table.getSelectedRow();
            if (row != -1) {
                String ma = model.getValueAt(row, 0).toString();
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

    private SuatChieu getForm() {
        String ma = txtMaSuat.getText().trim();
        String phim = cboPhim.getSelectedItem().toString().split(" - ")[0];
        String phong = cboPhong.getSelectedItem().toString();

        LocalDate ngay = txtNgay.getDate()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalTime gio = ((java.util.Date) spnGio.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        return new SuatChieu(ma, phim, phong, ngay, gio);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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