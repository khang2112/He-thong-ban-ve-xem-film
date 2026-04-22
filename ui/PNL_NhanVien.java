package ui;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_NhanVien extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMa, txtHo, txtTen, txtTuoi, txtLuong;
    private JComboBox<String> cboPhongBan;
    private JButton btnThem, btnXoa, btnSua, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    private NhanVienDAO nhanVienDAO;

    public PNL_NhanVien() {
        nhanVienDAO = new NhanVienDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- FORM NHẬP LIỆU ---
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);

        // Khung nhập: 3 hàng, 4 cột (gồm cả Lable và TextField)
        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 15, 15));
        pnlInput.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "THÔNG TIN NHÂN VIÊN");
        border.setTitleColor(Color.ORANGE);
        pnlInput.setBorder(border);

        pnlInput.add(createLabel("Mã Nhân Viên:"));
        txtMa = new JTextField();
        pnlInput.add(txtMa);

        pnlInput.add(createLabel("Họ Nhân Viên:"));
        txtHo = new JTextField();
        pnlInput.add(txtHo);

        pnlInput.add(createLabel("Tên Nhân Viên:"));
        txtTen = new JTextField();
        pnlInput.add(txtTen);

        pnlInput.add(createLabel("Tuổi:"));
        txtTuoi = new JTextField();
        pnlInput.add(txtTuoi);

        pnlInput.add(createLabel("Phòng Ban:"));
        String[] phongBan = {"Phòng tổ chức", "Phòng kỹ thuật", "Phòng nhân sự"};
        cboPhongBan = new JComboBox<>(phongBan);
        pnlInput.add(cboPhongBan);

        pnlInput.add(createLabel("Tiền Lương:"));
        txtLuong = new JTextField();
        pnlInput.add(txtLuong);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // --- NÚT CHỨC NĂNG ---
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlBtns.setOpaque(false);
        
        btnThem = createButton("Thêm", new Color(46, 204, 113));
        btnSua = createButton("Sửa", new Color(52, 152, 219));
        btnXoa = createButton("Xóa", new Color(231, 76, 60));
        btnXoaRong = createButton("Xóa Rỗng", Color.GRAY);

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnXoaRong);

        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU ---
        String[] cols = {"Mã NV", "Họ NV", "Tên NV", "Tuổi", "Phòng Ban", "Tiền Lương"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);

        loadDataToTable();
    }

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

    private void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<NhanVien> ds = nhanVienDAO.docTuBang();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN")); // Format tiền tệ
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                nv.getMaNV(), nv.getHoNV(), nv.getTenNV(), nv.getTuoi(), 
                nv.getPhongBan(), nf.format(nv.getTienLuong())
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            txtMa.setText(""); txtHo.setText(""); txtTen.setText("");
            txtTuoi.setText(""); txtLuong.setText(""); cboPhongBan.setSelectedIndex(0);
            txtMa.requestFocus();
            table.clearSelection();
        } 
        else if (o == btnThem) {
            try {
                String ma = txtMa.getText();
                String ho = txtHo.getText();
                String ten = txtTen.getText();
                int tuoi = Integer.parseInt(txtTuoi.getText());
                String phong = cboPhongBan.getSelectedItem().toString();
                double luong = Double.parseDouble(txtLuong.getText().replace(",", "")); // Bỏ dấu phẩy nếu có

                NhanVien nv = new NhanVien(ma, ho, ten, tuoi, phong, luong);
                if (nhanVienDAO.themNhanVien(nv)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Trùng mã nhân viên!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu! Kiểm tra lại Tuổi và Lương.");
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Chắc chắn xóa?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (nhanVienDAO.xoaNhanVien(ma)) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!");
                return;
            }
            try {
                String ma = txtMa.getText();
                String ho = txtHo.getText();
                String ten = txtTen.getText();
                int tuoi = Integer.parseInt(txtTuoi.getText());
                String phong = cboPhongBan.getSelectedItem().toString();
                double luong = Double.parseDouble(txtLuong.getText().replace(",", "").replace(".", "")); // Lọc bỏ dấu

                NhanVien nv = new NhanVien(ma, ho, ten, tuoi, phong, luong);
                if (nhanVienDAO.suaNhanVien(nv)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMa.setText(model.getValueAt(row, 0).toString());
            txtHo.setText(model.getValueAt(row, 1).toString());
            txtTen.setText(model.getValueAt(row, 2).toString());
            txtTuoi.setText(model.getValueAt(row, 3).toString());
            cboPhongBan.setSelectedItem(model.getValueAt(row, 4).toString());
            
            // Xóa dấu chấm/phẩy tiền tệ trước khi đưa lên textfield để dễ sửa
            String luongStr = model.getValueAt(row, 5).toString().replaceAll("[^0-9]", ""); 
            txtLuong.setText(luongStr);
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}