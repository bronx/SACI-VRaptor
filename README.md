# VRaptor SACI Plugin

O SACI oferece suporte a controle de acesso baseado em usuário/roles, além de níveis de acesso hierárquicos

SACI = Simple Access Controller Interface 

# Como instalar?

Você só precisa copiar o jar para o classpath da sua aplicação. Se estiver utilizando um servlet container 
que implemente Servlet 3.0+, o VRaptor registrará o SACI sem necessidade de mais nenhuma configuração.

Caso esteja utilizando o Maven, adicione a seguinte dependência no arquivo pom.xml da sua aplicação:
 
	<dependency>
	  <groupId>net.diegomaia.vraptor</groupId>
	  <artifactId>saci</artifactId>
	  <version>1.1</version>
	</dependency>

# Configuração em um container Servlet 2.5

Abra o Deployment Descriptor (web.xml) e adicione o parâmetro abaixo:

	<context-param>
	  <param-name>br.com.caelum.vraptor.packages</param-name>
	  <param-value>net.diegomaia.vraptor.saci</param-value>
	</context-param>
	
Se você já possui pacotes configurados, separe-os por vírgulas:

	<context-param>
	  <param-name>br.com.caelum.vraptor.packages</param-name>
	  <param-value>
	      um.pacote,
	      outro.pacote,
	      net.diegomaia.vraptor.saci
	  </param-value>
	</context-param>
	
Pronto! 

# Como usar?

O próximo passo é criar um componente que implemente a interface Profile que vem com o SACI. 
Um exemplo simples:

	@Component
	public class Perfil implements Profile {

		@Override
		public int getAccessLevel() {
			return 0;
		}

		@Override
		public List<String> getRoles() {
			List<String> roles = new ArrayList<String>();
			roles.add("um_role");
			roles.add("outro_role");
			return roles;
		}

		@Override
		public boolean isLoggedIn() {
			//sempre logado!
			//aqui você poderia veridicar se a sessão ainda está ativa. Fica a seu critéria! ;D
			return true;
		}
	}

Nós podemos configurar qual será a página de login, e para onde devemos redirecionar a navegação em caso de acesso negado.
Para fazer isso, basta anotar os métodos com @LoginPage e @AccessDeniedPage:

	@Resource
	public class AcessoController {
	
		@LoginPage
		@Get("/login")
		public void login() {}
		
		@AccessDeniedPage
		@Get("/access/denied")
		public void accessDenied() {}
	}
	
Se não forem especificadas, o SACI simplesmente retorna um HTTP Code 403 - Forbiden.
Agora só resta fazer mais uma coisa: restrigir o acesso ao sistema! 

### Com @LoggedIn

Podemos utilizar a annotation @LoggedIn:

	@Resource
	public class SomenteUsuarioLogadoController {
		
		@LoggedIn
		public void apenasUsuariosLogadosPodemMeAcessar() {}
		
		@LoggedIn
		public void apenasUsuariosLogadosPodemMeAcessarTambem() {}
	}
	
Com isso, quando um usuário que não estiver logado no sistema tentar acessar a página, ele será redirecionado para a página de login. 
Se você não especificar uma página de login, o SACI retornará HTTP 403.
Experimente mudar o método isLoggedIn() para ver essa feature em ação:

	@Override
	public boolean isLoggedIn() {
		//nunca logado!
		return false;
	}

Se todos os métodos da classe possuem a mesma restrição (@LoggedIn), podemos aplicá-la para a classe toda:
	@LoggedIn
	@Resource
	public class SomenteUsuarioLogadoController {
		
		public void apenasUsuariosLogadosPodemMeAcessar() {}
		
		public void apenasUsuariosLogadosPodemMeAcessarTambem() {}
	}
	
### Com @Role
	
Usando a anotação @Role, você restringe o acesso de acordo com os papéis desempenhados pelo usuário logado (retornado pelo método getRoles() da interface Profile).

	@Resource
	public class SomenteGerenteOuSupervisorController {
	
		@Role(roles="gerente,supervisor")
		public void somenteGerentesOuSupervisoresPodemMeAcessar() {}
		
		@Role(roles="gerente")
		public void somenteGerentesPodemMeAcessar() {}
			
		@Role(roles="supervisor")
		public void somenteSupervisoresPodemMeAcessar() {}
	}	
	
Se todos os métodos possuem as mesmas restrições, podemos anotar a classe:

	@Resource
	@Role(roles="gerente")
	public class SomenteGerenteController {
	
		public void somenteGerentesPodemMeAcessar() {}
		
		public void somenteGerentesPodemMeAcessarTambem() {}
	}	

E é assim que utilizamos o @Roles.

### Com @AccessLevel

A anotação @AccessLevel restringe o acesso de forma hierárquica, definindo níveis de acesso mínimos e/ou máximos para um determinado método/recurso.
Considere o seguinte exemplo:

	@Resource
	public class HierarchicalController {
	
		@AccessLevel(minimumAccessLevel=4)
		public void primeiroMetodo() {}
		
		@AccessLevel(minimumAccessLevel=10)
		public void segundoMetodo() {}
		
		@AccessLevel(minimumAccessLevel=30)
		public void terceiroMetodo() {}
	}
	
Neste caso, temos as seguintes restrições de acesso:

- O método "primeiroMetodo()" só pode ser acessado por usuários que possuam nível de acesso maior ou igual a 4;
- O método "segundoMetodo()" só pode ser acessado por usuários que possuam nível de acesso maior ou igual a 10;
- O método "terceiroMetodo()" só pode ser acessado por usuários que possuam nível de acesso maior ou igual a 30;

Podemos generalizar as restrições para a classe:

	@Resource
	@AccessLevel(minimumAccessLevel=4)
	public class HierarchicalController {
	
		public void primeiroMetodo() {}
		
		@AccessLevel(minimumAccessLevel=10)
		public void segundoMetodo() {}
		
		@AccessLevel(minimumAccessLevel=30)
		public void terceiroMetodo() {}
	}

Há também o nível máximo de acesso, o que permite criar intervalos de níveis de acesso:

	@Resource
	public class HierarchicalController {
	
		@AccessLevel(minimumAccessLevel=4, maximumAccessLevel=9)
		public void primeiroMetodo() {}
		
		@AccessLevel(minimumAccessLevel=10, maximumAccessLevel=20)
		public void segundoMetodo() {}
		
		@AccessLevel(minimumAccessLevel=30, maximumAccessLevel=50)
		public void terceiroMetodo() {}
	}
	
É isso! esse é o SACI. =D
		


### ENGLISH

# VRaptor SACI Plugin

VRaptor SACI Plugin provides support to user/role control access

SACI = Simple Access Control Interface 

# How to install?

You only need to copy the jar to your classpath. VRaptor will register plugin when 
your application starts without any configurations. 
Downloads are available in downloads area or in Maven Repository:
 
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

### By LoggedIn

To do so, we can use the annotation @LoggedIn:

	@Resource
	public class OnlyLoggedUsersController {
		
		@LoggedIn
		public void onlyLoggedUsersCanAccessThisFirstMethod() {}
		
		@LoggedIn
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

		public void onlyLoggedUsersCanAccessThisFirstMethod() {}
		
		public void onlyLoggedUsersCanAccessThisSecondMethod() {}
	}
	
### By Role
	
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

### By AccessLevel

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
		