package com.hfjy.framework.view.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.entity.ResultInfo;
import com.hfjy.framework.init.ConfigDbUtil;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.view.factory.SwingContainerFactory;

public class SystemConfigDialog extends JDialog implements ActionListener, DocumentListener {
	private static final long serialVersionUID = 1L;
	private JPanel jpSelectList, jpConfigShow, jpSystemConfig, jpAchieveConfig, jpScriptConfig, jpServiceConfig, jpOperat;
	private JButton jbSystemConfig, jbAchieveConfig, jbScriptConfig, jbServiceConfig, jbSave, jbClose;
	private CardLayout cardLayout = new CardLayout();
	private Map<Document, String> documentMap = new HashMap<>();
	private Map<JComboBox<String>, String> comboBoxMap = new HashMap<>();
	private Map<String, String> saveSqlMap = new TreeMap<>();

	public SystemConfigDialog(JFrame mainWin) {
		super(mainWin, true);
		buildWin();
		loadEvent();
		this.setSize((int) (mainWin.getWidth() * 0.8), (int) (mainWin.getHeight() * 0.8));
		this.setTitle("系统配置");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}

	private void buildWin() {
		this.setLayout(new BorderLayout());
		jbSystemConfig = new JButton("默认配置");
		jbSystemConfig.setFont(SwingContainerFactory.getFont());
		jbAchieveConfig = new JButton("组件配置");
		jbAchieveConfig.setFont(SwingContainerFactory.getFont());
		jbScriptConfig = new JButton("脚本配置");
		jbScriptConfig.setFont(SwingContainerFactory.getFont());
		jbServiceConfig = new JButton("服务配置");
		jbServiceConfig.setFont(SwingContainerFactory.getFont());
		jpSelectList = new JPanel(new GridLayout(10, 1, 0, 5));
		jpSelectList.add(jbSystemConfig);
		jpSelectList.add(jbAchieveConfig);
		jpSelectList.add(jbScriptConfig);
		jpSelectList.add(jbServiceConfig);

		JPanel jPanel = null;

		jpConfigShow = new JPanel(cardLayout);
		List<Component> component = getSystemConfig();
		jpSystemConfig = new JPanel(new GridLayout(component.size() / 3, 3, 0, 5));
		for (int i = 0; i < component.size(); i++) {
			jpSystemConfig.add(component.get(i));
		}
		jPanel = new JPanel();
		jPanel.add(jpSystemConfig);
		jpConfigShow.add(new JScrollPane(jPanel), "systemConfig");

		component = getAchieveConfig();
		jpAchieveConfig = new JPanel(new GridLayout(component.size() / 3, 3, 0, 5));
		for (int i = 0; i < component.size(); i++) {
			jpAchieveConfig.add(component.get(i));
		}
		jPanel = new JPanel();
		jPanel.add(jpAchieveConfig);
		jpConfigShow.add(new JScrollPane(jPanel), "achieveConfig");

		component = getScriptConfig();
		jpScriptConfig = new JPanel(new GridLayout(component.size() / 3, 3, 0, 5));
		for (int i = 0; i < component.size(); i++) {
			jpScriptConfig.add(component.get(i));
		}
		jPanel = new JPanel();
		jPanel.add(jpScriptConfig);
		jpConfigShow.add(new JScrollPane(jPanel), "scriptConfig");

		component = getServiceConfig();
		jpServiceConfig = new JPanel(new GridLayout(component.size() / 4, 4, 0, 5));
		for (int i = 0; i < component.size(); i++) {
			jpServiceConfig.add(component.get(i));
		}
		jPanel = new JPanel();
		jPanel.add(jpServiceConfig);
		jpConfigShow.add(new JScrollPane(jPanel), "serviceConfig");

		jbSave = new JButton("保存");
		jbSave.setFont(SwingContainerFactory.getFont());
		jbClose = new JButton("关闭");
		jbClose.setFont(SwingContainerFactory.getFont());
		jpOperat = new JPanel();
		jpOperat.add(jbSave);
		jpOperat.add(jbClose);

		this.add(jpConfigShow, BorderLayout.CENTER);
		this.add(jpSelectList, BorderLayout.WEST);
		this.add(jpOperat, BorderLayout.SOUTH);
	}

	private void loadEvent() {
		jbSave.addActionListener(this);
		jbClose.addActionListener(this);
		jbAchieveConfig.addActionListener(this);
		jbServiceConfig.addActionListener(this);
		jbScriptConfig.addActionListener(this);
		jbSystemConfig.addActionListener(this);
	}

	private List<Component> getSystemConfig() {
		List<Component> tmpList = new ArrayList<>();
		Font pFont = new Font("", Font.PLAIN, 14);
		ResultInfo ri = ConfigDbUtil.init().getSysParameterList();
		for (int i = 0; i < ri.getRowNum(); i++) {
			String name = ri.getData(i, "name").toString();
			String desc = ri.getData(i, "desc").toString();
			String value = ri.getData(i, "value").toString();
			JTextField jtf = getField(value, 15);
			tmpList.add(SwingContainerFactory.getJLabel(name, SwingConstants.LEFT, pFont));
			tmpList.add(jtf);
			tmpList.add(SwingContainerFactory.getJLabel(desc, SwingConstants.LEFT, pFont));
			documentMap.put(jtf.getDocument(), name);
		}
		return tmpList;
	}

	private List<Component> getAchieveConfig() {
		List<Component> tmpList = new ArrayList<>();
		Font pFont = new Font("", Font.PLAIN, 14);
		ResultInfo ri = ConfigDbUtil.init().getSysAchieveList();
		Map<String, JComboBox<String>> jComboBoxMap = new HashMap<>();
		Map<String, JLabel> jLabelMap = new HashMap<>();
		for (int i = 0; i < ri.getRowNum(); i++) {
			String code = ri.getData(i, "code").toString();
			String name = ri.getData(i, "name").toString();
			String desc = ri.getData(i, "desc").toString();
			String status = ri.getData(i, "status").toString();
			JComboBox<String> jcbTmp = jComboBoxMap.get(code);
			if (jcbTmp == null) {
				tmpList.add(SwingContainerFactory.getJLabel(code, SwingConstants.LEFT, pFont));
				jcbTmp = new JComboBox<>();
				jcbTmp.addActionListener(this);
				JLabel jLabel = SwingContainerFactory.getJLabel(desc, SwingConstants.LEFT, pFont);
				tmpList.add(jcbTmp);
				tmpList.add(jLabel);
				jComboBoxMap.put(code, jcbTmp);
				jLabelMap.put(code, jLabel);
				comboBoxMap.put(jcbTmp, code);
			}
			jcbTmp.addItem(name);
			if (status.equals("true")) {
				jLabelMap.get(code).setText(desc);
				jcbTmp.setSelectedItem(name);
			}
		}
		return tmpList;
	}

	private List<Component> getScriptConfig() {
		List<Component> tmpList = new ArrayList<>();
		Font pFont = new Font("", Font.PLAIN, 14);
		ResultInfo ri = ConfigDbUtil.init().getSysSqlScriptList();
		for (int i = 0; i < ri.getRowNum(); i++) {
			String dbType = ri.getData(i, "database_type").toString();
			String sqlType = ri.getData(i, "script_type").toString();
			String name = StringUtils.unite(dbType, "_", sqlType);
			String sql = ri.getData(i, "sql") != null ? ri.getData(i, "sql").toString() : "";
			JTextField jtf = getField(sql, 19);
			tmpList.add(SwingContainerFactory.getJLabel(name, SwingConstants.LEFT, pFont));
			tmpList.add(jtf);
			tmpList.add(SwingContainerFactory.getJLabel(ri.getData(i, "database_type").toString(), SwingConstants.LEFT, pFont));
			documentMap.put(jtf.getDocument(), StringUtils.unite(dbType, ",", sqlType));
		}
		return tmpList;
	}

	private List<Component> getServiceConfig() {
		List<Component> tmpList = new ArrayList<>();
		Font pFont = new Font("", Font.PLAIN, 14);
		ResultInfo ri = ConfigDbUtil.init().getSysSendServiceList();
		for (int i = 0; i < ri.getRowNum(); i++) {
			String name = ri.getData(i, "name").toString();
			String desc = ri.getData(i, "desc").toString();
			String ip = ri.getData(i, "ip").toString();
			String port = ri.getData(i, "port").toString();
			JTextField ipJtf = getField(ip, 15);
			JTextField portJtf = getField(port, 15);
			tmpList.add(SwingContainerFactory.getJLabel(name, SwingConstants.LEFT, pFont));
			tmpList.add(ipJtf);
			tmpList.add(portJtf);
			tmpList.add(SwingContainerFactory.getJLabel(desc, SwingConstants.LEFT, pFont));
			documentMap.put(ipJtf.getDocument(), StringUtils.unite("ip", ",", name));
			documentMap.put(portJtf.getDocument(), StringUtils.unite("port", ",", name));
		}
		return tmpList;
	}

	private JTextField getField(String text, int size) {
		JTextField textField = new JTextField(size);
		textField.setText(text);
		textField.setFont(new Font("", Font.ITALIC, 14));
		textField.getDocument().addDocumentListener(this);
		return textField;
	}

	public void insertUpdate(DocumentEvent e) {
		updateText(e);
	}

	public void removeUpdate(DocumentEvent e) {
		updateText(e);
	}

	public void changedUpdate(DocumentEvent e) {
		updateText(e);
	}

	private void updateText(DocumentEvent e) {
		try {
			Document thisDocument = e.getDocument();
			if (e != null && thisDocument != null) {
				StringBuilder sql = new StringBuilder();
				String name = documentMap.get(thisDocument);
				String value = thisDocument.getText(0, thisDocument.getLength());
				String[] values = name.split(",");
				if (values.length == 1) {
					sql.append("update sys_parameter_list set value ='");
					sql.append(value);
					sql.append("' where name = '");
					sql.append(name);
					sql.append("'");
					saveSqlMap.put(name, sql.toString());
				} else if (values[0].equals("ip")) {
					sql.append("update sys_send_service_config set ip ='");
					sql.append(value);
					sql.append("' where name = '");
					sql.append(values[1]);
					sql.append("'");
					saveSqlMap.put(name, sql.toString());
				} else if (values[0].equals("port")) {
					sql.append("update sys_send_service_config set port ='");
					sql.append(value);
					sql.append("' where name = '");
					sql.append(values[1]);
					sql.append("'");
					saveSqlMap.put(name, sql.toString());
				} else {
					sql.append("update sys_sql_script set sql = '");
					sql.append(value);
					sql.append("' where database_type ='");
					sql.append(values[0]);
					sql.append("' , script_type = '");
					sql.append(values[1]);
					sql.append("'");
					saveSqlMap.put(name, sql.toString());
				}

			}
		} catch (Exception exception) {
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object tmp = e.getSource();
		if (tmp == jbSave) {
			if (Initial.CONFIG_DB_INIT_OK) {
				Iterator<String> iterator = saveSqlMap.keySet().iterator();
				while (iterator.hasNext()) {
					ConfigDbUtil.init().executeSql(saveSqlMap.get(iterator.next()));
				}
				saveSqlMap.clear();
				documentMap.clear();
				comboBoxMap.clear();
				JOptionPane.showMessageDialog(this, "保存成功！");
				this.setVisible(false);
			} else {
				JOptionPane.showMessageDialog(this, "保存失败。");
			}
		} else if (tmp == jbClose) {
			saveSqlMap.clear();
			documentMap.clear();
			comboBoxMap.clear();
			this.setVisible(false);
		} else if (tmp == jbAchieveConfig) {
			cardLayout.show(jpConfigShow, "achieveConfig");
		} else if (tmp == jbServiceConfig) {
			cardLayout.show(jpConfigShow, "serviceConfig");
		} else if (tmp == jbScriptConfig) {
			cardLayout.show(jpConfigShow, "scriptConfig");
		} else if (tmp == jbSystemConfig) {
			cardLayout.show(jpConfigShow, "systemConfig");
		} else if (tmp.getClass() == JComboBox.class) {
			JComboBox<?> tmpJComboBox = (JComboBox<?>) tmp;
			StringBuilder sql = new StringBuilder();
			sql.append("update sys_default_achieve set status ='false' where code = '");
			sql.append(comboBoxMap.get(tmp));
			sql.append("'");
			saveSqlMap.put(StringUtils.unite(comboBoxMap.get(tmp), "1"), sql.toString());
			sql.delete(0, sql.length());
			sql.append("update sys_default_achieve set status ='true' where code = '");
			sql.append(comboBoxMap.get(tmp));
			sql.append("' and name = '");
			sql.append(tmpJComboBox.getSelectedItem());
			sql.append("'");
			saveSqlMap.put(StringUtils.unite(comboBoxMap.get(tmp), "2"), sql.toString());
		}
	}
}
