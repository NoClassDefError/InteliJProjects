##Problem occurs during creating a servlet
###1 decription
tomcat log:
```
28-Feb-2019 19:09:32.368 信息 [RMI TCP Connection(5)-127.0.0.1] org.apache.jasper.servlet.TldScanner.scanJars At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.

28-Feb-2019 19:09:32.513 严重 [RMI TCP Connection(6)-127.0.0.1] org.apache.catalina.core.ContainerBase.addChildInternal ContainerBase.addChild: start: 
```
Analysation shows that the problem is not caused by the servlet configuration but the content of the method `doPost` itself.
According to the test, the invoking of certain classes in the commons-fileupload.jar caused this error.
<br>
```
ServletFileUpload sfu = new ServletFileUpload(factory);
```
###2
Tomcat could start and deploy the project yet report an error of NoClassDefFoundError for the class org/apache/commons/io/IOUtils

```
ServletInputStream in = request.getInputStream();
String s = IOUtils.toString(in);
System.out.println(s);
```

###3
As I switched a workspace to find out whether it is the old project environment that went wrong, but it stayed its original circumstances.
Actually it's the need of the commons jar file that has caused this problem, since it has announced a FileNotFoundException below:
```
Caused by: java.lang.ClassNotFoundException: org.apache.commons.fileupload.FileUploadException
   	at org.apache.catalina.loader.WebappClassLoaderBase.loadClass(WebappClassLoaderBase.java:1309)
   	at org.apache.catalina.loader.WebappClassLoaderBase.loadClass(WebappClassLoaderBase.java:1138)
   	... 55 more
```
Another important thing that I should remember is that, to correctly create an java ee module in intellij, I should not have check in the "java ee application" box but the "Web application" box
so that it would create a web.xml automatically. If I had check in the first one, the tomcat would not show the index.jsp but a 404 error page instead.

###4 solution
There is finally a solution found on StackOverFLow.com:<br>

You need to put commons-fileupload.jar and commons-io.jar to your WEB-INF/lib forlder. <i>Classpath is used during build, but they must be available during runtime also.</i><p>
Actually, if you had used IDE for Java EE development (like Eclipse) putting these Jars to WEB-INF/lib would be enough, as they would be automatically visible in the claspath for build.
<p>
After i had create the lib directory and change the classpath, the IDE remind me to add the jar files to the "artifact".<br>
 
artifact是一种用于装载项目资产以便于测试，部署，或者分布式软件的解决方案。例如集中编译class，存档java应用包，web程序作为目录结构，或者web程序存档等。

##Problem occurs while the servlet reading the upload file
###1 the fileItemList turns out to be 0 in size
Then I decided to use the servlet to read and print out the whole httpServletRequest:
```$xslt
        BufferedReader br = request.getReader();
        String str;
        StringBuilder wholeStr = new StringBuilder();
        while((str = br.readLine()) != null){
            wholeStr.append(str);
        }
        System.out.println(wholeStr);
```
Unfortunately, i encountered exception:
```$xslt
java.lang.IllegalStateException: getReader() has already been called for this request
	org.apache.catalina.connector.Request.getInputStream(Request.java:1049)
	org.apache.catalina.connector.RequestFacade.getInputStream(RequestFacade.java:365)
	org.apache.commons.fileupload.servlet.ServletRequestContext.getInputStream(ServletRequestContext.java:115)
```
Looks like the restlet framework has called getRequestEntityStream() on the Request object which in turn calls getInputStream(), so calling getReader() on the request throws IllegalStateException. The Servlet API documentation for getReader() and getInputStream() says:
```
public java.io.BufferedReader getReader()
    ...
    ...
Throws:
    java.lang.IllegalStateException - if getInputStream() method has been called on this request

public ServletInputStream getInputStream()
    ...
    ...
    Throws:
    java.lang.IllegalStateException - if the getReader() method has already been called for this request
```
 
From the documentation it seems that we cannot call both getReader() and getInputStream() on the Request object. I suggest you use getInputStream() rather than getReader() in your wrapper.
The reason that the filelist is empty is that I forgot to set a name property for the `form`attribute.
###2 the usage of the JSTL 
The first notable JSTL
###3 jsp includes
Not only a jsp file could include another jsp file but also could include a servlet.
Assuming that the url-patterns of the servlet is "/image", then the include sentence should be looked like this:
```
<jsp:include page='<%="/image"%>'>    --static inclusion
<%@ include file="">    --dynamic inclusion
```
However there is difference between the static inclusion and the dynamic inclusion.
How do we parse request while performing a dynamic inclusion?
```
<jsp:include page="included2.jsp" flush="true">    
     <jsp:param name="ref1" value="AAA"/>
     <jsp:param name="ref2" value="BBB"/>
</jsp:include>
```
When we need to change the attribute 'value' using java sentences, there would be a exception:org.apache.jasper.JasperException: /image.jsp (line: [15], column: [41]) Attribute value [request.getParameter("filename") ] is quoted with ["] which must be escaped when used within the value
```
 value="<%=request.getParameter("filename") %>"
```
the recommand style is like this:
```
value=‘<%=""+request.getAttribute("name")%>‘
```
###4 view pictures online
###5 view videos online
A video is much larger than a image and would throw an exception of "the header is too large", so we need a new html5 tag `<video>`.

html5中的video标签可以很容易的解决，但是当我在项目中的jsp页面中使用时，部署后却无法播放。于是我将jsp生成的页面源码复制到一个html文件中是却可以使用。在网上也找了一些资料，后来怀疑是路径问题，就做了一些测试，下面是结果总结：

html5的video标签的src属性支持绝对路径和相对路径，但前提是一个单独存在的html文档，如果是部署到服务器上的话使用绝对路径就无法播放，需要改成相对路径。

试图采用向项目文件夹复制的方法，但项目文件夹拒绝访问
```
FileOutputStream outputStream = new FileOutputStream(request.getSession().getServletContext().getRealPath("/"));
IOUtils.copy(fileInputStream, outputStream);
```
报错:
```
java.io.FileNotFoundException: C:\Users\DELL\Desktop\code\InteliJProjects\out\artifacts\untitled_war_exploded (拒绝访问。)
```
此处的项目文件夹指的是out文件夹的字节码文件所在的文件夹，其中只有一个WEB_INF子目录。
但在uploadServlet中上传时，项目文件夹是可以访问，并可以向其中复制文件的。

若要使得jsp能够访问项目文件夹以外的目录，则应当在tomcat中配置虚拟路径映射。
###6 encoding problems about the get request
According to the RFC regulations, the url contains english letters, numbers and `! * ’ ( ) ; : @ & = + $ , / ? # [ ] - _ . ~` only, the chinese characters are not included.<br>
So, how are we supposed to redirect, paring request containing Chinese characters?
Since there are no method for a post redirect, we can actually write a hidden form tag in the servlet, and use it to redirect in the post way.
###7 zip4j

###8 display the upload process
