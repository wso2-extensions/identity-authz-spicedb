# identity-authz-spicedb

This extension can be used to communicate with 
[spiceDB authorization engine](https://authzed.com/docs/spicedb/getting-started/discovering-spicedb) 
using HTTP requests to enable fine-grained authorization for WSO2 Identity Server. This implementation enables the 
ability to perform authorization checks and manipulate authorization data including the authorization schema which 
defines the authorization model.

## Setting up

1. Install and set up a spiceDB instance using a way you prefer. Click 
[here](https://authzed.com/docs/spicedb/getting-started/install/macos) to see the available options and set up 
instructions.
2. Go to ``identity-authz-spicedb/components/org.wso2.carbon.identity.application.authz.spicedb/src/main/java/org/wso2/
carbon/identity/ application/authz/spicedb/constants/SpiceDbConstants.java``and change the Field ``BASE_URL`` to your 
base url. (If you are running on ``localhost:8443`` port this step is not necessary.)
3. Stay in the same file and add the gRPC pre shared key you created with the spiceDB instance to the ``PRE_SHARED_KEY``
field.
3. Build this repository and get the ``.jar`` file from ``identity-authz-spicedb\target``.
4. Add the ``.jar`` file to ``\repository\components\dropins`` folder in wso2is pack.
5. Restart WSO2 Identity Server.

## Building from the source

If you want to build **identity-authz-spicedb** from the source code:

1. Install Java 11 (or Java 17)
2. Install Apache Maven 3.x.x (https://maven.apache.org/download.cgi#)
3. Get a clone or download the source from this repository (https://github.com/wso2-extensions/identity-authz-spicedb)
4. Run the Maven command ``mvn clean install`` from the ``identity-authz-spicedb`` directory.