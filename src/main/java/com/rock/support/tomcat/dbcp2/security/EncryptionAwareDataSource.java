package com.rock.support.tomcat.dbcp2.security;
  
import java.util.Hashtable; 
import java.util.Properties; 

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference; 

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory; 
  
import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;

import com.rock.support.encrypt.Seed; 

public class EncryptionAwareDataSource extends BasicDataSourceFactory{
 
    private static final Log log = LogFactory.getLog(EncryptionAwareDataSource.class);

    private static final String PROP_DEFAULTAUTOCOMMIT = "defaultAutoCommit";
    private static final String PROP_DEFAULTREADONLY = "defaultReadOnly";
    private static final String PROP_DEFAULTTRANSACTIONISOLATION = "defaultTransactionIsolation";
    private static final String PROP_DEFAULTCATALOG = "defaultCatalog";
    private static final String PROP_CACHESTATE ="cacheState";
    private static final String PROP_DRIVERCLASSNAME = "driverClassName";
    private static final String PROP_LIFO = "lifo";
    private static final String PROP_MAXTOTAL = "maxTotal";
    private static final String PROP_MAXIDLE = "maxIdle";
    private static final String PROP_MINIDLE = "minIdle";
    private static final String PROP_INITIALSIZE = "initialSize";
    private static final String PROP_MAXWAITMILLIS = "maxWaitMillis";
    private static final String PROP_TESTONCREATE = "testOnCreate";
    private static final String PROP_TESTONBORROW = "testOnBorrow";
    private static final String PROP_TESTONRETURN = "testOnReturn";
    private static final String PROP_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    private static final String PROP_NUMTESTSPEREVICTIONRUN = "numTestsPerEvictionRun";
    private static final String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    private static final String PROP_SOFTMINEVICTABLEIDLETIMEMILLIS = "softMinEvictableIdleTimeMillis";
    private static final String PROP_EVICTIONPOLICYCLASSNAME = "evictionPolicyClassName";
    private static final String PROP_TESTWHILEIDLE = "testWhileIdle";
    private static final String PROP_PASSWORD = "password";
    private static final String PROP_URL = "url";
    private static final String PROP_USERNAME = "username";
    private static final String PROP_VALIDATIONQUERY = "validationQuery";
    private static final String PROP_VALIDATIONQUERY_TIMEOUT = "validationQueryTimeout"; 

    /**
     * The property name for connectionInitSqls.
     * The associated value String must be of the form [query;]*
     */
    private static final String PROP_CONNECTIONINITSQLS = "connectionInitSqls";
    private static final String PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED = "accessToUnderlyingConnectionAllowed";
    private static final String PROP_REMOVEABANDONEDONBORROW = "removeAbandonedOnBorrow";
    private static final String PROP_REMOVEABANDONEDONMAINTENANCE = "removeAbandonedOnMaintenance";
    private static final String PROP_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    private static final String PROP_LOGABANDONED = "logAbandoned";
    private static final String PROP_POOLPREPAREDSTATEMENTS = "poolPreparedStatements";
    private static final String PROP_MAXOPENPREPAREDSTATEMENTS = "maxOpenPreparedStatements";
    private static final String PROP_CONNECTIONPROPERTIES = "connectionProperties";
    private static final String PROP_MAXCONNLIFETIMEMILLIS = "maxConnLifetimeMillis";
    private static final String PROP_LOGEXPIREDCONNECTIONS = "logExpiredConnections";
    private static final String PROP_ROLLBACK_ON_RETURN = "rollbackOnReturn";
    private static final String PROP_ENABLE_AUTOCOMMIT_ON_RETURN = "enableAutoCommitOnReturn";
    private static final String PROP_DEFAULT_QUERYTIMEOUT = "defaultQueryTimeout";
    private static final String PROP_FASTFAIL_VALIDATION = "fastFailValidation";

    /**
     * Value string must be of the form [STATE_CODE,]*
     */
    private static final String PROP_DISCONNECTION_SQL_CODES = "disconnectionSqlCodes";
 

    private static final String[] ALL_PROPERTIES = {
        PROP_DEFAULTAUTOCOMMIT,
        PROP_DEFAULTREADONLY,
        PROP_DEFAULTTRANSACTIONISOLATION,
        PROP_DEFAULTCATALOG,
        PROP_CACHESTATE,
        PROP_DRIVERCLASSNAME,
        PROP_LIFO,
        PROP_MAXTOTAL,
        PROP_MAXIDLE,
        PROP_MINIDLE,
        PROP_INITIALSIZE,
        PROP_MAXWAITMILLIS,
        PROP_TESTONCREATE,
        PROP_TESTONBORROW,
        PROP_TESTONRETURN,
        PROP_TIMEBETWEENEVICTIONRUNSMILLIS,
        PROP_NUMTESTSPEREVICTIONRUN,
        PROP_MINEVICTABLEIDLETIMEMILLIS,
        PROP_SOFTMINEVICTABLEIDLETIMEMILLIS,
        PROP_EVICTIONPOLICYCLASSNAME,
        PROP_TESTWHILEIDLE,
        PROP_PASSWORD,
        PROP_URL,
        PROP_USERNAME,
        PROP_VALIDATIONQUERY,
        PROP_VALIDATIONQUERY_TIMEOUT,
        PROP_CONNECTIONINITSQLS,
        PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED,
        PROP_REMOVEABANDONEDONBORROW,
        PROP_REMOVEABANDONEDONMAINTENANCE,
        PROP_REMOVEABANDONEDTIMEOUT,
        PROP_LOGABANDONED,
        PROP_POOLPREPAREDSTATEMENTS,
        PROP_MAXOPENPREPAREDSTATEMENTS,
        PROP_CONNECTIONPROPERTIES,
        PROP_MAXCONNLIFETIMEMILLIS,
        PROP_LOGEXPIREDCONNECTIONS,
        PROP_ROLLBACK_ON_RETURN,
        PROP_ENABLE_AUTOCOMMIT_ON_RETURN,
        PROP_DEFAULT_QUERYTIMEOUT,
        PROP_FASTFAIL_VALIDATION,
        PROP_DISCONNECTION_SQL_CODES
    }; 
	
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?,?> environment)
        throws Exception { 
    	
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
