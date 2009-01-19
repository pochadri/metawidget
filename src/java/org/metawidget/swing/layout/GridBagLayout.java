// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.metawidget.swing.layout;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.metawidget.MetawidgetException;
import org.metawidget.swing.Facet;
import org.metawidget.swing.Stub;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.util.simple.StringUtils;

/**
 * Layout to arrange widgets using <code>javax.awt.GridBagLayout</code>.
 * <p>
 * Widgets are arranged in a table, with one column for labels and another for the widget,
 * <p>
 * This implementation recognizes the following parameters:
 * <p>
 * <ul>
 * <li><code>numberOfColumns</code>
 * <li><code>labelAlignment</code> - one of <code>SwingConstants.LEFT</code>,
 * <code>SwingConstants.CENTER</code>, <code>SwingConstants.RIGHT</code>,
 * <code>SwingConstants.LEADING</code> or <code>SwingConstants.TRAILING</code>
 * <li><code>sectionStyle</code> - either <code>SECTION_AS_HEADING</code> (the default) or
 * <code>SECTION_AS_TAB</code> (render sections as tabs in a <code>JTabbedPane</code>)
 * </ul>
 *
 * @author Richard Kennard
 */

public class GridBagLayout
	extends BaseLayout
{
	//
	// Public statics
	//

	public final static int		SECTION_AS_HEADING				= 0;

	public final static int		SECTION_AS_TAB					= 1;

	//
	// Private statics
	//

	private final static Insets	INSETS_TOP_LEFT_COLUMN_LABEL	= new Insets( 1, 0, 3, 3 );

	private final static Insets	INSETS_TOP_LABEL				= new Insets( 1, 5, 3, 3 );

	private final static Insets	INSETS_LEFT_COLUMN_LABEL		= new Insets( 3, 0, 3, 3 );

	private final static Insets	INSETS_LABEL					= new Insets( 3, 5, 3, 3 );

	private final static Insets	INSETS_TOP_COMPONENT			= new Insets( 0, 0, 2, 0 );

	private final static Insets	INSETS_COMPONENT				= new Insets( 2, 0, 2, 0 );

	private final static Insets	INSETS_SECTION_LABEL			= new Insets( 10, 0, 5, 5 );

	private final static Insets	INSETS_SECTION					= new Insets( 12, 0, 5, 0 );

	private final static Insets	INSETS_TABBED_PANE				= new Insets( 5, 0, 5, 0 );

	private final static Border	INSETS_TAB						= BorderFactory.createEmptyBorder( 3, 3, 3, 3 );

	//
	// Private members
	//

	private String				mCurrentSection;

	private int					mLabelAlignment;

	private int					mSectionStyle;

	private int					mNumberOfColumns;

	private int					mCurrentColumn;

	private int					mCurrentRow;

	private int					mPanelInsertedRow;

	private JPanel				mPanelCurrent;

	/**
	 * Whether a spacer row is required on the last row of the layout.
	 * <p>
	 * By default, GridBagLayouts expand to fill their vertical space, and 'align middle' their
	 * group of components. To make them 'align top' at least one of the components must have a
	 * vertical constraint weighting &gt; 0.
	 */

	private boolean				mNeedParentSpacerRow			= true;

	/**
	 * Whether a spacer row is required on the last row of the current panel.
	 * <p>
	 * By default, GridBagLayouts expand to fill their vertical space, and 'align middle' their
	 * group of components. To make them 'align top' at least one of the components must have a
	 * vertical constraint weighting &gt; 0.
	 */

	private boolean				mNeedPanelSpacerRow;

	//
	// Constructor
	//

	public GridBagLayout( SwingMetawidget metawidget )
	{
		super( metawidget );

		// Read parameters

		Object numberOfColumns = metawidget.getParameter( "numberOfColumns" );

		if ( numberOfColumns == null || metawidget.getParent() instanceof SwingMetawidget )
		{
			mNumberOfColumns = 1;
		}
		else
		{
			mNumberOfColumns = (Integer) numberOfColumns;

			if ( mNumberOfColumns < 1 )
				throw MetawidgetException.newException( "numberOfColumns must be >= 1" );
		}

		Object labelAlignment = metawidget.getParameter( "labelAlignment" );

		if ( labelAlignment == null )
			mLabelAlignment = SwingConstants.LEFT;
		else
			mLabelAlignment = (Integer) labelAlignment;

		Object sectionStyle = metawidget.getParameter( "sectionStyle" );

		if ( sectionStyle == null )
			mSectionStyle = SECTION_AS_HEADING;
		else
			mSectionStyle = (Integer) sectionStyle;
	}

	//
	// Public methods
	//

	@Override
	public void layoutBegin()
	{
		getMetawidget().setLayout( new java.awt.GridBagLayout() );
	}

	public void layoutChild( Component component, Map<String, String> attributes )
	{
		// Do not render empty stubs

		if ( component instanceof Stub && ( (Stub) component ).getComponentCount() == 0 )
			return;

		// Special support for large components

		boolean largeComponent = ( component instanceof JScrollPane || component instanceof SwingMetawidget || ( attributes != null && TRUE.equals( attributes.get( LARGE ) ) ) );

		if ( largeComponent && mCurrentColumn > 0 )
		{
			mCurrentColumn = 0;
			mCurrentRow++;
		}

		// Layout a label...

		String labelText = null;

		if ( attributes != null )
			labelText = getMetawidget().getLabelString( attributes );

		layoutBeforeChild( component, labelText, attributes );

		// ...and layout the component

		GridBagConstraints componentConstraints = new GridBagConstraints();

		if ( !( component instanceof JButton ) )
			componentConstraints.fill = GridBagConstraints.BOTH;

		componentConstraints.anchor = GridBagConstraints.WEST;

		if ( labelText != null )
		{
			componentConstraints.gridx = ( mCurrentColumn * 2 ) + 1;
		}
		else
		{
			componentConstraints.gridx = mCurrentColumn * 2;
			componentConstraints.gridwidth = 2;
		}

		componentConstraints.gridy = mCurrentRow;
		componentConstraints.weightx = 1.0f / mNumberOfColumns;

		if ( largeComponent )
		{
			componentConstraints.gridwidth = GridBagConstraints.REMAINDER;
			mCurrentColumn = mNumberOfColumns;
		}

		if ( mCurrentRow == 0 )
			componentConstraints.insets = INSETS_TOP_COMPONENT;
		else
			componentConstraints.insets = INSETS_COMPONENT;

		// Hack for spacer row (see JavaDoc for mNeedSpacerRow): assume components
		// embedded in a JScrollPane are their own spacer row

		if ( component instanceof JScrollPane )
		{
			componentConstraints.weighty = 1.0f;

			if ( mPanelCurrent == null )
				mNeedParentSpacerRow = false;
			else
				mNeedPanelSpacerRow = false;
		}

		// Add to either current panel or direct to the Metawidget

		if ( mPanelCurrent == null )
			getMetawidget().add( component, componentConstraints );
		else
			mPanelCurrent.add( component, componentConstraints );

		mCurrentColumn++;

		if ( mCurrentColumn >= mNumberOfColumns )
		{
			mCurrentColumn = 0;
			mCurrentRow++;
		}
	}

	@Override
	public void layoutEnd()
	{
		sectionEnd();

		// Buttons

		Facet buttonsFacet = getMetawidget().getFacet( "buttons" );

		if ( buttonsFacet != null )
		{
			if ( mCurrentColumn > 0 )
			{
				mCurrentColumn = 0;
				mCurrentRow++;
			}

			GridBagConstraints buttonConstraints = new GridBagConstraints();
			buttonConstraints.fill = GridBagConstraints.BOTH;
			buttonConstraints.anchor = GridBagConstraints.WEST;
			buttonConstraints.gridy = mCurrentRow;
			buttonConstraints.gridwidth = GridBagConstraints.REMAINDER;

			// (buttons facet can act as the spacer row)

			if ( mNeedParentSpacerRow )
			{
				buttonConstraints.weighty = 1.0f;
				mNeedParentSpacerRow = false;
			}

			getMetawidget().add( buttonsFacet, buttonConstraints );

			mCurrentRow++;
		}

		// Spacer row: see JavaDoc for mNeedParentSpacerRow

		if ( mNeedParentSpacerRow )
		{
			if ( mCurrentColumn > 0 )
			{
				mCurrentColumn = 0;
				mCurrentRow++;
			}

			JPanel panelSpacer = new JPanel();
			panelSpacer.setOpaque( false );
			GridBagConstraints constraintsSpacer = new GridBagConstraints();
			constraintsSpacer.gridy = mCurrentRow;
			constraintsSpacer.weighty = 1.0f;
			getMetawidget().add( panelSpacer, constraintsSpacer );
		}
	}

	//
	// Protected methods
	//

	protected String layoutBeforeChild( Component component, String labelText, Map<String, String> attributes )
	{
		if ( attributes != null )
		{
			// Section headings

			String section = attributes.get( SECTION );

			if ( section != null && !section.equals( mCurrentSection ) )
			{
				mCurrentSection = section;
				layoutSection( section );
			}
		}

		// Add label

		if ( labelText != null && !"".equals( labelText ) && !( component instanceof JButton ) )
		{
			JLabel label = new JLabel();
			label.setHorizontalAlignment( mLabelAlignment );

			// Required

			if ( attributes != null && TRUE.equals( attributes.get( REQUIRED ) ) && !TRUE.equals( attributes.get( READ_ONLY ) ) && !getMetawidget().isReadOnly() )
				label.setText( labelText + "*:" );
			else
				label.setText( labelText + ":" );

			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.fill = GridBagConstraints.BOTH;
			labelConstraints.anchor = GridBagConstraints.NORTHWEST;
			labelConstraints.gridx = mCurrentColumn * 2;
			labelConstraints.gridy = mCurrentRow;
			labelConstraints.weightx = 0.1f / mNumberOfColumns;

			// Top align all labels, not just those belonging to 'tall' components,
			// so that tall components, regular components and nested Metawidget
			// components all line up

			label.setVerticalAlignment( SwingConstants.TOP );

			if ( mCurrentColumn == 0 )
			{
				if ( mCurrentRow == 0 )
					labelConstraints.insets = INSETS_TOP_LEFT_COLUMN_LABEL;
				else
					labelConstraints.insets = INSETS_LEFT_COLUMN_LABEL;
			}
			else
			{
				if ( mCurrentRow == 0 )
					labelConstraints.insets = INSETS_TOP_LABEL;
				else
					labelConstraints.insets = INSETS_LABEL;
			}

			// Add to either current panel or direct to the Metawidget

			if ( mPanelCurrent == null )
				getMetawidget().add( label, labelConstraints );
			else
				mPanelCurrent.add( label, labelConstraints );
		}

		return labelText;
	}

	protected void layoutSection( String section )
	{
		if ( "".equals( section ) )
		{
			sectionEnd();
			return;
		}

		// Section name (possibly localized)

		String localizedSection = getMetawidget().getLocalizedKey( StringUtils.camelCase( section ) );

		if ( localizedSection == null )
			localizedSection = section;

		// Start a new row

		if ( mCurrentColumn > 0 )
		{
			mCurrentColumn = 0;
			mCurrentRow++;
		}

		// Insert the section

		switch ( mSectionStyle )
		{
			case SECTION_AS_TAB:

				JTabbedPane tabbedPane;

				if ( mPanelCurrent == null )
				{
					tabbedPane = new JTabbedPane();

					mPanelInsertedRow = mCurrentRow;

					GridBagConstraints constraintsTabbedPane = new GridBagConstraints();
					constraintsTabbedPane.fill = GridBagConstraints.BOTH;
					constraintsTabbedPane.anchor = GridBagConstraints.WEST;
					constraintsTabbedPane.gridy = mPanelInsertedRow;
					constraintsTabbedPane.gridwidth = GridBagConstraints.REMAINDER;
					constraintsTabbedPane.weighty = 1.0f;
					constraintsTabbedPane.insets = INSETS_TABBED_PANE;
					mNeedParentSpacerRow = false;

					getMetawidget().add( tabbedPane, constraintsTabbedPane );
				}
				else
				{
					tabbedPane = (JTabbedPane) mPanelCurrent.getParent();
					sectionEnd();
				}

				mPanelCurrent = new JPanel();
				mPanelCurrent.setBorder( INSETS_TAB );
				mPanelCurrent.setName( localizedSection );
				mPanelCurrent.setLayout( new java.awt.GridBagLayout() );
				mNeedPanelSpacerRow = true;

				tabbedPane.add( mPanelCurrent );

				mCurrentRow = 0;
				mCurrentColumn = 0;
				break;

			default:

				// Separator panel (a GridBagLayout within a GridBagLayout)

				JPanel panelSection = new JPanel();
				panelSection.setLayout( new java.awt.GridBagLayout() );
				panelSection.setOpaque( false );

				GridBagConstraints constraintsLabel = new GridBagConstraints();
				constraintsLabel.insets = INSETS_SECTION_LABEL;
				JLabel labelSection = new JLabel( localizedSection );
				panelSection.add( labelSection, constraintsLabel );

				GridBagConstraints constraintsSeparator = new GridBagConstraints();
				constraintsSeparator.fill = GridBagConstraints.HORIZONTAL;
				constraintsSeparator.weightx = 1.0;
				constraintsSeparator.insets = INSETS_SECTION;
				constraintsSeparator.gridwidth = GridBagConstraints.REMAINDER;
				panelSection.add( new JSeparator( SwingConstants.CENTER ), constraintsSeparator );

				GridBagConstraints constraintsSection = new GridBagConstraints();
				constraintsSection.fill = GridBagConstraints.HORIZONTAL;
				constraintsSection.anchor = GridBagConstraints.WEST;
				constraintsSection.gridy = mCurrentRow;
				constraintsSection.gridwidth = GridBagConstraints.REMAINDER;
				getMetawidget().add( panelSection, constraintsSection );

				mCurrentRow++;
				break;
		}
	}

	protected void sectionEnd()
	{
		if ( mPanelCurrent == null )
			return;

		// Spacer row: see JavaDoc for mNeedParentSpacerRow

		if ( mNeedPanelSpacerRow )
		{
			if ( mCurrentColumn > 0 )
			{
				mCurrentColumn = 0;
				mCurrentRow++;
			}

			JPanel panelSpacer = new JPanel();
			panelSpacer.setOpaque( false );
			GridBagConstraints constraintsSpacer = new GridBagConstraints();
			constraintsSpacer.gridy = mCurrentRow;
			constraintsSpacer.weighty = 1.0f;
			mPanelCurrent.add( panelSpacer, constraintsSpacer );
		}

		mCurrentRow = mPanelInsertedRow + 1;
		mCurrentColumn = 0;

		mPanelCurrent = null;
	}
}
