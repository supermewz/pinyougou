//首页控制器
app.controller('indexController',function($scope,loginService){
	// $scope.showName=function(){
    //
	// 		loginService.showName().success(
	// 				function(response){
	// 					$scope.loginName=response.loginName;
	// 				}
	// 		);
	// }


    $scope.findAll=function(){
		alert(111111111);
        loginService.findAll().success(
            function(response){
            	alert(11111111);
                $scope.orderppList=response;
            }
        );
    }

    // $scope.address=function(){
    //     alert(111111111);
    //     addressService.address().success(
    //         function(response){
    //             alert(11111111);
    //             $scope.orderppList=response;
    //         }
    //     );
    // }
});