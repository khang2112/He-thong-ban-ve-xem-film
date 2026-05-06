package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

// IMPORT DAO VÀ ENTITY (Nhớ đảm bảo đúng tên package của bạn)
import dao.SuatChieuDAO;
import entity.SuatChieu;

public class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {
	private JTextField txtMaSuat;
	private UI_TrangChu ui_TrangChu;
    private JButton btnDenBanVe;
    private dao.PhimDAO phimDAO;       
    private dao.HoaDonDAO hoaDonDAO;
	private JDateChooser txtNgayChieu;
	private JSpinner spnGioChieu;
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
				public void mouseEntered(MouseEvent e) {
					setBackground(bgColor.brighter());
					repaint();
				}

				public void mouseExited(MouseEvent e) {
					setBackground(bgColor);
					repaint();
				}
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

	public PNL_SuatChieu(UI_TrangChu ui_TrangChu) {
		this.ui_TrangChu = ui_TrangChu;
		
		suatChieuDAO = new SuatChieuDAO(); // Nạp kết nối CSDL
		phimDAO = new dao.PhimDAO();      
        hoaDonDAO = new dao.HoaDonDAO();
		
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

		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)),
				"THÔNG TIN SUẤT CHIẾU", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14),
				themeRed);

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
		txtNgayChieu = new JDateChooser();
		txtNgayChieu.setDateFormatString("yyyy-MM-dd");
		
		JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) txtNgayChieu.getDateEditor();
		dateEditor.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		dateEditor.setBackground(new Color(40, 40, 40)); // Nền xám đen
		dateEditor.setForeground(Color.WHITE);           // Chữ trắng
		dateEditor.setCaretColor(themeRed);              // Con trỏ chuột màu đỏ
		dateEditor.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(80, 80, 80)),
				new EmptyBorder(5, 10, 5, 10)));
		txtNgayChieu.setBackground(bgPanel); // Màu nền của cả JDateChooser
		dateEditor.addPropertyChangeListener("foreground", e -> dateEditor.setForeground(Color.WHITE));
		pnlInput.add(txtNgayChieu);

		// Hàng 3
		pnlInput.add(createLabel("Giờ Chiếu (HH:mm):"));
		SpinnerDateModel timeModel = new SpinnerDateModel();
		spnGioChieu = new JSpinner(timeModel);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGioChieu, "HH:mm");
		spnGioChieu.setEditor(timeEditor);
		spnGioChieu.setValue(java.sql.Time.valueOf("00:00:00"));
		
		JFormattedTextField txtSpinner = timeEditor.getTextField();
		txtSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtSpinner.setBackground(new Color(40, 40, 40)); // Nền xám đen
		txtSpinner.setForeground(Color.WHITE);           // Chữ trắng
		txtSpinner.setCaretColor(themeRed);              // Con trỏ chuột màu đỏ
		txtSpinner.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding cho chữ
		
		spnGioChieu.setBackground(new Color(40, 40, 40));
		spnGioChieu.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80))); // Viền ngoài JSpinner
		pnlInput.add(spnGioChieu);

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
		btnDenBanVe = new PosButton("BÁN VÉ", new Color(212, 175, 55), Color.BLACK);
		
		pnlBtns.add(btnThem);
		pnlBtns.add(btnSua);
		pnlBtns.add(btnXoa);
		pnlBtns.add(btnXoaRong);
		pnlBtns.add(btnDenBanVe);
		
		pnlTop.add(pnlBtns, BorderLayout.SOUTH);
		add(pnlTop, BorderLayout.NORTH);

		// ==========================================
		// 3. BẢNG DỮ LIỆU
		// ==========================================
		JPanel pnlTableWrapper = new JPanel(new BorderLayout());
		pnlTableWrapper.setBackground(bgPanel);
		pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(60, 60, 60), 1),
				new EmptyBorder(5, 5, 5, 5)));

		String[] cols = { "Mã Suất", "Tên Phim", "Phòng Chiếu", "Ngày Chiếu", "Giờ Chiếu", "Trạng Thái Vé"};
		model = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
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
		btnDenBanVe.addActionListener(this);

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
		txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)),
				new EmptyBorder(5, 10, 5, 10)));
		return txt;
	}

	private JComboBox<String> createComboBox() {
		JComboBox<String> cbo = new JComboBox<>();
		cbo.setUI(new BasicComboBoxUI());
		cbo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		cbo.setBackground(new Color(40, 40, 40));
		cbo.setForeground(Color.WHITE);
		cbo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)),
				new EmptyBorder(5, 5, 5, 5)));
		return cbo;
	}

	// --- HÀM LOAD DỮ LIỆU TỪ SQL ---
	public void loadDataToTable() {
		model.setRowCount(0);
		try {
			ArrayList<SuatChieu> ds = suatChieuDAO.docTuBang();
            ArrayList<entity.Phim> dsPhim = phimDAO.docTuBang();
            
			for (SuatChieu s : ds) {
                String tenPhim = s.getMaPhim();
                for(entity.Phim p : dsPhim) {
                    if(p.getMaPhim().equals(s.getMaPhim())) { tenPhim = p.getTenPhim(); break; }
                }
                
                int tongGhe = 48; // Giả sử rạp bạn có 48 ghế
                int gheDaBan = hoaDonDAO.layDanhSachGheDaBan(s.getMaSuat()).size();
                int veCon = tongGhe - gheDaBan;
                String trangThaiVe = veCon > 0 ? (veCon + "/" + tongGhe + " vé") : "HẾT VÉ";

				model.addRow(new Object[] { 
                    s.getMaSuat(), tenPhim, s.getPhongChieu(), s.getNgayChieu(), s.getGioChieu(), trangThaiVe 
                });
			}
		} catch (Exception e) {
			System.out.println("Lỗi Load Data: Bạn cần kiểm tra lại SuatChieuDAO");
		}
	}

	// --- VALIDATE DỮ LIỆU ---
	private boolean validData() {
		String maS = txtMaSuat.getText().trim();
		String maP = cboPhim.getSelectedItem().toString().split(" - ")[0];
		String phong = cboPhong.getSelectedItem().toString();

		java.util.Date ngay = txtNgayChieu.getDate();
		java.util.Date gio = (java.util.Date) spnGioChieu.getValue();

		if (!(maS.length() > 0 && maS.matches("^S[0-9]{3}$"))) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập mã suất chiếu đúng định dạng. VD: S001");
			txtMaSuat.requestFocus();
			return false;
		}

		if (ngay == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày chiếu");
			txtNgayChieu.requestFocus();
			return false;
		}

		LocalDate ngayChon = ngay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalTime gioChon = gio.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

		LocalDate ngayHT = LocalDate.now();
		LocalTime gioHT = LocalTime.now();

		if (ngayChon.isBefore(ngayHT)) {
			JOptionPane.showMessageDialog(this, "Ngày chiếu không được ở quá khứ");
			txtNgayChieu.requestFocus();
			return false;
		} else if (ngayChon.equals(ngayHT)) {
			if (gioChon.isBefore(gioHT)) {
				JOptionPane.showMessageDialog(this, "Suất chiếu phải diễn ra sau giờ hiện tại");
				spnGioChieu.requestFocus();
				return false;
			}
		}
		return true;
	}

	// --- XỬ LÝ SỰ KIỆN NÚT BẤM ---
	// --- XỬ LÝ SỰ KIỆN NÚT BẤM ---
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		// 1. XÓA RỖNG
		if (o == btnXoaRong) {
			txtMaSuat.setText("");
			txtNgayChieu.setDate(null);
			spnGioChieu.setValue(java.sql.Time.valueOf("00:00:00"));
			if (cboPhim.getItemCount() > 0)
				cboPhim.setSelectedIndex(0);
			if (cboPhong.getItemCount() > 0)
				cboPhong.setSelectedIndex(0);

			txtMaSuat.requestFocus();
			txtMaSuat.setEditable(true);
			txtMaSuat.setBackground(new Color(40, 40, 40));
			table.clearSelection();
		}
		// 2. THÊM SUẤT CHIẾU
		else if (o == btnThem) {
			if (!validData())
				return;
			try {
				String ma = txtMaSuat.getText().trim();
				String phim = cboPhim.getSelectedItem().toString().split(" - ")[0];
				String phong = cboPhong.getSelectedItem().toString();

				// --- FIX: Lấy dữ liệu từ JDateChooser và JSpinner giống Code 1 ---
				java.util.Date dateNgay = txtNgayChieu.getDate();
				java.util.Date dateGio = (java.util.Date) spnGioChieu.getValue();
				LocalDate ngay = dateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalTime gio = dateGio.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

				SuatChieu s = new SuatChieu(ma, phim, phong, ngay, gio);

				// Mở comment khi muốn chạy DB thực tế
				if (suatChieuDAO.themSuatChieu(s)) {
					loadDataToTable();
					JOptionPane.showMessageDialog(this, "Thêm suất chiếu thành công!");
					btnXoaRong.doClick();
				} else {
					JOptionPane.showMessageDialog(this, "Trùng mã suất chiếu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
				JOptionPane.showMessageDialog(this, "Đã bắt sự kiện Thêm.");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi lấy dữ liệu Ngày/Giờ!", "Lỗi hệ thống",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		// 3. XÓA SUẤT CHIẾU
		else if (o == btnXoa) {
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu cần xóa từ bảng!", "Nhắc nhở",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa suất chiếu này?", "Xác nhận",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				String ma = model.getValueAt(row, 0).toString();
				if (suatChieuDAO.xoaSuatChieu(ma)) {
					loadDataToTable();
					JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
					btnXoaRong.doClick();
				} else {
					JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
				}
				JOptionPane.showMessageDialog(this, "Đã bắt sự kiện Xóa.");
			}
		}
		// 4. SỬA SUẤT CHIẾU
		else if (o == btnSua) {
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu cần cập nhật!", "Nhắc nhở",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// --- FIX: Sửa lỗi gõ nhầm validateData() thành validData() ---
			if (!validData())
				return;

			try {
				String ma = txtMaSuat.getText().trim();
				String phim = cboPhim.getSelectedItem().toString().split(" - ")[0];
				String phong = cboPhong.getSelectedItem().toString();

				// --- FIX: Lấy dữ liệu từ JDateChooser và JSpinner giống Code 1 ---
				java.util.Date dateNgay = txtNgayChieu.getDate();
				java.util.Date dateGio = (java.util.Date) spnGioChieu.getValue();
				LocalDate ngay = dateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalTime gio = dateGio.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

				SuatChieu s = new SuatChieu(ma, phim, phong, ngay, gio);
				if (suatChieuDAO.suaSuatChieu(s)) {
					loadDataToTable();
					JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Sửa thất bại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
				}
				JOptionPane.showMessageDialog(this, "Đã bắt sự kiện Sửa.");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi lấy dữ liệu Ngày/Giờ!", "Lỗi hệ thống",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		// TÌM HÀM actionPerformed, THÊM KHỐI LỆNH NÀY VÀO CUỐI HÀM:
	    else if (o == btnDenBanVe) {
	        int row = table.getSelectedRow();
	        if (row == -1) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 suất chiếu từ bảng để bán vé!", "Nhắc nhở", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        
	        String maSuat = model.getValueAt(row, 0).toString();
	        String tenPhim = model.getValueAt(row, 1).toString();
	        String ngayChieu = model.getValueAt(row, 3).toString(); 
	        String trangThai = model.getValueAt(row, 5).toString();
	        
	        if(trangThai.equals("HẾT VÉ")) {
	            JOptionPane.showMessageDialog(this, "Suất chiếu này đã hết vé!", "Thông báo", JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        if (ui_TrangChu != null) {
	            ui_TrangChu.chuyenTrangBanVeTheoSuat(tenPhim, ngayChieu, maSuat);
	        }
	    }
	}

	// --- SỰ KIỆN CLICK VÀO BẢNG ---
	@Override
	public void mouseClicked(MouseEvent e) {
		int row = table.getSelectedRow();
		if (row != -1) {
			txtMaSuat.setText(model.getValueAt(row, 0).toString());

			// Xử lý ComboBox Phim (nếu bạn gộp chuỗi giống code 1)
			String maPhimTable = model.getValueAt(row, 1).toString();
			for (int i = 0; i < cboPhim.getItemCount(); i++) {
				if (cboPhim.getItemAt(i).startsWith(maPhimTable)) {
					cboPhim.setSelectedIndex(i);
					break;
				}
			}

			cboPhong.setSelectedItem(model.getValueAt(row, 2).toString());

			// --- FIX: Xử lý đổ dữ liệu ra Lịch (JDateChooser) ---
			String ngayStr = model.getValueAt(row, 3).toString();
			if (!ngayStr.isEmpty()) {
				txtNgayChieu.setDate(java.sql.Date.valueOf(LocalDate.parse(ngayStr)));
			}

			// --- FIX: Xử lý đổ dữ liệu ra Khung giờ (JSpinner) ---
			String gioStr = model.getValueAt(row, 4).toString();
			if (!gioStr.isEmpty()) {
				spnGioChieu.setValue(java.sql.Time.valueOf(LocalTime.parse(gioStr)));
			}

			// Khóa mã suất chiếu không cho sửa để bảo vệ Database
			txtMaSuat.setEditable(false);
			txtMaSuat.setBackground(new Color(60, 60, 60));
		}
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	// HÀM LỌC PHIM
	public void locTheoPhim(String tenPhim) {
		loadDataToTable(); 
	    for(int i = model.getRowCount() - 1; i >= 0; i--) {
	        if(!model.getValueAt(i, 1).toString().equalsIgnoreCase(tenPhim)) {
	            model.removeRow(i);
	        }
	    }
	}
}
