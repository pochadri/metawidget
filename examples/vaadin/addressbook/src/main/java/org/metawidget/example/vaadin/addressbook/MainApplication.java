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

package org.metawidget.example.vaadin.addressbook;

import java.util.ResourceBundle;

import org.metawidget.vaadin.VaadinMetawidget;

import com.vaadin.Application;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;

/**
 * @author Loghman Barari
 */

public class MainApplication
	extends Application {

	//
	// Private statics
	//

	private static boolean				mTestMode;

	//
	// Private members
	//

	private AddressBook					mAddressBook;

	private static final ResourceBundle	mBundle;

	static {
		mBundle = ResourceBundle.getBundle( "org.metawidget.example.shared.addressbook.resource.Resources" );
	}

	//
	// Constructor
	//

	public MainApplication() {

		// Create AddressBook

		mAddressBook = new AddressBook();
	}

	//
	// Public methods
	//

	@Override
	public void init() {

		VaadinMetawidget metawidget = new VaadinMetawidget();
		metawidget.setConfig( "org/metawidget/example/vaadin/addressbook/metawidget.xml" );

		setTheme( "addressbook" );
		Window mainWindow = new Window( "Address Book (Metawidget Vaadin Example)" );
		((Layout) mainWindow.getContent()).setMargin( false );
		mainWindow.addComponent( mAddressBook.getContent() );

		setMainWindow( mainWindow );
	}

	public static ResourceBundle getBundle() {

		return mBundle;
	}

	/**
	 * Gets the AddressBook.
	 * <p>
	 * Used by Unit Tests.
	 */

	public AddressBook getAddressBook() {

		return mAddressBook;
	}

	public static boolean isTestMode() {

		return mTestMode;
	}

	public static void setTestMode( boolean testMode ) {

		mTestMode = testMode;
	}
}
