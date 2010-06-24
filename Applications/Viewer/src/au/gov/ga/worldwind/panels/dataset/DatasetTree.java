package au.gov.ga.worldwind.panels.dataset;

import javax.swing.tree.DefaultTreeModel;

import au.gov.ga.worldwind.components.lazytree.LazyTree;
import au.gov.ga.worldwind.panels.layers.ClearableBasicTreeUI;

public class DatasetTree extends LazyTree
{
	public DatasetTree(DefaultTreeModel treeModel)
	{
		super(treeModel);

		setUI(new ClearableBasicTreeUI());
		setCellRenderer(new DatasetCellRenderer());

		setRootVisible(false);
		setShowsRootHandles(true);
		setRowHeight(0);
	}

	@Override
	public DefaultTreeModel getModel()
	{
		return (DefaultTreeModel) super.getModel();
	}

	@Override
	public ClearableBasicTreeUI getUI()
	{
		return (ClearableBasicTreeUI) super.getUI();
	}

	public DatasetCellRenderer getDatasetCellRenderer()
	{
		return (DatasetCellRenderer) getCellRenderer();
	}
}