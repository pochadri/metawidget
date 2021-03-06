// Metawidget (licensed under LGPL)
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
import org.metawidget.util.MetawidgetTestUtils;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Richard Kennard
 */

public class TextViewLayoutDecoratorTest
	extends TestCase {

	//
	// Public methods
	//

	public void testConfig() {

		MetawidgetTestUtils.testEqualsAndHashcode( TextViewLayoutDecoratorConfig.class, new TextViewLayoutDecoratorConfig() {
			// Subclass
		} );
	}

	public void testTextViewLayoutDecorator() {

		AndroidMetawidget androidMetawidget = new AndroidMetawidget( null );
		androidMetawidget.setLayout( new TextViewLayoutDecorator( new TextViewLayoutDecoratorConfig().setLayout( new TableLayout() ) ) );
		androidMetawidget.setToInspect( new Foo() );

		Facet facet = new Facet( null );
		facet.setName( "buttons" );
		androidMetawidget.addView( facet );

		android.widget.TableLayout tableLayout = (android.widget.TableLayout) androidMetawidget.getChildAt( 0 );
		TableRow tableRow = (TableRow) tableLayout.getChildAt( 0 );
		assertEquals( "Bar: ", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );

		// Heading #1

		tableRow = (TableRow) tableLayout.getChildAt( 1 );
		assertEquals( "heading1", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		tableRow = (TableRow) tableLayout.getChildAt( 2 );
		android.widget.LinearLayout linearLayout = (android.widget.LinearLayout) tableRow.getChildAt( 0 );
		assertEquals( android.widget.LinearLayout.VERTICAL, linearLayout.getOrientation() );
		assertEquals( android.widget.LinearLayout.HORIZONTAL, new android.widget.LinearLayout( null ).getOrientation() );
		android.widget.TableLayout sectionTableLayout = (android.widget.TableLayout) linearLayout.getChildAt( 0 );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 0 );
		assertEquals( "Baz: ", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		assertTrue( tableRow.getChildAt( 1 ) instanceof CheckBox );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 1 );
		assertEquals( "Abc: ", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );
		assertEquals( 2, sectionTableLayout.getChildCount() );

		// Heading #2

		tableRow = (TableRow) tableLayout.getChildAt( 3 );
		assertEquals( "heading2", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		tableRow = (TableRow) tableLayout.getChildAt( 4 );
		linearLayout = (android.widget.LinearLayout) tableRow.getChildAt( 0 );
		sectionTableLayout = (android.widget.TableLayout) linearLayout.getChildAt( 0 );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 0 );
		assertEquals( "Def: ", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );
		assertEquals( 1, sectionTableLayout.getChildCount() );

		// Separate component

		tableRow = (TableRow) tableLayout.getChildAt( 5 );
		assertEquals( "Ghi: ", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		assertTrue( tableRow.getChildAt( 1 ) instanceof Spinner );

		// Heading #3

		tableRow = (TableRow) tableLayout.getChildAt( 6 );
		assertEquals( "heading3", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		tableRow = (TableRow) tableLayout.getChildAt( 7 );
		linearLayout = (android.widget.LinearLayout) tableRow.getChildAt( 0 );
		sectionTableLayout = (android.widget.TableLayout) linearLayout.getChildAt( 0 );
		tableRow = (TableRow) sectionTableLayout.getChildAt( 0 );
		assertEquals( "Jkl: ", ( (TextView) tableRow.getChildAt( 0 ) ).getText() );
		assertTrue( tableRow.getChildAt( 1 ) instanceof EditText );
		assertEquals( 1, sectionTableLayout.getChildCount() );

		assertEquals( facet, androidMetawidget.getChildAt( 1 ) );
		assertEquals( 2, androidMetawidget.getChildCount() );
	}

	//
	// Inner class
	//

	public static class Foo {

		public String getBar() {

			return null;
		}

		public void setBar( @SuppressWarnings( "unused" ) String bar ) {

			// Do nothing
		}

		@UiComesAfter( "bar" )
		@UiSection( "heading1" )
		public boolean isBaz() {

			return false;
		}

		public void setBaz( @SuppressWarnings( "unused" ) boolean bar ) {

			// Do nothing
		}

		@UiComesAfter( "baz" )
		public String getAbc() {

			return null;
		}

		public void setAbc( @SuppressWarnings( "unused" ) String abc ) {

			// Do nothing
		}

		@UiSection( "heading2" )
		public String getDef() {

			return null;
		}

		public void setDef( @SuppressWarnings( "unused" ) String def ) {

			// Do nothing
		}

		@UiSection( "" )
		@UiLookup( { "foo", "bar" } )
		public String getGhi() {

			return null;
		}

		public void setGhi( @SuppressWarnings( "unused" ) String ghi ) {

			// Do nothing
		}

		@UiSection( "heading3" )
		public String getJkl() {

			return null;
		}

		public void setJkl( @SuppressWarnings( "unused" ) String jkl ) {

			// Do nothing
		}
	}
}
