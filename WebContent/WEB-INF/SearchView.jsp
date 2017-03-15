<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

<title>Home</title>

</head>

<body>

	<nav class="navbar navbar-inverse" role="navigation">

	<div class="navbar-collapse collapse">

		<ul class="nav navbar-nav navbar-left">

			<li><a href="">Home</a></li>

			<li><a href="">History</a></li>

		</ul>

	</div>

	</nav>

	<div class="container">

		<div class="col-md-12">

			<div class="jumbotron"
				style="background-color: rgba(120, 120, 120, 0.3)">

				<center>
					<h1>ENGINE KILLER</h1>
				</center>

			</div>

		</div>

		<div class="col-md-2"></div>

		<form action="SearchController" class="form-horizontal" method="post">
			<div class="col-md-8">

				<div class="input-group">

					<span class="input-group-addon" id="basic-addon2">Enter text
						to search</span> <input type="text" name="search" class="form-control">

				</div>

				<br>

				<center>

					<input type="submit" class="btn btn-primary" value="Search">

				</center>

			</div>
		</form>



	</div>
	<br>
		<div class="col-md-20">
			<c:forEach var="country" items="${result}">			 
				<a href="${country.key}"> ${country.key}</a>   ${country.value}<br> 
			</c:forEach>
		</div>
	


</body>

</html>