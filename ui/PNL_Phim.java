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
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    private PhimDAO phimDAO;

    // --- BẢNG MÀU CHỦ ĐỀ ĐỎ (NETFLIX / CGV THEME) ---
    private Color bgDark = new Color(18, 18, 18);          // Nền chính tối đen
    private Color bgPanel = new Color(30, 30, 30);         // Nền khung phụ xám đen
    private Color textWhite = new Color(240, 240, 240);    // Chữ trắng sáng
    private Color themeRed = new Color(229, 9, 20);        // Đỏ chuẩn Netflix (Điểm nhấn)

    // --- LỚP NÚT BẤM CAO CẤP ---
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
            setPreferredSize(new Dimension(120, 40));

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
        // 1. FORM NHẬP LIỆU
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(2, 4, 20, 20));
        pnlInput.setBackground(bgPanel);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        // Đổi màu tiêu đề khung sang Đỏ
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "THÔNG TIN PHIM",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setBackground(bgPanel);
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

        pnlInput.add(createLabel("Giá Vé (VNĐ):"));
        txtGiaVe = createTextField();
        pnlInput.add(txtGiaVe);

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG
        // ==========================================
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setOpaque(false);
        
        // Vẫn giữ các màu chức năng quen thuộc để người dùng dễ nhận diện
        btnThem = new PosButton("THÊM", new Color(46, 204, 113), Color.WHITE);
        btnSua = new PosButton("CẬP NHẬT", new Color(52, 152, 219), Color.WHITE);
        btnXoa = new PosButton("XÓA", themeRed, Color.WHITE); // Nút Xóa lấy luôn màu themeRed
        btnXoaRong = new PosButton("LÀM MỚI", new Color(100, 100, 100), Color.WHITE);

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnXoaRong);

        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // 3. BẢNG DỮ LIỆU
        // ==========================================
        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setBackground(bgPanel);
        pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(5, 5, 5, 5)
        ));

        String[] cols = {"Mã Phim", "Tên Phim", "Thể Loại", "Giá Vé"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.setRowHeight(35);
        table.setBackground(bgPanel); 
        table.setForeground(textWhite);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        // Đổi màu nền khi chọn dòng trong bảng thành màu Đỏ Netflix
        table.setSelectionBackground(themeRed); 
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false); 
        
        // Custom Header Bảng chữ Đỏ
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(20, 20, 20));
        headerRenderer.setForeground(themeRed); 
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(bgPanel); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        pnlTableWrapper.add(scrollPane, BorderLayout.CENTER);
        add(pnlTableWrapper, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);
        
        loadDataToTable();
    }

    // --- CÁC HÀM HỖ TRỢ GIAO DIỆN ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBackground(new Color(40, 40, 40)); 
        txt.setForeground(Color.WHITE); 
        // Con trỏ nhấp nháy trong ô nhập văn bản cũng đổi thành màu đỏ
        txt.setCaretColor(themeRed); 
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), 
            new EmptyBorder(5, 10, 5, 10) 
        ));
        return txt;
    }

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
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        for (Phim p : ds) {
            model.addRow(new Object[]{
                p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), nf.format(p.getGiaVe()) + " đ"
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
            txtMaPhim.setBackground(new Color(40, 40, 40));
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
                    btnXoaRong.doClick();
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
            } else {
                JOptionPane.showMessageDialog(this, "Sửa thất bại!", "Lỗi CSDL", ERROR);
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
            
            String giaStr = model.getValueAt(row, 3).toString().replaceAll("[. đ]", "");
            txtGiaVe.setText(giaStr);
            
            txtMaPhim.setEditable(false); 
            txtMaPhim.setBackground(new Color(60, 60, 60)); 
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}