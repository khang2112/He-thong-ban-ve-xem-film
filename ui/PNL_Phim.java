package ui;

import dao.PhimDAO;
import entity.Phim;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PNL_Phim extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMaPhim, txtTenPhim, txtTheLoai, txtGiaVe;
    private JButton btnThem, btnXoa, btnSua, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    private PhimDAO phimDAO; 

    // --- BẢNG MÀU CHUẨN POS ---
    private Color bgDark = new Color(18, 18, 18);
    private Color bgPanel = new Color(30, 30, 30);
    private Color textWhite = new Color(240, 240, 240);
    private Color colorGold = new Color(212, 175, 55);

    // --- LỚP NÚT BẤM CAO CẤP CHỐNG WINDOWS UI ---
    class PosButton extends JButton {
        private Color bgColor;
        public PosButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(bgColor.brighter()); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(bgColor); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() == null ? bgColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public PNL_Phim() {
        phimDAO = new PhimDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        // ==========================================
        // 1. PHẦN NORTH: FORM NHẬP LIỆU
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 15, 20));
        pnlInput.setBackground(bgPanel);
        
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "THÔNG TIN PHIM");
        border.setTitleColor(colorGold);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlInput.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 15, 15, 15)));

        pnlInput.add(createLabel("Mã Phim:"));
        txtMaPhim = createTextField();
        pnlInput.add(txtMaPhim);

        pnlInput.add(createLabel("Tên Phim:"));
        txtTenPhim = createTextField();
        pnlInput.add(txtTenPhim);

        pnlInput.add(createLabel("Thể Loại:"));
        txtTheLoai = createTextField();
        pnlInput.add(txtTheLoai);

        pnlInput.add(createLabel("Giá Vé (VNĐ):"));
        txtGiaVe = createTextField();
        pnlInput.add(txtGiaVe);

        pnlTop.add(pnlInput, BorderLayout.CENTER);

        // --- NÚT CHỨC NĂNG ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlButtons.setOpaque(false);
        
        btnThem = new PosButton("  THÊM MỚI  ", new Color(46, 204, 113), Color.WHITE);
        btnSua = new PosButton("  CẬP NHẬT  ", new Color(52, 152, 219), Color.WHITE);
        btnXoa = new PosButton("  XÓA PHIM  ", new Color(231, 76, 60), Color.WHITE);
        btnXoaRong = new PosButton("  LÀM MỚI  ", new Color(100, 100, 100), Color.WHITE);

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnXoaRong);

        pnlTop.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // 2. PHẦN CENTER: BẢNG DỮ LIỆU
        // ==========================================
        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setBackground(bgPanel);
        pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        String[] cols = {"Mã Phim", "Tên Phim", "Thể Loại", "Giá Vé Mặc Định"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Khóa không cho sửa trực tiếp trên ô
        };
        table = new JTable(model);
        
        // Custom Bảng theo chuẩn Dark Mode
        table.setRowHeight(35);
        table.setBackground(bgPanel);
        table.setForeground(textWhite);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(41, 128, 185));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Custom Header Bảng
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(20, 20, 20));
        headerRenderer.setForeground(colorGold);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Căn phải và format tiền tệ cho cột Giá vé
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(bgPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTableWrapper.add(scroll, BorderLayout.CENTER);
        add(pnlTableWrapper, BorderLayout.CENTER);

        // ĐĂNG KÝ SỰ KIỆN
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);

        // Nạp dữ liệu từ SQL
        loadDataToTable();
    }

    // --- HÀM HỖ TRỢ TẠO GIAO DIỆN ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txt.setBackground(new Color(40, 40, 40));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(colorGold);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(80, 80, 80), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    // --- HÀM LOAD DỮ LIỆU ---
    private void loadDataToTable() {
        model.setRowCount(0); 
        ArrayList<Phim> dsPhim = phimDAO.docTuBang();
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (Phim p : dsPhim) {
            model.addRow(new Object[]{
                p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), nf.format(p.getGiaVe()) + " đ"
            });
        }
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // XÓA RỖNG
        if (o == btnXoaRong) {
            txtMaPhim.setText("");
            txtTenPhim.setText("");
            txtTheLoai.setText("");
            txtGiaVe.setText("");
            txtMaPhim.setEditable(true); // MỞ KHÓA MÃ PHIM ĐỂ THÊM MỚI
            txtMaPhim.setBackground(new Color(40, 40, 40));
            txtMaPhim.requestFocus(); 
            table.clearSelection();
        } 
        
        // THÊM PHIM
        else if (o == btnThem) {
            try {
                String ma = txtMaPhim.getText().trim();
                String ten = txtTenPhim.getText().trim();
                String tl = txtTheLoai.getText().trim();
                double gia = Double.parseDouble(txtGiaVe.getText().trim());

                if (ma.isEmpty() || ten.isEmpty() || tl.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin phim!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Phim p = new Phim(ma, ten, tl, gia);
                if (phimDAO.themPhim(p)) { 
                    loadDataToTable(); // Load lại toàn bộ để format lại tiền luôn
                    JOptionPane.showMessageDialog(this, "Thêm phim thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    btnXoaRong.doClick(); // Tự động làm mới khung nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Mã phim đã tồn tại trong hệ thống!", "Lỗi trùng mã", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giá vé phải là con số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        } 
        
        // XÓA PHIM
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần xóa từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int hoiNhac = JOptionPane.showConfirmDialog(this, "Hành động này sẽ xóa dữ liệu phim.\nBạn có chắc chắn muốn xóa?", "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (hoiNhac == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                if (phimDAO.xoaPhim(ma)) { 
                    model.removeRow(row);  
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    btnXoaRong.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa! Phim này đang có suất chiếu hoặc hóa đơn tồn tại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            }
        } 
        
        // SỬA PHIM
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần sửa từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String ma = txtMaPhim.getText().trim(); // Mã không đổi
                String ten = txtTenPhim.getText().trim();
                String tl = txtTheLoai.getText().trim();
                double gia = Double.parseDouble(txtGiaVe.getText().trim());

                if (ten.isEmpty() || tl.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng không để trống thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Phim p = new Phim(ma, ten, tl, gia);
                if (phimDAO.suaPhim(p)) { 
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật dữ liệu phim!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giá vé phải là con số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
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
            
            // Xử lý cắt chữ " đ" và dấu chấm phẩy khỏi chuỗi tiền tệ để nhét lại vào TextField
            String giaStr = model.getValueAt(row, 3).toString().replaceAll("[. đ]", "");
            txtGiaVe.setText(giaStr);
            
            // KHÓA MÃ PHIM LẠI KHÔNG CHO SỬA (TRÁNH LỖI KHÓA CHÍNH SQL)
            txtMaPhim.setEditable(false);
            txtMaPhim.setBackground(new Color(60, 60, 60)); // Làm tối ô lại báo hiệu bị khóa
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}