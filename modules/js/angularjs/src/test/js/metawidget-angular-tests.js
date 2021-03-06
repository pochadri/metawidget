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

( function() {

	'use strict';

	describe(
			"The AngularMetawidget",
			function() {

				it(
						"populates itself with widgets to match the properties of business objects",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );
							var controller = myApp.controller( 'TestController', function( $scope ) {

								$scope.foo = {
									bar: "Bar"
								};
							} );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'foo' );
							mw.setAttribute( 'read-only', 'readOnly' );
							mw.setAttribute( 'config', 'metawidgetConfig' );

							var body = document.createElement( 'body' );
							body.setAttribute( 'ng-controller', 'TestController' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML )
												.toBe(
														'<table id="table-foo"><tbody><tr id="table-fooBar-row"><th id="table-fooBar-label-cell"><label for="fooBar" id="table-fooBar-label">Bar:</label></th><td id="table-fooBar-cell"><input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/></td><td/></tr></tbody></table>' );

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/>' );

										// Test watching ngModel

										var scope = angular.element( body ).scope();
										scope.foo = {
											baz: "Baz"
										};
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBaz" ng-model="foo.baz" class="ng-pristine ng-valid"/>' );
										expect( mw.innerHTML ).toNotContain( '<input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/>' );

										// Test watching readOnly

										scope.readOnly = true;
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<output id="fooBaz" ng-bind="foo.baz" class="ng-binding">Baz</output>' );

										// Test watching config

										scope.metawidgetConfig = {
											layout: new metawidget.layout.SimpleLayout()
										};
										scope.$digest();

										expect( mw.innerHTML ).toBe( '<output id="fooBaz" ng-bind="foo.baz" class="ng-binding">Baz</output>' );
										expect( mw.innerHTML ).toNotContain( '<table' );
									} );
						} );

				it(
						"watches toInspects that are 'undefined'",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'foo' );

							var body = document.createElement( 'body' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML ).toBe( '<table id="table-foo"><tbody/></table>' );

										var scope = angular.element( body ).scope();
										scope.foo = {
											bar: "Bar"
										};
										scope.$digest();

										expect( mw.innerHTML )
												.toBe(
														'<table id="table-foo"><tbody><tr id="table-fooBar-row"><th id="table-fooBar-label-cell"><label for="fooBar" id="table-fooBar-label">Bar:</label></th><td id="table-fooBar-cell"><input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/></td><td/></tr></tbody></table>' );
									} );
						} );
				it(
						"minimizes reinspection",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );
							var inspectionCount = 0;
							var buildingCount = 0;
							var controller = myApp.controller( 'TestController', function( $scope ) {

								$scope.foo = {
									bar: "Bar"
								};

								$scope.metawidgetConfig = {
									inspectionResultProcessors: [ function( inspectionResult, mw, toInspect, path, names ) {

										inspectionCount++;
										return inspectionResult;
									} ],
									addWidgetProcessors: [ function( widget, attributes, mw ) {

										buildingCount++;
										return widget;
									} ]
								};
							} );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'foo' );
							mw.setAttribute( 'read-only', 'readOnly' );
							mw.setAttribute( 'config', 'metawidgetConfig' );

							var body = document.createElement( 'body' );
							body.setAttribute( 'ng-controller', 'TestController' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML )
												.toBe(
														'<table id="table-foo"><tbody><tr id="table-fooBar-row"><th id="table-fooBar-label-cell"><label for="fooBar" id="table-fooBar-label">Bar:</label></th><td id="table-fooBar-cell"><input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/></td><td/></tr></tbody></table>' );

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/>' );

										expect( inspectionCount ).toBe( 1 );
										expect( buildingCount ).toBe( 1 );

										// Test changing two things at once

										var scope = angular.element( body ).scope();
										scope.foo = {
											baz: "Baz"
										};
										scope.readOnly = true;
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<output id="fooBaz" ng-bind="foo.baz" class="ng-binding">Baz</output>' );
										expect( inspectionCount ).toBe( 2 );
										expect( buildingCount ).toBe( 2 );

										// Test changing to the same value

										scope.toInspect = scope.toInspect;
										scope.readOnly = scope.readOnly;
										scope.$digest();

										// Test rebuilding but not reinspecting

										scope.readOnly = false;
										scope.$digest();
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBaz" ng-model="foo.baz" class="ng-pristine ng-valid"/>' );
										expect( inspectionCount ).toBe( 2 );
										expect( buildingCount ).toBe( 3 );

										// Test changing toInspect to a similar
										// value

										scope.foo = {
											baz: "Baz"
										};
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBaz" ng-model="foo.baz" class="ng-pristine ng-valid"/>' );
										expect( inspectionCount ).toBe( 3 );
										expect( buildingCount ).toBe( 4 );

										// Test changing config

										scope.metawidgetConfig = {
											layout: new metawidget.layout.SimpleLayout()
										};

										scope.$digest();

										expect( mw.innerHTML ).toBe( '<input type="text" id="fooBaz" ng-model="foo.baz" class="ng-pristine ng-valid"/>' );
										expect( mw.innerHTML ).toNotContain( '<table' );
										expect( inspectionCount ).toBe( 4 );
										expect( buildingCount ).toBe( 5 );
									} );
						} );

				it(
						"supports arrays of configs",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );
							var inspectionCount = 0;
							var buildingCount = 0;
							var controller = myApp.controller( 'TestController', function( $scope ) {

								$scope.foo = {
									bar: "Bar"
								};

								$scope.metawidgetConfig1 = {
									inspectionResultProcessors: [ function( inspectionResult, mw, toInspect, path, names ) {

										inspectionCount++;
										return inspectionResult;
									} ]
								};
								$scope.metawidgetConfig2 = {
									addWidgetProcessors: [ function( widget, attributes, mw ) {

										buildingCount++;
										return widget;
									} ]
								};
							} );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'foo' );
							mw.setAttribute( 'read-only', 'readOnly' );
							mw.setAttribute( 'configs', '[metawidgetConfig1,metawidgetConfig2]' );

							var body = document.createElement( 'body' );
							body.setAttribute( 'ng-controller', 'TestController' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML )
												.toBe(
														'<table id="table-foo"><tbody><tr id="table-fooBar-row"><th id="table-fooBar-label-cell"><label for="fooBar" id="table-fooBar-label">Bar:</label></th><td id="table-fooBar-cell"><input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/></td><td/></tr></tbody></table>' );

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBar" ng-model="foo.bar" class="ng-pristine ng-valid"/>' );

										expect( inspectionCount ).toBe( 1 );
										expect( buildingCount ).toBe( 1 );

										// Test changing two things at once

										var scope = angular.element( body ).scope();
										scope.foo = {
											baz: "Baz"
										};
										scope.readOnly = true;
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<output id="fooBaz" ng-bind="foo.baz" class="ng-binding">Baz</output>' );
										expect( inspectionCount ).toBe( 2 );
										expect( buildingCount ).toBe( 2 );

										// Test changing to the same value

										scope.toInspect = scope.toInspect;
										scope.readOnly = scope.readOnly;
										scope.$digest();

										// Test rebuilding but not reinspecting

										scope.readOnly = false;
										scope.$digest();
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBaz" ng-model="foo.baz" class="ng-pristine ng-valid"/>' );
										expect( inspectionCount ).toBe( 2 );
										expect( buildingCount ).toBe( 3 );

										// Test changing toInspect to a similar
										// value

										scope.foo = {
											baz: "Baz"
										};
										scope.$digest();

										expect( mw.innerHTML ).toContain( '<input type="text" id="fooBaz" ng-model="foo.baz" class="ng-pristine ng-valid"/>' );
										expect( inspectionCount ).toBe( 3 );
										expect( buildingCount ).toBe( 4 );

										// Test changing configs is *not*
										// watched

										scope.metawidgetConfig1 = {
											layout: new metawidget.layout.SimpleLayout()
										};

										scope.$digest();

										expect( inspectionCount ).toBe( 3 );
										expect( buildingCount ).toBe( 4 );
									} );
						} );

				it( "defensively copies overridden widgets", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.foo = {
							foo: "Foo",
							bar: "Bar"
						};
					} );

					var mw = document.createElement( 'metawidget' );
					mw.setAttribute( 'ng-model', 'foo' );
					var bar = document.createElement( 'span' );
					bar.setAttribute( 'id', 'fooBar' );
					mw.appendChild( bar );
					var baz = document.createElement( 'span' );
					baz.setAttribute( 'id', 'fooBaz' );
					mw.appendChild( baz );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<input type="text" id="fooFoo" ng-model="foo.foo"' );
						expect( mw.innerHTML ).toContain( '<td id="table-fooBar-cell"><span id="fooBar"' );
						expect( mw.innerHTML ).toContain( '<td colspan="2"><span id="fooBaz"' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it( "can be used purely for layout", function() {

					var mw = document.createElement( 'metawidget' );
					var bar = document.createElement( 'span' );
					bar.setAttribute( 'id', 'fooBar' );
					mw.appendChild( bar );
					var baz = document.createElement( 'span' );
					baz.setAttribute( 'id', 'fooBaz' );
					mw.appendChild( baz );
					var ignore = document.createTextNode( 'ignore' );
					mw.appendChild( ignore );

					var body = document.createElement( 'body' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'metawidget' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<td colspan="2"><span id="fooBar"' );
						expect( mw.innerHTML ).toContain( '<td colspan="2"><span id="fooBaz"' );
						expect( mw.innerHTML ).toNotContain( 'ignore' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it( "inspects from parent", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.foo = {
							bar: "Bar"
						};
					} );

					var mw = document.createElement( 'metawidget' );
					mw.setAttribute( 'ng-model', 'foo.bar' );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<label for="fooBar" id="table-fooBar-label">Bar:</label>' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it( "supports stubs with their own metadata", function() {

					var mw = document.createElement( 'metawidget' );
					var stub = document.createElement( 'stub' );
					stub.setAttribute( 'title', 'Foo' );
					stub.appendChild( document.createElement( 'input' ) );
					mw.appendChild( stub );

					var body = document.createElement( 'body' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'metawidget' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toBe( '<table><tbody><tr><th><label>Foo:</label></th><td><stub title="Foo" class="ng-scope"><input/></stub></td><td/></tr></tbody></table>' );
					} );
				} );

				it( "defensively copies overridden widgets", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.foo = {
							bar: "Bar"
						};
					} );

					var mw = document.createElement( 'metawidget' );
					var bar = document.createElement( 'span' );
					bar.setAttribute( 'ng-model', 'foo.bar' );
					mw.appendChild( bar );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<label id="table-bar-label">Bar:</label>' );
						expect( mw.innerHTML ).toContain( '<span ng-model="foo.bar" class="ng-scope ng-pristine ng-valid"/>' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it( "supports normalized attribute names", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.foo = {
							bar: "Bar",
							baz: "Baz"
						};
					} );

					// x-ng-bind

					var mw = document.createElement( 'metawidget' );
					var bar = document.createElement( 'span' );
					bar.setAttribute( 'x-ng-bind', 'foo.bar' );
					mw.appendChild( bar );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<label id="table-bar-label">Bar:</label>' );
						expect( mw.innerHTML ).toContain( '<span x-ng-bind="foo.bar" class="ng-scope ng-binding">Bar</span>' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );

					// ng:model

					var baz = document.createElement( 'span' );
					baz.setAttribute( 'ng:model', 'foo.baz' );
					mw.appendChild( baz );

					injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<label id="table-baz-label">Baz:</label>' );
						expect( mw.innerHTML ).toContain( '<span ng:model="foo.baz" class="ng-scope ng-pristine ng-valid"/>' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );

					// ngmodel (Angular's template mechanism lowercases
					// attribute names)

					var baz = document.createElement( 'span' );
					baz.setAttribute( 'ngmodel', 'foo.baz' );
					mw.appendChild( baz );

					injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<label id="table-baz-label">Baz:</label>' );
						expect( mw.innerHTML ).toContain( '<span ngmodel="foo.baz" class="ng-scope"/>' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it( "does not suppress undefined child inspection results", function() {

					var mw = document.createElement( 'metawidget' );
					var bar = document.createElement( 'span' );
					bar.setAttribute( 'ng-model', 'fooBar' );
					mw.appendChild( bar );

					var body = document.createElement( 'body' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'metawidget' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<td colspan="2"><span ng-model="fooBar"' );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it(
						"supports multiselects",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );
							var controller = myApp.controller( 'TestController', function( $scope ) {

								$scope.foo = {
									bar: [ "Abc" ]
								};

								$scope.metawidgetConfig = {
									inspector: function() {

										return {
											"properties": {
												"bar": {
													type: "array",
													enum: [ "Abc", "Def", "Ghi" ]
												}
											}
										};
									}
								};
							} );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'foo' );
							mw.setAttribute( 'config', 'metawidgetConfig' );

							var body = document.createElement( 'body' );
							body.setAttribute( 'ng-controller', 'TestController' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML ).toContain( '<th id="table-fooBar-label-cell"><label for="fooBar" id="table-fooBar-label">Bar:</label></th>' );
										expect( mw.innerHTML )
												.toContain(
														'<div id="fooBar"><label><input type="checkbox" value="Abc" ng-checked="foo.bar.indexOf(&apos;Abc&apos;)&gt;=0" ng-click="_mwUpdateSelection($event,&apos;foo.bar&apos;)" checked="checked"/>Abc</label>' );
										expect( mw.innerHTML )
												.toContain(
														'<label><input type="checkbox" value="Def" ng-checked="foo.bar.indexOf(&apos;Def&apos;)&gt;=0" ng-click="_mwUpdateSelection($event,&apos;foo.bar&apos;)"/>Def</label>' );
										expect( mw.innerHTML )
												.toContain(
														'<label><input type="checkbox" value="Ghi" ng-checked="foo.bar.indexOf(&apos;Ghi&apos;)&gt;=0" ng-click="_mwUpdateSelection($event,&apos;foo.bar&apos;)"/>Ghi</label>' );
										expect( mw.innerHTML ).toContain( '</div></td><td/></tr></tbody></table>' );
									} );
						} );

				it( "supports radio buttons", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.foo = {
							bar: "Def"
						};

						$scope.metawidgetConfig = {
							inspector: function() {

								return {
									"properties": {
										"bar": {
											componentType: "radio",
											enum: [ "Abc", "Def", "Ghi" ]
										}
									}
								};
							}
						};
					} );

					var mw = document.createElement( 'metawidget' );
					mw.setAttribute( 'ng-model', 'foo' );
					mw.setAttribute( 'config', 'metawidgetConfig' );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toContain( '<th id="table-fooBar-label-cell"><label for="fooBar" id="table-fooBar-label">Bar:</label></th>' );
						expect( mw.innerHTML ).toContain( '<div id="fooBar"><label><input type="radio" value="Abc" ng-model="foo.bar" class="ng-pristine ng-valid" name="' );
						expect( mw.innerHTML ).toContain( '"/>Abc</label><label><input type="radio" value="Def" ng-model="foo.bar" class="ng-pristine ng-valid" name="' );
						expect( mw.innerHTML ).toContain( '"/>Def</label><label><input type="radio" value="Ghi" ng-model="foo.bar" class="ng-pristine ng-valid" name="' );
						expect( mw.innerHTML ).toContain( '"/>Ghi</label></div></td><td/></tr></tbody></table>' );
					} );
				} );

				it( "guards against infinite recursion", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.metawidgetConfig = {
							inspector: function() {

								return {
									"properties": {
										"foo": {}
									}
								};
							}
						};
					} );

					var mw = document.createElement( 'metawidget' );
					mw.setAttribute( 'ng-model', 'root' );
					mw.setAttribute( 'config', 'metawidgetConfig' );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.childNodes[0].tagName ).toBe( 'TABLE' );
						expect( mw.childNodes[0].childNodes[0].tagName ).toBe( 'TBODY' );

						var childNode = mw.childNodes[0].childNodes[0];
						var idMiddle = 'Foo';

						for ( var loop = 0; loop < 10; loop++ ) {

							expect( childNode.childNodes[0].tagName ).toBe( 'TR' );
							expect( childNode.childNodes[0].id ).toBe( 'table-root' + idMiddle + '-row' );
							expect( childNode.childNodes[0].childNodes[0].tagName ).toBe( 'TH' );
							expect( childNode.childNodes[0].childNodes[0].getAttribute( 'id' ) ).toBe( 'table-root' + idMiddle + '-label-cell' );
							expect( childNode.childNodes[0].childNodes[0].childNodes[0].tagName ).toBe( 'LABEL' );
							expect( childNode.childNodes[0].childNodes[0].childNodes[0].getAttribute( 'for' ) ).toBe( 'root' + idMiddle );
							expect( childNode.childNodes[0].childNodes[0].childNodes[0].getAttribute( 'id' ) ).toBe( 'table-root' + idMiddle + '-label' );
							expect( childNode.childNodes[0].childNodes[0].childNodes[0].innerHTML ).toBe( 'Foo:' );
							expect( childNode.childNodes[0].childNodes[1].tagName ).toBe( 'TD' );
							expect( childNode.childNodes[0].childNodes[1].getAttribute( 'id' ) ).toBe( 'table-root' + idMiddle + '-cell' );
							expect( childNode.childNodes[0].childNodes[1].childNodes[0].tagName ).toBe( 'METAWIDGET' );
							expect( childNode.childNodes[0].childNodes[1].childNodes[0].getAttribute( 'id' ) ).toBe( 'root' + idMiddle );
							expect( childNode.childNodes[0].childNodes[1].childNodes[0].childNodes[0].tagName ).toBe( 'TABLE' );
							expect( childNode.childNodes[0].childNodes[1].childNodes[0].childNodes[0].getAttribute( 'id' ) ).toBe( 'table-root' + idMiddle );
							expect( childNode.childNodes[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].tagName ).toBe( 'TBODY' );
							expect( childNode.childNodes[0].childNodes.length ).toBe( 3 );
							expect( childNode.childNodes.length ).toBe( 1 );

							idMiddle += 'Foo';
							childNode = childNode.childNodes[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0];
						}

						expect( childNode.childNodes.length ).toBe( 0 );

						expect( mw.childNodes[0].childNodes.length ).toBe( 1 );
						expect( mw.childNodes.length ).toBe( 1 );
					} );
				} );

				it( "does not watch primitives", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var inspectionCount = 0;
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.foo = 'hello';

						$scope.metawidgetConfig = {
							inspectionResultProcessors: [ function( inspectionResult, mw, toInspect, path, names ) {

								inspectionCount++;
								return inspectionResult;
							} ]
						}
					} );

					var mw = document.createElement( 'metawidget' );
					mw.setAttribute( 'ng-model', 'foo' );
					mw.setAttribute( 'config', 'metawidgetConfig' );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toBe(
								'<table id="table-foo"><tbody><tr><td colspan="2"><input type="text" id="foo" ng-model="foo" class="ng-pristine ng-valid"/></td><td/></tr></tbody></table>' );

						expect( inspectionCount ).toBe( 1 );

						var scope = angular.element( body ).scope();
						scope.foo = 'goodbye';
						scope.$digest();

						expect( inspectionCount ).toBe( 1 );
					} );
				} );

				it( "provides access to attached element", function() {

					var myApp = angular.module( 'test-app', [ 'metawidget' ] );
					var attachedElement = [];
					var controller = myApp.controller( 'TestController', function( $scope ) {

						$scope.metawidgetConfig = {
							inspector: function() {

								return {};
							},
							inspectionResultProcessors: [ function( inspectionResult, mw, toInspect, path, names ) {

								attachedElement.push( mw.getElement() );
								return inspectionResult;
							} ]
						}
					} );

					var mw = document.createElement( 'metawidget' );
					mw.setAttribute( 'config', 'metawidgetConfig' );

					var body = document.createElement( 'body' );
					body.setAttribute( 'ng-controller', 'TestController' );
					body.appendChild( mw );

					var injector = angular.bootstrap( body, [ 'test-app' ] );

					injector.invoke( function() {

						expect( mw.innerHTML ).toBe( '<table><tbody/></table>' );
						expect( attachedElement[0] ).toBe( mw );
						expect( attachedElement.length ).toBe( 1 );
					} );
				} );

				it(
						"wraps nodes that have directives that add a sibling element",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );
							var attachedElement = [];
							var controller = myApp.controller( 'TestController', function( $scope ) {

								$scope.model = {
									"foo": "fooValue",
									"bar": "barValue"
								}
							} );

							myApp.directive( 'input', function() {

								return {
									restrict: 'E',
									scope: {
										ngModel: '=',
									},
									compile: function compile( element, attrs, transclude ) {

										if ( attrs.ngModel === 'model.foo' ) {
											element.after( '<div class="input-sibling"></div>' );
										}
									}
								}
							} );

							var body = document.createElement( 'body' );
							body.setAttribute( 'ng-controller', 'TestController' );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'model' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML )
												.toBe(
														'<table id="table-model"><tbody><tr id="table-modelFoo-row"><th id="table-modelFoo-label-cell"><label for="modelFoo-wrapper" id="table-modelFoo-label">Foo:</label></th><td id="table-modelFoo-cell"><stub class="ng-scope" id="modelFoo-wrapper"><input type="text" id="modelFoo" ng-model="model.foo" class="ng-isolate-scope ng-scope ng-pristine ng-valid"/><div class="input-sibling"/></stub></td><td/></tr><tr id="table-modelBar-row"><th id="table-modelBar-label-cell"><label for="modelBar" id="table-modelBar-label">Bar:</label></th><td id="table-modelBar-cell"><input type="text" id="modelBar" ng-model="model.bar" class="ng-isolate-scope ng-scope ng-pristine ng-valid"/></td><td/></tr></tbody></table>' );
										expect( mw.innerHTML )
												.toContain(
														'<stub class="ng-scope" id="modelFoo-wrapper"><input type="text" id="modelFoo" ng-model="model.foo" class="ng-isolate-scope ng-scope ng-pristine ng-valid"/><div class="input-sibling"/></stub>' );
										expect( mw.innerHTML ).toContain(
												'<td id="table-modelBar-cell"><input type="text" id="modelBar" ng-model="model.bar" class="ng-isolate-scope ng-scope ng-pristine ng-valid"/></td>' );
										expect( mw.innerHTML ).toContain( '<stub class="ng-scope" id="modelFoo' );
										expect( mw.innerHTML ).toNotContain( '<stub class="ng-scope" id="modelBar' );
									} );
						} );
			} );

	describe(
			"The AngularInspectionResultProcessor",
			function() {

				it( "executes Angular expressions inside inspection results", function() {

					var injector = angular.bootstrap();

					injector.invoke( function( $rootScope ) {

						var processor = new metawidget.angular.inspectionresultprocessor.AngularInspectionResultProcessor( $rootScope.$new() );
						var inspectionResult = {
							properties: {
								"foo": {
									value: "{{1+2}}",
									ignore: 3,
									missing: undefined
								}
							}
						};

						inspectionResult = processor.processInspectionResult( inspectionResult );

						expect( inspectionResult.properties.foo.value ).toBe( '3' );
					} );
				} );

				it(
						"watches expressions and invalidates inspection results",
						function() {

							var myApp = angular.module( 'test-app', [ 'metawidget' ] );
							var controller = myApp.controller( 'TestController', function( $scope ) {

								$scope.readOnlyz = true;

								$scope.foo = {
									edit: function() {

									},
									save: function() {

									}
								};

								$scope.metawidgetConfig = {
									inspector: new metawidget.inspector.CompositeInspector( [ new metawidget.inspector.PropertyTypeInspector(), function( toInspect, type, names ) {

										return {
											properties: {
												edit: {
													"hidden": "{{!readOnlyz}}"
												},
												save: {
													"hidden": "{{readOnlyz}}"
												}
											}
										}
									} ] )
								};
							} );

							var mw = document.createElement( 'metawidget' );
							mw.setAttribute( 'ng-model', 'foo' );
							mw.setAttribute( 'config', 'metawidgetConfig' );

							var body = document.createElement( 'body' );
							body.setAttribute( 'ng-controller', 'TestController' );
							body.appendChild( mw );

							var injector = angular.bootstrap( body, [ 'test-app' ] );

							injector
									.invoke( function() {

										expect( mw.innerHTML )
												.toBe(
														'<table id="table-foo"><tbody><tr id="table-fooEdit-row"><th id="table-fooEdit-label-cell"><label for="fooEdit" id="table-fooEdit-label">Edit:</label></th><td id="table-fooEdit-cell"><button id="fooEdit" ng-click="foo.edit()">Edit</button></td><td/></tr></tbody></table>' );

										expect( mw.innerHTML ).toContain( '<button id="fooEdit" ng-click="foo.edit()">Edit</button>' );

										var scope = angular.element( body ).scope();
										scope.readOnlyz = false;
										scope.$digest();

										expect( mw.innerHTML ).toNotContain( 'fooEdit' );
										expect( mw.innerHTML ).toContain( '<button id="fooSave" ng-click="foo.save()">Save</button>' );

										scope.readOnlyz = true;
										scope.$digest();

										expect( mw.innerHTML ).toNotContain( 'fooSave' );
										expect( mw.innerHTML ).toContain( '<button id="fooEdit" ng-click="foo.edit()">Edit</button>' );
									} );
						} );
			} );

	describe( "The AngularWidgetProcessor", function() {

		it( "processes widgets and binds Angular models", function() {

			var injector = angular.bootstrap();

			injector.invoke( function( $compile, $parse, $rootScope ) {

				var processor = new metawidget.angular.widgetprocessor.AngularWidgetProcessor( $compile, $parse, $rootScope.$new() );
				var attributes = {
					name: "foo",
					required: "true",
					minLength: "3",
					maxLength: "97"
				};
				var mw = {
					toInspect: {},
					path: "testPath"
				};

				// Inputs

				var widget = document.createElement( 'input' );
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-model' ) ).toBe( 'testPath.foo' );
				expect( widget.getAttribute( 'ng-required' ) ).toBe( 'true' );
				expect( widget.getAttribute( 'ng-minlength' ) ).toBe( '3' );
				expect( widget.getAttribute( 'ng-maxlength' ) ).toBe( '97' );

				// Textareas (same as inputs, not same as outputs)

				widget = document.createElement( 'textarea' );
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-model' ) ).toBe( 'testPath.foo' );

				// Buttons

				attributes = {
					name: "bar"
				};
				widget = document.createElement( 'button' );
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-click' ) ).toBe( 'testPath.bar()' );
				expect( widget.getAttribute( 'ng-required' ) ).toBe( null );
				expect( widget.getAttribute( 'ng-minlength' ) ).toBe( null );
				expect( widget.getAttribute( 'ng-maxlength' ) ).toBe( null );

				// Outputs

				widget = document.createElement( 'output' );
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-bind' ) ).toBe( 'testPath.bar' );

				attributes = {
					name: "bar",
					type: "array"
				}
				widget = document.createElement( 'output' );
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-bind' ) ).toBe( "testPath.bar.join(', ')" );

				// Root-level

				widget = document.createElement( 'output' );
				processor.processWidget( widget, 'entity', {}, mw );
				expect( widget.getAttribute( 'ng-bind' ) ).toBe( 'testPath' );
			} );
		} );

		it( "ignores overridden widgets", function() {

			var injector = angular.bootstrap();

			injector.invoke( function( $compile, $parse, $rootScope ) {

				var processor = new metawidget.angular.widgetprocessor.AngularWidgetProcessor( $compile, $parse, $rootScope.$new() );
				var attributes = {
					name: "foo",
				};
				var mw = {
					toInspect: {}
				};

				var widget = document.createElement( 'input' );
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-model' ) ).toBeDefined();

				widget = document.createElement( 'input' );
				widget.overridden = true;
				processor.processWidget( widget, 'property', attributes, mw );
				expect( widget.getAttribute( 'ng-model' ) ).toBe( null );
			} );
		} );
	} );
} )();