package com.hfjy.framework.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.hfjy.framework.common.entity.generate.EntityGenerateTool;
import com.hfjy.framework.common.entity.generate.MySqlGenerateTool;
import com.hfjy.framework.common.util.LocalResourcesUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.base.DBAccess;
import com.hfjy.framework.database.base.DatabaseTools;
import com.hfjy.framework.database.base.SimpleDBAccess;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.database.entity.DBScriptType;
import com.hfjy.framework.database.entity.ResultInfo;
import com.hfjy.framework.init.ConfigDbUtil;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.view.entity.ExecuteRunnable;
import com.hfjy.framework.view.factory.SwingActivatorFactory;
import com.hfjy.framework.view.factory.SwingContainerFactory;

public class DatabBaseViewPanel extends JPanel implements ActionListener, TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JPanel jpChoose, jpState, jpDBObject;
	private JTree jtDBObject;
	private JPopupMenu jpmDBoRight, jpmSQLExec;
	private JMenuItem jmiExportObject, jmiExec;
	private DefaultTreeModel rootModel;
	private DefaultMutableTreeNode root;
	private JLabel jlUique, jlDBObject, jlConnInfo;
	private JComboBox<Object> jcbUnique;
	private JCheckBox jcbIsEdit;
	private JTextArea jtaSqlEdit;
	private JScrollPane jspDBObject, jspResultSet, jspSqlEdit;
	private JTable jtResultSet;
	private JTabbedPane jtpMainView;
	private JFileChooser jfcBrowse;
	private Properties properties = LocalResourcesUtil.getProperties(Initial.DB_CONFIG_FILE);
	boolean sqlEditLock = true;

	public DatabBaseViewPanel() {
		this.setLayout(new BorderLayout());
		buildWin();
		loadEvent();
		this.setVisible(true);
	}

	private void buildWin() {
		jfcBrowse = new JFileChooser(getClass().getResource("/").getFile().toString());
		jtpMainView = new JTabbedPane();
		jlDBObject = new JLabel("##### 数据库对象 #####");
		jlDBObject.setFont(SwingContainerFactory.getFont());
		jlUique = new JLabel("     标记：");
		jlUique.setFont(SwingContainerFactory.getFont());
		jlConnInfo = new JLabel("", SwingConstants.RIGHT);
		jlConnInfo.setFont(SwingContainerFactory.getFont());
		jcbUnique = new JComboBox<Object>(properties.keySet().toArray());
		jcbUnique.setFont(SwingContainerFactory.getFont());
		jcbIsEdit = new JCheckBox("编辑结果集", true);
		jcbIsEdit.setFont(SwingContainerFactory.getFont());
		jpmDBoRight = new JPopupMenu();
		jpmSQLExec = new JPopupMenu();
		jmiExportObject = new JMenuItem("导出表为实体类");
		jmiExportObject.setFont(SwingContainerFactory.getFont());
		jmiExec = new JMenuItem("执行");
		jmiExec.setFont(SwingContainerFactory.getFont());
		jpmDBoRight.add(jmiExportObject);
		jpmSQLExec.add(jmiExec);
		jtaSqlEdit = new JTextArea();
		jtaSqlEdit.setFont(new Font("", Font.BOLD, 14));
		jtaSqlEdit.setComponentPopupMenu(jpmSQLExec);
		root = new DefaultMutableTreeNode("没有加载");
		rootModel = new DefaultTreeModel(root);
		jtDBObject = new JTree(rootModel);
		jtDBObject.setComponentPopupMenu(jpmDBoRight);
		jtResultSet = new JTable();
		jtResultSet.setRowHeight(20);
		jtResultSet.setBackground(Color.white);
		jtResultSet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jspDBObject = new JScrollPane(jtDBObject);
		jspResultSet = new JScrollPane(jtResultSet);
		jspSqlEdit = new JScrollPane(jtaSqlEdit);
		jtpMainView.setFont(SwingContainerFactory.getFont());
		jtpMainView.add(jspSqlEdit, "SQL语句编辑框");
		jtpMainView.add(jspResultSet, "结果集");
		jpChoose = new JPanel();
		jpState = new JPanel(new BorderLayout());
		jpDBObject = new JPanel(new BorderLayout());
		jpChoose.add(jcbIsEdit);
		jpChoose.add(jlUique);
		jpChoose.add(jcbUnique);
		jpState.add(jlConnInfo, BorderLayout.CENTER);
		jpDBObject.add(jlDBObject, BorderLayout.NORTH);
		jpDBObject.add(jspDBObject, BorderLayout.CENTER);
		this.add(jpChoose, BorderLayout.NORTH);
		this.add(jpState, BorderLayout.SOUTH);
		this.add(jtpMainView, BorderLayout.CENTER);
		this.add(jpDBObject, BorderLayout.WEST);
	}

	private void loadEvent() {
		jcbIsEdit.addActionListener(this);
		jcbUnique.addActionListener(this);
		jmiExportObject.addActionListener(this);
		jmiExec.addActionListener(this);
		jtDBObject.addTreeSelectionListener(this);
	}

	private DBConnectionInfo getSerectDBInfo() {
		String configFile = properties.getProperty(jcbUnique.getSelectedItem().toString());
		String path = StringUtils.unite(Initial.DB_CONFIG_PATH, File.separator, configFile);
		Properties dbConfig = LocalResourcesUtil.getProperties(path);
		DBConnectionInfo dbinfo = new DBConnectionInfo(jcbUnique.getSelectedItem().toString(), dbConfig);
		return dbinfo;
	}

	public void actionPerformed(ActionEvent e) {
		SwingActivatorFactory.run(new ExecuteRunnable(e) {
			@Override
			public void process() {
				Object tmp = eventObject.getSource();
				if (tmp == jcbIsEdit) {
					sqlEditLock = jcbIsEdit.isSelected();
				} else if (tmp == jcbUnique) {
					initDBObject();
				} else if (tmp == jmiExportObject) {
					exportObject();
				} else if (tmp == jmiExec) {
					if (jcbUnique.getSelectedItem().toString().equals("没有加载")) {
						JOptionPane.showMessageDialog(null, "没有加载配置文件！");
						return;
					}
					if (jtaSqlEdit.getText().length() < 1) {
						JOptionPane.showMessageDialog(null, "没有SQL命令！");
						return;
					}
					if (jtaSqlEdit.getSelectedText() == null) {
						selectTable(jtaSqlEdit.getText());
					} else {
						selectTable(jtaSqlEdit.getSelectedText());
					}
					jpDBObject.updateUI();
				}
			}
		});
		jtDBObject.updateUI();
	}

	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() == null) {
			return;
		}
		Object[] path = e.getNewLeadSelectionPath().getPath();
		if (path.length > 2) {
			String mode = e.getPath().getParentPath().getLastPathComponent().toString();
			String patgName = e.getPath().getLastPathComponent().toString();
			String sql = "";
			if (mode != null && mode.equals("表")) {
				sql = ConfigDbUtil.init().getDBScript(getSerectDBInfo().getDbType(), DBScriptType.selectTable);
			} else if (mode != null && mode.equals("视图")) {
				sql = ConfigDbUtil.init().getDBScript(getSerectDBInfo().getDbType(), DBScriptType.selectView);
			} else if (mode != null && mode.equals("存储过程")) {

			}
			sql = String.format(sql, patgName);
			jtaSqlEdit.setText(sql);
			selectTable(sql);
			jpDBObject.updateUI();
		}
	}

	private void selectTable(String sql) {
		DBAccess da = new SimpleDBAccess(getSerectDBInfo());
		ResultInfo ri = da.executeQuery(sql);
		DefaultTableModel model = new DefaultTableModel();
		TableColumnModel tcm = jtResultSet.getColumnModel();
		jtResultSet.setModel(model);
		for (int i = 0; i < ri.getColNum(); i++) {
			String[] tmp = new String[ri.getRowNum()];
			int l = 0;
			for (int r = 0; r < ri.getRowNum(); r++) {
				if (ri.getData(r, i) != null) {
					tmp[r] = ri.getData(r, i).toString();
				} else {
					tmp[r] = null;
				}
				if (tmp[r] != null && l < tmp[r].length()) {
					l = tmp[r].length();
				}
			}
			model.addColumn(ri.getColNames()[i], tmp);
			tcm.getColumn(i).setPreferredWidth(l * 10);
		}
		String errorMessage = ri.getResultException() == null ? "" : ri.getResultException().getMessage();
		if (ri.getUpdateNum() > 0) {
			jlConnInfo.setText(ri.getUpdateNum() + "行受影响 " + errorMessage);
		} else {
			jlConnInfo.setText("返回" + ri.getRowNum() + "条记录 " + errorMessage);
			if (ri.getRowNum() > 0) {
				jtpMainView.setSelectedIndex(1);
			}
		}
	}

	private boolean exportObject() {
		jfcBrowse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int state = jfcBrowse.showOpenDialog(null);
		if (state == 0) {
			String code = jcbUnique.getSelectedItem().toString();
			String path = StringUtils.unite(jfcBrowse.getSelectedFile().getAbsolutePath(), File.separator);
			String packageName = JOptionPane.showInputDialog(this, "请输入包路径");
			StringBuilder sb = new StringBuilder();
			sb.append(StringUtils.unite("数据库标识: ", code, "\r\n"));
			sb.append(StringUtils.unite("生成路径: ", path, "\r\n"));
			sb.append(StringUtils.unite("包路径: ", packageName, "\r\n"));
			int isOk = JOptionPane.showConfirmDialog(this, sb.toString(), "确认生成吗？", JOptionPane.OK_CANCEL_OPTION);
			if (isOk == 0 && StringUtils.isNotEmpty(packageName)) {
				DBConnectionInfo dbInfo = getSerectDBInfo();
				EntityGenerateTool entityGenerateTool = null;
				switch (dbInfo.getDbType()) {
				case MYSQL:
					entityGenerateTool = new MySqlGenerateTool(code);
					break;
				case ORACLE:

					break;
				case SQLSERVER:

					break;
				case DB2:

					break;
				case SQLITE:

					break;
				default:
					entityGenerateTool = new MySqlGenerateTool(code);
					break;
				}
				try {
					entityGenerateTool.start(path, packageName);
					JOptionPane.showMessageDialog(this, "生成结束。");
					return true;
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "生成出错！\r\n" + e.getMessage());
					return false;
				}
			}
		}
		return false;
	}

	private void initDBObject() {
		if (jcbUnique.getSelectedItem() == null) {
			return;
		}
		String dbUnique = jcbUnique.getSelectedItem().toString();
		DBConnectionInfo dbInfo = getSerectDBInfo();
		DBAccess dbs = DatabaseTools.getDBAccess(dbUnique);
		String getTablesSql = ConfigDbUtil.init().getDBScript(dbInfo.getDbType(), DBScriptType.getTables);
		String getViewsSql = ConfigDbUtil.init().getDBScript(dbInfo.getDbType(), DBScriptType.getViews);
		String getProceduresSql = ConfigDbUtil.init().getDBScript(dbInfo.getDbType(), DBScriptType.getProcedures);
		ResultInfo tabinfo = dbs.executeQuery(getTablesSql);
		ResultInfo vieinfo = dbs.executeQuery(getViewsSql);
		ResultInfo spinfo = dbs.executeQuery(getProceduresSql);
		DefaultMutableTreeNode tables = new DefaultMutableTreeNode("表");
		DefaultMutableTreeNode views = new DefaultMutableTreeNode("视图");
		DefaultMutableTreeNode procedures = new DefaultMutableTreeNode("存储过程");
		if (tabinfo != null) {
			for (int i = 0; i < tabinfo.getRowNum(); i++) {
				tables.add(new DefaultMutableTreeNode(tabinfo.getData(i, 0)));
			}
		}
		if (vieinfo != null) {
			for (int i = 0; i < vieinfo.getRowNum(); i++) {
				views.add(new DefaultMutableTreeNode(vieinfo.getData(i, 0)));
			}
		}
		if (spinfo != null) {
			for (int i = 0; i < spinfo.getRowNum(); i++) {
				procedures.add(new DefaultMutableTreeNode(spinfo.getData(i, 0)));
			}
		}
		DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode(dbInfo.getDbCode());
		newRoot.add(tables);
		newRoot.add(views);
		newRoot.add(procedures);
		rootModel.setRoot(newRoot);
	}
}
