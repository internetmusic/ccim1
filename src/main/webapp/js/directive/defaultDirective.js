/* 
* Generated by
* 
*      _____ _          __  __      _     _
*     / ____| |        / _|/ _|    | |   | |
*    | (___ | | ____ _| |_| |_ ___ | | __| | ___ _ __
*     \___ \| |/ / _` |  _|  _/ _ \| |/ _` |/ _ \ '__|
*     ____) |   < (_| | | | || (_) | | (_| |  __/ |
*    |_____/|_|\_\__,_|_| |_| \___/|_|\__,_|\___|_|
*
* The code generator that works in many programming languages
*
*			https://www.skaffolder.com
*
*
* You can generate the code from the command-line
*       https://npmjs.com/package/skaffolder-cli
*
*       npm install -g skaffodler-cli
*
*   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *
*
* To remove this comment please upgrade your plan here: 
*      https://app.skaffolder.com/#!/upgrade
*
* Or get up to 70% discount sharing your unique link:
*       https://app.skaffolder.com/#!/register?friend=5e1982d8a4f4b55911b4d29f
*
* You will get 10% discount for each one of your friends
* 
*/

/**
 * Hide an element if current user do not have role
 * 
 * EXAMPLE: 
 * 
 * <div ng-auth="ADMIN"> Private content only for ADMIN </div>
 * <div ng-auth="['ADMIN', 'MY_ROLE'] "> Private content for ADMIN and MY_ROLE user </div>
 * 
 */
app.directive('ngAuth', ['AuthenticationService', function (AuthenticationService) {
    return {
        restrict: 'A',
        link: function (scope, element, attributes) {

        	if (!AuthenticationService.hasRole(attributes.ngAuth)) {
            	$(element).remove();
            }
        }
    };
}]);

/**
 * Convert string to number
 */
app.directive('stringToNumber', function() {
    return {
        restrict: 'EA',
        require: 'ngModel',
        scope: {
        },
        link: function(scope, iElement, iAttrs, ngModel) {
            ngModel.$formatters.push(function(value){
				return Number.parseInt(value);
			});
        }
    }
});

/**
 * Convert string to date
 */
app.directive('stringToDate', function() {
    return {
        restrict: 'EA',
        require: 'ngModel',
        scope: {
        },
        link: function(scope, iElement, iAttrs, ngModel) {
            ngModel.$formatters.push(function(value){
				return new Date(value);
			});
        }
    }
});


/**
 * Link works on every type of tag
 * 
 * EXAMPLE:
 * 
 * <button ng-link="/home">Go to Home</button>
 * 
 */
app.directive('ngLink', ['$location', function($location) {
    return {
        link: function(scope, element, attrs) {

            $(element).on('mousedown', function(e) {
            	if( e.which == 2 ){
            		e.preventDefault();
            		e.stopPropagation();
            	}
            });
            
            $(element).on('mouseup', function(e) {
            	if( e.which == 3 ) return;
            	var params = {};
            	if (attrs.ngLinkParams)
            		params = JSON.parse(attrs.ngLinkParams);
            	
            	if( e.which == 1 ){
	                scope.$apply(function() {
	                    $location.path(attrs.ngLink).search(params);
	                });
            	}
            	
            	if( e.which == 2 ){
            		e.preventDefault();
            		e.stopPropagation();
            		window.open('#'+attrs.ngLink + "?" + $.param( params ));
            		return false;
            	}
            });
        }
    }
}]);