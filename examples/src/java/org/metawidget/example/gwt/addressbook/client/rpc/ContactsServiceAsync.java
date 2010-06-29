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

package org.metawidget.example.gwt.addressbook.client.rpc;

import java.util.List;

import org.metawidget.example.shared.addressbook.model.Contact;
import org.metawidget.example.shared.addressbook.model.ContactSearch;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Richard Kennard
 */

public interface ContactsServiceAsync
	extends RemoteService {

	//
	// Methods
	//

	void getAllByExample( ContactSearch search, AsyncCallback<List<Contact>> callback );

	void load( long id, AsyncCallback<Contact> callback );

	void save( Contact contact, AsyncCallback<?> callback );

	void delete( Contact contact, AsyncCallback<Boolean> callback );
}
