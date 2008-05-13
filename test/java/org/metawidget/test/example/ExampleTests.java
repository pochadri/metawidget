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

package org.metawidget.test.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.metawidget.test.example.swing.addressbook.SwingAddressBookTest;
import org.metawidget.test.example.swing.tutorial.SwingTutorialTest;

/**
 * @author Richard Kennard
 */

public class ExampleTests
	extends TestCase
{
	//
	//
	// Public statics
	//
	//

	public static Test suite()
	{
		TestSuite suite = new TestSuite( "Example Tests" );
		suite.addTestSuite( SwingAddressBookTest.class );
		suite.addTestSuite( SwingTutorialTest.class );

		// Note: GwtAddressBookTest is performed separately

		// Note: SwingAllWidgetsTest.class is performed separately to test JDK 1.4 compatibiltiy

		// Note: Web tests are performed by /test/web/examples/*/addressbook-test.xml

		return suite;
	}

}