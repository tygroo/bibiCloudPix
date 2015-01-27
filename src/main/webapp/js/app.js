var app = angular.module('exampleApp', ['ngRoute', 'ngCookies', 'exampleApp.services','angularFileUpload','angularFileUpload']);
app.config(
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

							if (status == 401 && url =="rest/user/authenticate" && method =="POST") {
								$rootScope.error = "Login ou mot de passe incorrect";
								$location.path( "/login" );
							} else if(status == 500 && url == "rest/user/new" && method =="PUT") {
								$rootScope.error = "Le user existe deja";

							} else if(status == 401 ) {
								$location.path( "/login" );

							}else{
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

).run(function($rootScope, $location, $cookieStore, UserService) {

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
			$location.path("/login");
		};

		/* Try getting valid user from cookie or go to login page */
		var originalPath = $location.path();
		$location.path("/");
		var authToken = $cookieStore.get('authToken');
		if (authToken !== undefined) {
			$rootScope.authToken = authToken;
			UserService.get(function(user) {
				$rootScope.user = user;
				$location.path(originalPath);
			});
		}

		$rootScope.initialized = true;
	});

function IndexController($scope, $location , $routeParams ,PicturesService) {
	console.log("test index");

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


function CreateController($scope, $location, PicturesService, FileUploader ,$rootScope) {

	var authToken = $rootScope.authToken;
if (authToken === undefined){
	$scope.uploader =  new FileUploader({
		url: 'rest/upload/file/',
		method:'POST'
	});

}else {

	$scope.uploader =  new FileUploader({
		url: 'rest/upload/file/',
		method:'POST',
		headers : {
			'X-Auth-Token': authToken
		}
	});
	$scope.urltoken = "?token="+authToken;

}


	$scope.uploader.filters.push({
		name: 'imageFilter',
		fn: function(item /*{File|FileLikeObject}*/, options) {
			var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
			return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
		}
	});

	// CALLBACKS
	$scope.uploader.onSuccessItem = function(fileItem, response, status, headers) {
		console.info('onSuccessItem', fileItem, response);
	};

	$scope.uploader.onCompleteItem = function(fileItem, response, $location, headers) {
		//console.info('onCompleteItem', fileItem, response);
		fileItem.index = response.id;

		makeShortFull();
		makeShortMedium();
		makeShortLow();
		
		console.info('onCompleteItem2', fileItem, response);
	};
	$scope.uploader.onCompleteAll = function() {
		console.info('onCompleteAll');
	};

	$scope.pictureEntry = new PicturesService();
	console.log("test create ");

	$scope.onSuccess = function (response) {
		console.log('AppCtrl.onSuccess', response);
		//$scope.pictureEntry = response.data;
		$scope.uploads = $scope.uploads.concat(response.data.files);
	};

	$scope.save = function() {
		$scope.pictureEntry.$save(function() {
			$location.path('/');
		});
	};

	$scope.create = function(item, $location) {

		// Make HTTP request to goo.gl URL shortener
		var googleAPIKey = 'AIzaSyBZxqzzA98ToBe1N8LxkdZUhkZcUC1AsSY';
		var googleShortenerUrl =
			'https://www.googleapis.com/urlshortener/v1/url/?key='
			+ googleAPIKey;
		$http.post(googleShortenerUrl,
			{longUrl: $location.protocol()+'://'+$location.host()+':'+$location.port()+'/rest/files/full/'+item.id})
			.success(function (resp){

				//url.shortUrl = resp.id;
				console.info('shortURL',resp, resp.id);
				// Do something with the shortURL...

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

function SigninController($scope, $rootScope, $location, $cookieStore, UserService){

	$scope.rememberMe = false;

	$scope.signin = function() {
		UserService.signin($.param({username: $scope.username, password: $scope.password, password2: $scope.password2}), function(authenticationResult) {

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
function makeShortFull()
{
	var longUrl=document.getElementById("longurlfull").value;
	var token=document.getElementById("tokenfull").value;
	var request = gapi.client.urlshortener.url.insert({
		'resource': {
			'longUrl': 'https://openstack.gheberg.eu/cloudpix/rest/files/full/'+longUrl+token
		}
	});
	request.execute(function(response)
	{

		if(response.id != null)
		{
			str ="<b>Short URL:</b> <a href='"+response.id+"'>"+response.id+"</a><br>";
			document.getElementById("outputfull"+longUrl).innerHTML = str;
		}
		else
		{
			alert("error: creating short url n"+ response.error);
		}

	});
}

function makeShortMedium()
{
	var longUrl=document.getElementById("longurlmedium").value;
	var token=document.getElementById("tokenmedium").value;
	var request = gapi.client.urlshortener.url.insert({
		'resource': {
			'longUrl': 'https://openstack.gheberg.eu/cloudpix/rest/files/medium/'+longUrl+token
		}
	});
	request.execute(function(response)
	{

		if(response.id != null)
		{
			str ="<b>Short URL:</b> <a href='"+response.id+"'>"+response.id+"</a><br>";
			document.getElementById("outputmedium"+longUrl).innerHTML = str;
		}
		else
		{
			alert("error: creating short url n"+ response.error);
		}

	});
}

function makeShortLow()
{
	var longUrl=document.getElementById("longurllow").value;
	var token=document.getElementById("tokenlow").value;
	var request = gapi.client.urlshortener.url.insert({
		'resource': {
			'longUrl': 'https://openstack.gheberg.eu/cloudpix/rest/files/low/'+longUrl+token
		}
	});
	request.execute(function(response)
	{

		if(response.id != null)
		{
			str ="<b>Short URL:</b> <a href='"+response.id+"'>"+response.id+"</a><br>";
			document.getElementById("outputlow"+longUrl).innerHTML = str;
		}
		else
		{
			alert("error: creating short url n"+ response.error);
		}

	});
}
function getShortInfo()
{
	var shortUrl=document.getElementById("shorturl").value;

	var request = gapi.client.urlshortener.url.get({
		'shortUrl': shortUrl,
		'projection':'FULL'
	});
	request.execute(function(response)
	{

		if(response.longUrl!= null)
		{
			str ="<b>Long URL:</b>"+response.longUrl+"<br>";
			str +="<b>Create On:</b>"+response.created+"<br>";
			str +="<b>Short URL Clicks:</b>"+response.analytics.allTime.shortUrlClicks+"<br>";
			str +="<b>Long URL Clicks:</b>"+response.analytics.allTime.longUrlClicks+"<br>";

			document.getElementById("output").innerHTML = str;
		}
		else
		{
			alert("error: "+response.error);
		}

	});

}
function load() {
	//gapi.client.setApiKey(api_key);
	gapi.client.load('urlshortener', 'v1',function(){document.getElementById("output").innerHTML="";});
}


var services = angular.module('exampleApp.services', ['ngResource']);

services.factory('UserService', function($resource) {

	return $resource('rest/user/:action', {},
			{
				authenticate: {
					method: 'POST',
					params: {'action' : 'authenticate'},
					headers : {'Content-Type': 'application/x-www-form-urlencoded'}
				},
				signin: {
					method: 'PUT',
					params: {'action' : 'new'},
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

services.factory('UrlShortener',function($q, $rootScope){


});

app.directive('myDirectiveWithRestriction', function () {
	return {
		restrict: 'EA',
		scope: {
			tasks: '='
		},
		controller: function ($scope){
			$scope.uploader.onCompleteItem = function(fileItem, response) {
				//console.info('onCompleteItem', fileItem, response);
				fileItem.index = response.id;

				makeShortFull();
				makeShortMedium();
				makeShortLow();

				console.info('onCompleteItem2', fileItem, response);
			};
		}
	};
});