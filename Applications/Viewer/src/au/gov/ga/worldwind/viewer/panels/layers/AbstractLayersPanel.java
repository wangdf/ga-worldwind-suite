package au.gov.ga.worldwind.viewer.panels.layers;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.retrieve.RetrievalService;
import gov.nasa.worldwind.view.orbit.FlyToOrbitViewAnimator;
import gov.nasa.worldwind.view.orbit.OrbitView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import nasa.worldwind.retrieve.ExtendedRetrievalService;
import au.gov.ga.worldwind.common.ui.BasicAction;
import au.gov.ga.worldwind.common.ui.resizabletoolbar.ResizableToolBar;
import au.gov.ga.worldwind.common.util.FlyToSectorAnimator;
import au.gov.ga.worldwind.viewer.panels.layers.LayerEnabler.RefreshListener;
import au.gov.ga.worldwind.viewer.retrieve.LayerTreeRetrievalListener;
import au.gov.ga.worldwind.viewer.theme.AbstractThemePanel;
import au.gov.ga.worldwind.viewer.theme.Theme;
import au.gov.ga.worldwind.viewer.util.Icons;
import au.gov.ga.worldwind.viewer.util.SettingsUtil;

public abstract class AbstractLayersPanel extends AbstractThemePanel
{
	protected WorldWindow wwd;

	protected LayerTree tree;
	protected INode root;

	protected BasicAction enableAction;
	protected BasicAction disableAction;

	protected LayerEnabler layerEnabler;
	protected JSlider opacitySlider;
	private boolean ignoreOpacityChange = false;

	public AbstractLayersPanel()
	{
		super(new BorderLayout());
	}

	@Override
	public void setup(Theme theme)
	{
		root = createRootNode(theme);
		layerEnabler = new LayerEnabler();
		tree = new LayerTree(root, layerEnabler);
		layerEnabler.setTree(tree);

		RetrievalService rs = WorldWind.getRetrievalService();
		if (rs instanceof ExtendedRetrievalService)
		{
			LayerTreeRetrievalListener listener = new LayerTreeRetrievalListener(layerEnabler);
			((ExtendedRetrievalService) rs).addRetrievalListener(listener);
		}

		tree.setRootVisible(false);
		tree.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setPreferredSize(new Dimension(MINIMUM_LIST_HEIGHT, MINIMUM_LIST_HEIGHT));

		createActions();
		createToolBar();

		layerEnabler.addRefreshListener(new RefreshListener()
		{
			@Override
			public void refreshed()
			{
				enableComponents();
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				enableComponents();
			}
		});

		tree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
				{
					int row = tree.getRowForLocation(e.getX(), e.getY());
					Rectangle bounds = tree.getRowBounds(row);

					//remove height from left edge to ignore space taken by the checkbox
					bounds.width -= bounds.height;
					bounds.x += bounds.height;

					if (bounds.contains(e.getX(), e.getY()))
					{
						TreePath path = tree.getPathForRow(row);
						if (path != null)
						{
							Object o = path.getLastPathComponent();
							if (o != null && o instanceof ILayerNode)
							{
								ILayerNode layer = (ILayerNode) o;
								Sector sector = layerEnabler.getLayerExtents(layer);
								if (sector != null)
								{
									if (wwd.getView() instanceof OrbitView)
									{
										OrbitView orbitView = (OrbitView) wwd.getView();
										Position center = orbitView.getCenterPosition();
										Position newCenter;
										if (sector.contains(center)
												&& sector.getDeltaLatDegrees() > 90
												&& sector.getDeltaLonDegrees() > 90)
										{
											newCenter = center;
										}
										else
										{
											newCenter = new Position(sector.getCentroid(), 0);
										}

										LatLon endVisibleDelta =
												new LatLon(sector.getDeltaLat(), sector
														.getDeltaLon());
										long lengthMillis =
												SettingsUtil.getScaledLengthMillis(center,
														newCenter);

										FlyToOrbitViewAnimator animator =
												FlyToSectorAnimator.createFlyToSectorAnimator(
														orbitView, center, newCenter,
														orbitView.getHeading(),
														orbitView.getPitch(), orbitView.getZoom(),
														endVisibleDelta, lengthMillis);
										orbitView.addAnimator(animator);

										wwd.redraw();
									}
								}
							}
						}
					}
				}
			}
		});

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				Dimension size = getLayout().preferredLayoutSize(AbstractLayersPanel.this);
				size.width = 100;
				setPreferredSize(size);
			}
		});

		enableComponents();

		wwd = theme.getWwd();
		layerEnabler.setWwd(theme.getWwd());
	}

	protected abstract INode createRootNode(Theme theme);

	protected void createActions()
	{
		enableAction = new BasicAction("Enable selected", Icons.check.getIcon());
		enableAction.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				enableSelected(true);
			}
		});

		disableAction = new BasicAction("Disable selected", Icons.uncheck.getIcon());
		disableAction.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				enableSelected(false);
			}
		});
	}

	private void createToolBar()
	{
		JToolBar toolBar = new ResizableToolBar();
		add(toolBar, BorderLayout.PAGE_START);

		setupToolBarBeforeSlider(toolBar);

		toolBar.add(enableAction);
		toolBar.add(disableAction);
		createOpacitySlider();
		toolBar.add(opacitySlider);

		setupToolBarAfterSlider(toolBar);
	}

	private void createOpacitySlider()
	{
		opacitySlider = new JSlider(0, 100, 100);
		opacitySlider.setToolTipText("Layer opacity");
		Dimension size = opacitySlider.getPreferredSize();
		size.width = 60;
		opacitySlider.setPreferredSize(size);
		opacitySlider.setMinimumSize(size);
		opacitySlider.setMaximumSize(size);

		opacitySlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setSelectedOpacity();
			}
		});
	}

	private void enableComponents()
	{
		setOpacitySlider();

		TreePath[] selected = tree.getSelectionPaths();
		ILayerNode layer = firstChildLayer(selected, false);
		enableAction.setEnabled(layer != null);
		disableAction.setEnabled(layer != null);
	}

	private void enableSelected(boolean enabled)
	{
		TreePath[] selected = tree.getSelectionPaths();
		Set<ILayerNode> nodes = getChildLayers(selected, false);

		for (ILayerNode node : nodes)
		{
			if (tree.getModel().isEnabled(node) != enabled)
			{
				tree.getModel().setEnabled(node, enabled);
				relayoutRepaint(node);
			}
		}
	}

	private void setOpacitySlider()
	{
		if (!ignoreOpacityChange)
		{
			TreePath[] selected = tree.getSelectionPaths();
			ILayerNode layer = firstChildLayer(selected, true);
			if (layer != null)
			{
				double opacity = layer.isEnabled() ? layer.getOpacity() * 100d : 0;
				ignoreOpacityChange = true;
				opacitySlider.setValue((int) Math.round(opacity));
				ignoreOpacityChange = false;
				opacitySlider.setEnabled(true);
			}
			else
			{
				opacitySlider.setEnabled(false);
			}
		}
	}

	private void setSelectedOpacity()
	{
		if (!ignoreOpacityChange)
		{
			ignoreOpacityChange = true;

			TreePath[] selected = tree.getSelectionPaths();
			Set<ILayerNode> nodes = getChildLayers(selected, true);

			double opacity = opacitySlider.getValue() / 100d;
			boolean enabled = opacity > 0;

			for (ILayerNode node : nodes)
			{
				tree.getModel().setEnabled(node, enabled);
				tree.getModel().setOpacity(node, opacity);
				relayoutRepaint(node);
			}

			ignoreOpacityChange = false;
			setOpacitySlider();
		}
	}

	private void relayoutRepaint(INode node)
	{
		TreePath path = new TreePath(tree.getModel().getPathToRoot(node));
		tree.getUI().relayout(path);

		Rectangle bounds = tree.getPathBounds(path);
		if (bounds != null)
			tree.repaint(bounds);
	}

	protected ILayerNode firstChildLayer(TreePath[] selected, boolean hasLayer)
	{
		if (selected == null)
			return null;

		for (TreePath path : selected)
		{
			Object o = path.getLastPathComponent();
			if (o instanceof INode)
			{
				ILayerNode layer = firstChildLayer((INode) o, hasLayer);
				if (layer != null)
					return layer;
			}
		}
		return null;
	}

	protected ILayerNode firstChildLayer(INode node, boolean hasLayer)
	{
		if (node instanceof ILayerNode)
		{
			ILayerNode layer = (ILayerNode) node;
			if (!hasLayer || layerEnabler.hasLayer(layer))
				return layer;
		}
		for (int i = 0; i < node.getChildCount(); i++)
		{
			ILayerNode layer = firstChildLayer(node.getChild(i), hasLayer);
			if (layer != null)
				return layer;
		}
		return null;
	}

	protected Set<ILayerNode> getChildLayers(TreePath[] selected, boolean hasLayer)
	{
		Set<ILayerNode> layers = new HashSet<ILayerNode>();
		if (selected != null)
		{
			for (TreePath path : selected)
			{
				Object o = path.getLastPathComponent();
				if (o instanceof INode)
					addChildLayers((INode) o, layers, hasLayer);
			}
		}
		return layers;
	}

	protected Set<ILayerNode> getChildLayers(INode node, boolean hasLayer)
	{
		Set<ILayerNode> layers = new HashSet<ILayerNode>();
		addChildLayers(node, layers, hasLayer);
		return layers;
	}

	private void addChildLayers(INode node, Set<ILayerNode> list, boolean hasLayer)
	{
		if (node instanceof ILayerNode)
		{
			ILayerNode layer = (ILayerNode) node;
			if (!hasLayer || layerEnabler.hasLayer(layer))
				list.add(layer);
		}
		for (int i = 0; i < node.getChildCount(); i++)
		{
			addChildLayers(node.getChild(i), list, hasLayer);
		}
	}

	protected void setupToolBarBeforeSlider(JToolBar toolBar)
	{
	}

	protected void setupToolBarAfterSlider(JToolBar toolBar)
	{
	}

	public void addQueryClickListener(QueryClickListener listener)
	{
		tree.getLayerCellRenderer().addQueryClickListener(listener);
	}

	public void removeQueryClickListener(QueryClickListener listener)
	{
		tree.getLayerCellRenderer().removeQueryClickListener(listener);
	}
}