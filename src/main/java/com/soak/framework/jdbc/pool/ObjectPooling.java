package com.soak.framework.jdbc.pool;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will provide objecting pooling mechanism with hash tables locked and unlocked.
 * Hash table will contain the object and time of creation.
 * Locked table will hold the object in use and Unlocked table will hold unused objects.
 * @author viswa
 *
 * @param <Object>
 */
@SuppressWarnings("hiding")
public abstract class ObjectPooling<Object> {

	/**
	 * Configuration properties.
	 */
	private final Properties confProperties = null;
	/**
	 * Expiration to to expire object.
	 */
	private final long expirationTime =  100000 ;
	/**
	 * Minimum Connections.
	 */
	private final Integer minimumConnection = 5 ;

	/**
	 * Logger .
	 */
	private static Logger logger = null ;
	/**
	 * Hash tables to hold the used and unused objects.
	 */
	private final Hashtable<Object, Long> locked, unlocked;

	/**
	 * Constructor to Initialize the expiration and hash tables.
	 */
	public ObjectPooling() {
//		this.expirationTime = Long.parseLong(this.confProperties.getProperty("DB.CONNECTION.EXPIRATION.TIME"));
//		this.minimumConnection = Integer.parseInt(this.confProperties.getProperty("DB.CONNECTION.POOL.MINIMUM.SIZE"));
		this.locked = new Hashtable<Object, Long>();
		this.unlocked = new Hashtable<Object, Long>();
	}
	/**
	 * Abstract method to create objects.
	 * @return Object.
	 */
	protected abstract Object create();
	/**
	 * Validate the object .
	 * @param objPool .
	 * @return object valid or not.
	 */
	public abstract boolean validate(Object objPool);

	/**
	 * Expire the given object.
	 * @param objPool .
	 */
	public abstract void expire(Object objPool);

	/**
	 * Checking object available in the used Hash table and return the available object to use.
	 * @return Object.
	 */
	public final synchronized Object checkOut() {
		long now = System.currentTimeMillis();
		Object objPool=null;
		if (this.unlocked.size() > 0) {
			Enumeration<Object> enumObj = this.unlocked.keys();
			while (enumObj.hasMoreElements()) {
				objPool = enumObj.nextElement();
				//Checking object expired or not, and if expired removed from the unlocked table.
				if ((now - this.unlocked.get(objPool)) > this.expirationTime) {
					logger.info("Object Time expired creating new object");
					this.unlocked.remove(objPool);
					this.expire(objPool);
					objPool = null;
					if(this.unlocked.size() < this.minimumConnection){
						Object newObj = this.create();
						this.unlocked.put(newObj, System.currentTimeMillis());
					}
				}else {
					if (this.validate(objPool)) {
						this.unlocked.remove(objPool);
						this.locked.put(objPool, now);
						return (objPool);
					} else {
						this.unlocked.remove(objPool);
						this.expire(objPool);
						objPool = null;
					}
				}
			}
		}
		objPool=this.create();
		this.locked.put(objPool, now);
		return (objPool);
	}
	
	/**
	 * Removing object from the locked state to unlocked state once job completed.
	 * @param objPool .
	 */
	public final synchronized void checkIn(final Object objPool) {
		this.locked.remove(objPool);
		this.unlocked.put(objPool, System.currentTimeMillis());
	}
	
	/**
	 * Returning the hash table.
	 * @return Hashtable<Object, Long> .
	 */
	final Hashtable<Object, Long> getLockedHash(){
		return this.locked;
	}
	
}
