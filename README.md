# identity-authz-spicedb

This extension can be used to communicate with 
[SpiceDB Authorization Engine](https://authzed.com/docs/spicedb/getting-started/discovering-spicedb) 
using HTTP requests to enable fine-grained authorization for WSO2 Identity Server. This implementation enables the 
ability to perform authorization checks and search objects(Resources, Subjects or Actions) from SpiceDB and send back 
to WSO2 Identity Server.

## Setting up

1. Install and set up a SpiceDB instance using a way you prefer. Click 
[here](https://authzed.com/docs/spicedb/getting-started/install/macos) to see the available options and set up 
instructions.
2. Go to `deployment.toml` file in the WSO2 Identity Server pack `([HOME]/repository/conf/deployment.toml)` and add the
    following configurations.
    
    ```toml
    [fgaEngineConfig]
    # The URL of the spiceDB instance (e.g. http://localhost:8443/)
    BasePath = "<base_path>"
    
    [fgaEngineConfig.authentication]
    # the gRPC Pre Shared Key to use when connecting to the spiceDB instance
    PreSharedKey ="Bearer <pre_shared_key>"
   ```
3. Build this repository and get the ``.jar`` file from ``components/org.wso2.carbon.identity.authz.spicedb/target``.
4. Add the ``.jar`` file to ``[HOME]/repository/components/dropins`` folder in Identity Server pack.
5. Restart WSO2 Identity Server.

## Building from the source

If you want to build **identity-authz-spicedb** from the source code:

1. Install Java 11 (or Java 17)
2. Install Apache Maven 3.x.x (https://maven.apache.org/download.cgi#)
3. Get a clone or download the source from this repository (https://github.com/wso2-extensions/identity-authz-spicedb)
4. Run the Maven command ``mvn clean install`` from the ``identity-authz-spicedb`` directory.