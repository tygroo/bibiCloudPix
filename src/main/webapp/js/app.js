angular.module('exampleApp', ['ngRoute', 'ngCookies', 'exampleApp.services','angularFileUpload'])
	.config(
		[ '$routeProvider', '$locationProvider', '$httpProvider', function($routeProvider, $locationProvider, $httpProvider) {


			$routeProvider.when('/create', {
				templateUrl: 'partials/create.html',
				controller: CreateController
			});

			$routeProvider.when('/edit/:id', {
				templateUrl: 'partials/edit.html',
				controller: EditController
			});

			$routeProvider.when('/login', {
				templateUrl: 'partials/login.html',
				controller: LoginController
			});

			$routeProvider.when('/signin', {
				templateUrl: 'partials/signin.html',
				controller: SigninController
			});

			$routeProvider.when('/infos', {
				templateUrl: 'partials/infos.html',
				controller: CreateController
			});

			$routeProvider.otherwise({
				templateUrl: 'partials/index.html',
				controller: IndexController
			});

			$locationProvider.hashPrefix('!');

			/* Register error provider that shows message on failed requests or redirects to login page on
			 * unauthenticated requests */
		    $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
			        return {
			        	'responseError': function(rejection) {
			        		var status = rejection.status;
			        		var config = rejection.config;
			        		var method = config.method;
			        		var url = config.url;

			        		if (status == 401) {
			        			$location.path( "/login" );
			        		} else {
			        			$rootScope.error = method + " on " + url + " failed with status " + status;
			        		}

			        		return $q.reject(rejection);
			        	}
			        };
			    }
		    );

		    /* Registers auth token interceptor, auth token is either passed by header or by query parameter
		     * as soon as there is an authenticated user */
		    $httpProvider.interceptors.push(function ($q, $rootScope, $location) {
		        return {
		        	'request': function(config) {
		        		var isRestCall = config.url.indexOf('rest') == 0;
		        		if (isRestCall && angular.isDefined($rootScope.authToken)) {
		        			var authToken = $rootScope.authToken;
		        			if (exampleAppConfig.useAuthTokenHeader) {
		        				config.headers['X-Auth-Token'] = authToken;
		        			} else {
		        				config.url = config.url + "?token=" + authToken;
		        			}
		        		}
		        		return config || $q.when(config);
		        	}
		        };
		    }
	    );
		} ]

	).run(function($rootScope, $location, $cookieStore, UserService, FileUploader) {

		/* Reset error when a new view is loaded */
		$rootScope.$on('$viewContentLoaded', function() {
			delete $rootScope.error;
		});

		$rootScope.hasRole = function(role) {

			if ($rootScope.user === undefined) {
				return false;
			}

			if ($rootScope.user.roles[role] === undefined) {
				return false;
			}

			return $rootScope.user.roles[role];
		};

		$rootScope.logout = function() {
			delete $rootScope.user;
			delete $rootScope.authToken;
			$cookieStore.remove('authToken');
			$location.path("/");
		};

		 /* Try getting valid user from cookie or go to login page */
		var originalPath = $location.path();
		$location.path("/ ");
		var authToken = $cookieStore.get('authToken');
		if (authToken !== undefined) {
			$rootScope.authToken = authToken;
			UserService.get(function(user) {
				$rootScope.user = user;
				$location.path(originalPath);
			});
		}

		$rootScope.initialized = true;
	})
	.controller('exampleApp', function($scope, FileUploader) {
		$scope.uploader = new FileUploader();

		// FILTERS

		uploader.filters.push({
			name: 'imageFilter',
			fn: function(item /*{File|FileLikeObject}*/, options) {
				var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
				return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
			}
		});

		// CALLBACKS

		uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
			console.info('onWhenAddingFileFailed', item, filter, options);
		};
		uploader.onAfterAddingFile = function(fileItem) {
			console.info('onAfterAddingFile', fileItem);
		};
		uploader.onAfterAddingAll = function(addedFileItems) {
			console.info('onAfterAddingAll', addedFileItems);
		};
		uploader.onBeforeUploadItem = function(item) {
			console.info('onBeforeUploadItem', item);
		};
		uploader.onProgressItem = function(fileItem, progress) {
			console.info('onProgressItem', fileItem, progress);
		};
		uploader.onProgressAll = function(progress) {
			console.info('onProgressAll', progress);
		};
		uploader.onSuccessItem = function(fileItem, response, status, headers) {
			console.info('onSuccessItem', fileItem, response, status, headers);
		};
		uploader.onErrorItem = function(fileItem, response, status, headers) {
			console.info('onErrorItem', fileItem, response, status, headers);
		};
		uploader.onCancelItem = function(fileItem, response, status, headers) {
			console.info('onCancelItem', fileItem, response, status, headers);
		};
		uploader.onCompleteItem = function(fileItem, response, status, headers) {
			console.info('onCompleteItem', fileItem, response, status, headers);
		};
		uploader.onCompleteAll = function() {
			console.info('onCompleteAll');
		};

		console.info('uploader', uploader);
	});


function IndexController($scope, PicturesService) {
	console.log("test index");
	$scope.pictureEntries = PicturesService.query();

	$scope.deleteEntry = function(pictureEntry) {
		pictureEntry.$remove(function() {
			$scope.pictureEntries = PicturesService.query();
		});
	};
	$scope.rememberMe = false;
};


function InfosController($scope, PicturesService) {
	console.log("test infos");
	$scope.pictureEntries = PicturesService.query();

	$scope.deleteEntry = function(pictureEntry) {
		pictureEntry.$remove(function() {
			$scope.pictureEntries = PicturesService.query();
		});
	};
	$scope.rememberMe = false;
};

function EditController($scope, $routeParams, $location, PicturesService) {

	$scope.pictureEntry = PicturesService.get({id: $routeParams.id});

	$scope.save = function() {
		$scope.pictureEntry.$save(function() {
			$location.path('/');
		});
	};
};


function CreateController($scope, $location, PicturesService) {

	$scope.pictureEntry = new PicturesService();
	console.log("test create ");
	$scope.save = function() {
		$scope.pictureEntry.$save(function() {
			$location.path('/infos');
		});
	};
};


function LoginController($scope, $rootScope, $location, $cookieStore, UserService) {

	$scope.rememberMe = false;

	$scope.login = function() {
		UserService.authenticate($.param({username: $scope.username, password: $scope.password}), function(authenticationResult) {
			var authToken = authenticationResult.token;
			$rootScope.authToken = authToken;
			if ($scope.rememberMe) {
				$cookieStore.put('authToken', authToken);
			}
			UserService.get(function(user) {
				$rootScope.user = user;
				$location.path("/");
			});
		});
	};
};

function SigninController(){

};

var services = angular.module('exampleApp.services', ['ngResource']);

services.factory('UserService', function($resource) {

	return $resource('rest/user/:action', {},
			{
				authenticate: {
					method: 'POST',
					params: {'action' : 'authenticate'},
					headers : {'Content-Type': 'application/x-www-form-urlencoded'}
				}
			}
		);
});

//services.factory('NewsService', function($resource) {
//
//	return $resource('rest/news/:id', {id: '@id'});
//});

services.factory('PicturesService', function($resource) {

	return $resource('rest/picture/:id', {id: '@id'});
});

services.factory('UploadService', function($resource) {

	return $resource('rest/upload/file/', {id: '@id'});
});