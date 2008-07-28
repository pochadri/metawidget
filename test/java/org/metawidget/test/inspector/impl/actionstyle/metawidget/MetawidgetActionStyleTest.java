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

package org.metawidget.test.inspector.impl.actionstyle.metawidget;

import java.util.Map;

import junit.framework.TestCase;

import org.metawidget.inspector.annotation.UiAction;
import org.metawidget.inspector.impl.actionstyle.Action;
import org.metawidget.inspector.impl.actionstyle.metawidget.MetawidgetActionStyle;

/**
 * @author Richard Kennard
 */

public class MetawidgetActionStyleTest
	extends TestCase
{
	//
	//
	// Public methods
	//
	//

	public void testMetawidgetActionStyle()
	{
		MetawidgetActionStyle actionStyle = new MetawidgetActionStyle();
		Map<String, Action> actions = actionStyle.getActions( Foo.class );

		assertTrue( actions.size() == 1 );

		assertTrue( "bar".equals( actions.get( "bar" ).toString() ) );
	}

	//
	//
	// Inner class
	//
	//

	abstract class Foo
	{
		@UiAction
		public abstract void bar();
	}
}
