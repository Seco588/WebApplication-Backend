package it.tss.pw.secondi;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing
 * JAX-RS resources in your application.
 *
 * @author airhacks.com
 */

@LoginConfig(authMethod = "MP-JWT" , realmName = "MP-JWT")
@DeclareRoles({"users"})
@ApplicationPath("resources")
public class JAXRSConfiguration extends Application {

}
