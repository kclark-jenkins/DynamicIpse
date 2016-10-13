## Synopsis

This is a sample ICSE that can be used in conjuction with my OTCookbook project.  This allows for you to pass any number of request parameters to the ICSE which can later be recalled.

## Installation

1. Stop the iHub

2. Place "dynamicicse.jar" in C:\OpenText\InformationHub\modules\BIRTiHub\iHub\web\birtservice\WEB-INF\lib

3. Download Gson and place it in C:\OpenText\InformationHub\modules\BIRTiHub\iHub\web\birtservice\WEB-INF\lib

4. Add the following entry to iHub's web.xml at C:\OpenText\InformationHub\modules\BIRTiHub\iHub\web\birtservice\WEB-INF\web.xml
```xml
<param-name>SECURITY_ADAPTER_CLASS</param-name>
<param-value>com.actuate.sample.icse.DynamicIcse</param-value>
</context-param>
```

Start the iHub, As long as you have OTCookbook installed you can verify this is working by navigating to

```
http://localhost:8700/iportal/dashboard/jsp/myfiles.jsp?__vp=Default%20Volume&vol=Default%20Volume&uname=connorjay&connectionString=jdbc:mysql:test&testParameter1=value1&testParameter2=value2
```
