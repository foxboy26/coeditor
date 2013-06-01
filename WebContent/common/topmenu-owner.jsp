    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="index.jsp">XQL Shopping</a>
          <div class="nav-collapse collapse">
            <p class="navbar-text pull-right">
              <i class="icon-user icon-white"></i>
              <a href="#" class="navbar-link"><%= session.getAttribute("username") %></a>
            </p>
            <ul class="nav">
              <li id="nav-categories"><a href="categories.jsp">Categories</a></li>
              <li id="nav-products"><a href="products.jsp">Products</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
