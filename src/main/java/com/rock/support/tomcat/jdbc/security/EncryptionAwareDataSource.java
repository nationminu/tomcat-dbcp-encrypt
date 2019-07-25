package com.rock.support.tomcat.jdbc.security;

 
import java.util.Hashtable;
import java.util.Properties;
 
import javax.naming.Context; 
import javax.naming.Name; 
import javax.naming.RefAddr;
import javax.naming.Reference; 

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory; 
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import com.rock.support.encrypt.Seed; 

public class EncryptionAwareDataSource extends DataSourceFactory{

    private static final Log log = LogFactory.getLog(EncryptionAwareDataSource.class);
    
    protected static final String PROP_DEFAULTAUTOCOMMIT = "defaultAutoCommit";
    protected static final String PROP_DEFAULTREADONLY = "defaultReadOnly";
    protected static final String PROP_DEFAULTTRANSACTIONISOLATION = "defaultTransactionIsolation";
    protected static final String PROP_DEFAULTCATALOG = "defaultCatalog";

    protected static final String PROP_DRIVERCLASSNAME = "driverClassName";
    protected static final String PROP_PASSWORD = "password";
    protected static final String PROP_URL = "url";
    protected static final String PROP_USERNAME = "username";

    protected static final String PROP_MAXACTIVE = "maxActive";
    protected static final String PROP_MAXIDLE = "maxIdle";
    protected static final String PROP_MINIDLE = "minIdle";
    protected static final String PROP_INITIALSIZE = "initialSize";
    protected static final String PROP_MAXWAIT = "maxWait";
    protected static final String PROP_MAXAGE = "maxAge";

    protected static final String PROP_TESTONBORROW = "testOnBorrow";
    protected static final String PROP_TESTONRETURN = "testOnReturn";
    protected static final String PROP_TESTWHILEIDLE = "testWhileIdle";
    protected static final String PROP_TESTONCONNECT = "testOnConnect";
    protected static final String PROP_VALIDATIONQUERY = "validationQuery";
    protected static final String PROP_VALIDATIONQUERY_TIMEOUT = "validationQueryTimeout";
    protected static final String PROP_VALIDATOR_CLASS_NAME = "validatorClassName";

    protected static final String PROP_NUMTESTSPEREVICTIONRUN = "numTestsPerEvictionRun";
    protected static final String PROP_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    protected static final String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";

    protected static final String PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED = "accessToUnderlyingConnectionAllowed";

    protected static final String PROP_REMOVEABANDONED = "removeAbandoned";
    protected static final String PROP_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    protected static final String PROP_LOGABANDONED = "logAbandoned";
    protected static final String PROP_ABANDONWHENPERCENTAGEFULL = "abandonWhenPercentageFull";

    protected static final String PROP_POOLPREPAREDSTATEMENTS = "poolPreparedStatements";
    protected static final String PROP_MAXOPENPREPAREDSTATEMENTS = "maxOpenPreparedStatements";
    protected static final String PROP_CONNECTIONPROPERTIES = "connectionProperties";

    protected static final String PROP_INITSQL = "initSQL";
    protected static final String PROP_INTERCEPTORS = "jdbcInterceptors";
    protected static final String PROP_VALIDATIONINTERVAL = "validationInterval";
    protected static final String PROP_JMX_ENABLED = "jmxEnabled";
    protected static final String PROP_FAIR_QUEUE = "fairQueue";

    protected static final String PROP_USE_EQUALS = "useEquals";
    protected static final String PROP_USE_CON_LOCK = "useLock";

    protected static final String PROP_DATASOURCE= "dataSource";
    protected static final String PROP_DATASOURCE_JNDI = "dataSourceJNDI";

    protected static final String PROP_SUSPECT_TIMEOUT = "suspectTimeout";

    protected static final String PROP_ALTERNATE_USERNAME_ALLOWED = "alternateUsernameAllowed";

    protected static final String PROP_COMMITONRETURN = "commitOnReturn";
    protected static final String PROP_ROLLBACKONRETURN = "rollbackOnReturn";

    protected static final String PROP_USEDISPOSABLECONNECTIONFACADE = "useDisposableConnectionFacade";

    protected static final String PROP_LOGVALIDATIONERRORS = "logValidationErrors";

    protected static final String PROP_PROPAGATEINTERRUPTSTATE = "propagateInterruptState";

    protected static final String PROP_IGNOREEXCEPTIONONPRELOAD = "ignoreExceptionOnPreLoad";

    protected static final String PROP_USESTATEMENTFACADE = "useStatementFacade";

    public static final int UNKNOWN_TRANSACTIONISOLATION = -1;

    public static final String OBJECT_NAME = "object_name";
 
    protected static final String[] ALL_PROPERTIES = {
        PROP_DEFAULTAUTOCOMMIT,
        PROP_DEFAULTREADONLY,
        PROP_DEFAULTTRANSACTIONISOLATION,
        PROP_DEFAULTCATALOG,
        PROP_DRIVERCLASSNAME,
        PROP_MAXACTIVE,
        PROP_MAXIDLE,
        PROP_MINIDLE,
        PROP_INITIALSIZE,
        PROP_MAXWAIT,
        PROP_TESTONBORROW,
        PROP_TESTONRETURN,
        PROP_TIMEBETWEENEVICTIONRUNSMILLIS,
        PROP_NUMTESTSPEREVICTIONRUN,
        PROP_MINEVICTABLEIDLETIMEMILLIS,
        PROP_TESTWHILEIDLE,
        PROP_TESTONCONNECT,
        PROP_PASSWORD,
        PROP_URL,
        PROP_USERNAME,
        PROP_VALIDATIONQUERY,
        PROP_VALIDATIONQUERY_TIMEOUT,
        PROP_VALIDATOR_CLASS_NAME,
        PROP_VALIDATIONINTERVAL,
        PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED,
        PROP_REMOVEABANDONED,
        PROP_REMOVEABANDONEDTIMEOUT,
        PROP_LOGABANDONED,
        PROP_POOLPREPAREDSTATEMENTS,
        PROP_MAXOPENPREPAREDSTATEMENTS,
        PROP_CONNECTIONPROPERTIES,
        PROP_INITSQL,
        PROP_INTERCEPTORS,
        PROP_JMX_ENABLED,
        PROP_FAIR_QUEUE,
        PROP_USE_EQUALS,
        OBJECT_NAME,
        PROP_ABANDONWHENPERCENTAGEFULL,
        PROP_MAXAGE,
        PROP_USE_CON_LOCK,
        PROP_DATASOURCE,
        PROP_DATASOURCE_JNDI,
        PROP_SUSPECT_TIMEOUT,
        PROP_ALTERNATE_USERNAME_ALLOWED,
        PROP_COMMITONRETURN,
        PROP_ROLLBACKONRETURN,
        PROP_USEDISPOSABLECONNECTIONFACADE,
        PROP_LOGVALIDATIONERRORS,
        PROP_PROPAGATEINTERRUPTSTATE,
        PROP_IGNOREEXCEPTIONONPRELOAD,
        PROP_USESTATEMENTFACADE
    };

    @Override  
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                    Hashtable<?,?> environment) throws Exception { 
    	
        // We only know how to deal with <code>javax.naming.Reference</code>s
        // that specify a class name of "javax.sql.DataSource"
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference) obj;
        if (!"javax.sql.DataSource".equals(ref.getClassName())) {
            return null;
        }  

        Properties properties = new Properties();
        for (String propertyName : ALL_PROPERTIES) {
            RefAddr ra = ref.get(propertyName);
            if (ra != null) {
                String propertyValue = ra.getContent().toString();
        		  log.info("DBCP argument : " + propertyName + " = " + propertyValue);
                properties.setProperty(propertyName, propertyValue);
            }
        }
        
		String enc_password = null;
		String dec_password = null;
		
		enc_password = properties.getProperty(PROP_PASSWORD);
		//log.info("--------- value : " + value);

		if (enc_password != null) {
			dec_password = Seed.dec_word(enc_password);
			//log.info("--------- password : " + password);
			properties.setProperty(PROP_PASSWORD, dec_password);
		} 
 
		return createDataSource(properties);
    }
}
