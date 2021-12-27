//package java.net;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class AddressCache {
//    /**
//     * When the cache contains more entries than this, we start dropping the oldest ones.
//     * This should be a power of two to avoid wasted space in our custom map.
//     */
//    private static final int MAX_ENTRIES = 10;
//
//    // The TTL for the Java-level cache is short, just 2s.
//    private static final long TTL_NANOS = 2 * 1000000000L;
//
//    // The actual cache.
//
//    private final BasicLruCache<AddressCacheKey, AddressCacheEntry> cache
//            = new BasicLruCache<AddressCacheKey, AddressCacheEntry>(MAX_ENTRIES);
//
//    static class AddressCacheKey {
//
//        private final String mHostname;
//        private final int mNetId;
//
//        AddressCacheKey(String hostname, int netId) {
//            mHostname = hostname;
//            mNetId = netId;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) {
//                return true;
//            }
//            if (!(o instanceof AddressCacheKey)) {
//                return false;
//            }
//            AddressCacheKey lhs = (AddressCacheKey) o;
//            return mHostname.equals(lhs.mHostname) && mNetId == lhs.mNetId;
//        }
//
//        @Override
//        public int hashCode() {
//            int result = 17;
//            result = 31 * result + mNetId;
//            result = 31 * result + mHostname.hashCode();
//            return result;
//        }
//    }
//
//    static class AddressCacheEntry {
//        // Either an InetAddress[] for a positive entry,
//        // or a String detail message for a negative entry.
//
//        final Object value;
//
//        /**
//         * The absolute expiry time in nanoseconds. Nanoseconds from System.nanoTime is ideal
//         * because -- unlike System.currentTimeMillis -- it can never go backwards.
//         * <p>
//         * We don't need to worry about overflow with a TTL_NANOS of 2s.
//         */
//        final long expiryNanos;
//
//
//        AddressCacheEntry(Object value) {
//            this.value = value;
//            this.expiryNanos = System.nanoTime() + TTL_NANOS;
//        }
//    }
//
//    /**
//     * Removes all entries from the cache.
//     */
//    public void clear() {
//        cache.evictAll();
//    }
//
//    /**
//     * Returns the cached InetAddress[] for 'hostname' on network 'netId'. Returns null
//     * if nothing is known about 'hostname'. Returns a String suitable for use as an
//     * UnknownHostException detail message if 'hostname' is known not to exist.
//     */
//    public Object get(String hostname, int netId) {
//        AddressCacheEntry entry = cache.get(new AddressCacheKey(hostname, netId));
//        // Do we have a valid cache entry?
//        if (entry != null && entry.expiryNanos >= System.nanoTime()) {
//            return entry.value;
//        }
//        // Either we didn't find anything, or it had expired.
//        // No need to remove expired entries: the caller will provide a replacement shortly.
//        return null;
//    }
//
//    /**
//     * Associates the given 'addresses' with 'hostname'. The association will expire after a
//     * certain length of time.
//     */
//    public void put(String hostname, int netId, InetAddress[] addresses) {
//        cache.put(new AddressCacheKey(hostname, netId), new AddressCacheEntry(addresses));
//    }
//
//    /**
//     * Records that 'hostname' is known not to have any associated addresses. (I.e. insert a
//     * negative cache entry.)
//     */
//    public void putUnknownHost(String hostname, int netId, String detailMessage) {
//        cache.put(new AddressCacheKey(hostname, netId), new AddressCacheEntry(detailMessage));
//    }
//
//
//    public class BasicLruCache<K, V> {
//        private final LinkedHashMap<K, V> map;
//        private final int maxSize;
//
//        public BasicLruCache(int maxSize) {
//            if (maxSize <= 0) {
//                throw new IllegalArgumentException("maxSize <= 0");
//            }
//            this.maxSize = maxSize;
//            this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
//        }
//
//        /**
//         * Returns the value for {@code key} if it exists in the cache or can be
//         * created by {@code #create}. If a value was returned, it is moved to the
//         * head of the queue. This returns null if a value is not cached and cannot
//         * be created.
//         */
//        public final V get(K key) {
//            if (key == null) {
//                throw new NullPointerException("key == null");
//            }
//
//            V result;
//            synchronized (this) {
//                result = map.get(key);
//                if (result != null) {
//                    return result;
//                }
//            }
//
//            // Don't hold any locks while calling create.
//            result = create(key);
//
//            synchronized (this) {
//                // NOTE: Another thread might have already inserted a value for |key| into the map.
//                // This shouldn't be an observable change as long as create creates equal values for
//                // equal keys. We will however attempt to trim the map twice, but that shouldn't be
//                // a big deal since uses of this class aren't heavily contended (and this class
//                // isn't design for such usage anyway).
//                if (result != null) {
//                    map.put(key, result);
//                    trimToSize(maxSize);
//                }
//            }
//
//            return result;
//        }
//
//        /**
//         * Caches {@code value} for {@code key}. The value is moved to the head of
//         * the queue.
//         *
//         * @return the previous value mapped by {@code key}. Although that entry is
//         * no longer cached, it has not been passed to {@link #entryEvicted}.
//         */
//        public synchronized final V put(K key, V value) {
//            if (key == null) {
//                throw new NullPointerException("key == null");
//            } else if (value == null) {
//                throw new NullPointerException("value == null");
//            }
//
//            V previous = map.put(key, value);
//            trimToSize(maxSize);
//            return previous;
//        }
//
//        private void trimToSize(int maxSize) {
////            while (map.size() > maxSize) {
////                Map.Entry<K, V> toEvict = map.eldest();
////
////                K key = toEvict.getKey();
////                V value = toEvict.getValue();
////                map.remove(key);
////
////                entryEvicted(key, value);
////            }
//        }
//
//        /**
//         * Called for entries that have reached the tail of the least recently used
//         * queue and are be removed. The default implementation does nothing.
//         */
//        protected void entryEvicted(K key, V value) {
//        }
//
//        /**
//         * Called after a cache miss to compute a value for the corresponding key.
//         * Returns the computed value or null if no value can be computed. The
//         * default implementation returns null.
//         */
//        protected V create(K key) {
//            return null;
//        }
//
//        /**
//         * Returns a copy of the current contents of the cache, ordered from least
//         * recently accessed to most recently accessed.
//         */
//        public synchronized final Map<K, V> snapshot() {
//            return new LinkedHashMap<K, V>(map);
//        }
//
//        /**
//         * Clear the cache, calling {@link #entryEvicted} on each removed entry.
//         */
//        public synchronized final void evictAll() {
//            trimToSize(0);
//        }
//    }
//}
//
//
//
//
