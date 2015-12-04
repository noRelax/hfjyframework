package com.hfjy.framework.view.panel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.hfjy.framework.cache.CacheAccess;
import com.hfjy.framework.cache.CacheAccessServer;
import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.common.util.LocalResourcesUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.view.entity.ExecuteRunnable;
import com.hfjy.framework.view.factory.SwingActivatorFactory;
import com.hfjy.framework.view.factory.SwingContainerFactory;

public class CacheViewPanel extends JPanel implements ActionListener, TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	private JPanel jpChoose, jpState, jpCacheObject;
	private JTree jtCacheObject;
	private JPopupMenu jpmCacheObjectRight;
	private JMenuItem jmiDeleteObject;
	private DefaultTreeModel rootModel;
	private DefaultMutableTreeNode root;
	private JLabel jlServers, jlCacheObject, jlCacheStatus;
	private JComboBox<Object> jcbServers;
	private JCheckBox jcbIsAutoRefresh;
	private JTextArea jtaCacheObjectInfo, jtaCacheServerInfo;
	private JScrollPane jspCacheObject, jspCacheObjectInfo, jspCacheServerInfo;
	private JTabbedPane jtpMainView, jtpRightView;
	private JButton jbRefresh, jbClear;
	private Properties properties = LocalResourcesUtil.getProperties(Initial.JEDIS_CONFIG_FILE);
	private JProgressBar jpbLoading;
	private Timer autoRefreshTimer;

	public CacheViewPanel() {
		this.setLayout(new BorderLayout());
		buildWin();
		loadEvent();
		this.setVisible(true);
	}

	private void buildWin() {
		jtpMainView = new JTabbedPane();
		jtpRightView = new JTabbedPane();
		jlCacheObject = new JLabel("##### 缓存对象 #####");
		jlCacheObject.setFont(SwingContainerFactory.getFont());
		jlServers = new JLabel("     服务器列表：");
		jlServers.setFont(SwingContainerFactory.getFont());
		jbRefresh = new JButton("刷新");
		jbRefresh.setFont(SwingContainerFactory.getFont());
		jbClear = new JButton("清空");
		jbClear.setFont(SwingContainerFactory.getFont());
		jlCacheStatus = new JLabel("未操作", SwingConstants.RIGHT);
		jlCacheStatus.setFont(SwingContainerFactory.getFont());
		jpbLoading = new JProgressBar(0, 100);
		jcbServers = new JComboBox<Object>(properties.values().toArray());
		jcbServers.setFont(SwingContainerFactory.getFont());
		jcbIsAutoRefresh = new JCheckBox("是否自动刷新", false);
		jcbIsAutoRefresh.setFont(SwingContainerFactory.getFont());
		jpmCacheObjectRight = new JPopupMenu();
		jmiDeleteObject = new JMenuItem("删除选择缓存对象");
		jmiDeleteObject.setFont(SwingContainerFactory.getFont());
		jpmCacheObjectRight.add(jmiDeleteObject);
		jtaCacheObjectInfo = new JTextArea();
		jtaCacheObjectInfo.setEditable(false);
		jtaCacheObjectInfo.setFont(new Font("", Font.ITALIC, 14));
		jtaCacheServerInfo = new JTextArea();
		jtaCacheServerInfo.setEditable(false);
		jtaCacheServerInfo.setFont(new Font("", Font.ITALIC, 14));
		root = new DefaultMutableTreeNode("没有加载");
		rootModel = new DefaultTreeModel(root);
		jtCacheObject = new JTree(rootModel);
		jtCacheObject.setComponentPopupMenu(jpmCacheObjectRight);
		jspCacheObject = new JScrollPane(jtCacheObject);
		jspCacheObjectInfo = new JScrollPane(jtaCacheObjectInfo);
		jspCacheServerInfo = new JScrollPane(jtaCacheServerInfo);
		jtpMainView.setFont(SwingContainerFactory.getFont());
		jtpMainView.add(jspCacheObjectInfo, "缓存对象详情");
		jtpRightView.setFont(SwingContainerFactory.getFont());
		jtpRightView.add(jspCacheServerInfo, "缓存服务详情");
		jpChoose = new JPanel();
		jpState = new JPanel(new BorderLayout());
		jpCacheObject = new JPanel(new BorderLayout());
		jpChoose.add(jcbIsAutoRefresh);
		jpChoose.add(jlServers);
		jpChoose.add(jcbServers);
		jpChoose.add(jbRefresh);
		jpChoose.add(jbClear);
		jpState.add(jpbLoading, BorderLayout.CENTER);
		jpState.add(jlCacheStatus, BorderLayout.WEST);
		jpCacheObject.add(jlCacheObject, BorderLayout.NORTH);
		jpCacheObject.add(jspCacheObject, BorderLayout.CENTER);
		this.add(jpChoose, BorderLayout.NORTH);
		this.add(jpState, BorderLayout.SOUTH);
		this.add(jtpMainView, BorderLayout.CENTER);
		this.add(jtpRightView, BorderLayout.EAST);
		this.add(jpCacheObject, BorderLayout.WEST);
	}

	private void loadEvent() {
		jcbIsAutoRefresh.addActionListener(this);
		jcbServers.addActionListener(this);
		jmiDeleteObject.addActionListener(this);
		jtCacheObject.addTreeSelectionListener(this);
		jbRefresh.addActionListener(this);
		jbClear.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		SwingActivatorFactory.run(new ExecuteRunnable(e) {
			@Override
			public void process() {
				Object tmp = eventObject.getSource();
				if (tmp == jcbIsAutoRefresh) {
					setAutoRefresh();
				} else if (tmp == jcbServers || tmp == jbRefresh) {
					if (jcbServers.getSelectedItem() != null) {
						initCacheObject(jcbServers.getSelectedItem().toString());
					}
				} else if (tmp == jmiDeleteObject) {
					deleteCacheObject();
				} else if (tmp == jbClear) {
					serverCacheClear();
				}
			}
		});
		jtCacheObject.updateUI();
	}

	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() == null) {
			return;
		}
		Object[] path = e.getNewLeadSelectionPath().getPath();
		if (path.length > 2) {
			String server = path[0].toString();
			String dadaType = null;
			String code = null;
			String key = null;
			if (path.length == 3) {
				dadaType = path[1].toString();
				key = path[2].toString();
			} else if (path.length == 4) {
				dadaType = path[1].toString();
				code = path[2].toString();
				key = path[3].toString();
			}
			CacheAccess<String, Object> cacheAccess = ClassUtil.newInstance("com.hfjy.framework3rd.cache.SingleRedisAccess", server);
			if (cacheAccess != null && StringUtils.isNotEmpty(dadaType)) {
				if (dadaType.indexOf("简单") > -1) {
					Object object = cacheAccess.get(key);
					jtaCacheObjectInfo.setText(object == null ? "" : JsonUtil.showJson(object));
				} else if (dadaType.indexOf("散列") > -1) {
					if (StringUtils.isNotEmpty(code)) {
						Object object = cacheAccess.getMapValue(code.substring(0, code.indexOf('(')), key);
						jtaCacheObjectInfo.setText(object == null ? "" : JsonUtil.showJson(object));
					} else {
						Object object = cacheAccess.getMap(key.substring(0, key.indexOf('(')));
						jtaCacheObjectInfo.setText(object == null ? "" : JsonUtil.showJson(object));
					}
				} else if (dadaType.indexOf("队列") > -1) {
					if (StringUtils.isNotEmpty(code)) {
						Object object = cacheAccess.getListValue(code.substring(0, code.indexOf('(')), Integer.valueOf(key));
						jtaCacheObjectInfo.setText(object == null ? "" : JsonUtil.showJson(object));
					} else {
						Object object = cacheAccess.getList(key.substring(0, key.indexOf('(')));
						jtaCacheObjectInfo.setText(object == null ? "" : JsonUtil.showJson(object));
					}
				}
			}
			jpCacheObject.updateUI();
		}
	}

	private void initCacheObject(String cacheServer) {
		double loading = 0;
		jpbLoading.setValue((int) loading);
		jlCacheStatus.setText("正在加载...");
		CacheAccessServer<?> cacheAccessServer = ClassUtil.newInstance("com.hfjy.framework3rd.cache.SingleRedisAccessServer", cacheServer);
		CacheAccess<String, Object> cacheAccess = ClassUtil.newInstance("com.hfjy.framework3rd.cache.SingleRedisAccess", cacheServer);
		Set<String> keys = cacheAccess.getKeys();
		DefaultMutableTreeNode cacheSingle = new DefaultMutableTreeNode(StringUtils.unite("简单数据(", keys.size(), ")"));
		Iterator<String> keyIterator = keys.iterator();
		loading = 5;
		jpbLoading.setValue((int) loading);
		while (keyIterator.hasNext()) {
			cacheSingle.add(new DefaultMutableTreeNode(keyIterator.next()));
			loading = loading + 30 / keys.size();
			jpbLoading.setValue((int) loading);
		}
		loading = 35;
		jpbLoading.setValue((int) loading);
		keys = cacheAccess.getMapCodes();
		DefaultMutableTreeNode cacheMap = new DefaultMutableTreeNode(StringUtils.unite("散列数据(", keys.size(), ")"));
		Iterator<String> mapCodes = keys.iterator();
		while (mapCodes.hasNext()) {
			String code = mapCodes.next();
			Map<String, Object> tmpMap = cacheAccess.getMap(code);
			if (tmpMap != null) {
				DefaultMutableTreeNode mapNode = new DefaultMutableTreeNode(StringUtils.unite(code, "(", tmpMap.size(), ")"));
				Iterator<String> mapKeys = tmpMap.keySet().iterator();
				while (mapKeys.hasNext()) {
					mapNode.add(new DefaultMutableTreeNode(mapKeys.next()));
				}
				cacheMap.add(mapNode);
			}
			loading = loading + 30 / keys.size();
			jpbLoading.setValue((int) loading);
		}
		loading = 65;
		jpbLoading.setValue((int) loading);
		keys = cacheAccess.getListCodes();
		DefaultMutableTreeNode cacheList = new DefaultMutableTreeNode(StringUtils.unite("队列数据(", keys.size(), ")"));
		Iterator<String> listCodes = keys.iterator();
		while (listCodes.hasNext()) {
			String code = listCodes.next();
			List<Object> tmpList = cacheAccess.getList(code);
			if (tmpList != null) {
				DefaultMutableTreeNode listNode = new DefaultMutableTreeNode(StringUtils.unite(code, "(", tmpList.size(), ")"));
				for (int i = 0; i < tmpList.size(); i++) {
					listNode.add(new DefaultMutableTreeNode(i));
				}
				cacheList.add(listNode);
			}
			loading = loading + 30 / keys.size();
			jpbLoading.setValue((int) loading);
		}
		loading = 95;
		jpbLoading.setValue((int) loading);
		DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode(cacheServer);
		newRoot.add(cacheSingle);
		newRoot.add(cacheMap);
		newRoot.add(cacheList);
		rootModel.setRoot(newRoot);
		jtaCacheServerInfo.setText(cacheAccessServer.serverInfo());
		jtpMainView.updateUI();
		loading = 100;
		jpbLoading.setValue((int) loading);
		jlCacheStatus.setText("加载完毕");
	}

	private void setAutoRefresh() {
		if (jcbIsAutoRefresh.getSelectedObjects() == null) {
			autoRefreshTimer.cancel();
		} else {
			autoRefreshTimer = new Timer();
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					if (jcbServers.getSelectedItem() != null) {
						initCacheObject(jcbServers.getSelectedItem().toString());
					}
				}
			};
			autoRefreshTimer.schedule(timerTask, 1000, 30000);
		}
	}

	private void deleteCacheObject() {
		TreePath seletePath = jtCacheObject.getSelectionPath();
		if (seletePath != null) {
			Object[] path = seletePath.getPath();
			if (path.length > 2) {
				String server = path[0].toString();
				String dadaType = null;
				String code = null;
				String key = null;
				if (path.length == 3) {
					dadaType = path[1].toString();
					key = path[2].toString();
				} else if (path.length == 4) {
					dadaType = path[1].toString();
					code = path[2].toString();
					key = path[3].toString();
				}
				StringBuilder info = new StringBuilder();
				info.append("确认要删除\r\n");
				for (int i = 0; i < path.length; i++) {
					info.append("[");
					info.append(path[i]);
					info.append("] ");
				}
				info.append("\r\n这条数据");
				boolean deleteIsOk = false;
				int isOk = JOptionPane.showConfirmDialog(this, info.toString(), "删除缓存对象", JOptionPane.OK_CANCEL_OPTION);
				if (isOk != 0) {
					return;
				}
				CacheAccess<String, Object> cacheAccess = ClassUtil.newInstance("com.hfjy.framework3rd.cache.SingleRedisAccess", server);
				if (cacheAccess != null && StringUtils.isNotEmpty(dadaType)) {
					if (dadaType.indexOf("简单") > -1) {
						deleteIsOk = cacheAccess.remove(key);
					} else if (dadaType.indexOf("散列") > -1) {
						if (StringUtils.isNotEmpty(code)) {
							deleteIsOk = cacheAccess.removeMapKey(code.substring(0, code.indexOf('(')), key);
						} else {
							deleteIsOk = cacheAccess.removeMap(key.substring(0, key.indexOf('(')));
						}
					} else if (dadaType.indexOf("队列") > -1) {
						if (StringUtils.isEmpty(code)) {
							deleteIsOk = cacheAccess.removeList(key.substring(0, key.indexOf('(')));
						}
					}
				}
				if (deleteIsOk) {
					JOptionPane.showMessageDialog(this, "删除成功！");
				} else {
					JOptionPane.showMessageDialog(this, "删除失败。");
				}
				initCacheObject(server);
			}
		} else {
			JOptionPane.showMessageDialog(this, "没有选中任何对象！");
		}
	}

	private void serverCacheClear() {
		String server = jcbServers.getSelectedItem().toString();
		String info = StringUtils.unite("确认要清空", server, "的缓存吗？");
		int isOk = JOptionPane.showConfirmDialog(this, info, "服务器缓存清空", JOptionPane.OK_CANCEL_OPTION);
		if (isOk == 0) {
			CacheAccessServer<?> cacheAccessServer = ClassUtil.newInstance("com.hfjy.framework3rd.cache.SingleRedisAccessServer", server);
			if (cacheAccessServer.clear()) {
				JOptionPane.showMessageDialog(this, "清理成功！");
			} else {
				JOptionPane.showMessageDialog(this, "清理失败。");
			}
			initCacheObject(server);
		}
	}
}
