package simternet.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import sim.display.GUIState;
import simternet.agents.nsp.NetworkProvider;
import simternet.engine.Simternet;
import simternet.engine.SimternetWithUI;

public class NetworkProvidersDisplay extends JFrame {

	protected class NSPTableModel extends AbstractTableModel {

		String[]	columnNames	= { "Network Service Provider", "Resouces", "Total Customers" };

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		protected Double getLiquidAssets(int rowIndex) {
			return getNSP(rowIndex).financials.getAssetsLiquid();
		}

		protected NetworkProvider getNSP(int rowIndex) {
			Object[] anp = networkServiceProviders.toArray();
			return (NetworkProvider) anp[rowIndex];
		}

		@Override
		public int getRowCount() {
			return networkServiceProviders.size();
		}

		protected Double getTotalCustomers(int rowIndex) {
			return getNSP(rowIndex).getCustomers();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			if (columnIndex == 0)
				return getNSP(rowIndex).getName();
			if (columnIndex == 1)
				return getLiquidAssets(rowIndex);
			if (columnIndex == 2)
				return getTotalCustomers(rowIndex);

			return "Value at " + rowIndex + "," + columnIndex;
		}

	}

	protected Collection<NetworkProvider>	networkServiceProviders;

	public NetworkProvidersDisplay(double width, double height, GUIState simulation, long interval) {

		SimternetWithUI gui = (SimternetWithUI) simulation;
		networkServiceProviders = ((Simternet) gui.state).getNetworkServiceProviders();

		setTitle("Network Service Providers Console");

		JPanel panel = new JPanel(new GridLayout(1, 0));

		JTable table = new JTable(new NSPTableModel());
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		table.setShowGrid(true);
		table.setGridColor(Color.GRAY);

		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);

		panel.setOpaque(true);

		setContentPane(panel);
		pack();
		setVisible(true);

	}

}
