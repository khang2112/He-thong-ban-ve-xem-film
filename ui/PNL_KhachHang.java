package ui;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_KhachHang extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMa, txtTen, txtSDT, txtDiem;
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    // Gọi DAO để kết nối SQL Server
    private KhachHangDAO khachHangDAO;

    public PNL_KhachHang() {
        khachHangDAO = new KhachHangDAO(); // Khởi tạo DAO
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. FORM NHẬP LIỆU (Tối giản, chuyên nghiệp)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 20, 20));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1), "THÔNG TIN KHÁCH HÀNG",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(200, 200, 200)
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setOpaque(false);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        pnlInput.add(createLabel("Mã Khách Hàng:"));
        txtMa = createTextField();
        pnlInput.add(txtMa);

        pnlInput.add(createLabel("Tên Khách Hàng:"));
        txtTen = createTextField();
        pnlInput.add(txtTen);

        pnlInput.add(createLabel("Số Điện Thoại:"));
        txtSDT = createTextField();
        pnlInput.add(txtSDT);

        pnlInput.add(createLabel("Điểm Tích Lũy:"));
        txtDiem = createTextField();
        txtDiem.setText("0"); // Mặc định khách mới là 0 điểm
        txtDiem.setEditable(false); // Khóa lại, không cho nhân viên tự sửa điểm
        txtDiem.setForeground(new Color(241, 196, 15)); // Đổi màu vàng cho nổi bật
        pnlInput.add(txtDiem);

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
        String[] cols = {"Mã KH", "Tên Khách Hàng", "Số Điện Thoại", "Điểm Tích Lũy"};
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

    // --- CÁC HÀM HỖ TRỢ GIAO DIỆN ---
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

    // ========================================================
    // HÀM KIỂM TRA RÀNG BUỘC DỮ LIỆU (VALIDATION)
    // ========================================================
    private boolean validateData() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();

        // 1. Kiểm tra rỗng
        if (ma.isEmpty() || ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã, Tên và Số điện thoại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2. Kiểm tra Mã KH không chứa khoảng trắng
        if (ma.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng không được chứa khoảng trắng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtMa.requestFocus();
            return false;
        }

        // 3. Kiểm tra Tên KH không được chứa chữ số
        if (ten.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ (Không được chứa chữ số)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }

        // 4. Kiểm tra đúng định dạng Số điện thoại (VD: 090-123456)
        if (!sdt.matches("\\d{3}-\\d{6}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải nhập đúng định dạng XXX-YYYYYY\n(Ví dụ: 090-123456)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return false;
        }

        return true; // Qua hết các ải thì hợp lệ
    }

    // --- HÀM LOAD DỮ LIỆU TỪ SQL ---
    public void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<KhachHang> ds = khachHangDAO.docTuBang();
        for (KhachHang kh : ds) {
            model.addRow(new Object[]{
                kh.getMaKH(), kh.getTenKH(), kh.getSoDienThoai(), kh.getDiemTichLuy()
            });
        }
    }

    // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnXoaRong) {
            txtMa.setText(""); txtTen.setText(""); txtSDT.setText(""); txtDiem.setText("0");
            txtMa.requestFocus();
            txtMa.setEditable(true); // Cho phép sửa mã khi thêm mới
            table.clearSelection();
        } 
        else if (o == btnThem) {
            if (!validateData()) return; // Chặn nếu nhập sai
            
            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();

            KhachHang kh = new KhachHang(ma, ten, sdt, 0);
            if (khachHangDAO.themKhachHang(kh)) {
                loadDataToTable(); // Nạp lại bảng để thấy khách vừa thêm
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Mã khách hàng '" + ma + "' đã tồn tại!", "Lỗi trùng mã", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (khachHangDAO.xoaKhachHang(ma)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Khách hàng này có thể đang tồn tại trong hóa đơn.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa thông tin từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return;

            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            int diem = Integer.parseInt(txtDiem.getText().trim());

            KhachHang kh = new KhachHang(ma, ten, sdt, diem);
            if (khachHangDAO.suaKhachHang(kh)) {
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
            txtMa.setText(model.getValueAt(row, 0).toString());
            txtTen.setText(model.getValueAt(row, 1).toString());
            txtSDT.setText(model.getValueAt(row, 2).toString());
            txtDiem.setText(model.getValueAt(row, 3).toString());
            
            txtMa.setEditable(false); // Không cho phép sửa Khóa Chính (Mã KH)
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}