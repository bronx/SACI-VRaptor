# VRaptor SACI Plugin

VRaptor SACI Plugin provides support to user/role control access 

# How to install?

You only need to copy the jar to your classpath. VRaptor will register plugin when 
your application starts without any configurations. Downloads are available in 
downloads area or in Maven Repository:
 
	<dependency>
	  <groupId>br.com.caelum.vraptor</groupId>
	  <artifactId>vraptor-plugin-saci</artifactId>
	  <version>1.1.0</version>
	</dependency>

# Configuration

Open the Deployment Descriptor (web.xml) and add the param below:

	<context-param>
	  <param-name>br.com.caelum.vraptor.packages</param-name>
	  <param-value>net.diegomaia.vraptor.saci</param-value>
	</context-param>
	
If you already have configured package, just separate them by commas:

	<context-param>
	  <param-name>br.com.caelum.vraptor.packages</param-name>
	  <param-value>another.packages,net.diegomaia.vraptor.saci</param-value>
	</context-param>
	
There it is! No further configuration to be made!

# How to use?

The next step is implement a component interface using the Profile of SACI. 
A simple example:

	@Component
	public class ProfileImpl implements Profile {

		@Override
		public int getAccessLevel() {
			return 0;
		}

		@Override
		public List<String> getRoles() {
			List<String> roles = new ArrayList<String>();
			roles.add("any_role");
			roles.add("other_role");
			return roles;
		}

		@Override
		public boolean isLoggedIn() {
			//always logged!
			return true;
		}
	}

We must configure where to redirect navigation in case of denied access, and where is the login page if the user not be logged.
To do so, we annotate the methods with @LoginPage and @AccessDeniedPage:

	@Resource
	public class AccessController {
	
		@LoginPage
		@Get("/login")
		public void login() {}
		
		@AccessDeniedPage
		@Get("/access/denied")
		public void accessDenied() {}
	}
	
If not specified, the restrictor simply returns the HTTP Code 403 - Forbiden.
Now we just have one thing: restrict access to our system!

To do so, we use the annotation @LoggedIn:

	@Resource
	public class OnlyLoggedUsersController {
		
		@LoggedIn
		@Get("/first/only/logged")
		public void onlyLoggedUsersCanAccessThisFirstMethod() {}
		
		@LoggedIn
		@Get("/second/only/logged")
		public void onlyLoggedUsersCanAccessThisSecondMethod() {}
	}
	
So, when a user is not logged in the system and try to access these methods, he will be redirected to the login page. 
If no login address has been specified, then will return HTTP 403 response code.
Try changing the method isLoggedIn() to false to see the feature in action!

	@Override
	public boolean isLoggedIn() {
		//Always logoff
		return false;
	}

If all the methods of the class are in the same restriction (@LoggedIn), we can let the restriction a little more generic:
	@LoggedIn
	@Resource
	public class OnlyLoggedUsersController {

		@Get("/first/only/logged")
		public void onlyLoggedUsersCanAccessThisFirstMethod() {}
		
		@Get("/second/only/logged")
		public void onlyLoggedUsersCanAccessThisSecondMethod() {}
	}
	
	
When using the @Role annotation, you restrict access to method in accordance with the role played by the currently logged in user in the return method by getRoles().

	@Resource
	public class OnlyManagersOrSupervisorsController {
	
		@Role(roles="managers,supervisors")
		public void onlyManagersOrSupervisorsCanAccessThisMethod() {}
		
		@Role(roles="managers")
		public void onlyManagersCanAccessThisMethod() {}
			
		@Role(roles="supervisors")
		public void onlySupervisorsCanAccessThisMethod() {}
	}	
	
If all the methods of the class are in the same role restriction, we can let the restriction a little more generic too:

	@Resource
	@Role(roles="managers")
	public class OnlyManagersOrSupervisorsController {
	
		public void onlyManagersCanAccessThisMethod() {}
		
		public void onlyManagersCanAccessThisOtherMethod() {}
	}	

And that is how you use the @Roles.

The annotation @AccessLevel based access control in a hierarchical structure, where access levels are defined by minimum and/or maximum to access a given resource.
Consider the following example:

	@Resource
	public class HierarchicalController {
	
		@AccessLevel(minimumAccessLevel=4)
		public void firstMethod() {}
		
		@AccessLevel(minimumAccessLevel=10)
		public void secondMethod() {}
		
		@AccessLevel(minimumAccessLevel=30)
		public void thirdMethod() {}
	}
	
In this case, we have the following restrictions:

- The "firstMethod()" can only be accessed by users who have access level greater than or equal to 4;
- The "secondMethod()" can only be accessed by users who have access level greater than or equal to 10;
- The "thirdMethod()" can only be accessed by users who have access level greater than or equal to 30;

We can let the restriction a little more generic too:

	@Resource
	@AccessLevel(minimumAccessLevel=4)
	public class HierarchicalController {
	
		public void firstMethod() {}
		
		@AccessLevel(minimumAccessLevel=10)
		public void secondMethod() {}
		
		@AccessLevel(minimumAccessLevel=30)
		public void thirdMethod() {}
	}

There is also the highest level of access, which lets you create a range of access levels allowed to access the method/feature:

	@Resource
	public class HierarchicalController {
	
		@AccessLevel(minimumAccessLevel=4, maximumAccessLevel=9)
		public void firstMethod() {}
		
		@AccessLevel(minimumAccessLevel=10, maximumAccessLevel=20)
		public void secondMethod() {}
		
		@AccessLevel(minimumAccessLevel=30, maximumAccessLevel=50)
		public void thirdMethod() {}
	}
	
There it is! This is the SACI.
		