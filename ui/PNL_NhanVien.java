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
    private JTextField txtMa, txtHo, txtTen, txtTuoi, txtLuong, txtTim;
    private JComboBox<String> cboPhongBan;
    private JButton btnThem, btnXoa, btnSua, btnXoaRong, btnTim;
    private DefaultTableModel model;
    private JTable table;
    
    private NhanVienDAO nhanVienDAO;

    public PNL_NhanVien() {
        nhanVienDAO = new NhanVienDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. FORM NHẬP LIỆU (Thiết kế tối giản, chuyên nghiệp)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 20, 20));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60), 1), "THÔNG TIN NHÂN VIÊN",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), new Color(220, 20, 60)
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setOpaque(false);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        // Các trường nhập liệu
        pnlInput.add(createLabel("Mã NV:"));
        txtMa = createTextField();
        pnlInput.add(txtMa);

        pnlInput.add(createLabel("Phòng Ban:"));
        String[] phongBan = {"Phòng tổ chức", "Phòng kỹ thuật", "Phòng nhân sự"};
        cboPhongBan = new JComboBox<>(phongBan);
        cboPhongBan.setFont(new Font("Arial", Font.PLAIN, 14));
        pnlInput.add(cboPhongBan);

        pnlInput.add(createLabel("Họ lót:"));
        txtHo = createTextField();
        pnlInput.add(txtHo);

        pnlInput.add(createLabel("Tên:"));
        txtTen = createTextField();
        pnlInput.add(txtTen);

        pnlInput.add(createLabel("Tuổi:"));
        txtTuoi = createTextField();
        pnlInput.add(txtTuoi);

        pnlInput.add(createLabel("Tiền Lương:"));
        txtLuong = createTextField();
        pnlInput.add(txtLuong);

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG
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
        // 3. KHU VỰC TÌM KIẾM & BẢNG DỮ LIỆU
        // ==========================================
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);

        // --- Thanh Tìm Kiếm ---
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlTimKiem.setOpaque(false);
        
        JLabel lblTimKiem = createLabel("Tìm kiếm (Mã):");
        lblTimKiem.setForeground(new Color(241, 196, 15)); // Màu vàng nổi bật
        txtTim = createTextField();
        txtTim.setPreferredSize(new Dimension(250, 35));
        
        btnTim = createButton("Tìm", new Color(41, 128, 185)); // Nút tìm kiếm nhỏ hơn
        btnTim.setPreferredSize(new Dimension(80, 35));
        
        pnlTimKiem.add(lblTimKiem);
        pnlTimKiem.add(txtTim);
        pnlTimKiem.add(btnTim);

        pnlCenter.add(pnlTimKiem, BorderLayout.NORTH);

        // --- Bảng Dữ Liệu ---
        String[] cols = {"Mã NV", "Họ Lót", "Tên", "Tuổi", "Phòng Ban", "Tiền Lương"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(40, 40, 40)); 
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(52, 152, 219)); 
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        btnTim.addActionListener(this); // Thêm sự kiện nút Tìm
        table.addMouseListener(this);

        loadDataToTable();
    }

    // --- CÁC HÀM HỖ TRỢ GIAO DIỆN ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(220, 220, 220));
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Arial", Font.PLAIN, 14));
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
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 38)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        return btn;
    }

    // --- HÀM KIỂM TRA DỮ LIỆU (VALIDATION) ---
    private boolean validateData() {
        String ma = txtMa.getText().trim();
        String ho = txtHo.getText().trim();
        String ten = txtTen.getText().trim();
        String tuoiStr = txtTuoi.getText().trim();
        String luongStr = txtLuong.getText().replace(",", "").replace(".", "").trim();

        if (ma.isEmpty() || ho.isEmpty() || ten.isEmpty() || tuoiStr.isEmpty() || luongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (ho.matches(".*\\d.*") || ten.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Họ và Tên không được chứa chữ số.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            int tuoi = Integer.parseInt(tuoiStr);
            if (tuoi < 18 || tuoi > 62) {
                JOptionPane.showMessageDialog(this, "Tuổi nhân viên phải từ 18 đến 62.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtTuoi.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi phải là định dạng số nguyên.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtTuoi.requestFocus();
            return false;
        }

        try {
            double luong = Double.parseDouble(luongStr);
            if (luong <= 0) {
                JOptionPane.showMessageDialog(this, "Tiền lương phải lớn hơn 0.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtLuong.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tiền lương phải là định dạng số.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            txtLuong.requestFocus();
            return false;
        }

        return true; 
    }

    // --- LOAD DỮ LIỆU (Hàm nạp danh sách bất kỳ vào bảng) ---
    private void xoaDuLieuBang() {
        model.setRowCount(0);
    }
    
    private void hienThiDanhSach(ArrayList<NhanVien> ds) {
        xoaDuLieuBang();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN")); 
        for (NhanVien nv : ds) {
            model.addRow(new Object[]{
                nv.getMaNV(), nv.getHoNV(), nv.getTenNV(), nv.getTuoi(), 
                nv.getPhongBan(), nf.format(nv.getTienLuong())
            });
        }
    }

    private void loadDataToTable() {
        ArrayList<NhanVien> ds = nhanVienDAO.docTuBang();
        hienThiDanhSach(ds);
    }

    // --- XỬ LÝ SỰ KIỆN ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            // Xóa nội dung trên form
            txtMa.setText(""); txtHo.setText(""); txtTen.setText("");
            txtTuoi.setText(""); txtLuong.setText(""); cboPhongBan.setSelectedIndex(0);
            txtMa.requestFocus();
            
            // Xóa luôn ô tìm kiếm và tải lại toàn bộ dữ liệu (Như một nút Làm Mới)
            txtTim.setText("");
            loadDataToTable();
        } 
        else if (o == btnTim) {
            String tuKhoa = txtTim.getText().trim();
            if (tuKhoa.isEmpty()) {
                loadDataToTable(); // Nếu ô tìm kiếm rỗng thì hiện tất cả
            } else {
                ArrayList<NhanVien> dsTimKiem = nhanVienDAO.timNhanVien(tuKhoa);
                hienThiDanhSach(dsTimKiem);
                if (dsTimKiem.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        else if (o == btnThem) {
            if (!validateData()) return; 

            String ma = txtMa.getText().trim();
            String ho = txtHo.getText().trim();
            String ten = txtTen.getText().trim();
            int tuoi = Integer.parseInt(txtTuoi.getText().trim());
            String phong = cboPhongBan.getSelectedItem().toString();
            double luong = Double.parseDouble(txtLuong.getText().replace(",", "").replace(".", "").trim());

            NhanVien nv = new NhanVien(ma, ho, ten, tuoi, phong, luong);
            if (nhanVienDAO.themNhanVien(nv)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (nhanVienDAO.xoaNhanVien(ma)) {
                    loadDataToTable(); // Cập nhật lại toàn bảng thay vì chỉ xóa dòng
                    JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return; 

            String ma = txtMa.getText().trim();
            String ho = txtHo.getText().trim();
            String ten = txtTen.getText().trim();
            int tuoi = Integer.parseInt(txtTuoi.getText().trim());
            String phong = cboPhongBan.getSelectedItem().toString();
            double luong = Double.parseDouble(txtLuong.getText().replace(",", "").replace(".", "").trim());

            NhanVien nv = new NhanVien(ma, ho, ten, tuoi, phong, luong);
            if (nhanVienDAO.suaNhanVien(nv)) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
            
            String luongStr = model.getValueAt(row, 5).toString().replaceAll("[^0-9]", ""); 
            txtLuong.setText(luongStr);
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}