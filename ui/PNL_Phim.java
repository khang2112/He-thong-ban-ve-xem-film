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
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    // Gọi DAO để kết nối SQL Server
    private PhimDAO phimDAO;

    public PNL_Phim() {
        phimDAO = new PhimDAO(); // Khởi tạo DAO
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. FORM NHẬP LIỆU (Đồng bộ với Khách Hàng)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 20, 20));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60)), "THÔNG TIN PHIM",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(220, 20, 60)
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setOpaque(false);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        pnlInput.add(createLabel("Mã Phim:"));
        txtMaPhim = createTextField();
        pnlInput.add(txtMaPhim);

        pnlInput.add(createLabel("Tên Phim:"));
        txtTenPhim = createTextField();
        pnlInput.add(txtTenPhim);

        pnlInput.add(createLabel("Thể Loại:"));
        txtTheLoai = createTextField();
        pnlInput.add(txtTheLoai);

        pnlInput.add(createLabel("Giá Vé:"));
        txtGiaVe = createTextField();
        pnlInput.add(txtGiaVe);

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG (Flat UI)
        // ==========================================
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

        // ==========================================
        // 3. BẢNG DỮ LIỆU
        // ==========================================
        String[] cols = {"Mã Phim", "Tên Phim", "Thể Loại", "Giá Vé"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(40, 40, 40)); 
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(new Color(52, 152, 219)); 
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        add(scrollPane, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);
        
        // Tải dữ liệu từ SQL lên bảng khi vừa mở form
        loadDataToTable();
    }

    // --- CÁC HÀM HỖ TRỢ GIAO DIỆN (Đã đồng bộ) ---
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        return btn;
    }

    // --- KIỂM TRA DỮ LIỆU ---
    private boolean validateData() {
        String ma = txtMaPhim.getText().trim();
        String ten = txtTenPhim.getText().trim();
        String theLoai = txtTheLoai.getText().trim();
        String giaStr = txtGiaVe.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || theLoai.isEmpty() || giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin phim!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double gia = Double.parseDouble(giaStr);
            if (gia < 0) {
                JOptionPane.showMessageDialog(this, "Giá vé phải lớn hơn hoặc bằng 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá vé phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // --- HÀM LOAD DỮ LIỆU TỪ SQL ---
    public void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<Phim> ds = phimDAO.docTuBang();
        for (Phim p : ds) {
            model.addRow(new Object[]{
                p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), p.getGiaVe()
            });
        }
    }

    // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            txtMaPhim.setText(""); txtTenPhim.setText(""); txtTheLoai.setText(""); txtGiaVe.setText("");
            txtMaPhim.requestFocus();
            txtMaPhim.setEditable(true);
            table.clearSelection();
        } 
        else if (o == btnThem) {
            if (!validateData()) return;
            
            String ma = txtMaPhim.getText().trim();
            String ten = txtTenPhim.getText().trim();
            String tl = txtTheLoai.getText().trim();
            double gia = Double.parseDouble(txtGiaVe.getText().trim());

            Phim p = new Phim(ma, ten, tl, gia);
            if (phimDAO.themPhim(p)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Thêm phim thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                btnXoaRong.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Mã phim '" + ma + "' đã tồn tại!", "Lỗi trùng mã", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần xóa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phim này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (phimDAO.xoaPhim(ma)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Phim này có thể đang có suất chiếu.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần sửa!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return;

            String ma = txtMaPhim.getText().trim();
            String ten = txtTenPhim.getText().trim();
            String tl = txtTheLoai.getText().trim();
            double gia = Double.parseDouble(txtGiaVe.getText().trim());

            Phim p = new Phim(ma, ten, tl, gia);
            if (phimDAO.suaPhim(p)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // --- SỰ KIỆN CLICK VÀO BẢNG ---
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaPhim.setText(model.getValueAt(row, 0).toString());
            txtTenPhim.setText(model.getValueAt(row, 1).toString());
            txtTheLoai.setText(model.getValueAt(row, 2).toString());
            txtGiaVe.setText(model.getValueAt(row, 3).toString());
            
            txtMaPhim.setEditable(false); // Không cho sửa mã chính
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}