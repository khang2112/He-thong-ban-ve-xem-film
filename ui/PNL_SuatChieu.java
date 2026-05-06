package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// IMPORT DAO VÀ ENTITY (Nhớ đảm bảo đúng tên package của bạn)
import dao.SuatChieuDAO;
import entity.SuatChieu;

public class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMaSuat, txtNgayChieu, txtGioChieu;
    private JComboBox<String> cboPhim, cboPhong;
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    // KHỞI TẠO DAO
    private SuatChieuDAO suatChieuDAO;

    // --- BẢNG MÀU CHỦ ĐỀ ĐỎ (NETFLIX / CGV THEME) ---
    private Color bgDark = new Color(18, 18, 18);
    private Color bgPanel = new Color(30, 30, 30);
    private Color textWhite = new Color(240, 240, 240);
    private Color themeRed = new Color(229, 9, 20); 

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

    public PNL_SuatChieu() {
        suatChieuDAO = new SuatChieuDAO(); // Nạp kết nối CSDL

        setLayout(new BorderLayout(15, 15));
        setBackground(bgDark);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        // ==========================================
        // 1. FORM NHẬP LIỆU (NORTH)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 20, 15));
        pnlInput.setBackground(bgPanel);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), "THÔNG TIN SUẤT CHIẾU",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), themeRed
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setBackground(bgPanel);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        // Hàng 1
        pnlInput.add(createLabel("Mã Suất Chiếu:"));
        txtMaSuat = createTextField();
        pnlInput.add(txtMaSuat);
        
        pnlInput.add(createLabel("Chọn Phim:"));
        cboPhim = createComboBox(); 
        // BẠN CÓ THỂ LÀM 1 HÀM LOAD DANH SÁCH PHIM TỪ PhimDAO VÀO ĐÂY
        cboPhim.addItem("P001");
        cboPhim.addItem("P002");
        cboPhim.addItem("P003");
        pnlInput.add(cboPhim);

        // Hàng 2
        pnlInput.add(createLabel("Phòng Chiếu:"));
        cboPhong = createComboBox();
        cboPhong.addItem("Phòng 1 (2D)"); 
        cboPhong.addItem("Phòng 2 (3D)"); 
        cboPhong.addItem("Phòng 5 (VIP)");
        pnlInput.add(cboPhong);
        
        pnlInput.add(createLabel("Ngày Chiếu (YYYY-MM-DD):"));
        txtNgayChieu = createTextField(); 
        pnlInput.add(txtNgayChieu);

        // Hàng 3
        pnlInput.add(createLabel("Giờ Chiếu (HH:mm):"));
        txtGioChieu = createTextField();
        pnlInput.add(txtGioChieu);
        pnlInput.add(new JLabel("")); // Khoảng trống cho cân đối layout
        pnlInput.add(new JLabel(""));

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG
        // ==========================================
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setOpaque(false);
        
        btnThem = new PosButton("THÊM", new Color(46, 204, 113), Color.WHITE);
        btnSua = new PosButton("CẬP NHẬT", new Color(52, 152, 219), Color.WHITE);
        btnXoa = new PosButton("XÓA", themeRed, Color.WHITE);
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

        String[] cols = {"Mã Suất", "Mã Phim", "Phòng Chiếu", "Ngày Chiếu", "Giờ Chiếu"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.setRowHeight(35);
        table.setBackground(bgPanel); 
        table.setForeground(textWhite);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(themeRed); 
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false); 
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(20, 20, 20));
        headerRenderer.setForeground(themeRed); 
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

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
        
        // Gọi hàm load dữ liệu CSDL lên bảng
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
        txt.setCaretColor(themeRed); 
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), 
            new EmptyBorder(5, 10, 5, 10) 
        ));
        return txt;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setUI(new BasicComboBoxUI()); 
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbo.setBackground(new Color(40, 40, 40)); 
        cbo.setForeground(Color.WHITE);
        cbo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), 
            new EmptyBorder(5, 5, 5, 5) 
        ));
        return cbo;
    }

    // --- HÀM LOAD DỮ LIỆU TỪ SQL ---
    public void loadDataToTable() {
        model.setRowCount(0);
        try {
            ArrayList<SuatChieu> ds = suatChieuDAO.docTuBang();
            for (SuatChieu s : ds) {
                model.addRow(new Object[]{
                    s.getMaSuat(), 
                    s.getMaPhim(), 
                    s.getPhongChieu(), 
                    s.getNgayChieu(), 
                    s.getGioChieu()
                });
            }
        } catch (Exception e) {
            System.out.println("Lỗi Load Data: Bạn cần kiểm tra lại SuatChieuDAO");
        }
    }

    // --- VALIDATE DỮ LIỆU ---
    private boolean validateData() {
        if (txtMaSuat.getText().trim().isEmpty() || txtNgayChieu.getText().trim().isEmpty() || txtGioChieu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin suất chiếu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // 1. XÓA RỖNG
        if (o == btnXoaRong) {
            txtMaSuat.setText(""); 
            txtNgayChieu.setText(""); 
            txtGioChieu.setText("");
            if(cboPhim.getItemCount() > 0) cboPhim.setSelectedIndex(0);
            if(cboPhong.getItemCount() > 0) cboPhong.setSelectedIndex(0);
            
            txtMaSuat.requestFocus();
            txtMaSuat.setEditable(true);
            txtMaSuat.setBackground(new Color(40, 40, 40));
            table.clearSelection();
        } 
        // 2. THÊM SUẤT CHIẾU
        else if (o == btnThem) {
            if (!validateData()) return;
            try {
                String ma = txtMaSuat.getText().trim();
                String phim = cboPhim.getSelectedItem().toString();
                String phong = cboPhong.getSelectedItem().toString();
                
                // Parse chuỗi nhập vào thành kiểu Date/Time, 
                // NẾU ENTITY CỦA BẠN DÙNG STRING THÌ ĐỔI THÀNH STRING
                LocalDate ngay = LocalDate.parse(txtNgayChieu.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime gio = LocalTime.parse(txtGioChieu.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

                SuatChieu s = new SuatChieu(ma, phim, phong, ngay, gio);
                // if (suatChieuDAO.themSuatChieu(s)) {
                //     loadDataToTable();
                //     JOptionPane.showMessageDialog(this, "Thêm suất chiếu thành công!");
                //     btnXoaRong.doClick();
                // } else {
                //     JOptionPane.showMessageDialog(this, "Trùng mã suất chiếu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                // }
                
                JOptionPane.showMessageDialog(this, "Đã bắt sự kiện Thêm. (Cần mở comment hàm suatChieuDAO.themSuatChieu)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày/Giờ sai định dạng (YYYY-MM-DD / HH:mm)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
        // 3. XÓA SUẤT CHIẾU
        else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu cần xóa từ bảng!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa suất chiếu này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ma = model.getValueAt(row, 0).toString();
                // if (suatChieuDAO.xoaSuatChieu(ma)) {
                //     loadDataToTable();
                //     JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                //     btnXoaRong.doClick();
                // } else {
                //     JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                // }
                JOptionPane.showMessageDialog(this, "Đã bắt sự kiện Xóa. (Cần mở comment hàm suatChieuDAO.xoaSuatChieu)");
            }
        }
        // 4. SỬA SUẤT CHIẾU
        else if (o == btnSua) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu cần cập nhật!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateData()) return;
            try {
                String ma = txtMaSuat.getText().trim();
                String phim = cboPhim.getSelectedItem().toString();
                String phong = cboPhong.getSelectedItem().toString();
                LocalDate ngay = LocalDate.parse(txtNgayChieu.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalTime gio = LocalTime.parse(txtGioChieu.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

                SuatChieu s = new SuatChieu(ma, phim, phong, ngay, gio);
                // if (suatChieuDAO.suaSuatChieu(s)) {
                //     loadDataToTable();
                //     JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                // } else {
                //     JOptionPane.showMessageDialog(this, "Sửa thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                // }
                JOptionPane.showMessageDialog(this, "Đã bắt sự kiện Sửa. (Cần mở comment hàm suatChieuDAO.suaSuatChieu)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ngày/Giờ sai định dạng (YYYY-MM-DD / HH:mm)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- SỰ KIỆN CLICK VÀO BẢNG ---
    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaSuat.setText(model.getValueAt(row, 0).toString());
            cboPhim.setSelectedItem(model.getValueAt(row, 1).toString());
            cboPhong.setSelectedItem(model.getValueAt(row, 2).toString());
            txtNgayChieu.setText(model.getValueAt(row, 3).toString());
            txtGioChieu.setText(model.getValueAt(row, 4).toString());
            
            // Khóa mã suất chiếu không cho sửa để bảo vệ Database
            txtMaSuat.setEditable(false); 
            txtMaSuat.setBackground(new Color(60, 60, 60)); 
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}