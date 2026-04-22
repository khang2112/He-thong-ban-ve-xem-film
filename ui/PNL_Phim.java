package ui;

import dao.PhimDAO;
import entity.Phim;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_Phim extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMaPhim, txtTenPhim, txtTheLoai, txtGiaVe;
    private JButton btnThem, btnXoa, btnSua, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    private PhimDAO phimDAO; // Khai báo DAO

    public PNL_Phim() {
        phimDAO = new PhimDAO(); // Khởi tạo DAO
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PHẦN NORTH: FORM NHẬP LIỆU ---
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 15, 15));
        pnlInput.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "THÔNG TIN PHIM");
        border.setTitleColor(Color.ORANGE);
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        pnlInput.setBorder(border);

        pnlInput.add(createLabel("Mã Phim:"));
        txtMaPhim = new JTextField();
        pnlInput.add(txtMaPhim);

        pnlInput.add(createLabel("Tên Phim:"));
        txtTenPhim = new JTextField();
        pnlInput.add(txtTenPhim);

        pnlInput.add(createLabel("Thể Loại:"));
        txtTheLoai = new JTextField();
        pnlInput.add(txtTheLoai);

        pnlInput.add(createLabel("Giá Vé:"));
        txtGiaVe = new JTextField();
        pnlInput.add(txtGiaVe);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // --- PHẦN NÚT CHỨC NĂNG ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlButtons.setOpaque(false);
        
        btnThem = createButton("Thêm", new Color(46, 204, 113));
        btnSua = createButton("Sửa", new Color(52, 152, 219));
        btnXoa = createButton("Xóa", new Color(231, 76, 60));
        btnXoaRong = createButton("Xóa Rỗng", Color.GRAY);

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnXoaRong);

        pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // --- PHẦN CENTER: BẢNG DỮ LIỆU ---
        String[] cols = {"Mã Phim", "Tên Phim", "Thể Loại", "Giá Vé"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ĐĂNG KÝ SỰ KIỆN
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);

        // Nạp dữ liệu từ SQL lên bảng khi vừa mở form
        loadDataToTable();
    }

    // Hàm load dữ liệu từ SQL lên Table
    private void loadDataToTable() {
        model.setRowCount(0); // Xóa dữ liệu cũ trên bảng
        ArrayList<Phim> dsPhim = phimDAO.docTuBang();
        for (Phim p : dsPhim) {
            model.addRow(new Object[]{
                p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), p.getGiaVe()
            });
        }
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN (ACTION LISTENER) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // XÓA RỖNG
        if (o == btnXoaRong) {
            txtMaPhim.setText("");
            txtTenPhim.setText("");
            txtTheLoai.setText("");
            txtGiaVe.setText("");
            txtMaPhim.requestFocus(); // Nháy con trỏ về ô Mã Phim
            table.clearSelection();
        } 
        
        // THÊM PHIM
        else if (o == btnThem) {
            try {
                // Lấy dữ liệu từ TextFields
                String ma = txtMaPhim.getText().trim();
                String ten = txtTenPhim.getText().trim();
                String tl = txtTheLoai.getText().trim();
                double gia = Double.parseDouble(txtGiaVe.getText().trim());

                if (ma.isEmpty() || ten.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
                    return;
                }

                Phim p = new Phim(ma, ten, tl, gia);
                if (phimDAO.themPhim(p)) { // Thêm xuống SQL
                    model.addRow(new Object[]{ma, ten, tl, gia}); // Thêm vào bảng UI
                    JOptionPane.showMessageDialog(this, "Thêm phim thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Trùng mã phim!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Giá vé phải là số!");
            }
        } 
        
        // XÓA PHIM
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần xóa!");
                return;
            }
            int hoiNhac = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phim này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (hoiNhac == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (phimDAO.xoaPhim(ma)) { // Xóa dưới SQL
                    model.removeRow(row);  // Xóa trên bảng UI
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                }
            }
        } 
        
        // SỬA PHIM
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần sửa!");
                return;
            }
            try {
                String ma = txtMaPhim.getText().trim();
                String ten = txtTenPhim.getText().trim();
                String tl = txtTheLoai.getText().trim();
                double gia = Double.parseDouble(txtGiaVe.getText().trim());

                Phim p = new Phim(ma, ten, tl, gia);
                if (phimDAO.suaPhim(p)) { // Cập nhật SQL
                    // Cập nhật lại UI
                    model.setValueAt(ten, row, 1);
                    model.setValueAt(tl, row, 2);
                    model.setValueAt(gia, row, 3);
                    JOptionPane.showMessageDialog(this, "Sửa thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể sửa mã phim!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu nhập vào!");
            }
        }
    }

    // --- SỰ KIỆN CLICK CHUỘT VÀO BẢNG ---
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaPhim.setText(model.getValueAt(row, 0).toString());
            txtTenPhim.setText(model.getValueAt(row, 1).toString());
            txtTheLoai.setText(model.getValueAt(row, 2).toString());
            txtGiaVe.setText(model.getValueAt(row, 3).toString());
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}