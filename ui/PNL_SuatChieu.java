package ui;

import dao.PhimDAO;
import dao.SuatChieuDAO;
import entity.Phim;
import entity.SuatChieu;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMaSuat, txtPhong, txtNgay, txtGio;
    private JComboBox<String> cboPhim; // Combo box chọn phim
    private JButton btnThem, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    private SuatChieuDAO suatChieuDAO;
    private PhimDAO phimDAO;

    public PNL_SuatChieu() {
        suatChieuDAO = new SuatChieuDAO();
        phimDAO = new PhimDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- FORM NHẬP LIỆU ---
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 15, 15));
        pnlInput.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "THÔNG TIN SUẤT CHIẾU");
        border.setTitleColor(Color.ORANGE);
        pnlInput.setBorder(border);

        pnlInput.add(createLabel("Mã Suất:"));
        txtMaSuat = new JTextField();
        pnlInput.add(txtMaSuat);

        pnlInput.add(createLabel("Chọn Phim:"));
        cboPhim = new JComboBox<>();
        loadPhimToComboBox(); // Nạp dữ liệu phim vào ComboBox
        pnlInput.add(cboPhim);

        pnlInput.add(createLabel("Phòng Chiếu:"));
        txtPhong = new JTextField();
        pnlInput.add(txtPhong);

        pnlInput.add(createLabel("Ngày (YYYY-MM-DD):"));
        txtNgay = new JTextField();
        pnlInput.add(txtNgay);

        pnlInput.add(createLabel("Giờ (HH:MM):"));
        txtGio = new JTextField();
        pnlInput.add(txtGio);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // --- NÚT BẤM ---
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtns.setOpaque(false);
        btnThem = new JButton("Thêm Suất");
        btnXoaRong = new JButton("Xóa Rỗng");
        pnlBtns.add(btnThem);
        pnlBtns.add(btnXoaRong);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU ---
        String[] cols = {"Mã Suất", "Mã Phim", "Phòng", "Ngày", "Giờ"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnThem.addActionListener(this);
        btnXoaRong.addActionListener(this);
        
        loadDataToTable();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    // Hàm load tên phim từ Database ném vào JComboBox
    private void loadPhimToComboBox() {
        ArrayList<Phim> dsPhim = phimDAO.docTuBang();
        for (Phim p : dsPhim) {
            // Định dạng: "MãPhim - Tên Phim" để dễ nhìn
            cboPhim.addItem(p.getMaPhim() + " - " + p.getTenPhim());
        }
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<SuatChieu> ds = suatChieuDAO.docTuBang();
        for (SuatChieu s : ds) {
            model.addRow(new Object[]{s.getMaSuat(), s.getMaPhim(), s.getPhongChieu(), s.getNgayChieu(), s.getGioChieu()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnThem) {
            try {
                String maS = txtMaSuat.getText();
                // Tách lấy Mã Phim từ chuỗi "P001 - Mai"
                String maP = cboPhim.getSelectedItem().toString().split(" - ")[0]; 
                String phong = txtPhong.getText();
                String ngay = txtNgay.getText();
                String gio = txtGio.getText();

                SuatChieu s = new SuatChieu(maS, maP, phong, ngay, gio);
                if (suatChieuDAO.themSuatChieu(s)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Trùng mã hoặc lỗi dữ liệu!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!");
            }
        } else if (e.getSource() == btnXoaRong) {
            txtMaSuat.setText(""); txtPhong.setText(""); 
            txtNgay.setText(""); txtGio.setText("");
        }
    }

    // (Bỏ trống các hàm MouseListener cho gọn code nhé)
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}