//package libcore.util;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Set;
//
//public class BasicLruCache<K, V> {
//    /**
//     * When the cache contains more entries than this, we start dropping the oldest ones.
//     * This should be a power of two to avoid wasted space in our custom map.
//     */
//
//    private final LinkedHashMap<K, V> map;
//    private final int maxSize;
//    private final HashMap<String, K> tempMap = new HashMap<>();
//
//    public BasicLruCache(int maxSize) {
//        if (maxSize <= 0) {
//            throw new IllegalArgumentException("maxSize <= 0");
//        }
//        this.maxSize = maxSize;
//        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
//    }
//
//    /**
//     * Returns the value for {@code key} if it exists in the cache or can be
//     * created by {@code #create}. If a value was returned, it is moved to the
//     * head of the queue. This returns null if a value is not cached and cannot
//     * be created.
//     */
//    public final V get(K key) {
//        if (key == null) {
//            throw new NullPointerException("key == null");
//        }
//        key = tempMap.get(hookHostName(key));
//        V result;
//        synchronized (this) {
//            result = map.get(key);
//            if (result != null) {
//
//                return result;
//            }
//        }
//
//        // Don't hold any locks while calling create.
//        result = create(key);
//
//        synchronized (this) {
//            // NOTE: Another thread might have already inserted a value for |key| into the map.
//            // This shouldn't be an observable change as long as create creates equal values for
//            // equal keys. We will however attempt to trim the map twice, but that shouldn't be
//            // a big deal since uses of this class aren't heavily contended (and this class
//            // isn't design for such usage anyway).
//            if (result != null) {
//                map.put(key, result);
//                trimToSize(maxSize);
//            }
//        }
//        return result;
//    }
//
//    /**
//     * Caches {@code value} for {@code key}. The value is moved to the head of
//     * the queue.
//     *
//     * @return the previous value mapped by {@code key}. Although that entry is
//     * no longer cached, it has not been passed to {@link #entryEvicted}.
//     */
//    public synchronized final V put(K key, V value) {
//        if (key == null) {
//            throw new NullPointerException("key == null");
//        } else if (value == null) {
//            throw new NullPointerException("value == null");
//        }
//        tempMap.put(hookHostName(key), key);
//        V previous = map.put(key, value);
//        trimToSize(maxSize);
//        return previous;
//    }
//
//    private String hookHostName(K key) {
//        Class AddressCacheKeyClass = null;
//        try {
//            AddressCacheKeyClass = Class.forName("java.net.AddressCache$AddressCacheKey");
//            if (AddressCacheKeyClass != null) {
//                Field expiryNanos = AddressCacheKeyClass.getDeclaredField("mHostname");
//                expiryNanos.setAccessible(true);
//                String hostName = (String) expiryNanos.get(key);
//                return hostName;
//            }
//        } catch (ClassNotFoundException | IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//
//    private void hookHostName2(V value) {
//        Class AddressCacheEntryClass = null;
//        try {
//            AddressCacheEntryClass = Class.forName("java.net.AddressCache$AddressCacheEntry");
//            if (AddressCacheEntryClass != null) {
//                Field expiryNanos = AddressCacheEntryClass.getDeclaredField("expiryNanos");
//                expiryNanos.setAccessible(true);
//                expiryNanos.set(value, Long.MAX_VALUE);
//            }
//        } catch (ClassNotFoundException | IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void trimToSize(int maxSize) {
////            while (map.size() > maxSize) {
////                Map.Entry<K, V> toEvict = map.eldest();
////
////                K key = toEvict.getKey();
////                V value = toEvict.getValue();
////                map.remove(key);
////
////                entryEvicted(key, value);
////            }
//    }
//
//    /**
//     * Called for entries that have reached the tail of the least recently used
//     * queue and are be removed. The default implementation does nothing.
//     */
//    protected void entryEvicted(K key, V value) {
//    }
//
//    /**
//     * Called after a cache miss to compute a value for the corresponding key.
//     * Returns the computed value or null if no value can be computed. The
//     * default implementation returns null.
//     */
//    protected V create(K key) {
//        return null;
//    }
//
//    /**
//     * Returns a copy of the current contents of the cache, ordered from least
//     * recently accessed to most recently accessed.
//     */
//    public synchronized final Map<K, V> snapshot() {
//        return new LinkedHashMap<K, V>(map);
//    }
//
//    /**
//     * Clear the cache, calling {@link #entryEvicted} on each removed entry.
//     */
//    public synchronized final void evictAll() {
//        trimToSize(0);
//    }
//
//}
//
//
//
//
