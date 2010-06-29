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

package org.metawidget.inspector.swing;

import static org.metawidget.inspector.InspectionResultConstants.*;
import junit.framework.TestCase;

import org.jdesktop.application.Action;
import org.metawidget.inspector.swing.SwingAppFrameworkInspector;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Richard Kennard
 */

public class SwingAppFrameworkInspectorTest
	extends TestCase {

	//
	// Public methods
	//

	public void testAnnotations() {

		SwingAppFrameworkInspector inspector = new SwingAppFrameworkInspector();

		Document document = XmlUtils.documentFromString( inspector.inspect( null, Foo.class.getName() ) );

		assertEquals( "inspection-result", document.getFirstChild().getNodeName() );

		// Entity

		Element entity = (Element) document.getFirstChild().getFirstChild();
		assertEquals( ENTITY, entity.getNodeName() );
		assertEquals( Foo.class.getName(), entity.getAttribute( TYPE ) );
		assertFalse( entity.hasAttribute( NAME ) );

		// Actions

		Element action = (Element) entity.getFirstChild();
		assertEquals( ACTION, action.getNodeName() );
		assertEquals( "doBar", action.getAttribute( NAME ) );
		assertEquals( "barLabel", action.getAttribute( LABEL ) );
		assertTrue( action.getAttributes().getLength() == 2 );

		assertTrue( entity.getChildNodes().getLength() == 1 );
	}

	//
	// Inner class
	//

	public static class Foo {

		@Action( name = "barLabel" )
		public boolean doBar() {

			return true;
		}
	}
}
