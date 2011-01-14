package simternet.ui;

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
import simternet.Simternet;
import simternet.SimternetWithUI;
import simternet.nsp.AbstractNetworkProvider;

public class NetworkProvidersDisplay extends JFrame {

	protected class NSPTableModel extends AbstractTableModel {

		String[] columnNames = { "Network Service Provider", "Resouces",
				"Total Customers" };

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return this.columnNames[column];
		}

		protected Double getLiquidAssets(int rowIndex) {
			return this.getNSP(rowIndex).financials.getAssetsLiquid();
		}

		protected AbstractNetworkProvider getNSP(int rowIndex) {
			Object[] anp = NetworkProvidersDisplay.this.networkServiceProviders
					.toArray();
			return (AbstractNetworkProvider) anp[rowIndex];
		}

		@Override
		public int getRowCount() {
			return NetworkProvidersDisplay.this.networkServiceProviders.size();
		}

		protected Double getTotalCustomers(int rowIndex) {
			return this.getNSP(rowIndex).getCustomers();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {

			if (columnIndex == 0)
				return this.getNSP(rowIndex).getName();
			if (columnIndex == 1)
				return this.getLiquidAssets(rowIndex);
			if (columnIndex == 2)
				return this.getTotalCustomers(rowIndex);

			return "Value at " + rowIndex + "," + columnIndex;
		}

	}

	protected Collection<AbstractNetworkProvider> networkServiceProviders;

	public NetworkProvidersDisplay(double width, double height,
			GUIState simulation, long interval) {

		SimternetWithUI gui = (SimternetWithUI) simulation;
		this.networkServiceProviders = ((Simternet) (gui.state))
				.getNetworkServiceProviders();

		this.setTitle("Network Service Providers Console");

		JPanel panel = new JPanel(new GridLayout(1, 0));

		JTable table = new JTable(new NSPTableModel());
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		table.setShowGrid(true);
		table.setGridColor(Color.GRAY);

		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane);

		panel.setOpaque(true);

		this.setContentPane(panel);
		this.pack();
		this.setVisible(true);

	}

}
