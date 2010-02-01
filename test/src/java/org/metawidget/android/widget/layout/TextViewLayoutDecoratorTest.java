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

package org.metawidget.android.widget.layout;

import junit.framework.TestCase;

import org.metawidget.android.widget.AndroidMetawidget;
import org.metawidget.android.widget.Facet;
import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.inspector.annotation.UiLookup;
import org.metawidget.inspector.annotation.UiSection;
import org.metawidget.layout.decorator.LayoutDecoratorTest;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Richard Kennard
 */

public class TextViewLayoutDecoratorTest
	extends TestCase
{
	//
	// Public methods
	//

	public void testConfig()
	{
		TextViewLayoutDecoratorConfig config1 = new TextViewLayoutDecoratorConfig();
		TextViewLayoutDecoratorConfig config2 = new TextViewLayoutDecoratorConfig();

		assertTrue( !config1.equals( "foo" ) );
		assertTrue( config1.equals( config1 ) );
		assertTrue( config1.equals( config2 ) );
		assertTrue( config1.hashCode() == config2.hashCode() );

		// style

		config1.setStyle( 100 );
		assertTrue( 100 == config1.getStyle() );
		assertTrue( !config1.equals( config2 ) );

		config2.setStyle( 100 );
		assertTrue( config1.equals( config2 ) );
		assertTrue( config1.hashCode() == config2.hashCode() );

		// superclass

		LayoutDecoratorTest.testConfig( config1, config2, new TableLayout() );
	}

	public void testTextViewLayoutDecorator()
	{
		AndroidMetawidget androidMetawidget = new AndroidMetawidget( null );
		androidMetawidget.setLayout( new TextViewLayoutDecorator( new TextViewLayoutDecoratorConfig().setLayout( new TableLayout() )));
		androidMetawidget.setToInspect( new Foo() );

		Facet facet = new Facet( null );
		facet.setName( "buttons" );
		androidMetawidget.addView( facet );

		android.widget.TableLayout tableLayout = (android.widget.TableLayout) androidMetawidget.getChildAt( 0 );
		TableRow tableRow = (TableRow) tableLayout.getChildAt( 0 );
		assertTrue( "Bar: ".equals( ( (TextView) tableRow.getChildAt( 0 ) ).getText() ) );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );

		// Heading #1

		tableRow = (TableRow) tableLayout.getChildAt( 1 );
		assertTrue( "heading1".equals( ((TextView) tableRow.getChildAt( 0 ) ).getText() ));
		tableRow = (TableRow) tableLayout.getChildAt( 2 );
		android.widget.LinearLayout linearLayout = (android.widget.LinearLayout) tableRow.getChildAt( 0 );
		assertTrue( android.widget.LinearLayout.VERTICAL == linearLayout.getOrientation() );
		assertTrue( android.widget.LinearLayout.HORIZONTAL == new android.widget.LinearLayout( null ).getOrientation() );
		android.widget.TableLayout sectionTableLayout = (android.widget.TableLayout) linearLayout.getChildAt( 0 );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 0 );
		assertTrue( "Baz: ".equals( ( (TextView) tableRow.getChildAt( 0 ) ).getText() ) );
		assertTrue( tableRow.getChildAt( 1 ) instanceof CheckBox );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 1 );
		assertTrue( "Abc: ".equals( ( (TextView) tableRow.getChildAt( 0 ) ).getText() ) );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );
		assertTrue( 2 == sectionTableLayout.getChildCount() );

		// Heading  #2

		tableRow = (TableRow) tableLayout.getChildAt( 3 );
		assertTrue( "heading2".equals( ((TextView) tableRow.getChildAt( 0 ) ).getText() ));
		tableRow = (TableRow) tableLayout.getChildAt( 4 );
		linearLayout = (android.widget.LinearLayout) tableRow.getChildAt( 0 );
		sectionTableLayout = (android.widget.TableLayout) linearLayout.getChildAt( 0 );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 0 );
		assertTrue( "Def: ".equals( ( (TextView) tableRow.getChildAt( 0 ) ).getText() ) );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );
		assertTrue( 1 == sectionTableLayout.getChildCount() );

		// Separate component

		tableRow = (TableRow) tableLayout.getChildAt( 5 );
		assertTrue( "Ghi: ".equals( ( (TextView) tableRow.getChildAt( 0 ) ).getText() ) );
		assertTrue( tableRow.getChildAt( 1 ) instanceof Spinner );

		// Heading #3

		tableRow = (TableRow) tableLayout.getChildAt( 6 );
		assertTrue( "heading3".equals( ((TextView) tableRow.getChildAt( 0 ) ).getText() ));
		tableRow = (TableRow) tableLayout.getChildAt( 7 );
		linearLayout = (android.widget.LinearLayout) tableRow.getChildAt( 0 );
		sectionTableLayout = (android.widget.TableLayout) linearLayout.getChildAt( 0 );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 0 );
		assertTrue( "Jkl: ".equals( ( (TextView) tableRow.getChildAt( 0 ) ).getText() ) );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );
		assertTrue( 1 == sectionTableLayout.getChildCount() );

		assertTrue( facet == androidMetawidget.getChildAt( 1 ));
		assertTrue( 2 == androidMetawidget.getChildCount() );
	}

	//
	// Inner class
	//

	public static class Foo
	{
		public String	bar;

		@UiComesAfter( "bar" )
		@UiSection( "heading1" )
		public boolean	baz;

		@UiComesAfter( "baz" )
		public String	abc;

		@UiSection( "heading2" )
		public String	def;

		@UiSection( "" )
		@UiLookup( { "foo", "bar" } )
		public String	ghi;

		@UiSection( "heading3" )
		public String	jkl;
	}
}
