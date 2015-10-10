package org.cacheonix.impl.cache.distributed.partitioned;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.cacheonix.cache.Cache;
import org.cacheonix.cache.subscriber.EntryModifiedSubscriber;
import org.cacheonix.impl.cache.distributed.partitioned.subscriber.LocalSubscription;
import org.cacheonix.impl.cache.item.Binary;
import org.cacheonix.impl.cache.store.BinaryEntryModifiedSubscriberAdapter;
import org.cacheonix.impl.net.ClusterNodeAddress;
import org.cacheonix.impl.net.processor.RequestProcessor;
import org.cacheonix.impl.util.array.IntHashSet;

/**
 * A context for cache requests.
 */
public interface CacheProcessor extends RequestProcessor {

   /**
    * Operational state. It means that the cluster node is operating normally and that cache requests may operate on the
    * cache processor's state normally.
    */
   int STATE_OPERATIONAL = 0;

   /**
    * Blocked state. It means that the cluster node is undergoing re-configuration and that cache requests should return
    * retry instead of trying to operate on the cache state.
    */
   int STATE_BLOCKED = 1;

   /**
    * Sets this cache processor's state.
    *
    * @param state the state to set.
    * @see #STATE_BLOCKED
    * @see #STATE_OPERATIONAL
    */
   void setState(int state);

   /**
    * Returns this cache processor's state.
    *
    * @return this cache processor's state.
    * @see #STATE_BLOCKED
    * @see #STATE_OPERATIONAL
    */
   int getState();

   /**
    * Returns a cache name this processor serves.
    *
    * @return the cache name this processor serves.
    */
   String getCacheName();

   /**
    * Returns an executor used to execute event notifications outside of the processor's loop.
    *
    * @return the executor used to execute event notifications outside of the processor's loop.
    */
   Executor getEventNotificationExecutor();

   FrontCache getFrontCache();

   ClusterNodeAddress getBucketOwner(int storageNumber, int bucketNumber);

   int getBucketOwnerCount();

   Bucket createBucket(int storageNumber, Integer bucketNumber);

   int getBucketNumber(Binary key);

   int getReplicaCount();

   int getBucketCount();

   /**
    * Returns a new bucket set populated with bucket numbers according to the bucket count.
    *
    * @return the a new bucket set populated with bucket numbers according to the bucket count.
    */
   IntHashSet createBucketSet();

   /**
    * Removes buckets from a storage.
    *
    * @param storageNumber a storage numbers.
    * @param bucketNumbers a list of bucket numbers.
    */
   void removeBuckets(byte storageNumber, List<Integer> bucketNumbers);

   /**
    * Removes a bucket from a storage.
    *
    * @param storageNumber a storage number.
    * @param bucketNumber  a bucket number.
    * @return the removed bucket.
    */
   Bucket removeBucket(int storageNumber, Integer bucketNumber);

   /**
    * Sets bucket in the given storage.
    *
    * @param storageNumber a storage number.
    * @param bucketNumber  a bucket number.
    * @param bucket        a bucket to set.
    * @return a previously stored bucket.
    */
   Bucket setBucket(int storageNumber, Integer bucketNumber, Bucket bucket);

   /**
    * Restores a primary bucket from a replica.
    *
    * @param bucketNumber         a primary bucket number to restore.
    * @param replicaStorageNumber a replica storage number.
    */
   void restorePrimaryBucket(Integer bucketNumber, int replicaStorageNumber);

   Bucket getBucket(int storageNumber, int integerBucketNumber);

   /**
    * Returns client-provided subscribers.
    *
    * @return The returned map has a client's subscriber's identity as a key and a BinaryEntryModifiedSubscriber as a
    * value. The BinaryEntryModifiedSubscriber is created by wrapping a client subscriber to a
    * <code>BinaryEntryModifiedSubscriberAdapter</code>
    * @see Cache#addEventSubscriber(Set, EntryModifiedSubscriber)
    * @see BinaryEntryModifiedSubscriberAdapter
    */
   Map<Integer, LocalSubscription> getLocalEntryModifiedSubscriptions();

   /**
    * Returns a max number of elements in memory.
    *
    * @return the max number of elements in memory.
    */
   long getMaxSize();

   boolean hasBucket(int storageNumber, int bucketNumber);

   boolean isBucketOwner(int storageNumber, int bucketNumber);

   /**
    * {@inheritDoc}
    * <p/>
    * This implementation extends the default behaviour by destroying disk storages created at construction.
    */
   void shutdown();
}
